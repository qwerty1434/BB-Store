package kr.bb.store.domain.cargo.facade;

import bloomingblooms.domain.flower.StockChangeDto;
import bloomingblooms.domain.notification.NotificationKind;
import kr.bb.store.domain.cargo.controller.response.RemainingStocksResponse;
import kr.bb.store.domain.cargo.dto.StockModifyDto;
import kr.bb.store.domain.cargo.exception.LockInterruptedException;
import kr.bb.store.domain.cargo.exception.StockChangeFailedException;
import kr.bb.store.domain.cargo.service.CargoService;
import kr.bb.store.message.OrderStatusSQSPublisher;
import kr.bb.store.message.OutOfStockSQSPublisher;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class CargoFacade {
    private final CargoService cargoService;
    private final RedissonClient redissonClient;
    private final OutOfStockSQSPublisher outOfStockSQSPublisher;
    private final OrderStatusSQSPublisher orderStatusSQSPublisher;

    public void modifyAllStocksWithLock(Long storeId, List<StockModifyDto> stockModifyDtos) {
        RLock lock = redissonClient.getLock(makeRedissonKey(storeId));
        try {
            // TODO : waitTime, leaseTime 외부 환경변수로 빼기
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

    public void plusStocksWithLock(Long userId, List<StockChangeDto> stockChangeDtos) {
        try {
            Set<Long> sufficientStores = cargoService.plusStockCounts(stockChangeDtos);
            sufficientStores.forEach(outOfStockSQSPublisher::publish);
        } catch (Exception e) {
            String phoneNumber = stockChangeDtos.get(0).getPhoneNumber();
            orderStatusSQSPublisher.publish(userId, phoneNumber, NotificationKind.INVALID_COUPON);
            throw e;
        }
    }

    public void minusStocksWithLock(Long userId, List<StockChangeDto> stockChangeDtos) {
        try {
            Set<Long> sufficientStores = cargoService.minusStockCounts(stockChangeDtos);
            sufficientStores.forEach(outOfStockSQSPublisher::publish);
        } catch (Exception e) {
            String phoneNumber = stockChangeDtos.get(0).getPhoneNumber();
            orderStatusSQSPublisher.publish(userId, phoneNumber, NotificationKind.INVALID_COUPON);
            throw e;
        }
    }

    public RemainingStocksResponse getAllStocks(Long storeId) {
        return cargoService.getAllStocks(storeId);
    }

    private String makeRedissonKey(Long storeId) {
        return storeId.toString();
    }

}
