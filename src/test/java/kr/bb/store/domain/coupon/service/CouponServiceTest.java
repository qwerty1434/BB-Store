package kr.bb.store.domain.coupon.service;


import kr.bb.store.client.ProductFeignClient;
import kr.bb.store.domain.AbstractContainer;
import kr.bb.store.domain.coupon.controller.request.CouponEditRequest;
import kr.bb.store.domain.coupon.entity.Coupon;
import kr.bb.store.domain.coupon.entity.IssuedCoupon;
import kr.bb.store.domain.coupon.entity.IssuedCouponId;
import kr.bb.store.domain.coupon.exception.UnAuthorizedCouponException;
import kr.bb.store.domain.coupon.repository.CouponRepository;
import kr.bb.store.domain.coupon.repository.IssuedCouponRepository;
import kr.bb.store.domain.coupon.util.RedisUtils;
import kr.bb.store.domain.coupon.util.RedisOperation;
import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.repository.StoreRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Testcontainers
@SpringBootTest
class CouponServiceTest extends AbstractContainer {
    @Autowired
    private CouponService couponService;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private EntityManager em;
    @Autowired
    private IssuedCouponRepository issuedCouponRepository;
    @Autowired
    private RedisOperation redisOperation;
    @MockBean
    private ProductFeignClient productFeignClient;
    @MockBean
    private RedissonClient redissonClient;


    @AfterEach
    void teardown() {
        issuedCouponRepository.deleteAllInBatch();
        couponRepository.deleteAllInBatch();
        storeRepository.deleteAllInBatch();
    }

    @DisplayName("요청받은 내용으로 쿠폰 정보를 수정한다")
    @Test
    void editCoupon() {
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
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .build();

        // when
        couponService.editCoupon(store.getId(), coupon.getId(), couponRequest);

        Coupon result = couponRepository.findById(savedCoupon.getId()).get();
        assertThat(result.getCouponName()).isEqualTo("변경된 쿠폰이름");
        assertThat(result.getDiscountPrice()).isEqualTo(99_999L);
        assertThat(result.getEndDate()).isEqualTo(LocalDate.now());

    }

    @DisplayName("가게Id정보가 일치하지 않으면 쿠폰을 수정할 수 없다")
    @Test
    void cannotEditCouponWhenStoreIdMismatches() {
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
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .build();

        Long wrongStoreId = 999L;

        // when // then
        assertThatThrownBy(() -> couponService.editCoupon(wrongStoreId, coupon.getId(), couponRequest))
                .isInstanceOf(UnAuthorizedCouponException.class)
                .hasMessage("해당 쿠폰에 대한 권한이 없습니다.");

    }


    @DisplayName("쿠폰을 삭제한다")
    @Test
    void softDeleteCoupon() {
        // given
        Store store = createStore();
        storeRepository.save(store);
        Coupon coupon = couponCreator(store);
        couponRepository.save(coupon);

        // when
        couponService.softDeleteCoupon(store.getId(),coupon.getId());
        Coupon coupon1 = couponRepository.findById(coupon.getId()).get();

        // then
        assertThat(coupon1.getIsDeleted()).isTrue();

    }

    @DisplayName("가게Id정보가 일치하지 않으면 쿠폰을 삭제할 수 없다")
    @Test
    void cannotDeleteCouponWhenStoreIdMismatches() {
        // given
        Store store = createStore();
        storeRepository.save(store);
        Coupon coupon = couponCreator(store);
        couponRepository.save(coupon);

        Long wrongStoreId = 999L;

        // when // then
        assertThatThrownBy(() -> couponService.softDeleteCoupon(wrongStoreId, coupon.getId()))
                .isInstanceOf(UnAuthorizedCouponException.class)
                .hasMessage("해당 쿠폰에 대한 권한이 없습니다.");

    }

    @DisplayName("동시에 들어온 쿠폰을 모두 사용한다")
    @Test
    void useAllCoupons() {
        // given
        Store store = createStore();
        storeRepository.save(store);

        Coupon c1 = couponCreator(store);
        Coupon c2 = couponCreator(store);
        Coupon c3 = couponCreator(store);
        couponRepository.saveAll(List.of(c1,c2,c3));

        Long userId = 1L;
        LocalDate useDate = LocalDate.now();

        IssuedCoupon ic1 = createIssuedCoupon(c1,userId);
        IssuedCoupon ic2 = createIssuedCoupon(c2,userId);
        IssuedCoupon ic3 = createIssuedCoupon(c3,userId);
        issuedCouponRepository.saveAll(List.of(ic1, ic2, ic3));

        List<Long> couponIds = List.of(c1.getId(), c2.getId(), c3.getId());
        List<IssuedCouponId> issuedCouponIds = List.of(ic1.getId(), ic2.getId(), ic3.getId());

        // when
        couponService.useAllCoupons(couponIds, userId, useDate);

        List<IssuedCoupon> result = issuedCouponRepository.findAllById(issuedCouponIds);

        // then
        assertThat(result).hasSize(3)
                .extracting("isUsed")
                .contains(true);

    }

    @DisplayName("멀티쓰레드 환경에서도 동시에 쿠폰 발급을 요청해도 정해진 수량만큼의 발급이 보장된다")
    @Test
    void issueCouponInMultiThread() throws InterruptedException, ExecutionException {
        // given
        int limitCount = 100;
        int applicantsCount = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(applicantsCount);

        Future<Long> couponCreate = executorService.submit(() -> {
            Store store = createStore();
            storeRepository.save(store);

            Coupon coupon = couponCreator(store, limitCount);
            couponRepository.save(coupon).getId();

            String redisKey = RedisUtils.makeRedisKey(coupon);
            redisOperation.addAndSetExpr(redisKey, LocalDate.now().plusDays(1));

            return coupon.getId();

        });

        final Long couponId = couponCreate.get();

        // when
        LongStream.rangeClosed(1L, applicantsCount)
                .forEach(userId ->
                    executorService.submit(() -> {
                        try {
                            couponService.downloadCoupon(userId,couponId,LocalDate.now());
                        } catch (Exception ignored) {
                        } finally {
                            latch.countDown();
                        }
                    })
                );

        latch.await();

        long issuedCouponCount = issuedCouponRepository.count();

        // then
        assertThat(issuedCouponCount).isEqualTo(limitCount);

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
                .couponCode(UUID.randomUUID().toString())
                .store(store)
                .limitCount(100)
                .couponName("쿠폰이름")
                .discountPrice(10000L)
                .minPrice(100000L)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .build();
    }
    private Coupon couponCreator(Store store, int limitCount) {
        return Coupon.builder()
                .couponCode(UUID.randomUUID().toString())
                .store(store)
                .limitCount(limitCount)
                .couponName("쿠폰이름")
                .discountPrice(10000L)
                .minPrice(100000L)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .build();
    }


}