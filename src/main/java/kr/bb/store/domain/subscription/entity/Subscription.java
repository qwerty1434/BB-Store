package kr.bb.store.domain.subscription.entity;

import kr.bb.store.domain.common.entity.BaseEntity;
import kr.bb.store.domain.store.entity.Store;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
