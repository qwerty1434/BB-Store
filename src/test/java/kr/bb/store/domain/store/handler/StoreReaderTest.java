package kr.bb.store.domain.store.handler;

import kr.bb.store.domain.store.entity.DeliveryPolicy;
import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.entity.StoreAddress;
import kr.bb.store.domain.store.entity.address.Gugun;
import kr.bb.store.domain.store.entity.address.GugunRepository;
import kr.bb.store.domain.store.entity.address.Sido;
import kr.bb.store.domain.store.entity.address.SidoRepository;
import kr.bb.store.domain.store.exception.StoreNotFoundException;
import kr.bb.store.domain.store.handler.response.DetailInfoResponse;
import kr.bb.store.domain.store.repository.DeliveryPolicyRepository;
import kr.bb.store.domain.store.repository.StoreAddressRepository;
import kr.bb.store.domain.store.repository.StoreRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class StoreReaderTest {
    @Autowired
    private StoreReader storeReader;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private StoreAddressRepository storeAddressRepository;
    @Autowired
    private DeliveryPolicyRepository deliveryPolicyRepository;
    @Autowired
    private SidoRepository sidoRepository;
    @Autowired
    private GugunRepository gugunRepository;
    @Autowired
    private EntityManager em;


    @DisplayName("가게 아이디를 입력받아 가게에 대한 상세정보를 반환한다")
    @Test
    void readDetailInfo() {
        Long userId = 1L;

        Store store = createStore(userId);
        storeRepository.save(store);

        StoreAddress storeAddress = createStoreAddress(store);
        storeAddressRepository.save(storeAddress);

        DeliveryPolicy deliveryPolicy = createDeliveryPolicy(store);
        deliveryPolicyRepository.save(deliveryPolicy);

        em.flush();
        em.clear();

        // when
        DetailInfoResponse response = storeReader.readDetailInfo(store.getId());

        // then
        assertThat(response.getStoreName()).isEqualTo("가게");
        assertThat(response.getMinOrderPrice()).isEqualTo(10_000L);
        assertThat(response.getSido()).isEqualTo("서울");
    }

    @DisplayName("존재하지 않는 가게 Id로 요청하면 가게가 존재하지 않는다는 예외가 발생한다")
    @Test
    void cannotReadWhenUseInvalidStoreId() {
        // given
        Long storeId = 1L;

        // when
        Assertions.assertThatThrownBy(() -> storeReader.readDetailInfo(storeId))
                .isInstanceOf(StoreNotFoundException.class)
                .hasMessage("해당 가게가 존재하지 않습니다.");

    }

    @DisplayName("페이징 처리를 통해 모든 가게 리스트 정보를 반환한다")
    @Test
    void readStoresWithPaging() {
        // given
        Sido sido = new Sido("011", "서울");
        sidoRepository.save(sido);
        Gugun gugun = new Gugun("110011",sido,"강남구");
        gugunRepository.save(gugun);

        Store s1 = createStore(1L);
        Store s2 = createStore(1L);
        Store s3 = createStore(1L);
        Store s4 = createStore(1L);
        Store s5 = createStore(1L);
        Store s6 = createStore(1L);
        Store s7 = createStore(1L);

        storeRepository.saveAll(List.of(s1,s2,s3,s4,s5,s6,s7));

        em.flush();
        em.clear();

        int page = 1;
        int size = 5;
        Pageable pageable = PageRequest.of(page,size);

        // when
        Page<Store> stores = storeReader.readStoresWithPaging(pageable);

        // then
        assertThat(stores.getTotalPages()).isEqualTo(2);
        assertThat(stores.getContent()).hasSize(2);
        assertThat(stores.getTotalElements()).isEqualTo(7);

    }


    private StoreAddress createStoreAddress(Store store) {
        Sido sido = new Sido("011", "서울");
        sidoRepository.save(sido);
        Gugun gugun = new Gugun("110011",sido,"강남구");
        gugunRepository.save(gugun);

        return StoreAddress.builder()
                .store(store)
                .sido(sido)
                .gugun(gugun)
                .address("서울 강남구 남부순환로")
                .detailAddress("202호")
                .zipCode("001112")
                .lat(33.33322F)
                .lon(127.13123F)
                .build();
    }

    private Store createStore(Long userId) {
        return Store.builder()
                .storeManagerId(userId)
                .storeCode("가게코드")
                .storeName("가게")
                .detailInfo("가게 상세정보")
                .storeThumbnailImage("가게 썸네일")
                .phoneNumber("가게 전화번호")
                .accountNumber("가게 계좌정보")
                .bank("가게 계좌 은행정보")
                .build();
    }

    private DeliveryPolicy createDeliveryPolicy(Store store) {
        return DeliveryPolicy.builder()
                .store(store)
                .minOrderPrice(10_000L)
                .deliveryPrice(5_000L)
                .freeDeliveryMinPrice(10_000L)
                .build();
    }


}