package kr.bb.store.domain.store.handler;

import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.entity.StoreAddress;
import kr.bb.store.domain.store.entity.address.Gugun;
import kr.bb.store.domain.store.entity.address.GugunRepository;
import kr.bb.store.domain.store.entity.address.Sido;
import kr.bb.store.domain.store.entity.address.SidoRepository;
import kr.bb.store.domain.store.exception.address.GugunNotFoundException;
import kr.bb.store.domain.store.exception.address.SidoNotFoundException;
import kr.bb.store.domain.store.repository.StoreAddressRepository;
import kr.bb.store.domain.store.handler.request.StoreAddressRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class StoreAddressCreator {
    private final StoreAddressRepository storeAddressRepository;
    private final SidoRepository sidoRepository;
    private final GugunRepository gugunRepository;

    public StoreAddress create(Store store, StoreAddressRequest storeAddressRequest) {
        Sido sido = sidoRepository.findByName(storeAddressRequest.getSido())
                .orElseThrow(SidoNotFoundException::new);
        Gugun gugun = gugunRepository.findByName(storeAddressRequest.getGugun())
                .orElseThrow(GugunNotFoundException::new);
        StoreAddress storeAddress = StoreAddress.builder()
                .store(store)
                .sido(sido)
                .gugun(gugun)
                .address(storeAddressRequest.getAddress())
                .detailAddress(storeAddressRequest.getDetailAddress())
                .zipCode(storeAddressRequest.getZipCode())
                .lat(storeAddressRequest.getLat())
                .lon(storeAddressRequest.getLon())
                .build();
        return storeAddressRepository.save(storeAddress);
    }
}
