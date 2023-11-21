package kr.bb.store.domain.store.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class DeliveryPolicy {
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
}
