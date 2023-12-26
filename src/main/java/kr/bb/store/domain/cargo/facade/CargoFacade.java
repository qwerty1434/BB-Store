package kr.bb.store.domain.cargo.facade;

import bloomingblooms.domain.flower.StockChangeDto;
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
    private static final Long STOCK_ALERT_COUNT = 50L;

    public void modifyAllStocksWithLock(Long storeId, List<StockModifyDto> stockModifyDtos) {
        RLock lock = redissonClient.getLock(makeRedissonKey(storeId));
        try {
            boolean available = lock.tryLock(5,1, TimeUnit.SECONDS);
            if(!available) {
                throw new StockChangeFailedException();
            }

            cargoService.modifyAllStocks(storeId, stockModifyDtos);

        } catch (InterruptedException e){
            throw new LockInterruptedException();
        } finally {
            if(lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public void plusStockCountsWithLock(StockChangeDto stockChangeDto) {
        Long storeId = stockChangeDto.getStoreId();
        RLock lock = redissonClient.getLock(makeRedissonKey(storeId));
        try {
            boolean available = lock.tryLock(5,1, TimeUnit.SECONDS);
            if(!available) {
                throw new StockChangeFailedException();
            }

            Long minStockCount = cargoService.plusStockCounts(storeId, stockChangeDto.getStockDtos());
            if(isInsufficientCondition(minStockCount)) {
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

    public void minusStockCountsWithLock(StockChangeDto stockChangeDto) {
        Long storeId = stockChangeDto.getStoreId();
        RLock lock = redissonClient.getLock(makeRedissonKey(storeId));
        try {
            boolean available = lock.tryLock(5,1, TimeUnit.SECONDS);
            if(!available) {
                throw new StockChangeFailedException();
            }

            Long minStockCount = cargoService.minusStockCounts(storeId, stockChangeDto.getStockDtos());
            if(isInsufficientCondition(minStockCount)) {
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
    private String makeRedissonKey(Long storeId) {
        return storeId.toString();
    }

    private boolean isInsufficientCondition(Long stockCount) {
        return stockCount < STOCK_ALERT_COUNT;
    }

}
