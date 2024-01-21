package kr.bb.store.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class Resilience4jConfig {
    @Bean
    public CircuitBreakerConfig circuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(10) // 슬라이딩 윈도우 크기
                .failureRateThreshold(20) // 서킷을 open할 실패 비율
                .slowCallDurationThreshold(Duration.ofSeconds(5)) // 느린응답 조건
                .slowCallRateThreshold(20) // 서킷을 open할 느린응답 비율
                .minimumNumberOfCalls(5) // 서킷 확인을 위한 최소 요청 개수
                .waitDurationInOpenState(Duration.ofMinutes(5)) // open에서 half-open이 되기까지의 시간
                .maxWaitDurationInHalfOpenState(Duration.ofMinutes(1)) // half-open상태의 최대유지기간
                .permittedNumberOfCallsInHalfOpenState(3) // half-open상태에서 받아들일 요청 개수
                .build();
    }

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry(CircuitBreakerConfig circuitBreakerConfig) {
        return CircuitBreakerRegistry.of(circuitBreakerConfig);
    }

}
