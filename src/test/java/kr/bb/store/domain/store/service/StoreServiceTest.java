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
import kr.bb.store.domain.store.exception.address.InvalidParentException;
import kr.bb.store.domain.store.exception.address.SidoNotFoundException;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

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
                .lat(33.33322D)
                .lon(127.13123D)
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
        Store s1 = createStoreEntity(1L,"가게1");
        Store s2 = createStoreEntity(1L,"가게1");
        Store s3 = createStoreEntity(1L,"가게1");
        Store s4 = createStoreEntity(1L,"가게1");
        Store s5 = createStoreEntity(1L,"가게1");
        Store s6 = createStoreEntity(1L,"가게1");
        Store s7 = createStoreEntity(1L,"가게1");

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

        Store store = createStoreEntity(userId,"가게1");
        storeRepository.save(store);

        StoreAddress storeAddress = createStoreAddressEntity(store,0D,0D);
        storeAddressRepository.save(storeAddress);

        DeliveryPolicy deliveryPolicy = createDeliveryPolicyEntity(store);
        deliveryPolicyRepository.save(deliveryPolicy);

        em.flush();
        em.clear();

        // when
        StoreInfoUserResponse response = storeService.getStoreInfoForUser(store.getId());

        // then
        assertThat(response.getStoreName()).isEqualTo("가게1");
        assertThat(response.getAverageRating()).isEqualTo(0.0D);
    }

    @DisplayName("가게 사장에게 보이는 가게정보를 반환한다")
    @Test
    public void getStoreInfoForManager() {
        Long userId = 1L;

        Store store = createStoreEntity(userId,"가게1");
        storeRepository.save(store);

        StoreAddress storeAddress = createStoreAddressEntity(store,0D,0D);
        storeAddressRepository.save(storeAddress);

        DeliveryPolicy deliveryPolicy = createDeliveryPolicyEntity(store);
        deliveryPolicyRepository.save(deliveryPolicy);

        em.flush();
        em.clear();

        // when
        StoreInfoManagerResponse response = storeService.getStoreInfoForManager(store.getId());

        // then
        assertThat(response.getStoreName()).isEqualTo("가게1");
        assertThat(response.getAddress()).isEqualTo("서울 강남구 남부순환로");

    }

    @DisplayName("위/경도를 기반으로 반경 5KM 이내 가게를 찾아 반환한다")
    @Test
    void getNearbyStores() {
        // given
        Double centerLat = 0.0D;
        Double centerLON = 0.0D;

        Store s1 = createStoreEntity(1L,"가게1");
        Store s2 = createStoreEntity(1L,"가게2");
        Store s3 = createStoreEntity(1L,"가게3");
        Store s4 = createStoreEntity(1L,"가게4");
        Store s5 = createStoreEntity(1L,"가게5");
        storeRepository.saveAll(List.of(s1,s2,s3,s4,s5));

        StoreAddress sa1 = createStoreAddressEntity(s1,0.0D, 5D / (111.0 * Math.cos(0.0D))); // 반경 5KM 이내
        StoreAddress sa2 = createStoreAddressEntity(s2,0.0D, 5.001D / (111.0 * Math.cos(0.0D))); // 반경 5KM 이외

        StoreAddress sa3 = createStoreAddressEntity(s3,-5D/111D,0.0D); // 반경 5KM 이내
        StoreAddress sa4 = createStoreAddressEntity(s4,-5.001D/111D,0.0D); // 반경 5KM 이외

        StoreAddress sa5 = createStoreAddressEntity(s5,100D,100D); // 반경 5KM 이외
        storeAddressRepository.saveAll(List.of(sa1,sa2,sa3,sa4,sa5));

        em.flush();
        em.clear();

        // when
        StoreListForMapResponse nearbyStores = storeService.getNearbyStores(centerLat, centerLON);

        // then
        assertThat(nearbyStores.getStores()).hasSize(2);
        assertThat(nearbyStores.getStores()).extracting("storeName","lat","lon")
                .containsExactlyInAnyOrder(
                        tuple("가게1",0.0D,5D / (111.0 * Math.cos(0.0D))),
                        tuple("가게3",-5D/111D,0.0D)
                );

    }
    @DisplayName("시/도 이름과 구/군 이름을 통해 가게를 검색한다")
    @Test
    void getStoresWithRegion() {
        // given
        Sido sido1 = new Sido("1", "서울");
        Sido sido2 = new Sido("2", "부산");
        Gugun gugun1 = new Gugun("100",sido1,"강남구");
        Gugun gugun2 = new Gugun("200",sido1,"종로구");


        Store s1 = createStoreEntity(1L,"가게1");
        Store s2 = createStoreEntity(1L,"가게2");
        Store s3 = createStoreEntity(1L,"가게3");
        Store s4 = createStoreEntity(1L,"가게4");
        Store s5 = createStoreEntity(1L,"가게5");
        storeRepository.saveAll(List.of(s1,s2,s3,s4,s5));

        StoreAddress sa1 = createStoresAddressWithSidoGugun(s1, sido1, gugun1);
        StoreAddress sa2 = createStoresAddressWithSidoGugun(s2, sido1, gugun1);
        StoreAddress sa3 = createStoresAddressWithSidoGugun(s3, sido1, gugun2);
        StoreAddress sa4 = createStoresAddressWithSidoGugun(s4, sido1, gugun2);
        StoreAddress sa5 = createStoresAddressWithSidoGugun(s5, sido1, gugun2);
        storeAddressRepository.saveAll(List.of(sa1,sa2,sa3,sa4,sa5));

        em.flush();
        em.clear();

        StoreListForMapResponse storesWithRegion = storeService.getStoresWithRegion(sido1.getName(), gugun1.getName());
        assertThat(storesWithRegion.getStores()).hasSize(2)
                .extracting("storeName")
                .containsExactlyInAnyOrder(
                        "가게1","가게2"
                );
    }


    @DisplayName("지역으로 검색할 때 시/도 값은 필수로 입력해야 한다")
    @Test
    void sidoMustNotBeNullWhenGetStoresWithRegion() {
        // when // then
        assertThatThrownBy(() -> storeService.getStoresWithRegion(null,"강남구"))
                .isInstanceOf(SidoNotFoundException.class)
                .hasMessage("해당 시/도가 존재하지 않습니다.");

    }
    @DisplayName("시에 맞지 않는 군을 입력하면 에러가 발생한다")
    @Test
    void gugunHasRightSidoWhenGetStoresWithRegion() {
        // given
        Sido sido1 = new Sido("1", "서울");
        Sido sido2 = new Sido("2", "부산");
        Gugun gugun1 = new Gugun("300",sido2,"해운대구");
        sidoRepository.saveAll(List.of(sido1, sido2));
        gugunRepository.save(gugun1);

        // when // then
        assertThatThrownBy(() -> storeService.getStoresWithRegion(sido1.getName(),gugun1.getName()))
                .isInstanceOf(InvalidParentException.class)
                .hasMessage("선택한 시/도와 구/군이 맞지 않습니다.");

    }
    @DisplayName("군을 입력하지 않으면 시에 해당하는 모든 가게정보가 반환된다")
    @Test
    void getStoresWithRegionReadAllSidoWhenGugunIsBlank() {
        // given
        Sido sido1 = new Sido("1", "서울");
        Sido sido2 = new Sido("2", "부산");
        Gugun gugun1 = new Gugun("100",sido1,"강남구");
        Gugun gugun2 = new Gugun("200",sido1,"종로구");
        Gugun gugun3 = new Gugun("300",sido2,"해운대구");

        Store s1 = createStoreEntity(1L,"가게1");
        Store s2 = createStoreEntity(1L,"가게2");
        Store s3 = createStoreEntity(1L,"가게3");
        Store s4 = createStoreEntity(1L,"가게4");
        Store s5 = createStoreEntity(1L,"가게5");
        storeRepository.saveAll(List.of(s1,s2,s3,s4,s5));

        StoreAddress sa1 = createStoresAddressWithSidoGugun(s1, sido1, gugun1);
        StoreAddress sa2 = createStoresAddressWithSidoGugun(s2, sido1, gugun1);
        StoreAddress sa3 = createStoresAddressWithSidoGugun(s3, sido1, gugun1);
        StoreAddress sa4 = createStoresAddressWithSidoGugun(s4, sido1, gugun2);
        StoreAddress sa5 = createStoresAddressWithSidoGugun(s5, sido2, gugun3);
        storeAddressRepository.saveAll(List.of(sa1,sa2,sa3,sa4,sa5));

        em.flush();
        em.clear();

        StoreListForMapResponse storesWithRegion = storeService.getStoresWithRegion(sido1.getName(), "");
        assertThat(storesWithRegion.getStores()).hasSize(4)
                .extracting("storeName")
                .containsExactlyInAnyOrder(
                        "가게1","가게2","가게3","가게4"
                );

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
                .lat(33.333220D)
                .lon(127.13123D)
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

    private DeliveryPolicy createDeliveryPolicyEntity(Store store) {
        return DeliveryPolicy.builder()
                .store(store)
                .minOrderPrice(10_000L)
                .deliveryPrice(5_000L)
                .freeDeliveryMinPrice(10_000L)
                .build();
    }

}