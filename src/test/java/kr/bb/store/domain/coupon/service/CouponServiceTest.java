package kr.bb.store.domain.coupon.service;


import kr.bb.store.domain.coupon.controller.request.CouponEditRequest;
import kr.bb.store.domain.coupon.entity.Coupon;
import kr.bb.store.domain.coupon.exception.UnAuthorizedCouponException;
import kr.bb.store.domain.coupon.repository.CouponRepository;
import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.repository.StoreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class CouponServiceTest {
    @Autowired
    private CouponService couponService;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private EntityManager em;

    @DisplayName("요청받은 내용으로 쿠폰 정보를 수정한다")
    @Test
    public void editCoupon() {
        // given
        Store store = createStore();
        storeRepository.save(store);
        Coupon coupon = couponCreator(store);
        Coupon savedCoupon = couponRepository.save(coupon);
        CouponEditRequest couponRequest = CouponEditRequest.builder()
                .couponName("변경된 쿠폰이름")
                .discountPrice(99_999L)
                .minPrice(999_999L)
                .limitCount(999)
                .startDate(LocalDate.of(2023,11,25))
                .endDate(LocalDate.of(2023,11,25))
                .build();

        // when
        couponService.editCoupon(store.getId(), coupon.getId(), couponRequest);

        em.flush();
        em.clear();

        Coupon result = couponRepository.findById(savedCoupon.getId()).get();
        assertThat(result.getCouponName()).isEqualTo("변경된 쿠폰이름");
        assertThat(result.getDiscountPrice()).isEqualTo(99_999L);
        assertThat(result.getEndDate()).isEqualTo(LocalDate.of(2023,11,25));

    }

    @DisplayName("가게Id정보가 일치하지 않으면 쿠폰을 수정할 수 없다")
    @Test
    public void cannotEditCouponWhenStoreIdMismatches() {
        // given
        Store store = createStore();
        storeRepository.save(store);
        Coupon coupon = couponCreator(store);
        couponRepository.save(coupon);
        CouponEditRequest couponRequest = CouponEditRequest.builder()
                .couponName("변경된 쿠폰이름")
                .discountPrice(99_999L)
                .minPrice(999_999L)
                .limitCount(999)
                .startDate(LocalDate.of(2023,11,25))
                .endDate(LocalDate.of(2023,11,25))
                .build();

        Long wrongStoreId = 5L;

        // when // then
        assertThatThrownBy(() -> couponService.editCoupon(wrongStoreId, coupon.getId(), couponRequest))
                .isInstanceOf(UnAuthorizedCouponException.class)
                .hasMessage("해당 쿠폰에 대한 권한이 없습니다.");

    }


    @DisplayName("쿠폰을 삭제한다")
    @Test
    public void softDeleteCoupon() {
        // given
        Store store = createStore();
        storeRepository.save(store);
        Coupon coupon = couponCreator(store);
        couponRepository.save(coupon);

        // when
        couponService.softDeleteCoupon(store.getId(),coupon.getId());

        // then
        assertThat(coupon.getIsDeleted()).isTrue();

    }

    @DisplayName("가게Id정보가 일치하지 않으면 쿠폰을 삭제할 수 없다")
    @Test
    public void cannotDeleteCouponWhenStoreIdMismatches() {
        // given
        Store store = createStore();
        storeRepository.save(store);
        Coupon coupon = couponCreator(store);
        couponRepository.save(coupon);

        Long wrongStoreId = 5L;

        // when // then
        assertThatThrownBy(() -> couponService.softDeleteCoupon(wrongStoreId, coupon.getId()))
                .isInstanceOf(UnAuthorizedCouponException.class)
                .hasMessage("해당 쿠폰에 대한 권한이 없습니다.");

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

    private Coupon couponCreator(Store store) {
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