package kr.bb.store.domain.store.handler;

import kr.bb.store.domain.store.entity.address.Sido;
import kr.bb.store.domain.store.entity.address.SidoRepository;
import kr.bb.store.domain.store.exception.address.SidoNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class SidoReader {
    private final SidoRepository sidoRepository;

    public Sido readSidoByName(String sidoName) {
        return sidoRepository.findByName(sidoName)
                .orElseThrow(SidoNotFoundException::new);

    }
    public Sido readSido(String sidoCode) {
        return sidoRepository.findById(sidoCode)
                .orElseThrow(SidoNotFoundException::new);
    }


    public List<Sido> readAll() {
        return sidoRepository.findAll();
    }
}
