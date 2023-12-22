package kr.bb.store.client;

import bloomingblooms.response.CommonResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@FeignClient(name = "storeLike-service", url = "${endpoint.storeLike-service}")
public interface StoreLikeFeignClient {
    org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(StoreLikeFeignClient.class);

    @CircuitBreaker(
            name = "getStoreLikes",
            fallbackMethod = "getStoreLikesFallback"
    )
    @PostMapping("/client/likes/stores")
    CommonResponse<Map<Long,Boolean>> getStoreLikes(
            @RequestHeader(value = "userId") Long userId, @RequestBody List<Long> storeIds);

    default CommonResponse<Map<Long,Boolean>> getStoreLikesFallback(
            @RequestHeader(value = "userId") Long userId, @RequestBody List<Long> storeIds, Exception e) {
        log.error(e.toString());
        log.warn("{}'s Request of '{}' failed. request will return fallback data",
                "StoreLikeFeignClient", "getStoreLikes");
        Map<Long,Boolean> data = storeIds.stream().collect(Collectors.toMap(id -> id, id -> false));
        return CommonResponse.<Map<Long,Boolean>>builder()
                .data(data)
                .message("data from circuit")
                .build();
    }
}
