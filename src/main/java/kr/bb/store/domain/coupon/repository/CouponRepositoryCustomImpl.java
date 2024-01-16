package kr.bb.store.domain.coupon.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.bb.store.domain.coupon.dto.*;
import kr.bb.store.domain.coupon.entity.Coupon;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

import static kr.bb.store.domain.coupon.entity.QCoupon.coupon;
import static kr.bb.store.domain.coupon.entity.QIssuedCoupon.issuedCoupon;

public class CouponRepositoryCustomImpl implements CouponRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    public CouponRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<CouponForOwnerDto> findAllDtoByStoreId(Long storeId, LocalDate now) {
        return queryFactory
                .select(new QCouponForOwnerDto(
                        coupon.id,
                        coupon.couponCode,
                        coupon.couponName,
                        coupon.minPrice,
                        coupon.discountPrice,
                        coupon.limitCount.subtract(
                                JPAExpressions
                                        .select(issuedCoupon.count())
                                        .from(issuedCoupon)
                                        .where(issuedCoupon.id.couponId.eq(coupon.id))
                        ),
                        coupon.startDate,
                        coupon.endDate
                ))
                .from(coupon)
                .leftJoin(issuedCoupon)
                .on(coupon.id.eq(issuedCoupon.id.couponId))
                .where(
                        coupon.store.id.eq(storeId),
                        coupon.isDeleted.isFalse(),
                        coupon.endDate.goe(now)
                )
                .fetch();
    }

    @Override
    public List<Coupon> findAllDownloadableCouponsByStoreId(Long storeId, LocalDate now) {
        return queryFactory
                .selectFrom(coupon)
                .leftJoin(issuedCoupon)
                .on(issuedCoupon.coupon.id.eq(coupon.id))
                .where(
                        coupon.store.id.eq(storeId),
                        issuedCoupon.id.isNull(),
                        isCouponUnexpired(now),
                        coupon.isDeleted.isFalse()
                )
                .fetch();
    }

    @Override
    public List<CouponWithIssueStatusDto> findStoreCouponsForUser(Long userId, Long storeId, LocalDate now) {
        return queryFactory
                .select(new QCouponWithIssueStatusDto(
                    coupon.id,
                    coupon.couponName,
                    coupon.store.storeName,
                    coupon.discountPrice,
                    coupon.endDate,
                    coupon.minPrice,
                    JPAExpressions
                            .select(issuedCoupon.count().gt(0))
                            .from(issuedCoupon)
                            .where(
                                    issuedCoupon.id.couponId.eq(coupon.id),
                                    issuedCoupon.id.userId.eq(userId),
                                    coupon.isDeleted.isFalse()
                            )
                ))
                .from(coupon)
                .where(
                        coupon.store.id.eq(storeId),
                        isCouponUnexpired(now),
                        coupon.isDeleted.isFalse()
                )
                .fetch();
    }

    @Override
    public List<CouponWithAvailabilityDto> findAvailableCoupons(Long totalAmount, Long userId, Long storeId, LocalDate now) {
        return queryFactory.select(new QCouponWithAvailabilityDto(
                coupon.id,
                coupon.couponName,
                coupon.store.storeName,
                coupon.discountPrice,
                coupon.endDate,
                coupon.minPrice,
                coupon.minPrice.loe(totalAmount)
                ))
                .from(coupon)
                .leftJoin(issuedCoupon)
                .on(coupon.id.eq(issuedCoupon.id.couponId))
                .where(
                        coupon.store.id.eq(storeId),
                        issuedCoupon.id.userId.eq(userId),
                        issuedCoupon.isUsed.isFalse(),
                        isCouponUnexpired(now),
                        coupon.isDeleted.isFalse()
                )
                .fetch();
    }

    @Override
    public List<CouponDto> findMyValidCoupons(Long userId, LocalDate now) {
        return queryFactory
                .select(new QCouponDto(
                        coupon.id,
                        coupon.couponName,
                        coupon.store.storeName,
                        coupon.discountPrice,
                        coupon.endDate,
                        coupon.minPrice
                ))
                .from(coupon)
                .leftJoin(issuedCoupon)
                .on(coupon.id.eq(issuedCoupon.id.couponId))
                .where(
                        issuedCoupon.id.userId.eq(userId),
                        issuedCoupon.isUsed.isFalse(),
                        isCouponUnexpired(now),
                        coupon.isDeleted.isFalse()
                )
                .fetch();
    }

    @Override
    public Integer findMyValidCouponCount(Long userId, LocalDate now) {
        return Math.toIntExact(queryFactory
                .select(coupon.count())
                .from(coupon)
                .leftJoin(issuedCoupon)
                .on(coupon.id.eq(issuedCoupon.id.couponId))
                .where(
                        issuedCoupon.id.userId.eq(userId),
                        issuedCoupon.isUsed.isFalse(),
                        isCouponUnexpired(now),
                        coupon.isDeleted.isFalse()
                )
                .fetchFirst());
    }

    private BooleanExpression isCouponUnexpired(LocalDate now) {
        return coupon.endDate.after(now).or(coupon.endDate.eq(now));
    }

}
