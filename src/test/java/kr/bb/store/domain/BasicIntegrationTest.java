package kr.bb.store.domain;

import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class BasicIntegrationTest {
    /*
     * @SpringBootTest는 실제로 모든 빈을 등록함
     * RedisConfig에서는 RedissonClient를 빈으로 등록하는 factory method가 정의되어 있고 이를 빈으로 등록하려 함
     * 하지만 TestContainer를 쓰지 않는 환경에서는 Redis가 정의되어 있지 않아 빈 등록에 실패함
     * 그래서 RedissonClient만 모킹처리함
     */
    @MockBean
    private RedissonClient redissonClient;
}
