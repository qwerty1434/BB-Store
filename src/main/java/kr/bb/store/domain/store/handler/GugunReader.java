package kr.bb.store.domain.store.handler;

import kr.bb.store.domain.store.dto.GugunDto;
import kr.bb.store.domain.store.entity.address.Gugun;
import kr.bb.store.domain.store.entity.address.GugunRepository;
import kr.bb.store.domain.store.entity.address.Sido;
import kr.bb.store.domain.store.exception.address.GugunNotFoundException;
import kr.bb.store.domain.store.exception.address.InvalidParentException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class GugunReader {
    private final GugunRepository gugunRepository;

    public Gugun readGugunCorrespondingSido(Sido sido, String gugunName) {
        Gugun gugun = gugunRepository.findByName(gugunName)
                .orElseThrow(GugunNotFoundException::new);
        if(!gugun.getSido().getCode().equals(sido.getCode())) {
            throw new InvalidParentException();
        }
        return gugun;
    }

    public List<Gugun> readGuguns(String sidoCode) {
        return gugunRepository.findGugunBySidoCode(sidoCode);
    }
}
