package kr.bb.store.domain.coupon.entity;

import kr.bb.store.domain.common.entity.BaseEntity;
import kr.bb.store.domain.coupon.exception.InvalidCouponDurationException;
import kr.bb.store.domain.coupon.exception.InvalidCouponStartDateException;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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

    @NotNull
    private Long storeId;

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
    public Coupon(String couponCode, Long storeId, Integer limitCount, String couponName, Long discountPrice, Long minPrice, LocalDate startDate, LocalDate endDate) {
        dateValidationCheck(startDate, endDate);

        this.couponCode = couponCode;
        this.storeId = storeId;
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


    private void dateValidationCheck(LocalDate startDate, LocalDate endDate) {
        if(startDate.isBefore(LocalDate.now())) throw new InvalidCouponStartDateException();
        if(endDate.isBefore(startDate)) throw new InvalidCouponDurationException();
    }
}
