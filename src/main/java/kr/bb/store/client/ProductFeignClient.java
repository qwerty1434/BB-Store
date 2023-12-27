package kr.bb.store.client;

import bloomingblooms.domain.flower.FlowerDto;
import bloomingblooms.domain.product.StoreSubscriptionProductId;
import bloomingblooms.response.CommonResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;

@FeignClient(name = "product-service", url = "${endpoint.product-service}")
public interface ProductFeignClient {
    org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ProductFeignClient.class);

    @CircuitBreaker(
            name = "getFlowers",
            fallbackMethod = "getFlowersFallback"
    )
    @GetMapping("/client/flowers")
    CommonResponse<List<FlowerDto>> getFlowers();

    default CommonResponse<List<FlowerDto>> getFlowersFallback(Exception e) {
        // TODO : fallback제거 - 꽃 정보 없이 가게는 생성되면 안됨
        log.error(e.toString());
        log.warn("{}'s Request of '{}' failed. request will return fallback data", "ProductFeignClient", "getFlowers");
        return CommonResponse.<List<FlowerDto>>builder()
                .data(Collections.emptyList())
                .message("data from circuit")
                .build();
    }

    @CircuitBreaker(
            name = "getSubscriptionProductId",
            fallbackMethod = "getSubscriptionProductIdFallback"
    )
    @GetMapping("client/store")
    CommonResponse<StoreSubscriptionProductId> getSubscriptionProductId(@RequestParam(name="store-id") Long storeId);

    default CommonResponse<StoreSubscriptionProductId> getSubscriptionProductIdFallback(Exception e) {
        // TODO : fallback제거 - 단순 프론트테스트 편의를 높이기 위해 작성해둔 코드
        log.error(e.toString());
        log.warn("{}'s Request of '{}' failed. request will return fallback data",
                "ProductFeignClient", "getSubscriptionProductIdFallback");
        return CommonResponse.<StoreSubscriptionProductId>builder()
                .data(null)
                .message("data from circuit")
                .build();
    }
}
