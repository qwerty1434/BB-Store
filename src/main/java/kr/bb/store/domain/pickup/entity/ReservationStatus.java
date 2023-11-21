package kr.bb.store.domain.pickup.entity;

import lombok.Getter;

@Getter
public enum ReservationStatus {
    READY("예약 대기"), CONFIRMED("픽업 완료");

    private final String message;

    ReservationStatus(String message) {
        this.message = message;
    }
}
