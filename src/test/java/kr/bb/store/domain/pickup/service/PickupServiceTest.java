package kr.bb.store.domain.pickup.service;

import kr.bb.store.domain.pickup.controller.response.PickAndSubResponse;
import kr.bb.store.domain.pickup.entity.PickupReservation;
import kr.bb.store.domain.pickup.repository.PickupReservationRepository;
import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.repository.StoreRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@Transactional
class PickupServiceTest {
    @Autowired
    private PickupService pickupService;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private PickupReservationRepository pickupReservationRepository;

    @DisplayName("특정 가게의 달력에 표시될 픽업예약 및 정기구독 정보를 가져온다")
    @Test
    void getDataForCalendar() {
        // given
        Store targetStore = createStore(1L);
        Store s2 = createStore(1L);
        storeRepository.saveAll(List.of(targetStore, s2));

        PickupReservation p1 = createPickup(targetStore, LocalDate.of(2023, 11, 25));
        PickupReservation p2 = createPickup(targetStore, LocalDate.of(2023,11,25));
        PickupReservation p3 = createPickup(targetStore, LocalDate.of(2023,11,26));
        PickupReservation p4 = createPickup(targetStore, LocalDate.of(2023,11,27));
        PickupReservation p5 = createPickup(targetStore, LocalDate.of(2023,12,1));
        PickupReservation p6 = createPickup(s2, LocalDate.of(2023,11,25));
        PickupReservation p7 = createPickup(s2, LocalDate.of(2023,11,25));
        pickupReservationRepository.saveAll(List.of(p1,p2,p3,p4,p5,p6,p7));


        List<String> subscriptionDates = List.of("2023-11-27","2023-11-28");

        // when
        PickAndSubResponse result = pickupService.getDataForCalendar(targetStore.getId(), subscriptionDates);

        // then
        System.out.println(result.getData());
        Assertions.assertThat(result.getData()).hasSize(6);

    }

    private PickupReservation createPickup(Store store, LocalDate pickupDate) {
        return PickupReservation.builder()
                .store(store)
                .userId(1L)
                .orderPickupId(1L)
                .productId(1L)
                .reservationCode(UUID.randomUUID().toString().substring(0,8))
                .pickupDate(pickupDate)
                .pickupTime("13:00")
                .build();
    }

    private Store createStore(Long storeManagerId) {
        return Store.builder()
                .storeManagerId(storeManagerId)
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