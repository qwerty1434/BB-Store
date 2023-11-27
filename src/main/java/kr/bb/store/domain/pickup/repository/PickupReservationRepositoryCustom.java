package kr.bb.store.domain.pickup.repository;

import kr.bb.store.domain.pickup.dto.PickupsInMypageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PickupReservationRepositoryCustom {
    Page<PickupsInMypageDto> getPickupsWithPaging(Long userId, Pageable pageable);
}
