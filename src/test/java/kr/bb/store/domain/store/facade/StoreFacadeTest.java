package kr.bb.store.domain.store.facade;

import bloomingblooms.domain.flower.FlowerDto;
import bloomingblooms.domain.product.StoreSubscriptionProductId;
import bloomingblooms.response.CommonResponse;
import kr.bb.store.client.ProductFeignClient;
import kr.bb.store.client.StoreLikeFeignClient;
import kr.bb.store.client.StoreSubscriptionFeignClient;
import kr.bb.store.domain.BasicIntegrationTestEnv;
import kr.bb.store.domain.cargo.entity.FlowerCargo;
import kr.bb.store.domain.cargo.repository.FlowerCargoRepository;
import kr.bb.store.domain.store.controller.request.StoreCreateRequest;
import kr.bb.store.domain.store.controller.response.StoreForMapResponse;
import kr.bb.store.domain.store.controller.response.StoreInfoUserResponse;
import kr.bb.store.domain.store.controller.response.StoreListResponse;
import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.entity.StoreAddress;
import kr.bb.store.domain.store.entity.address.Gugun;
import kr.bb.store.domain.store.entity.address.GugunRepository;
import kr.bb.store.domain.store.entity.address.Sido;
import kr.bb.store.domain.store.entity.address.SidoRepository;
import kr.bb.store.domain.store.repository.StoreAddressRepository;
import kr.bb.store.domain.store.repository.StoreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

class StoreFacadeTest extends BasicIntegrationTestEnv {
    @Autowired
    private StoreFacade storeFacade;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private SidoRepository sidoRepository;
    @Autowired
    private GugunRepository gugunRepository;
    @Autowired
    private FlowerCargoRepository flowerCargoRepository;
    @Autowired
    private StoreAddressRepository storeAddressRepository;
    @Autowired
    private EntityManager em;
    @MockBean
    private ProductFeignClient productFeignClient;
    @MockBean
    private StoreLikeFeignClient storeLikeFeignClient;
    @MockBean
    private StoreSubscriptionFeignClient storeSubscriptionFeignClient;

    @DisplayName("store생성 시 재고정보도 함께 생성된다")
    @Test
    void createStore() {
        // given
        Long userId = 1L;
        StoreCreateRequest storeCreateRequest = createStoreCreateRequest();
        Sido sido = new Sido("011", "서울");
        sidoRepository.save(sido);
        Gugun gugun = new Gugun("110011",sido,"강남구");
        gugunRepository.save(gugun);
        FlowerDto flowerDto = FlowerDto.builder()
                        .flowerId(1L)
                        .flowerName("장미꽃")
                        .build();
        CommonResponse<List<FlowerDto>> data = CommonResponse.<List<FlowerDto>>builder().data(List.of(flowerDto)).build();
        BDDMockito.given(productFeignClient.getFlowers())
                .willReturn(data);

        // when
        Long storeId = storeFacade.createStore(userId, storeCreateRequest);

        em.flush();
        em.clear();

        FlowerCargo result = flowerCargoRepository.findAllByStoreId(storeId).get(0);

        // then
        assertThat(result.getFlowerName()).isEqualTo("장미꽃");
        assertThat(result.getStock()).isEqualTo(0);
    }

    @DisplayName("가게정보를 좋아요 여부와 함께 반환한다")
    @Test
    void getStoresWithLikes() {
        // given
        Store store1 = createStoreEntity(100L, "가게1");
        Store store2 = createStoreEntity(100L, "가게2");
        Store store3 = createStoreEntity(100L, "가게3");
        storeRepository.saveAll(List.of(store1,store2,store3));
        Long userId = 1L;
        PageRequest page = PageRequest.of(0, 5);
        Map<Long, Boolean> data = Map.of(store1.getId(),true,store2.getId(),true, store3.getId(), false);
        BDDMockito.given(storeLikeFeignClient.getStoreLikes(any(), any()))
                .willReturn(CommonResponse.<Map<Long,Boolean>>builder().data(data).build());
        // when
        List<StoreListResponse> result = storeFacade.getStoresWithLikes(userId, page).getStores();

        // then
        assertThat(result).hasSize(3)
                .extracting("isLiked")
                .containsExactly(true, true, false);

    }

