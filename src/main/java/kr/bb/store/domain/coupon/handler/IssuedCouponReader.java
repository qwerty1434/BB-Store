package kr.bb.store.domain.coupon.handler;

import kr.bb.store.domain.coupon.entity.IssuedCoupon;
import kr.bb.store.domain.coupon.entity.IssuedCouponId;
import kr.bb.store.domain.coupon.exception.NotIssuedCouponException;
import kr.bb.store.domain.coupon.repository.IssuedCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class IssuedCouponReader {
    private final IssuedCouponRepository issuedCouponRepository;

    public IssuedCoupon read(Long couponId, Long userId) {
        IssuedCouponId id = makeIssuedCouponId(couponId, userId);
        return issuedCouponRepository.findById(id).orElseThrow(NotIssuedCouponException::new);
    }

    public List<IssuedCoupon> readByCouponId(Long couponId, Pageable pageable) {
        long offset = pageable.getOffset();
        int pageSize = pageable.getPageSize();
        return issuedCouponRepository.findByCouponId(couponId, offset, pageSize);
    }

    public long countByCouponId(Long couponId) {
        return issuedCouponRepository.countByCouponId(couponId);
    }

    private IssuedCouponId makeIssuedCouponId(Long couponId, Long userId) {
        return IssuedCouponId.builder()
                .couponId(couponId)
                .userId(userId)
                .build();
    }

}
