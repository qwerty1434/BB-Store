package kr.bb.store.client;

import kr.bb.store.client.dto.FlowerDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "product-service")
public interface ProductFeignClient {
    @GetMapping
    List<FlowerDto> getFlowers();

    @GetMapping
    String getProductThumbnail(@RequestParam(name="productId") Long productId);

    @GetMapping
    String getSubscriptionProductId(@RequestParam(name="storeId") Long storeId);
}
