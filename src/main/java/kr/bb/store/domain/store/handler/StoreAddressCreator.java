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
import kr.bb.store.domain.store.dto.StoreAddressRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class StoreAddressCreator {
    private final StoreAddressRepository storeAddressRepository;


    public StoreAddress create(Sido sido, Gugun gugun, Store store, StoreAddressRequest storeAddressRequest) {
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
