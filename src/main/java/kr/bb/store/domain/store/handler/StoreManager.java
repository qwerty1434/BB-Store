package kr.bb.store.domain.store.handler;

import kr.bb.store.domain.common.entity.Gugun;
import kr.bb.store.domain.common.entity.GugunRepository;
import kr.bb.store.domain.common.entity.Sido;
import kr.bb.store.domain.common.entity.SidoRepository;
import kr.bb.store.domain.store.controller.request.StoreInfoEditRequest;
import kr.bb.store.domain.store.entity.DeliveryPolicy;
import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.entity.StoreAddress;
import kr.bb.store.domain.store.exception.DeliveryPolicyNotFoundException;
import kr.bb.store.domain.store.exception.StoreAddressNotFoundException;
import kr.bb.store.domain.store.exception.StoreNotFoundException;
import kr.bb.store.domain.store.exception.address.GugunNotFoundException;
import kr.bb.store.domain.store.exception.address.SidoNotFoundException;
import kr.bb.store.domain.store.repository.DeliveryPolicyRepository;
import kr.bb.store.domain.store.repository.StoreAddressRepository;
import kr.bb.store.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class StoreManager {
    private final StoreRepository storeRepository;
    private final StoreAddressRepository storeAddressRepository;
    private final DeliveryPolicyRepository deliveryPolicyRepository;
    private final SidoRepository sidoRepository;
    private final GugunRepository gugunRepository;

    public void edit(Long storeId, StoreInfoEditRequest storeInfoEditRequest) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(StoreNotFoundException::new);
        store.update(
                storeInfoEditRequest.getStoreName(),
                storeInfoEditRequest.getDetailInfo(),
                storeInfoEditRequest.getStoreThumbnailImage(),
                storeInfoEditRequest.getPhoneNumber(),
                storeInfoEditRequest.getAccountNumber(),
                storeInfoEditRequest.getBank()
        );

        StoreAddress storeAddress = storeAddressRepository.findByStoreId(storeId)
                .orElseThrow(StoreAddressNotFoundException::new);

        Sido sido = sidoRepository.findByName(storeInfoEditRequest.getSido())
                .orElseThrow(SidoNotFoundException::new);
        Gugun gugun = gugunRepository.findByName(storeInfoEditRequest.getGugun())
                .orElseThrow(GugunNotFoundException::new);

        storeAddress.update(
                sido,
                gugun,
                storeInfoEditRequest.getAddress(),
                storeInfoEditRequest.getDetailAddress(),
                storeInfoEditRequest.getZipCode(),
                storeInfoEditRequest.getLat(),
                storeInfoEditRequest.getLon()
        );

        DeliveryPolicy deliveryPolicy = deliveryPolicyRepository.findByStoreId(storeId)
                .orElseThrow(DeliveryPolicyNotFoundException::new);
        deliveryPolicy.update(
                storeInfoEditRequest.getMinOrderPrice(),
                storeInfoEditRequest.getDeliveryPrice(),
                storeInfoEditRequest.getFreeDeliveryMinPrice()
        );

    }

}
