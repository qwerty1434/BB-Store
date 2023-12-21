package kr.bb.store.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Resilience4jConfig {
    @Bean
    public CircuitBreakerConfig circuitBreakerConfig() {
        // TODO : 부하테스트 하면서 설정값 최적화하기
        // circuit for server test
        return CircuitBreakerConfig.custom()
                .slidingWindowSize(2)
                .failureRateThreshold(10)
                .build();
    }

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry(CircuitBreakerConfig circuitBreakerConfig) {
        return CircuitBreakerRegistry.of(circuitBreakerConfig);
    }

}
