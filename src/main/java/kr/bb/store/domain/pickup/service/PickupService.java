package kr.bb.store.domain.pickup.service;

import kr.bb.store.domain.pickup.controller.request.PickupCreateRequest;
import kr.bb.store.domain.pickup.controller.response.PickupsInMypageWithPageingResponse;
import kr.bb.store.domain.pickup.dto.PickupsInMypageDto;
import kr.bb.store.domain.pickup.entity.PickupReservation;
import kr.bb.store.domain.pickup.handler.PickupCreator;
import kr.bb.store.domain.pickup.handler.PickupReader;
import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.handler.StoreReader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PickupService {
    private final PickupCreator pickupCreator;
    private final PickupReader pickupReader;
    private final StoreReader storeReader;

    @Transactional
    public PickupReservation createPickup(PickupCreateRequest pickupCreateRequest) {
        Store store = storeReader.findStoreById(pickupCreateRequest.getStoreId());
        return pickupCreator.create(store, pickupCreateRequest);
    }

    public PickupsInMypageWithPageingResponse getPickupsForUser(Long userId, Pageable pageable) {
        Page<PickupsInMypageDto> pickupsInMypageDtos = pickupReader.readPickupsForMypage(userId, pageable);
        return PickupsInMypageWithPageingResponse.builder()
                .data(pickupsInMypageDtos.getContent())
                .totalCnt(pickupsInMypageDtos.getTotalElements())
                .build();
    }
}
