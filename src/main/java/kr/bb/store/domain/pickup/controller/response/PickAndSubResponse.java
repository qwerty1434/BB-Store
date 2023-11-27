package kr.bb.store.domain.pickup.controller.response;

import kr.bb.store.domain.pickup.entity.PickupReservation;
import kr.bb.store.domain.subscription.entity.Subscription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickAndSubResponse {
    List<String> data;

    public static PickAndSubResponse of(List<PickupReservation> pickupReservations, List<String> subscriptionDates) {
        Set<String> pickupSchedule = pickupReservations.stream()
                .map(PickupReservation::toCalendarFormat)
                .collect(Collectors.toSet());

        Set<String> subscriptionSchedule = subscriptionDates.stream()
                .map(date -> date +" SUBSCRIPTION")
                .collect(Collectors.toSet());

        List<String> schedules = Stream.concat(pickupSchedule.stream(), subscriptionSchedule.stream())
                .collect(Collectors.toList());

        Collections.sort(schedules);

        return PickAndSubResponse.builder()
                .data(schedules)
                .build();
    }
}
