package kr.bb.store.domain.coupon.handler;


import kr.bb.store.domain.coupon.entity.Coupon;
import kr.bb.store.domain.coupon.exception.InvalidCouponDurationException;
import kr.bb.store.domain.coupon.exception.InvalidCouponStartDateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class CouponCreatorTest {
    @Autowired
    private CouponCreator couponCreator;

    @DisplayName("쿠폰 정보를 전달받아 쿠폰을 생성한다")
    @Test
    void createCoupon() {
        // given
        Long storeId = 1L;
        Integer limitCount = 100;
        String couponName = "쿠폰명";
        Long discountPrice = 10000L;
        Long minPrice = 100000L;
        LocalDate startDate = LocalDate.of(2023,12,13);
        LocalDate endDate = LocalDate.of(2023,12,15);

        // when
        Coupon coupon = couponCreator.create(storeId, limitCount, couponName, discountPrice, minPrice, startDate, endDate);

        // then
        assertThat(coupon.getId()).isNotNull();

    }

    @DisplayName("쿠폰 종료일은 시작일보다 빠를 수 없다")
    @Test
    void endDateMustComesAfterStartDate() {
        // given
        Long storeId = 1L;
        Integer limitCount = 100;
        String couponName = "쿠폰명";
        Long discountPrice = 10000L;
        Long minPrice = 100000L;
        LocalDate startDate = LocalDate.of(2023,12,15);
        LocalDate endDate = LocalDate.of(2023,12,13);

        // when // then
        assertThatThrownBy(() ->
                couponCreator.create(storeId, limitCount, couponName, discountPrice, minPrice, startDate, endDate))
                .isInstanceOf(InvalidCouponDurationException.class)
                .hasMessage("시작일과 종료일이 올바르지 않습니다.");
    }

    @DisplayName("현재일보다 빠른 날짜로 쿠폰을 생성할 수 없다")
    @Test
    void startDateMustComesAfterNow() {
        // given
        Long storeId = 1L;
        Integer limitCount = 100;
        String couponName = "쿠폰명";
        Long discountPrice = 10000L;
        Long minPrice = 100000L;
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.minusDays(1);
        LocalDate endDate = now.plusDays(100);

        // when // then
        assertThatThrownBy(() ->
                couponCreator.create(storeId, limitCount, couponName, discountPrice, minPrice, startDate, endDate))
                .isInstanceOf(InvalidCouponStartDateException.class)
                .hasMessage("시작일이 올바르지 않습니다.");

    }

    @DisplayName("쿠폰의 시작일과 종료일은 동일할 수 있다")
    @Test
    void startDateAndEndDateCanEqual() {
        // given
        Long storeId = 1L;
        Integer limitCount = 100;
        String couponName = "쿠폰명";
        Long discountPrice = 10000L;
        Long minPrice = 100000L;
        LocalDate now = LocalDate.now();
        LocalDate startDate = now;
        LocalDate endDate = now;

        // when
        Coupon coupon = couponCreator.create(storeId, limitCount, couponName, discountPrice, minPrice, startDate, endDate);

        // then
        assertThat(coupon.getId()).isNotNull();
        assertThat(coupon.getStartDate()).isEqualTo(coupon.getEndDate());

    }

}