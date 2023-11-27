package kr.bb.store.domain.pickup.dto;

import com.querydsl.core.annotations.QueryProjection;
import kr.bb.store.domain.pickup.entity.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickupsInMypageDto {
    private Long key;
    private String reservationCode;
    private String productThumbnailImage;
    private String productName;
    private Long orderProductQuantity;
    private String storeAddress;
    private String paymentAmount;
    private ReservationStatus pickupStatus;
    private LocalDate pickupDate;
    private String pickupTime;

    @QueryProjection
    public PickupsInMypageDto(Long key, String reservationCode, String storeAddress, ReservationStatus pickupStatus, LocalDate pickupDate, String pickupTime) {
        this.key = key;
        this.reservationCode = reservationCode;
        this.storeAddress = storeAddress;
        this.pickupStatus = pickupStatus;
        this.pickupDate = pickupDate;
        this.pickupTime = pickupTime;
    }
}
