package kr.bb.store.domain.store.handler;

import kr.bb.store.domain.store.controller.request.StoreCreateRequest;
import kr.bb.store.domain.store.controller.request.StoreInfoEditRequest;
import kr.bb.store.domain.store.entity.DeliveryPolicy;
import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.entity.StoreAddress;
import kr.bb.store.domain.store.entity.address.Gugun;
import kr.bb.store.domain.store.entity.address.GugunRepository;
import kr.bb.store.domain.store.entity.address.Sido;
import kr.bb.store.domain.store.entity.address.SidoRepository;
import kr.bb.store.domain.store.repository.DeliveryPolicyRepository;
import kr.bb.store.domain.store.repository.StoreAddressRepository;
import kr.bb.store.domain.store.repository.StoreRepository;
import kr.bb.store.domain.store.service.StoreService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class StoreManagerTest {
    @Autowired
    private StoreManager storeManager;
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

    @DisplayName("요청받은 내용으로 가게 정보를 수정한다 - 가게명, 위도, 최소주문금액 수정")
    @Test
    public void editStore() {
        Sido sido = new Sido("011", "서울");
        sidoRepository.save(sido);
        Gugun gugun = new Gugun("110011",sido,"강남구");
        gugunRepository.save(gugun);

        Long userId = 1L;

        Store store = createStore(userId);
        storeRepository.save(store);

        StoreAddress storeAddress = createStoreAddress(store);
        storeAddressRepository.save(storeAddress);

        DeliveryPolicy deliveryPolicy = createDeliveryPolicy(store);
        deliveryPolicyRepository.save(deliveryPolicy);

        StoreInfoEditRequest storeEditRequest = StoreInfoEditRequest.builder()
                .storeName("가게2") // 수정됨
                .detailInfo("가게 상세정보")
                .storeThumbnailImage("가게 썸네일")
                .phoneNumber("가게 전화번호")
                .accountNumber("가게 계좌정보")
                .bank("가게 계좌 은행정보")
                .deliveryPrice(9_999L) // 수정됨
                .freeDeliveryMinPrice(10_000L)
                .sido("서울")
                .gugun("강남구")
                .address("서울 강남구 남부순환로")
                .detailAddress("202호")
                .zipCode("001112")
                .lat(-11.1111D) // 수정됨
                .lon(127.13123D)
                .build();
        em.flush();
        em.clear();

        Store savedStore = storeRepository.findById(store.getId()).get();
        StoreAddress savedStoreAddress = storeAddressRepository.findByStoreId(store.getId()).get();
        DeliveryPolicy savedDeliveryPolicy = deliveryPolicyRepository.findByStoreId(store.getId()).get();

        storeManager.edit(savedStore, savedStoreAddress, savedDeliveryPolicy, sido, gugun, storeEditRequest);
        em.flush();
        em.clear();

        Store changedStore = storeRepository.findById(store.getId()).get();
        StoreAddress changedStoreAddress = storeAddressRepository.findByStoreId(store.getId()).get();
        DeliveryPolicy changedDeliveryPolicy = deliveryPolicyRepository.findByStoreId(store.getId()).get();

        assertThat(changedStore.getStoreName()).isEqualTo("가게2");
        assertThat(changedStoreAddress.getLat()).isEqualTo(-11.1111D);
        assertThat(changedDeliveryPolicy.getDeliveryPrice()).isEqualTo(9_999L);

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
                .lat(33.33322D)
                .lon(127.13123D)
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
                .deliveryPrice(5_000L)
                .freeDeliveryMinPrice(10_000L)
                .build();
    }

}