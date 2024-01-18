package kr.bb.store.client;


import bloomingblooms.response.CommonResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url="${endpoint.user-service}")
public interface UserFeignClient {

    @CircuitBreaker(
            name = "getStoreSubscriptions"
    )
    @GetMapping("/client/users/{userId}/phone-number")
    CommonResponse<String> getPhoneNumber(@PathVariable(name = "userId") Long userId);

}
