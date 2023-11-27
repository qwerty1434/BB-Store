package kr.bb.store.domain.pickup.handler;

import kr.bb.store.domain.pickup.dto.PickupsInMypageDto;
import kr.bb.store.domain.pickup.entity.PickupReservation;
import kr.bb.store.domain.pickup.repository.PickupReservationRepository;
import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.repository.StoreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
class PickupReaderTest {
    @Autowired
    private PickupReader pickupReader;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private PickupReservationRepository pickupReservationRepository;

    @DisplayName("")
    @Test
    void readPickupsForMypage() {
        // given
        Long userId = 1L;
        Store s1 = createStore(99L);
        Store s2 = createStore(999L);
        storeRepository.saveAll(List.of(s1,s2));

        PickupReservation p1 = createPickup(s1,userId);
        PickupReservation p2 = createPickup(s2,userId);
        PickupReservation p3 = createPickup(s1,2L);
        pickupReservationRepository.saveAll(List.of(p1,p2,p3));
        Pageable pageable = PageRequest.of(0,5);

        // when
        Page<PickupsInMypageDto> result = pickupReader.readPickupsForMypage(userId, pageable);

        // then
        assertThat(result.getContent()).hasSize(2);

    }

    public PickupReservation createPickup(Store store, Long userId) {
        return PickupReservation.builder()
                .store(store)
                .userId(userId)
                .orderPickupId(1L)
                .productId(1L)
                .reservationCode(UUID.randomUUID().toString())
                .pickupDate(LocalDate.now())
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