package kr.bb.store.domain.cargo.service;


import bloomingblooms.domain.flower.FlowerDto;
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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CargoService {

    private final FlowerCargoRepository flowerCargoRepository;
    private final RedissonClient redissonClient;

    @Transactional
    public void modifyAllStocks(Long storeId, List<StockModifyDto> stockModifyDtos) {
        stockModifyDtos.forEach(stockModifyDto -> {
            FlowerCargoId flowerCargoId = makeKeys(storeId, stockModifyDto.getFlowerId());
            RLock lock = redissonClient.getLock(makeRedissonKey(storeId, stockModifyDto.getFlowerId()));
            try {
                boolean available = lock.tryLock(5,1, TimeUnit.SECONDS);
                if(!available) {
                    throw new StockChangeFailedException();
                }

                if(stockModifyDto.getStock() < 0) {
                    throw new StockCannotBeNegativeException();
                }

                flowerCargoRepository.modifyStock(flowerCargoId.getStoreId(), flowerCargoId.getFlowerId(), stockModifyDto.getStock());
            } catch (InterruptedException e){
                throw new LockInterruptedException();
            } finally {
                if(lock.isLocked() && lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        });
    }

    @Transactional
    public void plusStockCount(Long storeId, Long flowerId, Long stock) {
        FlowerCargoId flowerCargoId = makeKeys(storeId,flowerId);
        RLock lock = redissonClient.getLock(makeRedissonKey(storeId, flowerId));
        try {
            boolean available = lock.tryLock(5,1, TimeUnit.SECONDS);
            if(!available) {
                throw new StockChangeFailedException();
            }

            FlowerCargo flowerCargo = flowerCargoRepository.findById(flowerCargoId)
                    .orElseThrow(FlowerCargoNotFoundException::new);

            if(flowerCargo.getStock() < -stock) {
                throw new StockCannotBeNegativeException();
            }

            flowerCargoRepository.plusStock(flowerCargoId.getStoreId(),flowerCargoId.getFlowerId(),stock);

        } catch (InterruptedException e){
            throw new LockInterruptedException();
        } finally {
            if(lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }


    }

    @Transactional
    public void minusStockCount(Long storeId, Long flowerId, Long stock) {
        FlowerCargoId flowerCargoId = makeKeys(storeId,flowerId);
        RLock lock = redissonClient.getLock(makeRedissonKey(storeId, flowerId));
        try {
            boolean available = lock.tryLock(5,1, TimeUnit.SECONDS);
            if(!available) {
                throw new StockChangeFailedException();
            }

            FlowerCargo flowerCargo = flowerCargoRepository.findById(flowerCargoId)
                    .orElseThrow(FlowerCargoNotFoundException::new);
            if(flowerCargo.getStock() < stock) {
                throw new StockCannotBeNegativeException();
            }

            flowerCargoRepository.minusStock(flowerCargoId.getStoreId(),flowerCargoId.getFlowerId(),stock);
        } catch (InterruptedException e){
            throw new LockInterruptedException();
        } finally {
            if(lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Transactional(readOnly = true)
    public RemainingStocksResponse getAllStocks(Long storeId) {
        List<FlowerCargo> flowerCargos = flowerCargoRepository.findAllByStoreId(storeId);
        List<StockInfoDto> stockInfoDtos = flowerCargos.stream()
                .map(StockInfoDto::fromEntity)
                .collect(Collectors.toList());

        return RemainingStocksResponse.builder()
                .stockInfoDtos(stockInfoDtos)
                .build();
    }

    @Transactional
    public void createBasicCargo(Store store, List<FlowerDto> flowers) {
        List<FlowerCargo> flowerCargos = flowers.stream()
                .map(flowerDto -> FlowerCargo.builder()
                        .id(makeKeys(store.getId(), flowerDto.getFlowerId()))
                        .store(store)
                        .flowerName(flowerDto.getFlowerName())
                        .build()
                )
                .collect(Collectors.toList());
        flowerCargoRepository.saveAll(flowerCargos);

    }

    private FlowerCargoId makeKeys(Long storeId, Long flowerId) {
        return FlowerCargoId.builder()
                .storeId(storeId)
                .flowerId(flowerId)
                .build();
    }

    private String makeRedissonKey(Long storeId, Long flowerId) {
        return "s" + storeId + "f" + flowerId;
    }
}
