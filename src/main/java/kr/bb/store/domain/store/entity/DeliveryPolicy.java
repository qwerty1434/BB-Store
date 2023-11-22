package kr.bb.store.domain.store.entity;

import kr.bb.store.domain.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class DeliveryPolicy extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name="store_id")
    private Store store;

    @NotNull
    private Long minOrderPrice;

    @NotNull
    private Long freeDeliveryMinPrice;

    @NotNull
    private Long deliveryPrice;

    public void update(Long minOrderPrice, Long deliveryPrice, Long freeDeliveryMinPrice) {
        this.minOrderPrice = minOrderPrice;
        this.deliveryPrice = deliveryPrice;
        this.freeDeliveryMinPrice = freeDeliveryMinPrice;
    }
}
