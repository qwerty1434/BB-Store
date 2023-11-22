package kr.bb.store.domain.pickup.entity;

import kr.bb.store.domain.common.entity.BaseEntity;
import kr.bb.store.domain.store.entity.Store;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class PickupReservation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="store_id", nullable = false)
    private Store store;

    @NotNull
    private Long userId;

    @NotNull
    private Long orderPickupId;

    @NotNull
    private Long productId;

    @NotNull
    private String reservationCode;

    @NotNull
    private LocalDateTime pickupDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus;

}
