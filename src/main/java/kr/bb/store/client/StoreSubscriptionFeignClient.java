package kr.bb.store.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.Map;

@FeignClient(name = "storeSubscription-service")
public interface StoreSubscriptionFeignClient {
    @PostMapping
    Map<Long,Boolean> getStoreSubscriptions(@RequestHeader(value = "userId") Long userId,
                                            List<Long> storeIds);
}
