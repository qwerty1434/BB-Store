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

    // subscriptionProductId는 정기구독 신청 버튼을 위해 받아오는 데이터
    // 해당 값을 받아오지 못하더라도 다른 가게정보들은 고객이 볼 수 있는게 더 합리적이라고 팀적으로 판단함
    // subscriptionProductId가 null일 때 프론트에서는 해당 버튼을 클릭할 수 없는 비활성화 상태로 보이게 됨
    default CommonResponse<StoreSubscriptionProductId> getSubscriptionProductIdFallback(Exception e) {
        log.error(e.toString());
        log.warn("{}'s Request of '{}' failed. request will return fallback data",
                "ProductFeignClient", "getSubscriptionProductIdFallback");
        return CommonResponse.<StoreSubscriptionProductId>builder()
                .data(StoreSubscriptionProductId.builder()
                        .subscriptionProductId(null)
                        .build()
                )
                .message("data from circuit")
                .build();
    }
}
