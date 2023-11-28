package kr.bb.store.domain.store.handler;

import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.entity.StoreAddress;
import kr.bb.store.domain.store.entity.address.Gugun;
import kr.bb.store.domain.store.entity.address.Sido;
import kr.bb.store.domain.store.repository.StoreAddressRepository;
import kr.bb.store.domain.store.dto.StoreAddressDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class StoreAddressCreator {
    private final StoreAddressRepository storeAddressRepository;


    public StoreAddress create(Sido sido, Gugun gugun, Store store, StoreAddressDto storeAddressDto) {
        StoreAddress storeAddress = StoreAddress.builder()
                .store(store)
                .sido(sido)
                .gugun(gugun)
                .address(storeAddressDto.getAddress())
                .detailAddress(storeAddressDto.getDetailAddress())
                .zipCode(storeAddressDto.getZipCode())
                .lat(storeAddressDto.getLat())
                .lon(storeAddressDto.getLon())
                .build();
        return storeAddressRepository.save(storeAddress);
    }
}
