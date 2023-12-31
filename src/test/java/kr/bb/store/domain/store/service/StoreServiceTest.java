package kr.bb.store.domain.store.service;

import bloomingblooms.domain.flower.FlowerDto;
import bloomingblooms.domain.order.ValidatePriceDto;
import kr.bb.store.client.dto.StoreNameAndAddressDto;
import kr.bb.store.domain.BasicIntegrationTest;
import kr.bb.store.domain.store.controller.request.SortType;
import kr.bb.store.domain.store.controller.request.StoreCreateRequest;
import kr.bb.store.domain.store.controller.request.StoreInfoEditRequest;
import kr.bb.store.domain.store.controller.response.*;
import kr.bb.store.domain.store.entity.DeliveryPolicy;
import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.entity.StoreAddress;
import kr.bb.store.domain.store.entity.address.Gugun;
import kr.bb.store.domain.store.entity.address.GugunRepository;
import kr.bb.store.domain.store.entity.address.Sido;
import kr.bb.store.domain.store.entity.address.SidoRepository;
import kr.bb.store.domain.store.exception.DeliveryInconsistencyException;
import kr.bb.store.domain.store.exception.address.GugunNotFoundException;
import kr.bb.store.domain.store.exception.address.InvalidParentException;
import kr.bb.store.domain.store.exception.address.SidoNotFoundException;
import kr.bb.store.domain.store.repository.DeliveryPolicyRepository;
import kr.bb.store.domain.store.repository.StoreAddressRepository;
import kr.bb.store.domain.store.repository.StoreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StoreServiceTest extends BasicIntegrationTest {
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
        List<FlowerDto> flowers = Collections.emptyList();

        // when
        storeService.createStore(userId, storeCreateRequest, flowers);
        em.flush();
        em.clear();

        Store store = storeRepository.findByStoreManagerId(userId).get();
        // then
        assertThat(store.getId()).isNotNull();
        assertThat(store.getStoreManagerId()).isEqualTo(userId);
    }


    @DisplayName("존재하지 않는 시/도 정보로 가게주소를 생성할 수 없다")
    @Test
    void cannotCreateStoreAddressWithoutSido() {
        // given
        StoreCreateRequest storeCreateRequest = createStoreCreateRequest();
        List<FlowerDto> flowers = Collections.emptyList();

        // when // then
        assertThatThrownBy(() -> storeService.createStore(1L, storeCreateRequest, flowers))
                .isInstanceOf(SidoNotFoundException.class)
                .hasMessage("해당 시/도가 존재하지 않습니다.");
    }

    @DisplayName("존재하지 않는 구/군 정보로 가게주소를 생성할 수 없다")
    @Test
    void cannotCreateStoreAddressWithoutGugun() {
        // given
        Sido sido = new Sido("011", "서울");
        Gugun gugun = new Gugun("110011",sido,"강남구");
        sidoRepository.save(sido);
        StoreCreateRequest storeCreateRequest = createStoreCreateRequest();
        Store store = createStoreEntity(1L,"가게1");
        List<FlowerDto> flowers = Collections.emptyList();

        // when // then
        assertThatThrownBy(() -> storeService.createStore(1L, storeCreateRequest, flowers))
                .isInstanceOf(GugunNotFoundException.class)
                .hasMessage("해당 구/군이 존재하지 않습니다.");
    }


    @DisplayName("요청받은 내용으로 가게 정보를 수정한다 - 가게명 수정 예시")
    @Test
    public void editStore() {
        Sido sido = new Sido("011", "서울");
        sidoRepository.save(sido);
        Gugun gugun = new Gugun("110011",sido,"강남구");
        gugunRepository.save(gugun);
        List<FlowerDto> flowers = Collections.emptyList();


        Long userId = 1L;
        StoreCreateRequest request = createStoreCreateRequest();
        Long storeId = storeService.createStore(userId, request, flowers);
        StoreInfoEditRequest storeEditRequest = StoreInfoEditRequest.builder()
                .storeName("가게2") // 수정됨
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
        List<FlowerDto> flowers = Collections.emptyList();

        Long userId = 1L;
        StoreCreateRequest request = createStoreCreateRequest();
        Long storeId = storeService.createStore(userId, request, flowers);
        em.flush();
        em.clear();

        // when
        StoreDetailInfoResponse response = storeService.getStoreDetailInfo(storeId);

        // then
        assertThat(response.getStoreName()).isEqualTo("가게1");
        assertThat(response.getDeliveryPrice()).isEqualTo(5000L);
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
        Page<StoreListResponse> response = storeService.getStoresWithPaging(pageable);

        // then
        assertThat(response.getTotalElements()).isEqualTo(7);
        assertThat(response.getContent().get(0)).isInstanceOf(StoreListResponse.class);
    }

    @DisplayName("유저에게 보이는 가게정보를 반환한다")
    @Test
    public void getStoreInfoForUser() {
        Long userId = 1L;

        Store store = createStoreEntity(1L,"가게1");
        storeRepository.save(store);

        StoreAddress storeAddress = createStoreAddressEntity(store,0D,0D);
        storeAddressRepository.save(storeAddress);

        DeliveryPolicy deliveryPolicy = createDeliveryPolicyEntity(store);
        deliveryPolicyRepository.save(deliveryPolicy);

        String subscriptionProductId = "1";

        em.flush();
        em.clear();

        // when
        StoreInfoUserResponse response = storeService.getStoreInfoForUser(store.getId(), false, false, subscriptionProductId);

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

        StoreListForMapResponse storesWithRegion = storeService.getStoresWithRegion(sido1.getCode(), gugun1.getCode());
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
                .isInstanceOf(InvalidDataAccessApiUsageException.class);

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
        assertThatThrownBy(() -> storeService.getStoresWithRegion(sido1.getCode(),gugun1.getCode()))
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

        StoreListForMapResponse storesWithRegion = storeService.getStoresWithRegion(sido1.getCode(), "");
        assertThat(storesWithRegion.getStores()).hasSize(4)
                .extracting("storeName")
                .containsExactlyInAnyOrder(
                        "가게1","가게2","가게3","가게4"
                );

    }

    @DisplayName("가게사장 아이디를 통해 가게 아이디를 가져온다")
    @Test
    void getStoreId() {
        // given
        Long userId = 1L;
        Store store = createStoreWithManagerId(userId);
        storeRepository.save(store);

        // when
        Long result = storeService.getStoreId(userId);

        // then
        assertThat(result).isEqualTo(store.getId());

    }

    @DisplayName("가게 이름과 주소를 반환한다")
    @Test
    void getStoreNameAndAddress() {
        // given
        String storeName = "우리가게";
        Store store = createStoreWithStoreName(storeName);
        storeRepository.save(store);

        StoreAddress storeAddress = createStoreAddressEntity(store,"도로명 주소", "상세주소");
        storeAddressRepository.save(storeAddress);

        // when
        StoreNameAndAddressDto result = storeService.getStoreNameAndAddress(store.getId());

        // then
        assertThat(result.getStoreName()).isEqualTo(storeName);
        assertThat(result.getStoreAddress()).isEqualTo(storeAddress.getAddress() + " " + storeAddress.getDetailAddress());

    }

    @DisplayName("최소 주문 금액을 만족하면 배달비는 무료다")
    @Test
    void validateDeliveryPrice() {
        // given
        Store store = createStoreEntity();
        storeRepository.save(store);

        Long deliveryPrice =  10_000L;
        Long freeDeliveryMinPrice =  100_000L;
        DeliveryPolicy deliveryPolicy = createDeliveryPolicyEntity(store, deliveryPrice, freeDeliveryMinPrice);
        deliveryPolicyRepository.save(deliveryPolicy);

        ValidatePriceDto validatePriceDto = ValidatePriceDto.builder()
                .storeId(store.getId())
                .actualAmount(100_001L)
                .deliveryCost(10_000L)
                .build();

        // when // then
        assertThatThrownBy(() -> storeService.validateDeliveryPrice(List.of(validatePriceDto)))
                .isInstanceOf(DeliveryInconsistencyException.class)
                .hasMessage("해당 요청은 실제 배송 정보와 일치하지 않습니다.");

    }

    @DisplayName("최소 주문 금액을 만족하지 못했으면 배달비를 지불해야 한다")
    @Test
    void validateDeliveryPrice2() {
        // given
        Store store = createStoreEntity();
        storeRepository.save(store);

        Long deliveryPrice =  10_000L;
        Long freeDeliveryMinPrice =  100_000L;
        DeliveryPolicy deliveryPolicy = createDeliveryPolicyEntity(store, deliveryPrice, freeDeliveryMinPrice);
        deliveryPolicyRepository.save(deliveryPolicy);

        ValidatePriceDto validatePriceDto = ValidatePriceDto.builder()
                .storeId(store.getId())
                .actualAmount(99_999L)
                .deliveryCost(0L)
                .build();

        // when // then
        assertThatThrownBy(() -> storeService.validateDeliveryPrice(List.of(validatePriceDto)))
                .isInstanceOf(DeliveryInconsistencyException.class)
                .hasMessage("해당 요청은 실제 배송 정보와 일치하지 않습니다.");

    }

    @DisplayName("가게의 평균평점을 업데이트한다")
    @Test
    void updateAverageRating() {
        // given
        Store s1 = createStoreEntity();
        Store s2 = createStoreEntity();
        Store s3 = createStoreEntity();

        storeRepository.saveAll(List.of(s1, s2, s3));
        em.flush();
        em.clear();

        Map<Long, Double> averageRatings = Map.of(s1.getId(), 5D, s2.getId(), 4.2D, s3.getId(), 4.8D);

        // when
        storeService.updateAverageRating(averageRatings);
        em.flush();
        em.clear();

        List<Store> result = storeRepository.findAll();

        // then
        assertThat(result).extracting("averageRating")
                .containsExactlyInAnyOrder(5D,4.2D,4.8D);

    }
    @DisplayName("가게의 월 매출액을 업데이트한다")
    @Test
    void updateMonthlySalesRevenue() {
        // given
        Store s1 = createStoreEntity();
        Store s2 = createStoreEntity();
        Store s3 = createStoreEntity();

        storeRepository.saveAll(List.of(s1, s2, s3));
        em.flush();
        em.clear();

        Map<Long, Long> monthlySalesRevenues = Map.of(s1.getId(), 100_000L, s2.getId(), 200_000L, s3.getId(), 300_000L);

        // when
        storeService.updateMonthlySalesRevenue(monthlySalesRevenues);
        em.flush();
        em.clear();

        List<Store> result = storeRepository.findAll();

        // then
        assertThat(result).extracting("monthlySalesRevenue")
                .containsExactlyInAnyOrder(100_000L,200_000L,300_000L);

    }

    @DisplayName("관리자의 가게 조회 시 gugun은 선택하지 않을 수 있다")
    @Test
    void getStoresForAdmin1() {
        // given
        Sido sido1 = new Sido("1", "서울");
        Sido sido2 = new Sido("2", "부산");
        Gugun gugun1 = new Gugun("100",sido1,"강남구");
        Gugun gugun2 = new Gugun("200",sido1,"종로구");
        Gugun gugun3 = new Gugun("300",sido2,"해운대구");

        Store s1 = createStoreWithManagerId(1L);
        Store s2 = createStoreWithManagerId(2L);
        Store s3 = createStoreWithManagerId(3L);
        Store s4 = createStoreWithManagerId(4L);
        Store s5 = createStoreWithManagerId(5L);
        storeRepository.saveAll(List.of(s1,s2,s3,s4,s5));

        StoreAddress sa1 = createStoresAddressWithSidoGugun(s1,sido1,gugun1);
        StoreAddress sa2 = createStoresAddressWithSidoGugun(s2,sido1,gugun2);
        StoreAddress sa3 = createStoresAddressWithSidoGugun(s3,sido2,gugun3);
        StoreAddress sa4 = createStoresAddressWithSidoGugun(s4,sido1,gugun1);
        StoreAddress sa5 = createStoresAddressWithSidoGugun(s5,sido1,gugun1);
        storeAddressRepository.saveAll(List.of(sa1,sa2,sa3,sa4,sa5));

        Pageable page = PageRequest.of(0,5);
        SortType sort = SortType.DATE;
        String sidoCode = sido1.getCode();
        String gugunCode = "";

        // when
        List<Store> result = storeService.getStoresForAdmin(page, sort, sidoCode, gugunCode).getContent();

        // then
        assertThat(result).hasSize(4)
                .extracting("storeManagerId")
                .containsExactlyInAnyOrder(5L,4L,2L,1L);

    }

    @DisplayName("관리자의 가게 조회 시 정렬조건을 선택하지 않으면 날짜순으로 정렬된다")
    @Test
    void getStoresForAdmin2() {
        // given
        Sido sido1 = new Sido("1", "서울");
        Sido sido2 = new Sido("2", "부산");
        Gugun gugun1 = new Gugun("100",sido1,"강남구");
        Gugun gugun2 = new Gugun("200",sido1,"종로구");
        Gugun gugun3 = new Gugun("300",sido2,"해운대구");

        Store s1 = createStoreWithManagerId(1L);
        Store s2 = createStoreWithManagerId(2L);
        Store s3 = createStoreWithManagerId(3L);
        Store s4 = createStoreWithManagerId(4L);
        Store s5 = createStoreWithManagerId(5L);
        storeRepository.saveAll(List.of(s1,s2,s3,s4,s5));

        StoreAddress sa1 = createStoresAddressWithSidoGugun(s1,sido1,gugun1);
        StoreAddress sa2 = createStoresAddressWithSidoGugun(s2,sido1,gugun2);
        StoreAddress sa3 = createStoresAddressWithSidoGugun(s3,sido2,gugun3);
        StoreAddress sa4 = createStoresAddressWithSidoGugun(s4,sido1,gugun1);
        StoreAddress sa5 = createStoresAddressWithSidoGugun(s5,sido1,gugun1);
        storeAddressRepository.saveAll(List.of(sa1,sa2,sa3,sa4,sa5));

        Pageable page = PageRequest.of(0,5);
        SortType sort = null;
        String sidoCode = sido1.getCode();
        String gugunCode = gugun1.getCode();

        // when
        List<Store> result = storeService.getStoresForAdmin(page, sort, sidoCode, gugunCode).getContent();

        // then
        assertThat(result).hasSize(3);
        IntStream.range(1, result.size()).forEach(i -> {
            Store prev = result.get(i-1);
            Store next = result.get(i);
            assertThat(prev.getCreatedAt().isAfter(next.getCreatedAt()) || prev.getCreatedAt().isEqual(next.getCreatedAt())).isTrue();
        });
    }

    private Store createStoreWithManagerId(Long userId) {
        return Store.builder()
                .storeManagerId(userId)
                .storeCode("가게코드")
                .storeName("가게이름")
                .detailInfo("가게 상세정보")
                .storeThumbnailImage("가게 썸네일")
                .phoneNumber("가게 전화번호")
                .accountNumber("가게 계좌정보")
                .bank("가게 계좌 은행정보")
                .build();
    }
    private Store createStoreWithStoreName(String storeName) {
        return Store.builder()
                .storeManagerId(1L)
                .storeCode("가게코드")
                .storeName(storeName)
                .detailInfo("가게 상세정보")
                .storeThumbnailImage("가게 썸네일")
                .phoneNumber("가게 전화번호")
                .accountNumber("가게 계좌정보")
                .bank("가게 계좌 은행정보")
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
    private StoreAddress createStoreAddressEntity(Store store, String address, String detailAddress) {
        Sido sido = new Sido("011", "서울");
        sidoRepository.save(sido);
        Gugun gugun = new Gugun("110011",sido,"강남구");
        gugunRepository.save(gugun);

        return StoreAddress.builder()
                .store(store)
                .sido(sido)
                .gugun(gugun)
                .address(address)
                .detailAddress(detailAddress)
                .zipCode("001112")
                .lat(0d)
                .lon(0d)
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
                .deliveryPrice(5_000L)
                .freeDeliveryMinPrice(10_000L)
                .build();
    }
    private DeliveryPolicy createDeliveryPolicyEntity(Store store, Long deliveryPrice, Long freeDeliveryMinPrice) {
        return DeliveryPolicy.builder()
                .store(store)
                .deliveryPrice(deliveryPrice)
                .freeDeliveryMinPrice(freeDeliveryMinPrice)
                .build();
    }

    private Store createStoreEntity() {
        return Store.builder()
                .storeManagerId(1L)
                .storeCode("가게코드")
                .storeName("가게")
                .detailInfo("가게 상세정보")
                .storeThumbnailImage("가게 썸네일")
                .phoneNumber("가게 전화번호")
                .accountNumber("가게 계좌정보")
                .bank("가게 계좌 은행정보")
                .build();
    }


}