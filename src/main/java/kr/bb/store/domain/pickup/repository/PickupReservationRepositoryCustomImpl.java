package kr.bb.store.domain.pickup.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.bb.store.domain.pickup.dto.PickupsInMypageDto;
import kr.bb.store.domain.pickup.dto.QPickupsInMypageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.List;

import static kr.bb.store.domain.pickup.entity.QPickupReservation.pickupReservation;
import static kr.bb.store.domain.store.entity.QStoreAddress.storeAddress;

public class PickupReservationRepositoryCustomImpl implements PickupReservationRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    public PickupReservationRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<PickupsInMypageDto> getPickupsWithPaging(Long userId, Pageable pageable) {
        List<PickupsInMypageDto> contents = queryFactory.select(new QPickupsInMypageDto(
                        pickupReservation.id,
                        pickupReservation.reservationCode,
                        storeAddress.address,
                        pickupReservation.reservationStatus,
                        pickupReservation.pickupDate,
                        pickupReservation.pickupTime
                ))
                .from(pickupReservation)
                .leftJoin(storeAddress)
                .on(pickupReservation.store.id.eq(storeAddress.id))
                .where(pickupReservation.userId.eq(userId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(pickupReservation.count())
                .from(pickupReservation)
                .where(pickupReservation.userId.eq(userId))
                .fetchOne();
        return new PageImpl<>(contents,pageable,count);
    }
}
