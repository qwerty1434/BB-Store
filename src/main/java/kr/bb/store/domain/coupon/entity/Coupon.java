package kr.bb.store.domain.coupon.entity;

import kr.bb.store.domain.common.entity.BaseEntity;
import kr.bb.store.domain.coupon.exception.InvalidCouponDurationException;
import kr.bb.store.domain.coupon.exception.InvalidCouponStartDateException;
import kr.bb.store.domain.store.entity.Store;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Coupon extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String couponCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="store_id")
    private Store store;

    @NotNull
    private Integer limitCount;

    @NotNull
    private String couponName;

    @NotNull
    private Long discountPrice;

    @NotNull
    private Long minPrice;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @Builder
    public Coupon(String couponCode, Store store, Integer limitCount, String couponName, Long discountPrice, Long minPrice, LocalDate startDate, LocalDate endDate) {
        dateValidationCheck(startDate, endDate);

        this.couponCode = couponCode;
        this.store = store;
        this.limitCount = limitCount;
        this.couponName = couponName;
        this.discountPrice = discountPrice;
        this.minPrice = minPrice;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void update(Integer limitCount, String couponName, Long discountPrice, Long minPrice,
                       LocalDate startDate, LocalDate endDate) {
        dateValidationCheck(startDate, endDate);

        this.limitCount = limitCount;
        this.couponName = couponName;
        this.discountPrice = discountPrice;
        this.minPrice = minPrice;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public boolean isExpired(LocalDate now) {
        if(this.endDate.isBefore(now)) return true;
        return false;
    }

    public boolean isRightPrice(long receivedPaymentPrice, long receivedDiscountPrice) {
        return minPrice <= receivedPaymentPrice && discountPrice == receivedDiscountPrice;
    }

    private void dateValidationCheck(LocalDate startDate, LocalDate endDate) {
        if(startDate.isBefore(LocalDate.now())) throw new InvalidCouponStartDateException();
        if(endDate.isBefore(startDate)) throw new InvalidCouponDurationException();
    }
}
