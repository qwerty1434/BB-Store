package kr.bb.store.domain.pickup.repository;

import kr.bb.store.domain.pickup.dto.PickupsForDateDto;
import kr.bb.store.domain.pickup.dto.PickupsInMypageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface PickupReservationRepositoryCustom {
    Page<PickupsInMypageDto> getPickupsWithPaging(Long userId, Pageable pageable);

    List<PickupsForDateDto> getPickupsForDate(Long storeId, LocalDate date);
}
