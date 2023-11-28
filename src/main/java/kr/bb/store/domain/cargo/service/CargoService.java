package kr.bb.store.domain.cargo.service;


import kr.bb.store.domain.cargo.controller.response.RemainingStocksResponse;
import kr.bb.store.domain.cargo.dto.StockInfoDto;
import kr.bb.store.domain.cargo.dto.StockModifyDto;
import kr.bb.store.domain.cargo.entity.FlowerCargo;
import kr.bb.store.domain.cargo.entity.FlowerCargoId;
import kr.bb.store.domain.cargo.exception.FlowerCargoNotFoundException;
import kr.bb.store.domain.cargo.repository.FlowerCargoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CargoService {

    private final FlowerCargoRepository flowerCargoRepository;

    @Transactional
    public void modifyAllStocks(Long storeId, List<StockModifyDto> stockModifyDtos) {
        stockModifyDtos.forEach(stockModifyDto -> {
            FlowerCargoId flowerCargoId = makeKeys(storeId, stockModifyDto.getFlowerId());
            FlowerCargo flowerCargo = flowerCargoRepository.findById(flowerCargoId)
                    .orElseThrow(FlowerCargoNotFoundException::new);
            flowerCargo.modifyStock(stockModifyDto.getStock());
        });
    }

    @Transactional
    public void plusStockCount(Long storeId, Long flowerId, Long stock) {
        FlowerCargoId flowerCargoId = makeKeys(storeId,flowerId);
        FlowerCargo flowerCargo = flowerCargoRepository.findById(flowerCargoId)
                .orElseThrow(FlowerCargoNotFoundException::new);
        flowerCargo.updateStock(stock);
    }

    @Transactional
    public void minusStockCount(Long storeId, Long flowerId, Long stock) {
        FlowerCargoId flowerCargoId = makeKeys(storeId,flowerId);
        FlowerCargo flowerCargo = flowerCargoRepository.findById(flowerCargoId)
                .orElseThrow(FlowerCargoNotFoundException::new);
        flowerCargo.updateStock(-1 * stock);
    }

    @Transactional(readOnly = true)
    public RemainingStocksResponse getAllStocks(Long storeId) {
        List<FlowerCargo> flowerCargos = flowerCargoRepository.findAllByStoreId(storeId);
        List<StockInfoDto> stockInfoDtos = flowerCargos.stream()
                .map(StockInfoDto::fromEntity)
                .collect(Collectors.toList());

        return RemainingStocksResponse.builder()
                .stockInfoDtos(stockInfoDtos)
                .build();
    }

    private FlowerCargoId makeKeys(Long storeId, Long flowerId) {
        return FlowerCargoId.builder()
                .storeId(storeId)
                .flowerId(flowerId)
                .build();
    }
}