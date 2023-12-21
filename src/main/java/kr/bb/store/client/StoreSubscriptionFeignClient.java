package kr.bb.store.client;

import bloomingblooms.response.CommonResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@FeignClient(name = "storeSubscription-service")
public interface StoreSubscriptionFeignClient {
    org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(StoreSubscriptionFeignClient.class);

    @CircuitBreaker(
            name = "getStoreSubscriptions",
            fallbackMethod = "getStoreSubscriptionsFallback"
    )
    @PostMapping
    CommonResponse<Map<Long,Boolean>> getStoreSubscriptions(@RequestHeader(value = "userId") Long userId,
                                                            List<Long> storeIds);

    default CommonResponse<Map<Long,Boolean>> getStoreSubscriptionsFallback(@RequestHeader(value = "userId") Long userId,
                                                                    List<Long> storeIds, Exception e) {
        log.error(e.toString());
        log.warn("{}'s Request of '{}' failed. request will return fallback data",
                "StoreSubscriptionFeignClient", "getStoreSubscriptions");
        Map<Long,Boolean> data = storeIds.stream().collect(Collectors.toMap(id -> id, id -> false));
        return CommonResponse.<Map<Long,Boolean>>builder()
                .data(data)
                .message("data from circuit")
                .build();
    }

}
