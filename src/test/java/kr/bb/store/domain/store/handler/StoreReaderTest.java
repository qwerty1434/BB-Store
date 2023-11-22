package kr.bb.store.domain.store.handler;

import kr.bb.store.domain.store.controller.request.StoreCreateRequest;
import kr.bb.store.domain.store.entity.address.Gugun;
import kr.bb.store.domain.store.entity.address.GugunRepository;
import kr.bb.store.domain.store.entity.address.Sido;
import kr.bb.store.domain.store.entity.address.SidoRepository;
import kr.bb.store.domain.store.exception.StoreNotFoundException;
import kr.bb.store.domain.store.handler.response.DetailInfoResponse;
import kr.bb.store.domain.store.service.StoreService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class StoreReaderTest {
    @Autowired
    private StoreReader storeReader;
    @Autowired
    private StoreService storeService;
    @Autowired
    private SidoRepository sidoRepository;
    @Autowired
    private GugunRepository gugunRepository;
    @Autowired
    private EntityManager em;

    @DisplayName("가게 아이디를 입력받아 가게에 대한 상세정보를 반환한다")
    @Test
    public void readDetailInfo() {
        // given
        Sido sido = new Sido("011", "서울");
        sidoRepository.save(sido);
        Gugun gugun = new Gugun("110011",sido,"강남구");
        gugunRepository.save(gugun);

        Long userId = 1L;
        StoreCreateRequest request = createStoreCreateRequest();
        Long storeId = storeService.createStore(userId, request);
        em.flush();
        em.clear();

        // when
        DetailInfoResponse response = storeReader.readDetailInfo(storeId);

        // then
        assertThat(response.getStoreName()).isEqualTo("가게1");
        assertThat(response.getMinOrderPrice()).isEqualTo(10_000L);
        assertThat(response.getSido()).isEqualTo("서울");
    }

    @DisplayName("존재하지 않는 가게 Id로 요청하면 가게가 존재하지 않는다는 예외가 발생한다")
    @Test
    public void cannotReadWhenUseInvalidStoreId() {
        // given
        Sido sido = new Sido("011", "서울");
        sidoRepository.save(sido);
        Gugun gugun = new Gugun("110011",sido,"강남구");
        gugunRepository.save(gugun);
        Long storeId = 1L;

        // when
        Assertions.assertThatThrownBy(() -> storeReader.readDetailInfo(storeId))
                .isInstanceOf(StoreNotFoundException.class)
                .hasMessage("해당 가게가 존재하지 않습니다.");

    }


    private StoreCreateRequest createStoreCreateRequest() {
        return StoreCreateRequest.builder()
                .storeName("가게1")
                .detailInfo("가게 상세정보")
                .storeThumbnailImage("가게 썸네일")
                .phoneNumber("가게 전화번호")
                .accountNumber("가게 계좌정보")
                .bank("가게 계좌 은행정보")
                .minOrderPrice(10_000L)
                .deliveryPrice(5_000L)
                .freeDeliveryMinPrice(10_000L)
                .sido("서울")
                .gugun("강남구")
                .address("서울 강남구 남부순환로")
                .detailAddress("202호")
                .zipCode("001112")
                .lat(33.33322F)
                .lon(127.13123F)
                .build();
    }
}