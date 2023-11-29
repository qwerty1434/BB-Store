package kr.bb.store.domain.coupon.handler;

import kr.bb.store.domain.coupon.dto.CouponDto;
import kr.bb.store.domain.coupon.dto.CouponForOwnerDto;
import kr.bb.store.domain.coupon.dto.CouponWithIssueStatusDto;
import kr.bb.store.domain.coupon.entity.Coupon;
import kr.bb.store.domain.coupon.entity.IssuedCoupon;
import kr.bb.store.domain.coupon.entity.IssuedCouponId;
import kr.bb.store.domain.coupon.repository.CouponRepository;
import kr.bb.store.domain.coupon.repository.IssuedCouponRepository;
import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.repository.StoreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CouponReaderTest {
    @Autowired
    private CouponReader couponReader;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private IssuedCouponRepository issuedCouponRepository;
    @Autowired
    private EntityManager em;

    @DisplayName("가게 사장에게 보여줄 쿠폰 정보를 조회한다")
    @Test
    void readCouponsForOwner() {
        // given
        Store store = createStore(1L);
        storeRepository.save(store);
        Coupon c1 = createCoupon(store);
        Coupon c2 = createCoupon(store);
        couponRepository.saveAll(List.of(c1,c2));

        // when
        List<CouponForOwnerDto> result = couponReader.readCouponsForOwner(store.getId());

        // then
        assertThat(result).hasSize(2);

    }

    @DisplayName("쿠폰이 발급되면 가게사장이 보는 쿠폰 정보에도 차감된 개수가 전달된다")
    @Test
    void unUsedCountWillDecreaseWhenUserIssueCoupon() {
        // given
        Store store = createStore(1L);
        storeRepository.save(store);
        Coupon c1 = createCoupon(store);
        couponRepository.save(c1);

        Long userId = 1L;
        issuedCouponRepository.save(createIssuedCoupon(c1, userId));

        // when
        List<CouponForOwnerDto> result = couponReader.readCouponsForOwner(store.getId());

        // then
        assertThat(result.get(0).getUnusedCount()).isEqualTo(99);

    }

    @DisplayName("해당 가게의 쿠폰을 모두 보여준다")
    @Test
    void readStoreCouponsForUser() {
        // given
        Store store = createStore(1L);
        storeRepository.save(store);
        Coupon c1 = createCoupon(store);
        Coupon c2 = createCoupon(store);
        couponRepository.saveAll(List.of(c1,c2));

        Long userId = 1L;
        issuedCouponRepository.save(createIssuedCoupon(c1, userId));

        em.flush();
        em.clear();

        // when
        List<CouponWithIssueStatusDto> result = couponReader.readStoreCouponsForUser(userId, store.getId());

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting("isIssued")
                .containsExactlyInAnyOrder(true,false);
    }

    @DisplayName("사용 가능한 쿠폰을 조회한다")
    @Test
    void readAvailableCouponsInStore() {
        // given
        Store s1 = createStore(1L);
        Store s2 = createStore(1L);
        storeRepository.saveAll(List.of(s1,s2));

        Coupon c1 = createCoupon(s1);
        Coupon c2 = createCoupon(s1);
        couponRepository.saveAll(List.of(c1,c2));

        Long userId = 1L;
        // 사용할 수 있는 쿠폰
        issuedCouponRepository.save(createIssuedCoupon(c1, userId));

        // 이미 사용한 쿠폰
        IssuedCoupon issuedCoupon = issuedCouponRepository.save(createIssuedCoupon(c2, userId));
        em.flush();
        em.clear();
        IssuedCoupon couponsToUse = issuedCouponRepository.findById(issuedCoupon.getId()).get();
        couponsToUse.use(LocalDate.now());

        // 해당 상품과 관련없는 쿠폰
        Coupon c3 = createCoupon(s2);
        couponRepository.save(c3);
        issuedCouponRepository.save(createIssuedCoupon(c3, userId));

        // when
        List<CouponDto> result = couponReader.readAvailableCouponsInStore(userId, s1.getId(), LocalDate.now());

        // then
        assertThat(result).hasSize(1);

    }

    @DisplayName("내가 사용할 수 있는 모든 쿠폰을 조회한다")
    @Test
    void readMyValidCoupons() {
        // given
        LocalDate now = LocalDate.now();
        Store s1 = createStore(1L);
        Store s2 = createStore(1L);
        storeRepository.saveAll(List.of(s1,s2));
        Coupon c1 = createCouponWithDate(s1,now,now.plusDays(5));
        Coupon c2 = createCoupon(s1);

        couponRepository.saveAll(List.of(c1,c2));

        Long userId = 1L;
        // 사용할 수 있는 쿠폰
        issuedCouponRepository.save(createIssuedCoupon(c1, userId));

        // 이미 사용한 쿠폰
        IssuedCoupon issuedCoupon = issuedCouponRepository.save(createIssuedCoupon(c2, userId));
        em.flush();
        em.clear();
        IssuedCoupon couponsToUse = issuedCouponRepository.findById(issuedCoupon.getId()).get();
        couponsToUse.use(LocalDate.now());

        // 사용 기간이 지난 쿠폰
        Coupon c3 = createCouponWithDate(s2,now,now);
        couponRepository.save(c3);
        issuedCouponRepository.save(createIssuedCoupon(c3, userId));

        // when
        List<CouponDto> result = couponReader.readMyValidCoupons(userId,now.plusDays(1));

        // then
        assertThat(result).hasSize(1);
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

    private Coupon createCouponWithDate(Store store, LocalDate startDate, LocalDate endDate) {
        return Coupon.builder()
                .couponCode("쿠폰코드")
                .store(store)
                .limitCount(100)
                .couponName("쿠폰이름")
                .discountPrice(10000L)
                .minPrice(100000L)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }

}