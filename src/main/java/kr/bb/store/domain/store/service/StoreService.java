package kr.bb.store.domain.store.service;

import bloomingblooms.domain.flower.FlowerDto;
import kr.bb.store.client.StoreLikeFeignClient;
import kr.bb.store.client.StoreSubscriptionFeignClient;
import kr.bb.store.client.dto.StoreInfoDto;
import kr.bb.store.domain.cargo.service.CargoService;
import kr.bb.store.domain.store.controller.request.StoreCreateRequest;
import kr.bb.store.domain.store.controller.request.StoreInfoEditRequest;
import kr.bb.store.domain.store.controller.response.*;
import kr.bb.store.domain.store.dto.GugunDto;
import kr.bb.store.domain.store.dto.SidoDto;
import kr.bb.store.domain.store.entity.DeliveryPolicy;
import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.entity.StoreAddress;
import kr.bb.store.domain.store.entity.address.Gugun;
import kr.bb.store.domain.store.entity.address.Sido;
import kr.bb.store.domain.store.handler.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class StoreService {
    private final StoreCreator storeCreator;
    private final StoreManager storeManager;
    private final StoreReader storeReader;
    private final SidoReader sidoReader;
    private final GugunReader gugunReader;
    private final CargoService cargoService;


    @Transactional
    public Long createStore(Long userId, StoreCreateRequest storeCreateRequest, List<FlowerDto> flowers) {
        Sido sido = sidoReader.readSidoByName(storeCreateRequest.getSido());
        Gugun gugun = gugunReader.readGugunCorrespondingSido(sido, storeCreateRequest.getGugun());
        Store store = storeCreator.create(userId, storeCreateRequest, sido, gugun);
        cargoService.createBasicCargo(store, flowers);
        return store.getId();
    }

    @Transactional
    public void editStoreInfo(Long storeId, StoreInfoEditRequest storeInfoEditRequest) {
        Store store = storeReader.findStoreById(storeId);
        StoreAddress storeAddress = storeReader.findStoreAddressByStoreId(storeId);
        DeliveryPolicy deliveryPolicy = storeReader.findDeliveryPolicyByStoreId(storeId);
        Sido sido = sidoReader.readSidoByName(storeInfoEditRequest.getSido());
        Gugun gugun = gugunReader.readGugunCorrespondingSido(sido, storeInfoEditRequest.getGugun());
        storeManager.edit(store, storeAddress, deliveryPolicy, sido, gugun, storeInfoEditRequest);
    }

    public StoreDetailInfoResponse getStoreInfo(Long storeId) {
        return storeReader.readDetailInfo(storeId);
    }


    public Page<StoreListResponse> getStoresWithPaging(Pageable pageable) {
        return  storeReader.readStoresWithPaging(pageable);
    }

    public StoreInfoUserResponse getStoreInfoForUser(Long storeId, Boolean isLiked, Boolean isSubscribed, String subscriptionProductId) {
        return storeReader.readForUser(storeId, isLiked, isSubscribed, subscriptionProductId);
    }

    public StoreInfoManagerResponse getStoreInfoForManager(Long storeId) {
        return storeReader.readForManager(storeId);
    }

    public StoreListForMapResponse getNearbyStores(Double lat, Double lon, Integer level) {
        return storeReader.getNearbyStores(lat, lon, level);
    }
    public StoreListForMapResponse getStoresWithRegion(Long userId, String sidoCode, String gugunCode) {
        Sido sido = sidoReader.readSido(sidoCode);
        Gugun gugun = "".equals(gugunCode) ? null : gugunReader.readGugunCorrespondingSidoWithCode(sido, gugunCode);
        return storeReader.getStoresWithRegion(sido, gugun);
    }

    public Long getStoreId(Long userId) {
        return storeReader.getStoreByUserId(userId).getId();
    }

    public StoreInfoDto getStoreNameAndAddress(Long storeId) {
        Store store = storeReader.read(storeId);
        StoreAddress storeAddress = storeReader.readAddress(storeId);
        return StoreInfoDto.of(store, storeAddress);
    }


    public List<SidoDto> getSido() {
        return sidoReader.readAll()
                .stream()
                .map(SidoDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<GugunDto> getGugun(String sidoCode) {
        return gugunReader.readGuguns(sidoCode)
                .stream()
                .map(GugunDto::fromEntity)
                .collect(Collectors.toList());
    }
}
