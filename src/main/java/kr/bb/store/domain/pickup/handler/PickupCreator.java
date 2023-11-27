package kr.bb.store.domain.pickup.handler;

import kr.bb.store.domain.pickup.controller.request.PickupCreateRequest;
import kr.bb.store.domain.pickup.entity.PickupReservation;
import kr.bb.store.domain.pickup.repository.PickupReservationRepository;
import kr.bb.store.domain.store.entity.Store;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component
public class PickupCreator {
    private final PickupReservationRepository pickupReservationRepository;

    public PickupReservation create(Store store, PickupCreateRequest pickupCreateRequest) {

        PickupReservation pickup = PickupReservation.builder()
                .store(store)
                .userId(pickupCreateRequest.getUserId())
                .orderPickupId(pickupCreateRequest.getOrderPickupId())
                .productId(pickupCreateRequest.getProductId())
                .reservationCode(UUID.randomUUID().toString())
                .pickupDate(pickupCreateRequest.getPickupDate())
                .pickupTime(pickupCreateRequest.getPickupTime())
                .build();

        return pickupReservationRepository.save(pickup);
    }

}
