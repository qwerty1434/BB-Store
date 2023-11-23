package kr.bb.store.domain.store.handler;

import kr.bb.store.domain.store.entity.address.Gugun;
import kr.bb.store.domain.store.entity.address.GugunRepository;
import kr.bb.store.domain.store.exception.address.GugunNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class GugunReader {
    private final GugunRepository gugunRepository;

    public Gugun readGugun(String gugunName) {
        return gugunRepository.findByName(gugunName)
                .orElseThrow(GugunNotFoundException::new);
    }
}
