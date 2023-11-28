package kr.bb.store.domain.store.handler;

import kr.bb.store.domain.store.entity.address.Gugun;
import kr.bb.store.domain.store.entity.address.GugunRepository;
import kr.bb.store.domain.store.entity.address.Sido;
import kr.bb.store.domain.store.entity.address.SidoRepository;
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

    public void edit(Store store, StoreAddress storeAddress, DeliveryPolicy deliveryPolicy,
                     Sido sido, Gugun gugun, StoreInfoEditRequest storeInfoEditRequest) {

        store.update(
                storeInfoEditRequest.getStoreName(),
                storeInfoEditRequest.getDetailInfo(),
                storeInfoEditRequest.getStoreThumbnailImage(),
                storeInfoEditRequest.getPhoneNumber(),
                storeInfoEditRequest.getAccountNumber(),
                storeInfoEditRequest.getBank()
        );

        storeAddress.update(
                sido,
                gugun,
                storeInfoEditRequest.getAddress(),
                storeInfoEditRequest.getDetailAddress(),
                storeInfoEditRequest.getZipCode(),
                storeInfoEditRequest.getLat(),
                storeInfoEditRequest.getLon()
        );


        deliveryPolicy.update(
                storeInfoEditRequest.getMinOrderPrice(),
                storeInfoEditRequest.getDeliveryPrice(),
                storeInfoEditRequest.getFreeDeliveryMinPrice()
        );

    }

}
