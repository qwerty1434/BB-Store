package kr.bb.store.domain.coupon.handler;

import kr.bb.store.domain.common.entity.BaseEntity;
import kr.bb.store.domain.coupon.entity.Coupon;
import kr.bb.store.domain.coupon.entity.IssuedCoupon;
import kr.bb.store.domain.coupon.entity.IssuedCouponId;
import kr.bb.store.domain.coupon.exception.AlreadyIssuedCouponException;
import kr.bb.store.domain.coupon.exception.CouponOutOfStockException;
import kr.bb.store.domain.coupon.exception.DeletedCouponException;
import kr.bb.store.domain.coupon.exception.ExpiredCouponException;
import kr.bb.store.domain.coupon.repository.IssuedCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;

@RequiredArgsConstructor
@Component
public class CouponIssuer {
    private final IssuedCouponRepository issuedCouponRepository;

    public IssuedCoupon issueCoupon(Coupon coupon, Long userId, LocalDate issueDate) {
        if(coupon.getIsDeleted()) throw new DeletedCouponException();
        if(coupon.isExpired(issueDate)) throw new ExpiredCouponException();
        if(isExhausted(coupon)) throw new CouponOutOfStockException();
        // TODO : Persistable을 이용한 코드로 수정
        if(isDuplicated(coupon, userId)) throw new AlreadyIssuedCouponException();

        return issuedCouponRepository.save(makeIssuedCoupon(coupon,userId));
    }

    public void issuePossibleCoupons(List<Coupon> coupons, Long userId, LocalDate issueDate) {
        coupons.stream()
                .filter(Predicate.not(Coupon::getIsDeleted))
                .filter(Predicate.not(coupon -> coupon.isExpired(issueDate)))
                .filter(Predicate.not(this::isExhausted))
                .filter(Predicate.not(coupon -> isDuplicated(coupon,userId)))
                .forEach(coupon -> issuedCouponRepository.save(makeIssuedCoupon(coupon,userId)));
    }

    private IssuedCoupon makeIssuedCoupon(Coupon coupon, Long userId) {
        return IssuedCoupon.builder()
                .id(makeIssuedCouponId(coupon.getId(), userId))
                .coupon(coupon)
                .build();
    }

    private IssuedCouponId makeIssuedCouponId(Long couponId, Long userId) {
        return IssuedCouponId.builder()
                .couponId(couponId)
                .userId(userId)
                .build();
    }

    private boolean isExhausted(Coupon coupon) {
        Long issuedCouponCount = issuedCouponRepository.findIssuedCountByCouponId(coupon.getId());
        return coupon.getLimitCount() - issuedCouponCount == 0;
    }

    private boolean isDuplicated(Coupon coupon, Long userId) {
        IssuedCouponId id = makeIssuedCouponId(coupon.getId(), userId);
        return issuedCouponRepository.findById(id).isPresent();
    }

}
