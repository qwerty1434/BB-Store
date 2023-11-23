package kr.bb.store.domain.store.handler;

import kr.bb.store.domain.store.entity.address.Sido;
import kr.bb.store.domain.store.entity.address.SidoRepository;
import kr.bb.store.domain.store.exception.address.SidoNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SidoReader {
    private final SidoRepository sidoRepository;

    public Sido readSido(String sidoName) {
        return sidoRepository.findByName(sidoName)
                .orElseThrow(SidoNotFoundException::new);
    }
}
