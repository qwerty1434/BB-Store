package kr.bb.store.domain.coupon.handler;


import kr.bb.store.client.ProductFeignClient;
import kr.bb.store.domain.AbstractContainer;
import kr.bb.store.domain.coupon.entity.Coupon;
import kr.bb.store.domain.coupon.exception.InvalidCouponDurationException;
import kr.bb.store.domain.coupon.exception.InvalidCouponStartDateException;
import kr.bb.store.domain.coupon.handler.dto.CouponDto;
import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.repository.StoreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Testcontainers
@SpringBootTest
@Transactional
class CouponCreatorTest extends AbstractContainer {
    @Autowired
    private CouponCreator couponCreator;
    @Autowired
    private StoreRepository storeRepository;
    @MockBean
    private ProductFeignClient productFeignClient;

    @DisplayName("쿠폰 정보를 전달받아 쿠폰을 생성한다")
    @Test
    void createCoupon() {
        // given
        Store store = createStore();
        storeRepository.save(store);
        Integer limitCount = 100;
        String couponName = "쿠폰명";
        Long discountPrice = 10000L;
        Long minPrice = 100000L;
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now();
        CouponDto couponDto = CouponDto.builder()
                .limitCount(limitCount)
                .couponName(couponName)
                .discountPrice(discountPrice)
                .minPrice(minPrice)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        // when
        Coupon coupon = couponCreator.create(store, couponDto);

        // then
        assertThat(coupon.getId()).isNotNull();

    }

    @DisplayName("쿠폰 종료일은 시작일보다 빠를 수 없다")
    @Test
    void endDateMustComesAfterStartDate() {
        // given
        Store store = createStore();
        storeRepository.save(store);
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().minusDays(1);
        CouponDto couponDto = createCouponDtoWithDate(startDate, endDate);

        // when // then
        assertThatThrownBy(() ->
                couponCreator.create(store, couponDto))
                .isInstanceOf(InvalidCouponDurationException.class)
                .hasMessage("시작일과 종료일이 올바르지 않습니다.");
    }

    @DisplayName("현재일보다 빠른 날짜로 쿠폰을 생성할 수 없다")
    @Test
    void startDateMustComesAfterNow() {
        // given
        Store store = createStore();
        storeRepository.save(store);
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.minusDays(1);
        LocalDate endDate = now.plusDays(100);
        CouponDto couponDto = createCouponDtoWithDate(startDate, endDate);

        // when // then
        assertThatThrownBy(() ->
                couponCreator.create(store, couponDto))
                .isInstanceOf(InvalidCouponStartDateException.class)
                .hasMessage("시작일이 올바르지 않습니다.");

    }

    @DisplayName("쿠폰의 시작일과 종료일은 동일할 수 있다")
    @Test
    void startDateAndEndDateCanEqual() {
        // given
        Store store = createStore();
        storeRepository.save(store);
        LocalDate now = LocalDate.now();
        LocalDate startDate = now;
        LocalDate endDate = now;
        CouponDto couponDto = createCouponDtoWithDate(startDate, endDate);

        // when
        Coupon coupon = couponCreator.create(store, couponDto);

        // then
        assertThat(coupon.getId()).isNotNull();
        assertThat(coupon.getStartDate()).isEqualTo(coupon.getEndDate());

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

    private CouponDto createCouponDtoWithDate(LocalDate startDate, LocalDate endDate) {
        return CouponDto.builder()
                .couponName("변경된 쿠폰이름")
                .discountPrice(10_000L)
                .minPrice(100_000L)
                .limitCount(100)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }

}