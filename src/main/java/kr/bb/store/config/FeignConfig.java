package kr.bb.store.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.stereotype.Component;

@EnableFeignClients(basePackages = "kr.bb.store")
@Component
public class FeignConfig {
}
