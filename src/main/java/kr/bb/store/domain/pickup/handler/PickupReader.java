package kr.bb.store.domain.pickup.handler;

import kr.bb.store.domain.pickup.dto.PickupsInMypageDto;
import kr.bb.store.domain.pickup.repository.PickupReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PickupReader {
    private final PickupReservationRepository pickupReservationRepository;

    public Page<PickupsInMypageDto> readPickupsForMypage(Long userId, Pageable pageable) {
        return pickupReservationRepository.getPickupsWithPaging(userId, pageable);
    }
}
