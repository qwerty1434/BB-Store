package kr.bb.store.domain.coupon.handler;

import kr.bb.store.domain.coupon.entity.Coupon;
import kr.bb.store.domain.coupon.entity.IssuedCoupon;
import kr.bb.store.domain.coupon.entity.IssuedCouponId;
import kr.bb.store.domain.coupon.repository.CouponRepository;
import kr.bb.store.domain.coupon.repository.IssuedCouponRepository;
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

@SpringBootTest
@Transactional
class IssuedCouponReaderTest {
    @Autowired
    private IssuedCouponReader issuedCouponReader;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private IssuedCouponRepository issuedCouponRepository;

    @DisplayName("유저Id와 쿠폰Id로 해당 유저의 쿠폰 발급 여부를 조사한다")
    @Test
    void readIssuedCoupon() {
        // given
        Store store = createStore(1L);
        storeRepository.save(store);
        Coupon c1 = createCoupon(store);
        Coupon c2 = createCoupon(store);
        couponRepository.saveAll(List.of(c1,c2));
        Long userId = 1L;
        IssuedCoupon issuedCoupon = createIssuedCoupon(c1,userId);
        issuedCouponRepository.save(issuedCoupon);

        // when
        IssuedCoupon result = issuedCouponReader.read(c1.getId(), userId);

        // then
        Assertions.assertThat(result.getId()).isNotNull();

    }


    private Coupon createCoupon(Store store) {
        return Coupon.builder()
                .couponCode("쿠폰코드")
                .store(store)
                .limitCount(100)
                .couponName("쿠폰이름")
                .discountPrice(10000L)
                .minPrice(100000L)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .build();
    }

    private Store createStore(Long storeOwnerId) {
        return Store.builder()
                .storeManagerId(storeOwnerId)
                .storeCode("가게코드")
                .storeName("가게")
                .detailInfo("가게 상세정보")
                .storeThumbnailImage("가게 썸네일")
                .phoneNumber("가게 전화번호")
                .accountNumber("가게 계좌정보")
                .bank("가게 계좌 은행정보")
                .build();
    }

    private IssuedCoupon createIssuedCoupon(Coupon coupon, Long userId) {
        return IssuedCoupon.builder()
                .id(createIssuedCouponId(coupon.getId(),userId))
                .coupon(coupon)
                .build();
    }

    private IssuedCouponId createIssuedCouponId(Long couponId, Long userId) {
        return IssuedCouponId.builder()
                .couponId(couponId)
                .userId(userId)
                .build();
    }

}