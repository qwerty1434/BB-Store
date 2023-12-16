package kr.bb.store.domain.pickup.handler;

import kr.bb.store.client.ProductFeignClient;
import kr.bb.store.domain.pickup.controller.request.PickupCreateRequest;
import kr.bb.store.domain.pickup.entity.PickupReservation;
import kr.bb.store.domain.pickup.entity.ReservationStatus;
import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.repository.StoreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
class PickupCreatorTest {
    @Autowired
    private PickupCreator pickupCreator;
    @Autowired
    private StoreRepository storeRepository;
    @MockBean
    private ProductFeignClient productFeignClient;
    @MockBean
    private RedissonClient redissonClient;

    @DisplayName("픽업예약을 생성한다")
    @Test
    void create() {
        // given
        Store store = createStore();
        storeRepository.save(store);
        PickupCreateRequest pickupCreateRequest = createRequest(store.getId());

        // when
        PickupReservation pickupReservation = pickupCreator.create(store, pickupCreateRequest);

        // then
        assertThat(pickupReservation.getId()).isNotNull();
        assertThat(pickupReservation.getReservationStatus()).isEqualTo(ReservationStatus.READY);

    }


    private Store createStore() {
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


    private PickupCreateRequest createRequest(Long storeId) {
        return PickupCreateRequest.builder()
                .storeId(storeId)
                .userId(1L)
                .orderPickupId("1")
                .productId("1")
                .pickupDate(LocalDate.now())
                .pickupTime("13:00")
                .build();
    }
}