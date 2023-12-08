package kr.bb.store.domain.coupon.handler;

import kr.bb.store.domain.coupon.dto.CouponDto;
import kr.bb.store.domain.coupon.dto.CouponForOwnerDto;
import kr.bb.store.domain.coupon.dto.CouponWithAvailabilityDto;
import kr.bb.store.domain.coupon.dto.CouponWithIssueStatusDto;
import kr.bb.store.domain.coupon.entity.Coupon;
import kr.bb.store.domain.coupon.entity.IssuedCoupon;
import kr.bb.store.domain.coupon.entity.IssuedCouponId;
import kr.bb.store.domain.coupon.repository.CouponRepository;
import kr.bb.store.domain.coupon.repository.IssuedCouponRepository;
import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.repository.StoreRepository;
import net.bytebuddy.asm.Advice;
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
    @DisplayName("아무도 발급받지 않은 쿠폰의 수량은 처음 설정과 동일하다")
    @Test
    void couponCountWillEqualIfNobodyIssueTheCoupon() {
        // given
        Store store = createStore(1L);
        storeRepository.save(store);
        Coupon c1 = createCoupon(store);
        couponRepository.save(c1);

        // when
        List<CouponForOwnerDto> result = couponReader.readCouponsForOwner(store.getId());

        // then
        assertThat(result.get(0).getUnusedCount()).isEqualTo(c1.getLimitCount());

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

    @DisplayName("해당 가게의 모든 쿠폰을 유저에게 보여준다")
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

        LocalDate now = LocalDate.now();

        // when
        List<CouponWithIssueStatusDto> result = couponReader.readStoreCouponsForUser(userId, store.getId(), now);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting("isIssued")
                .containsExactlyInAnyOrder(true,false);
    }
    @DisplayName("만료된 쿠폰은 유저에게 노출되지 않는다")
    @Test
    void expiredCouponWillNotExposedToUser() {
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

        LocalDate expirationDate = LocalDate.now().plusDays(5);

        // when
        List<CouponWithIssueStatusDto> result = couponReader.readStoreCouponsForUser(userId, store.getId(), expirationDate);

        // then
        assertThat(result).hasSize(0);
    }

    @DisplayName("특정 가게의 상품을 주문할 때 사용 가능한 쿠폰을 조회한다")
    @Test
    void readAvailableCouponsInStore() {
        // given
        LocalDate now = LocalDate.now();
        Store s1 = createStore(1L);
        Store s2 = createStore(1L);
        storeRepository.saveAll(List.of(s1,s2));

        Coupon c1 = createCouponWithDate(s1,now,now.plusDays(5));
        Coupon c2 = createCouponWithDate(s1,now,now.plusDays(5));
        couponRepository.saveAll(List.of(c1,c2));

        Long totalAmount = Long.MAX_VALUE;

        Long userId = 1L;
        // 사용할 수 있는 쿠폰
        issuedCouponRepository.save(createIssuedCoupon(c1, userId));

        // 이미 사용한 쿠폰
        IssuedCoupon issuedCoupon = issuedCouponRepository.save(createIssuedCoupon(c2, userId));
        em.flush();
        em.clear();
        IssuedCoupon couponsToUse = issuedCouponRepository.findById(issuedCoupon.getId()).get();
        couponsToUse.use(LocalDate.now());

        // 해당 상품(가게)과 관련없는 쿠폰
        Coupon c3 = createCouponWithDate(s2,now,now.plusDays(5));
        couponRepository.save(c3);
        issuedCouponRepository.save(createIssuedCoupon(c3, userId));

        // 해당 가게의 유효한 쿠폰이지만 다운로드 받지 않은 쿠폰
        Coupon c4 = createCouponWithDate(s1,now,now.plusDays(5));
        couponRepository.save(c4);

        // 해당 가게의 기간이 지난 쿠폰
        Coupon c5 = createCouponWithDate(s1,now,now);
        couponRepository.save(c5);

        // when
        List<CouponWithAvailabilityDto> result = couponReader.readAvailableCouponsInStore(totalAmount, userId, s1.getId(), LocalDate.now().plusDays(2));

        // then
        assertThat(result).hasSize(1);

    }

    @DisplayName("주문금액이 쿠폰의 최소사용금액보다 작다면 사용불가로 표시된다")
    @Test
    void couponCannotUseWhenTotalAmountLowerThanCouponMinPrice() {
        // given
        LocalDate now = LocalDate.now();
        Store store = createStore(1L);
        storeRepository.save(store);

        Long minPrice = 10_000L;
        Coupon coupon = createCouponWithMinPrice(store,minPrice);
        couponRepository.save(coupon);

        Long totalAmount = minPrice - 1;

        Long userId = 1L;
        // 사용할 수 있는 쿠폰
        issuedCouponRepository.save(createIssuedCoupon(coupon, userId));

        // when
        List<CouponWithAvailabilityDto> result = couponReader.readAvailableCouponsInStore(totalAmount, userId, store.getId(), LocalDate.now());

        // then
        assertThat(result)
                .extracting("isAvailable")
                .containsExactly(false);

    }
    @DisplayName("주문금액과 쿠폰의 최소사용금액이 동일할 때는 사용가능으로 표시된다")
    @Test
    void couponCanUseWhenTotalAmountIsEqualToCouponMinPrice() {
        // given
        LocalDate now = LocalDate.now();
        Store store = createStore(1L);
        storeRepository.save(store);

        Long minPrice = 10_000L;
        Coupon coupon = createCouponWithMinPrice(store,minPrice);
        couponRepository.save(coupon);

        Long totalAmount = minPrice;

        Long userId = 1L;
        // 사용할 수 있는 쿠폰
        issuedCouponRepository.save(createIssuedCoupon(coupon, userId));

        // when
        List<CouponWithAvailabilityDto> result = couponReader.readAvailableCouponsInStore(totalAmount, userId, store.getId(), LocalDate.now());

        // then
        assertThat(result)
                .extracting("isAvailable")
                .containsExactly(true);

    }


    @DisplayName("내가 보유한 사용할 수 있는 모든 쿠폰을 조회한다")
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

        // 유효하지만 다운로드 받지 않은 쿠폰
        Coupon c4 = createCouponWithDate(s1,now,now.plusDays(5));
        couponRepository.save(c4);

        // when
        List<CouponDto> result = couponReader.readMyValidCoupons(userId,now.plusDays(1));

        // then
        assertThat(result).hasSize(1);
    }

    @DisplayName("현재 다운받을 수 있는 쿠폰 목록을 반환한다")
    @Test
    void readStoresAllValidateCoupon() {
        // given
        LocalDate now = LocalDate.now();
        Store s1 = createStore(1L);
        Store s2 = createStore(1L);
        storeRepository.saveAll(List.of(s1,s2));
        Long userId = 1L;

        Coupon c1 = createCouponWithDate(s1,now,now.plusDays(5)); // 다운로드 가능한 쿠폰

        Coupon c2 = createCouponWithDate(s1,now,now.plusDays(5)); // 다운로드 받은 쿠폰
        Coupon c3 = createCouponWithDate(s1,now,now.plusDays(5)); // 다운로드 받아서 사용한 쿠폰
        Coupon c4 = createCoupon(s1); // 만료된 쿠폰
        Coupon c5 = createCouponWithDate(s2,now,now.plusDays(5)); // 다른 가게의 쿠폰
        couponRepository.saveAll(List.of(c1,c2,c3,c4,c5));

        issuedCouponRepository.save(createIssuedCoupon(c2, userId));

        IssuedCoupon issuedCoupon = issuedCouponRepository.save(createIssuedCoupon(c3, userId));
        issuedCoupon.use(now);

        // when
        List<Coupon> result = couponReader.readStoresAllValidateCoupon(s1.getId(), now.plusDays(3));

        // then
        assertThat(result).hasSize(1)
                .containsExactly(c1);


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

    private Coupon createCouponWithMinPrice(Store store, Long minPrice) {
        return Coupon.builder()
                .couponCode("쿠폰코드")
                .store(store)
                .limitCount(100)
                .couponName("쿠폰이름")
                .discountPrice(10000L)
                .minPrice(minPrice)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .build();
    }

}