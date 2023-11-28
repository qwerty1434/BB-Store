package kr.bb.store.domain.subscription.entity;

import kr.bb.store.domain.common.entity.BaseEntity;
import kr.bb.store.domain.store.entity.Store;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@Entity
public class Subscription extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="store_id",nullable = false)
    private Store store;

    @NotNull
    private Long orderSubscriptionId;

    @NotNull
    private Long userId;

    @NotNull
    private Long subscriptionProductId;

    @NotNull
    private String subscriptionCode;

}
