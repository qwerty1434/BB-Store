package kr.bb.store.domain.pickup.entity;

import kr.bb.store.domain.common.entity.BaseEntity;
import kr.bb.store.domain.store.entity.Store;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class PickupReservation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
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
    private LocalDate pickupDate;

    @NotNull
    private String pickupTime;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus = ReservationStatus.READY;

    @Builder
    public PickupReservation(Store store, Long userId, Long orderPickupId, Long productId, String reservationCode, LocalDate pickupDate, String pickupTime) {
        this.store = store;
        this.userId = userId;
        this.orderPickupId = orderPickupId;
        this.productId = productId;
        this.reservationCode = reservationCode;
        this.pickupDate = pickupDate;
        this.pickupTime = pickupTime;
    }

    public String toCalendarFormat() {
        return this.pickupDate + " PICKUP";
    }
}
