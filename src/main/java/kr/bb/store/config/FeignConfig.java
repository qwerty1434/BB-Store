package kr.bb.store.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@EnableFeignClients(basePackages = "kr.bb.store")
@Configuration
public class FeignConfig {
}
