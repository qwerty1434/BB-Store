package kr.bb.store.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RedisCacheInitializer implements ApplicationRunner {
    @CacheEvict(value = {"store-list-with-paging"}, allEntries = true)
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("store-list Cache Initialized");
    }
}
