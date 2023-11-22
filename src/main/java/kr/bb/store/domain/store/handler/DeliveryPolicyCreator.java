package kr.bb.store.domain.store.handler;

import kr.bb.store.domain.store.entity.DeliveryPolicy;
import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.repository.DeliveryPolicyRepository;
import kr.bb.store.domain.store.handler.request.DeliveryPolicyRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DeliveryPolicyCreator {
    private final DeliveryPolicyRepository deliveryPolicyRepository;

    public DeliveryPolicy create(Store store, DeliveryPolicyRequest deliveryPolicyRequest) {
        DeliveryPolicy deliveryPolicy = DeliveryPolicy.builder()
                .store(store)
                .minOrderPrice(deliveryPolicyRequest.getMinOrderPrice())
                .freeDeliveryMinPrice(deliveryPolicyRequest.getFreeDeliveryMinPrice())
                .deliveryPrice(deliveryPolicyRequest.getDeliveryPrice())
                .build();
        return deliveryPolicyRepository.save(deliveryPolicy);
    }
}
