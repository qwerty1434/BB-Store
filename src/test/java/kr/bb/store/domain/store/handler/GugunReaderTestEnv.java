package kr.bb.store.domain.store.handler;

import kr.bb.store.domain.BasicIntegrationTestEnv;
import kr.bb.store.domain.store.entity.address.Gugun;
import kr.bb.store.domain.store.entity.address.GugunRepository;
import kr.bb.store.domain.store.entity.address.Sido;
import kr.bb.store.domain.store.entity.address.SidoRepository;
import kr.bb.store.domain.store.exception.address.InvalidParentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class GugunReaderTestEnv extends BasicIntegrationTestEnv {
    @Autowired
    private GugunReader gugunReader;
    @Autowired
    private SidoRepository sidoRepository;
    @Autowired
    private GugunRepository gugunRepository;

    @DisplayName("시/도, 그리고 구/군명을 입력받아 gugun값을 반환한다")
    @Test
    public void readGugun() {
        // given
        Sido sido = new Sido("011", "서울");
        sidoRepository.save(sido);
        Gugun gugun = new Gugun("110011",sido,"강남구");
        gugunRepository.save(gugun);

        // when
        Gugun gugunResult = gugunReader.readGugunCorrespondingSidoWithCode(sido, "110011");

        // then
        assertThat(gugunResult.getSido().getCode()).isEqualTo(sido.getCode());
        assertThat(gugunResult.getName()).isEqualTo("강남구");
    }

    @DisplayName("해당 시/도에 포함되지 않는 구/군명을 가져올 수 없다")
    @Test
    public void cannotReadWhenNotCorrespondingSidoAndGugun() {
        // given
        Sido sido1 = new Sido("011", "서울");
        Sido sido2 = new Sido("022", "수원");
        sidoRepository.saveAll(List.of(sido1,sido2));
        Gugun gugun1 = new Gugun("110011",sido1,"강남구");
        Gugun gugun2 = new Gugun("223322",sido2,"영통구");
        gugunRepository.saveAll(List.of(gugun1,gugun2));

        // when // then
        assertThatThrownBy(() -> gugunReader.readGugunCorrespondingSidoWithCode(sido1,"223322"))
                .isInstanceOf(InvalidParentException.class)
                .hasMessage("선택한 시/도와 구/군이 맞지 않습니다.");
    }



}