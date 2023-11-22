package kr.bb.store.domain.store.service;

import kr.bb.store.domain.store.controller.request.StoreCreateRequest;
import kr.bb.store.domain.store.controller.request.StoreInfoEditRequest;
import kr.bb.store.domain.store.entity.DeliveryPolicy;
import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.entity.StoreAddress;
import kr.bb.store.domain.store.entity.address.Gugun;
import kr.bb.store.domain.store.entity.address.GugunRepository;
import kr.bb.store.domain.store.entity.address.Sido;
import kr.bb.store.domain.store.entity.address.SidoRepository;
import kr.bb.store.domain.store.handler.response.*;
import kr.bb.store.domain.store.repository.DeliveryPolicyRepository;
import kr.bb.store.domain.store.repository.StoreAddressRepository;
import kr.bb.store.domain.store.repository.StoreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class StoreServiceTest {
    @Autowired
    private StoreService storeService;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private SidoRepository sidoRepository;
    @Autowired
    private GugunRepository gugunRepository;
    @Autowired
    private StoreAddressRepository storeAddressRepository;
    @Autowired
    private DeliveryPolicyRepository deliveryPolicyRepository;
    @Autowired
    private EntityManager em;

    @DisplayName("회원 번호를 전달받아 가게를 생성한다")
    @Test
    void createStore() {
        // given
        Long userId = 1L;
        StoreCreateRequest storeCreateRequest = createStoreCreateRequest();
        Sido sido = new Sido("011", "서울");
        sidoRepository.save(sido);
        Gugun gugun = new Gugun("110011",sido,"강남구");
        gugunRepository.save(gugun);

        // when
        storeService.createStore(userId, storeCreateRequest);
        em.flush();
        em.clear();

        Store store = storeRepository.findByStoreManagerId(userId).get();
        // then
        assertThat(store.getId()).isNotNull();
        assertThat(store.getStoreManagerId()).isEqualTo(userId);
    }


    @DisplayName("요청받은 내용으로 가게 정보를 수정한다 - 가게명 수정 예시")
    @Test
    public void editStore() {
        Sido sido = new Sido("011", "서울");
        sidoRepository.save(sido);
        Gugun gugun = new Gugun("110011",sido,"강남구");
        gugunRepository.save(gugun);

        Long userId = 1L;
        StoreCreateRequest request = createStoreCreateRequest();
        Long storeId = storeService.createStore(userId, request);
        StoreInfoEditRequest storeEditRequest = StoreInfoEditRequest.builder()
                .storeName("가게2") // 수정됨
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
        em.flush();
        em.clear();

        storeService.editStoreInfo(storeId, storeEditRequest);
        em.flush();
        em.clear();

        Store changedStore = storeRepository.findById(storeId).get();

        assertThat(changedStore.getStoreName()).isEqualTo("가게2");
    }

    @DisplayName("가게아이디를 통해 가게 상세정보를 받아온다")
    @Test
    void getStoreInfo() {
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
        DetailInfoResponse response = storeService.getStoreInfo(storeId);

        // then
        assertThat(response.getStoreName()).isEqualTo("가게1");
        assertThat(response.getMinOrderPrice()).isEqualTo(10_000L);
        assertThat(response.getSido()).isEqualTo("서울");
    }

    @DisplayName("사용자가 요청한 가게 페이징 데이터를 필요한 값만 담아서 반환한다")
    @Test
    public void getStoresWithPaing() {
        // given
        Sido sido = new Sido("011", "서울");
        sidoRepository.save(sido);
        Gugun gugun = new Gugun("110011",sido,"강남구");
        gugunRepository.save(gugun);

        Store s1 = createStoreEntity(1L);
        Store s2 = createStoreEntity(1L);
        Store s3 = createStoreEntity(1L);
        Store s4 = createStoreEntity(1L);
        Store s5 = createStoreEntity(1L);
        Store s6 = createStoreEntity(1L);
        Store s7 = createStoreEntity(1L);

        storeRepository.saveAll(List.of(s1,s2,s3,s4,s5,s6,s7));

        em.flush();
        em.clear();

        int page = 1;
        int size = 5;
        Pageable pageable = PageRequest.of(page,size);

        // when
        SimpleStorePagingResponse response = storeService.getStoresWithPaging(pageable);

        // then
        assertThat(response.getTotalCnt()).isEqualTo(7);
        assertThat(response.getSimpleStores().get(0)).isInstanceOf(SimpleStoreResponse.class);
    }

    @DisplayName("유저에게 보이는 가게정보를 반환한다")
    @Test
    public void getStoreInfoForUser() {
        Long userId = 1L;

        Store store = createStoreEntity(userId);
        storeRepository.save(store);

        StoreAddress storeAddress = createStoreAddressEntity(store);
        storeAddressRepository.save(storeAddress);

        DeliveryPolicy deliveryPolicy = createDeliveryPolicyEntity(store);
        deliveryPolicyRepository.save(deliveryPolicy);

        em.flush();
        em.clear();

        // when
        StoreInfoUserResponse response = storeService.getStoreInfoForUser(store.getId());

        // then
        assertThat(response.getStoreName()).isEqualTo("가게");
        assertThat(response.getAverageRating()).isEqualTo(0.0F);
    }

    @DisplayName("가게 사장에게 보이는 가게정보를 반환한다")
    @Test
    public void getStoreInfoForManager() {
        Long userId = 1L;

        Store store = createStoreEntity(userId);
        storeRepository.save(store);

        StoreAddress storeAddress = createStoreAddressEntity(store);
        storeAddressRepository.save(storeAddress);

        DeliveryPolicy deliveryPolicy = createDeliveryPolicyEntity(store);
        deliveryPolicyRepository.save(deliveryPolicy);

        em.flush();
        em.clear();

        // when
        StoreInfoManagerResponse response = storeService.getStoreInfoForManager(store.getId());

        // then
        assertThat(response.getStoreName()).isEqualTo("가게");
        assertThat(response.getAddress()).isEqualTo("서울 강남구 남부순환로");

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

    private StoreAddress createStoreAddressEntity(Store store) {
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

    private Store createStoreEntity(Long userId) {
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

    private DeliveryPolicy createDeliveryPolicyEntity(Store store) {
        return DeliveryPolicy.builder()
                .store(store)
                .minOrderPrice(10_000L)
                .deliveryPrice(5_000L)
                .freeDeliveryMinPrice(10_000L)
                .build();
    }

}