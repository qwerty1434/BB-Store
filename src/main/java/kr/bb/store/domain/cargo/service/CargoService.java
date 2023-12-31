package kr.bb.store.domain.cargo.service;


import bloomingblooms.domain.flower.FlowerDto;
import bloomingblooms.domain.flower.StockChangeDto;
import kr.bb.store.domain.cargo.controller.response.RemainingStocksResponse;
import kr.bb.store.domain.cargo.dto.StockInfoDto;
import kr.bb.store.domain.cargo.dto.StockModifyDto;
import kr.bb.store.domain.cargo.entity.FlowerCargo;
import kr.bb.store.domain.cargo.entity.FlowerCargoId;
import kr.bb.store.domain.cargo.exception.FlowerCargoNotFoundException;
import kr.bb.store.domain.cargo.exception.LockInterruptedException;
import kr.bb.store.domain.cargo.exception.StockCannotBeNegativeException;
import kr.bb.store.domain.cargo.exception.StockChangeFailedException;
import kr.bb.store.domain.cargo.repository.FlowerCargoRepository;
import kr.bb.store.domain.store.entity.Store;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static kr.bb.store.util.RedisUtils.makeRedissonKey;

@Slf4j
@Service
@RequiredArgsConstructor
public class CargoService {
    private final RedissonClient redissonClient;
    private final FlowerCargoRepository flowerCargoRepository;
    private static final Long EMPTY_COUNT = 0L;
    private static final Long STOCK_ALERT_COUNT = 50L;

    @Transactional
    public void modifyAllStocks(Long storeId, List<StockModifyDto> stockModifyDtos) {
        stockModifyDtos.forEach(stockModifyDto -> {
            if(stockModifyDto.getStock() < EMPTY_COUNT) {
                throw new StockCannotBeNegativeException();
            }
            flowerCargoRepository.modifyStock(storeId, stockModifyDto.getFlowerId(), stockModifyDto.getStock());
        });
    }

    @Transactional
    public Set<Long> plusStockCounts(List<StockChangeDto> stockChangeDtos) {
        return stockChangeDtos.stream()
                .flatMap(stockChangeDto -> stockChangeDto.getStockDtos().stream()
                        .map(stockDto -> {
                            long storeId = stockChangeDto.getStoreId();
                            long flowerId = stockDto.getFlowerId();
                            long stockCount = stockDto.getStock();
                            RLock lock = redissonClient.getLock(makeRedissonKey(storeId, flowerId));
                            try {
                                boolean available = lock.tryLock(5, 1, TimeUnit.SECONDS);
                                if (!available) {
                                    throw new StockChangeFailedException();
                                }

                                FlowerCargo flowerCargo = getFlowerCargo(storeId, flowerId);

                                long afterPlusCount = flowerCargo.getStock() + stockCount;
                                if (isOutOfStock(afterPlusCount)) {
                                    throw new StockCannotBeNegativeException();
                                }

                                flowerCargoRepository.plusStock(flowerCargo.getId().getStoreId(), flowerId, stockCount);

                                if (isInsufficientCondition(afterPlusCount)) {
                                    return storeId;
                                }
                            } catch (InterruptedException e) {
                                throw new LockInterruptedException();
                            } finally {
                                unlock(lock);
                            }
                            return null;
                        })
                )
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @Transactional
    public Set<Long> minusStockCounts(List<StockChangeDto> stockChangeDtos) {
        return stockChangeDtos.stream()
                .flatMap(stockChangeDto -> stockChangeDto.getStockDtos().stream()
                        .map(stockDto -> {
                            long storeId = stockChangeDto.getStoreId();
                            long flowerId = stockDto.getFlowerId();
                            long stockCount = stockDto.getStock();
                            RLock lock = redissonClient.getLock(makeRedissonKey(storeId, flowerId));
                            try {
                                boolean available = lock.tryLock(5, 1, TimeUnit.SECONDS);
                                if (!available) {
                                    throw new StockChangeFailedException();
                                }

                                FlowerCargo flowerCargo = getFlowerCargo(stockChangeDto.getStoreId(), flowerId);

                                long afterMinusCount = flowerCargo.getStock() - stockCount;
                                if (isOutOfStock(afterMinusCount)) {
                                    throw new StockCannotBeNegativeException();
                                }

                                flowerCargoRepository.minusStock(storeId, flowerId, stockCount);

                                if (isInsufficientCondition(afterMinusCount)) {
                                    return storeId;
                                }
                            } catch (InterruptedException e) {
                                throw new LockInterruptedException();
                            } finally {
                                unlock(lock);
                            }
                            return null;
                        }))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @Transactional
    public void createBasicCargo(Store store, List<FlowerDto> flowers) {
        List<FlowerCargo> flowerCargos = flowers.stream()
                .map(flowerDto -> FlowerCargo.builder()
                        .id(makeId(store.getId(), flowerDto.getFlowerId()))
                        .store(store)
                        .flowerName(flowerDto.getFlowerName())
                        .build()
                )
                .collect(Collectors.toList());
        flowerCargoRepository.saveAll(flowerCargos);
    }

    public RemainingStocksResponse getAllStocks(Long storeId) {
        List<FlowerCargo> flowerCargos = flowerCargoRepository.findAllByStoreId(storeId);
        List<StockInfoDto> stockInfoDtos = flowerCargos.stream()
                .map(StockInfoDto::fromEntity)
                .collect(Collectors.toList());

        return RemainingStocksResponse.from(stockInfoDtos);
    }

    private FlowerCargo getFlowerCargo(Long storeId, Long flowerId) {
        FlowerCargoId flowerCargoId = makeId(storeId, flowerId);
        return flowerCargoRepository.findById(flowerCargoId)
                .orElseThrow(FlowerCargoNotFoundException::new);
    }

    private FlowerCargoId makeId(Long storeId, Long flowerId) {
        return FlowerCargoId.builder()
                .storeId(storeId)
                .flowerId(flowerId)
                .build();
    }


    private boolean isOutOfStock(long afterChangeCount) {
        return afterChangeCount < EMPTY_COUNT;
    }

    private boolean isInsufficientCondition(Long stockCount) {
        return stockCount < STOCK_ALERT_COUNT;
    }

    private void unlock(RLock lock) {
        if(lock.isLocked() && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }
}
