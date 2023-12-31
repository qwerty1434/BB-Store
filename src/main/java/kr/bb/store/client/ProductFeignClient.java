package kr.bb.store.client;

import bloomingblooms.domain.flower.FlowerDto;
import bloomingblooms.domain.product.StoreSubscriptionProductId;
import bloomingblooms.response.CommonResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "product-service", url = "${endpoint.product-service}")
public interface ProductFeignClient {
    org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ProductFeignClient.class);

    @GetMapping("/client/flowers")
    CommonResponse<List<FlowerDto>> getFlowers();

    @CircuitBreaker(
            name = "getSubscriptionProductId",
            fallbackMethod = "getSubscriptionProductIdFallback"
    )
    @GetMapping("/client/store")
    CommonResponse<StoreSubscriptionProductId> getSubscriptionProductId(@RequestParam(name="store-id") Long storeId);

    default CommonResponse<StoreSubscriptionProductId> getSubscriptionProductIdFallback(Exception e) {
        // TODO : circuit을 사용할지 말지 프론트와 논의 (구독상품 클릭이 안되더라고 가게정보를 보이게 할건지)
        log.error(e.toString());
        log.warn("{}'s Request of '{}' failed. request will return fallback data",
                "ProductFeignClient", "getSubscriptionProductIdFallback");
        return CommonResponse.<StoreSubscriptionProductId>builder()
                .data(null)
                .message("data from circuit")
                .build();
    }
}
