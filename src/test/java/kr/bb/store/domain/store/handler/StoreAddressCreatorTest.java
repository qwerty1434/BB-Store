package kr.bb.store.domain.store.handler;

import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.entity.StoreAddress;
import kr.bb.store.domain.store.entity.address.Gugun;
import kr.bb.store.domain.store.entity.address.GugunRepository;
import kr.bb.store.domain.store.entity.address.Sido;
import kr.bb.store.domain.store.entity.address.SidoRepository;
import kr.bb.store.domain.store.exception.address.GugunNotFoundException;
import kr.bb.store.domain.store.exception.address.SidoNotFoundException;
import kr.bb.store.domain.store.dto.StoreAddressRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class StoreAddressCreatorTest {
    @Autowired
    private StoreAddressCreator storeAddressCreator;
    @Autowired
    private SidoRepository sidoRepository;
    @Autowired
    private GugunRepository gugunRepository;

    @DisplayName("가게주소 정보를 전달받아 가게주소를 생성한다")
    @Test
    void createStoreAddress() {
        // given
        Sido sido = new Sido("011", "서울");
        sidoRepository.save(sido);
        Gugun gugun = new Gugun("110011",sido,"강남구");
        gugunRepository.save(gugun);

        StoreAddressRequest storeAddressRequest = createStoreAddressRequest();
        Store store = createStore();

        // when
        StoreAddress storeAddress = storeAddressCreator.create(store, storeAddressRequest);

        // then
        assertThat(storeAddress.getId()).isNotNull();
    }

    @DisplayName("존재하지 않는 시/도 정보로 가게주소를 생성할 수 없다")
    @Test
    void cannotCreateStoreAddressWithoutSido() {
        // given
        StoreAddressRequest storeAddressRequest = createStoreAddressRequest();
        Store store = createStore();

        // when // then
        assertThatThrownBy(() -> storeAddressCreator.create(store, storeAddressRequest))
                .isInstanceOf(SidoNotFoundException.class)
                .hasMessage("해당 시/도가 존재하지 않습니다.");
    }

    @DisplayName("존재하지 않는 구/군 정보로 가게주소를 생성할 수 없다")
    @Test
    void cannotCreateStoreAddressWithoutGugun() {
        // given
        Sido sido = new Sido("011", "서울");
        sidoRepository.save(sido);
        // given
        StoreAddressRequest storeAddressRequest = createStoreAddressRequest();
        Store store = createStore();

        // when // then
        assertThatThrownBy(() -> storeAddressCreator.create(store, storeAddressRequest))
                .isInstanceOf(GugunNotFoundException.class)
                .hasMessage("해당 구/군이 존재하지 않습니다.");
    }



    private StoreAddressRequest createStoreAddressRequest() {
        return StoreAddressRequest.builder()
                .sido("서울")
                .gugun("강남구")
                .address("서울 강남구 남부순환로")
                .detailAddress("202호")
                .zipCode("001112")
                .lat(33.33322D)
                .lon(127.13123D)
                .build();
    }

    private Store createStore() {
        return Store.builder()
                .storeManagerId(1L)
                .storeCode("가게코드")
                .storeName("가게1")
                .detailInfo("가게 상세정보")
                .storeThumbnailImage("가게 썸네일")
                .phoneNumber("가게 전화번호")
                .accountNumber("가게 계좌정보")
                .bank("가게 계좌 은행정보")
                .build();
    }

}