    @DisplayName("좋아요와 구독여부를 포함한 가게정보를 반환한다")
    @Test
    void getStoreInfoForUser() {
        // given
        Store store = createStoreEntity(100L, "가게1");
        storeRepository.save(store);

        StoreAddress storeAddress = createStoreAddressEntity(store,0D,0D);
        storeAddressRepository.save(storeAddress);

        Long userId = 1L;
        StoreSubscriptionProductId subscriptionId = StoreSubscriptionProductId.builder()
                .subscriptionProductId("구독상품 아이디")
                .build();
        BDDMockito.given(productFeignClient.getSubscriptionProductId(any()))
                .willReturn(CommonResponse.<StoreSubscriptionProductId>builder().data(subscriptionId).build());
        Map<Long, Boolean> likeData = Map.of(store.getId(), true);
        BDDMockito.given(storeLikeFeignClient.getStoreLikes(any(), any()))
                .willReturn(CommonResponse.<Map<Long,Boolean>>builder().data(likeData).build());
        Map<Long, Boolean> subData = Map.of(store.getId(), false);
        BDDMockito.given(storeSubscriptionFeignClient.getStoreSubscriptions(any(), any()))
                .willReturn(CommonResponse.<Map<Long,Boolean>>builder().data(subData).build());

        // when
        StoreInfoUserResponse storeInfoForUser = storeFacade.getStoreInfoForUser(userId, store.getId());

        // then
        assertThat(storeInfoForUser.getSubscriptionProductId()).isEqualTo("구독상품 아이디");
        assertThat(storeInfoForUser.getIsLiked()).isTrue();
        assertThat(storeInfoForUser.getIsSubscribed()).isFalse();
    }

    @DisplayName("위치기반 가게를 반환할 때 좋아요 정보를 함께 반환한다")
    @Test
    void getStoresWithRegion() {
        // given
        Sido sido1 = new Sido("1", "서울");
        Gugun gugun1 = new Gugun("100",sido1,"강남구");
        Long userId = 1L;
        Store s1 = createStoreEntity(1L,"가게1");
        storeRepository.save(s1);
        StoreAddress sa1 = createStoresAddressWithSidoGugun(s1, sido1, gugun1);
        storeAddressRepository.save(sa1);

        Map<Long, Boolean> data = Map.of(s1.getId(), true);
        BDDMockito.given(storeLikeFeignClient.getStoreLikes(any(), any()))
                .willReturn(CommonResponse.<Map<Long,Boolean>>builder().data(data).build());

        // when
        List<StoreForMapResponse> stores = storeFacade.getStoresWithRegion(userId, sido1.getCode(), gugun1.getCode()).getStores();

        // then
        assertThat(stores)
                .hasSize(1)
                .extracting("isLiked")
                .containsExactly(true);
    }


    private StoreCreateRequest createStoreCreateRequest() {
        return StoreCreateRequest.builder()
                .storeName("가게1")
                .detailInfo("가게 상세정보")
                .storeThumbnailImage("가게 썸네일")
                .phoneNumber("가게 전화번호")
                .accountNumber("가게 계좌정보")
                .bank("가게 계좌 은행정보")
                .deliveryPrice(5_000L)
                .freeDeliveryMinPrice(10_000L)
                .sido("서울")
                .gugun("강남구")
                .address("서울 강남구 남부순환로")
                .detailAddress("202호")
                .zipCode("001112")
                .lat(33.333220D)
                .lon(127.13123D)
                .build();
    }

    private Store createStoreEntity(Long userId, String storeName) {
        return Store.builder()
                .storeManagerId(userId)
                .storeCode("가게코드")
                .storeName(storeName)
                .detailInfo("가게 상세정보")
                .storeThumbnailImage("가게 썸네일")
                .phoneNumber("가게 전화번호")
                .accountNumber("가게 계좌정보")
                .bank("가게 계좌 은행정보")
                .build();
    }

    private StoreAddress createStoreAddressEntity(Store store, double lat, double lon) {
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
                .lat(lat)
                .lon(lon)
                .build();
    }

    private StoreAddress createStoresAddressWithSidoGugun(Store store, Sido sido, Gugun gugun) {
        sidoRepository.save(sido);
        gugunRepository.save(gugun);
        return StoreAddress.builder()
                .store(store)
                .sido(sido)
                .gugun(gugun)
                .address("서울 강남구 남부순환로")
                .detailAddress("202호")
                .zipCode("001112")
                .lat(0.0D)
                .lon(0.0D)
                .build();
    }

}