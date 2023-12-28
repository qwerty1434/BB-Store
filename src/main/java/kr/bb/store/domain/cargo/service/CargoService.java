package kr.bb.store.domain.cargo.service;


import bloomingblooms.domain.flower.FlowerDto;
import bloomingblooms.domain.flower.StockChangeDto;
import bloomingblooms.domain.flower.StockDto;
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

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
                            RLock lock = redissonClient.getLock(makeRedissonKey(stockChangeDto.getStoreId(), stockDto.getFlowerId()));
                            try {
                                boolean available = lock.tryLock(5, 1, TimeUnit.SECONDS);
                                if (!available) {
                                    throw new StockChangeFailedException();
                                }

                                FlowerCargo flowerCargo = getFlowerCargo(stockChangeDto.getStoreId(), stockDto.getFlowerId());

                                long afterPlusCount = flowerCargo.getStock() + stockDto.getStock();
                                if (isOutOfStock(afterPlusCount)) {
                                    throw new StockCannotBeNegativeException();
                                }

                                flowerCargoRepository.plusStock(flowerCargo.getId().getStoreId(), flowerCargo.getId().getFlowerId(), stockDto.getStock());

                                if (isInsufficientCondition(afterPlusCount)) {
                                    return stockChangeDto.getStoreId();
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
                            RLock lock = redissonClient.getLock(makeRedissonKey(stockChangeDto.getStoreId(), stockDto.getFlowerId()));
                            try {
                                boolean available = lock.tryLock(5, 1, TimeUnit.SECONDS);
                                if (!available) {
                                    throw new StockChangeFailedException();
                                }

                                FlowerCargo flowerCargo = getFlowerCargo(stockChangeDto.getStoreId(), stockDto.getFlowerId());

                                long afterMinusCount = flowerCargo.getStock() - stockDto.getStock();
                                if (isOutOfStock(afterMinusCount)) {
                                    throw new StockCannotBeNegativeException();
                                }

                                flowerCargoRepository.minusStock(flowerCargo.getId().getStoreId(), flowerCargo.getId().getFlowerId(), stockDto.getStock());

                                if (isInsufficientCondition(afterMinusCount)) {
                                    return stockChangeDto.getStoreId();
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

    @Transactional(readOnly = true)
    public RemainingStocksResponse getAllStocks(Long storeId) {
        List<FlowerCargo> flowerCargos = flowerCargoRepository.findAllByStoreId(storeId);
        List<StockInfoDto> stockInfoDtos = flowerCargos.stream()
                .map(StockInfoDto::fromEntity)
                .collect(Collectors.toList());

        return RemainingStocksResponse.from(stockInfoDtos);
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

    private String makeRedissonKey(Long storeId, Long flowerId) {
        return  storeId + ":" + flowerId;
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
