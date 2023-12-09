package kr.bb.store.domain.coupon.handler;

import kr.bb.store.client.ProductFeignClient;
import kr.bb.store.domain.AbstractContainer;
import kr.bb.store.domain.coupon.entity.Coupon;
import kr.bb.store.domain.coupon.exception.InvalidCouponDurationException;
import kr.bb.store.domain.coupon.exception.InvalidCouponStartDateException;
import kr.bb.store.domain.coupon.handler.dto.CouponDto;
import kr.bb.store.domain.coupon.repository.CouponRepository;
import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.repository.StoreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.persistence.EntityManager;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Testcontainers
@SpringBootTest
@Transactional
class CouponManagerTest extends AbstractContainer {
    @Autowired
    private CouponManager couponManager;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private EntityManager em;
    @MockBean
    private ProductFeignClient productFeignClient;

    @DisplayName("요청받은 내용으로 쿠폰 정보를 수정한다")
    @Test
    public void editCoupon() {
        // given
        Store store = createStore();
        storeRepository.save(store);
        Coupon coupon = createCoupon(store);
        Coupon savedCoupon = couponRepository.save(coupon);
        CouponDto couponDto = CouponDto.builder()
                .couponName("변경된 쿠폰이름")
                .discountPrice(99_999L)
                .minPrice(999_999L)
                .limitCount(999)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .build();

        // when
        couponManager.edit(savedCoupon,couponDto);

        em.flush();
        em.clear();

        Coupon result = couponRepository.findById(savedCoupon.getId()).get();
        assertThat(result.getCouponName()).isEqualTo("변경된 쿠폰이름");
        assertThat(result.getDiscountPrice()).isEqualTo(99_999L);
        assertThat(result.getEndDate()).isEqualTo(LocalDate.now());

    }

    @DisplayName("쿠폰 종료일은 시작일보다 빠른 날짜로 수정할 수 없다")
    @Test
    public void endDateMustComesAfterStartDate() {
        // given
        Store store = createStore();
        storeRepository.save(store);
        Coupon coupon = createCoupon(store);
        Coupon savedCoupon = couponRepository.save(coupon);

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().minusDays(1);
        CouponDto couponDto = CouponDto.builder()
                .couponName("변경된 쿠폰이름")
                .discountPrice(99_999L)
                .minPrice(999_999L)
                .limitCount(999)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        // when
        assertThatThrownBy(() ->
                couponManager.edit(savedCoupon, couponDto))
                .isInstanceOf(InvalidCouponDurationException.class)
                .hasMessage("시작일과 종료일이 올바르지 않습니다.");
    }

    @DisplayName("현재일보다 빠른 날짜로 쿠폰을 수정할 수 없다")
    @Test
    void startDateMustComesAfterNow() {
        // given
        Store store = createStore();
        storeRepository.save(store);
        Coupon coupon = createCoupon(store);
        Coupon savedCoupon = couponRepository.save(coupon);

        LocalDate now = LocalDate.now();
        LocalDate startDate = now.minusDays(1);
        LocalDate endDate = now.plusDays(100);
        CouponDto couponDto = CouponDto.builder()
                .couponName("변경된 쿠폰이름")
                .discountPrice(99_999L)
                .minPrice(999_999L)
                .limitCount(999)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        // when
        assertThatThrownBy(() ->
                couponManager.edit(savedCoupon, couponDto))
                .isInstanceOf(InvalidCouponStartDateException.class)
                .hasMessage("시작일이 올바르지 않습니다.");
    }

    @DisplayName("쿠폰을 삭제한다")
    @Test
    void deleteCoupon() {
        // given
        Store store = createStore();
        storeRepository.save(store);
        Coupon coupon = createCoupon(store);
        Coupon savedCoupon = couponRepository.save(coupon);

        // when
        couponManager.softDelete(savedCoupon);

        // then
        assertThat(savedCoupon.getIsDeleted()).isTrue();
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
}