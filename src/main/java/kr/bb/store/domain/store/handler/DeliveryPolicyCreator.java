package kr.bb.store.domain.store.handler;

import kr.bb.store.domain.store.entity.DeliveryPolicy;
import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.repository.DeliveryPolicyRepository;
import kr.bb.store.domain.store.dto.DeliveryPolicyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DeliveryPolicyCreator {
    private final DeliveryPolicyRepository deliveryPolicyRepository;

    public DeliveryPolicy create(Store store, DeliveryPolicyDto deliveryPolicyDto) {
        DeliveryPolicy deliveryPolicy = DeliveryPolicy.builder()
                .store(store)
                .minOrderPrice(deliveryPolicyDto.getMinOrderPrice())
                .freeDeliveryMinPrice(deliveryPolicyDto.getFreeDeliveryMinPrice())
                .deliveryPrice(deliveryPolicyDto.getDeliveryPrice())
                .build();
        return deliveryPolicyRepository.save(deliveryPolicy);
    }
}
