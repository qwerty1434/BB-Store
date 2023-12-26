package kr.bb.store.domain.cargo.facade;

import kr.bb.store.domain.cargo.controller.response.RemainingStocksResponse;
import kr.bb.store.domain.cargo.dto.StockModifyDto;
import kr.bb.store.domain.cargo.entity.FlowerCargoId;
import kr.bb.store.domain.cargo.exception.LockInterruptedException;
import kr.bb.store.domain.cargo.exception.StockChangeFailedException;
import kr.bb.store.domain.cargo.service.CargoService;
import kr.bb.store.message.OutOfStockSQSPublisher;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class CargoFacade {
    private final CargoService cargoService;
    private final RedissonClient redissonClient;
    private final OutOfStockSQSPublisher outOfStockSQSPublisher;
    // TODO : Environment로 교체
    private static final Long STOCK_ALERT_COUNT = 50L;

    public void modifyAllStocksWithLock(Long storeId, List<StockModifyDto> stockModifyDtos) {
        stockModifyDtos.forEach(stockModifyDto -> {
            FlowerCargoId flowerCargoId = makeId(storeId, stockModifyDto.getFlowerId());
            RLock lock = redissonClient.getLock(makeRedissonKey(storeId, stockModifyDto.getFlowerId()));
            try {
                boolean available = lock.tryLock(5,1, TimeUnit.SECONDS);
                if(!available) {
                    throw new StockChangeFailedException();
                }

                cargoService.modifyAllStocks(stockModifyDto, flowerCargoId);

            } catch (InterruptedException e){
                throw new LockInterruptedException();
            } finally {
                if(lock.isLocked() && lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        });
    }

    public void plusStockCountWithLock(Long storeId, Long flowerId, Long stock) {
        FlowerCargoId flowerCargoId = makeId(storeId,flowerId);
        RLock lock = redissonClient.getLock(makeRedissonKey(storeId, flowerId));
        try {
            boolean available = lock.tryLock(5,1, TimeUnit.SECONDS);
            if(!available) {
                throw new StockChangeFailedException();
            }

            Long stockCount = cargoService.plusStockCount(flowerCargoId, stock);
            if(isInsufficientCondition(stockCount)) {
                outOfStockSQSPublisher.publish(storeId);
            }

        } catch (InterruptedException e){
            throw new LockInterruptedException();
        } finally {
            if(lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public void minusStockCountWithLock(Long storeId, Long flowerId, Long stock) {
        FlowerCargoId flowerCargoId = makeId(storeId,flowerId);
        RLock lock = redissonClient.getLock(makeRedissonKey(storeId, flowerId));
        try {
            boolean available = lock.tryLock(5,1, TimeUnit.SECONDS);
            if(!available) {
                throw new StockChangeFailedException();
            }

            Long stockCount = cargoService.minusStockCount(flowerCargoId, stock);
            if(isInsufficientCondition(stockCount)) {
                outOfStockSQSPublisher.publish(storeId);
            }

        } catch (InterruptedException e){
            throw new LockInterruptedException();
        } finally {
            if(lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public RemainingStocksResponse getAllStocks(Long storeId) {
        return cargoService.getAllStocks(storeId);
    }

    private FlowerCargoId makeId(Long storeId, Long flowerId) {
        return FlowerCargoId.builder()
                .storeId(storeId)
                .flowerId(flowerId)
                .build();
    }

    private String makeRedissonKey(Long storeId, Long flowerId) {
        return storeId + ":" + flowerId;
    }

    private boolean isInsufficientCondition(Long stockCount) {
        return stockCount < STOCK_ALERT_COUNT;
    }

}
