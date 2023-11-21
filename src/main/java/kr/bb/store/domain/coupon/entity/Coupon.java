package kr.bb.store.domain.coupon.entity;

import kr.bb.store.domain.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

}
