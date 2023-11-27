package kr.bb.store.domain.pickup.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickupsForDateDto {
    private Long pickupReservationId;
    private String reservationCode;
    private String productThumbnailImage;
    private String productName;
    private Integer count;
    private Integer orderPickupTotalAmount;
    private String nickname;
    private String phoneNumber;
    private LocalDate pickupDate;
    private String pickupTime;
    private Boolean isCanceled;

    @QueryProjection
    public PickupsForDateDto(Long pickupReservationId, String reservationCode, LocalDate pickupDate, String pickupTime) {
        this.pickupReservationId = pickupReservationId;
        this.reservationCode = reservationCode;
        this.pickupDate = pickupDate;
        this.pickupTime = pickupTime;
    }
}
