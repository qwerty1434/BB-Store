package kr.bb.store.domain.coupon.handler;


import kr.bb.store.domain.RedisContainerTestEnv;
import kr.bb.store.domain.coupon.entity.Coupon;
import kr.bb.store.domain.coupon.entity.IssuedCoupon;
import kr.bb.store.domain.coupon.exception.AlreadyIssuedCouponException;
import kr.bb.store.domain.coupon.exception.CouponOutOfStockException;
import kr.bb.store.domain.coupon.exception.ExpiredCouponException;
import kr.bb.store.domain.coupon.repository.CouponRepository;
import kr.bb.store.domain.coupon.repository.IssuedCouponRepository;
import kr.bb.store.util.RedisOperation;
import kr.bb.store.util.RedisUtils;
import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.repository.StoreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Testcontainers
@SpringBootTest
@Transactional
class CouponIssuerTestEnv extends RedisContainerTestEnv {
    @Autowired
    private CouponIssuer couponIssuer;
    @Autowired
    private IssuedCouponRepository issuedCouponRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private RedisOperation redisOperation;
    @Autowired
    private EntityManager em;


    @DisplayName("사용자에게 쿠폰을 발급해 준다")
    @Test
    public void issueCoupon(){
        // given
        Store store = createStore();
        storeRepository.save(store);

        Integer limitCnt = 100;
        Coupon coupon = createCoupon(store, limitCnt);
        couponRepository.save(coupon);

        Long userId = 1L;
        LocalDate issueDate = LocalDate.now();

        // when
        IssuedCoupon issuedCoupon = couponIssuer.issueCoupon(coupon, userId, issueDate);
        em.flush();
        em.clear();

        IssuedCoupon result = issuedCouponRepository.findById(issuedCoupon.getId()).get();

        // then
        assertThat(result.getCoupon().getCouponCode()).isEqualTo(coupon.getCouponCode());
        assertThat(result.getIsUsed()).isFalse();
    }

    @DisplayName("사용 날짜가 지난 쿠폰은 발급할 수 없다")
    @Test
    public void expiredCouponCannotBeIssued() {
        // given
        Store store = createStore();
        storeRepository.save(store);

        Integer limitCnt = 100;
        Coupon coupon = createCoupon(store, limitCnt);

        couponRepository.save(coupon);

        Long userId = 1L;
        LocalDate expiredDate = LocalDate.now().plusDays(5);

        // when // then
        assertThatThrownBy(() -> couponIssuer.issueCoupon(coupon, userId, expiredDate))
                .isInstanceOf(ExpiredCouponException.class)
                .hasMessage("기한이 만료된 쿠폰입니다.");

    }

    @DisplayName("유저는 동일한 쿠폰을 여러개 발급받을 수 없다")
    @Test
    public void cannotIssueDuplicateCoupon() {
        // given
        Store store = createStore();
        storeRepository.save(store);

        Integer limitCnt = 100;
        Coupon coupon = createCoupon(store, limitCnt);
        couponRepository.save(coupon);

        Long userId = 1L;
        LocalDate issueDate = LocalDate.now();

        // when
        assertThatThrownBy(() -> {
            couponIssuer.issueCoupon(coupon, userId, issueDate);
            couponIssuer.issueCoupon(coupon, userId, issueDate);
        })
                .isInstanceOf(AlreadyIssuedCouponException.class)
                .hasMessage("이미 발급받은 쿠폰입니다.");

    }

    @DisplayName("발급 수량을 초과해서 쿠폰을 발급할 수 없다")
    @Test
    public void CouponCannotBeIssuedExceedingLimitCount() {
        // given
        Store store = createStore();
        storeRepository.save(store);

        Integer limitCnt = 0;
        Coupon coupon = createCoupon(store, limitCnt);

        couponRepository.save(coupon);

        String redisKey = RedisUtils.makeRedisKey(coupon);
        redisOperation.addAndSetExpr(redisKey, LocalDate.now().plusDays(1));

        Long userId = 1L;
        LocalDate issueDate = LocalDate.now();

        // when // then
        assertThatThrownBy(() -> couponIssuer.issueCoupon(coupon, userId, issueDate))
                .isInstanceOf(CouponOutOfStockException.class)
                .hasMessage("준비된 쿠폰이 모두 소진되었습니다.");

    }

    @DisplayName("해당 가게에서 다운받을 수 있는 모든 쿠폰을 다운받는다")
    @Test
    public void issueAllCouponsOfStore() {
        // given
        Store store = createStore();
        storeRepository.save(store);

        Coupon normalCoupon = createCoupon(store, 100);

        Coupon possessedCoupon = createCoupon(store,100);

        Coupon exhaustedCoupon = createCoupon(store, 0);

        List<Coupon> coupons = List.of(normalCoupon,possessedCoupon,exhaustedCoupon);
        couponRepository.saveAll(coupons);

        Long userId = 1L;
        LocalDate issueDate = LocalDate.now();

        String redisKey = RedisUtils.makeRedisKey(exhaustedCoupon);
        redisOperation.addAndSetExpr(redisKey, LocalDate.now().plusDays(1));

        IssuedCoupon usedCoupon = couponIssuer.issueCoupon(possessedCoupon, userId, issueDate);
        usedCoupon.use(LocalDate.now());

        // when
        couponIssuer.issuePossibleCoupons(coupons, userId, issueDate);

        List<IssuedCoupon> usableCouponsOfUser = issuedCouponRepository.findUsableCouponsByUserId(userId);

        // then
        assertThat(usableCouponsOfUser).hasSize(1);
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

    private Coupon createCoupon(Store store, int limitCnt) {
        return Coupon.builder()
                .couponCode("쿠폰코드")
                .store(store)
                .limitCount(limitCnt)
                .couponName("쿠폰이름")
                .discountPrice(10000L)
                .minPrice(100000L)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .build();
    }

}