package kr.bb.store.domain.store.entity;

import kr.bb.store.domain.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="store_id")
    private Store store;

    @NotNull
    private Long freeDeliveryMinPrice;

    @NotNull
    private Long deliveryPrice;

    @Builder
    public DeliveryPolicy(Store store, Long freeDeliveryMinPrice, Long deliveryPrice) {
        this.store = store;
        this.freeDeliveryMinPrice = freeDeliveryMinPrice;
        this.deliveryPrice = deliveryPrice;
    }


    public void update(Long deliveryPrice, Long freeDeliveryMinPrice) {
        this.deliveryPrice = deliveryPrice;
        this.freeDeliveryMinPrice = freeDeliveryMinPrice;
    }
}
