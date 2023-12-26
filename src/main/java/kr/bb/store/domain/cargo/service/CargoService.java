package kr.bb.store.domain.cargo.service;


import bloomingblooms.domain.flower.FlowerDto;
import bloomingblooms.domain.flower.StockDto;
import kr.bb.store.domain.cargo.controller.response.RemainingStocksResponse;
import kr.bb.store.domain.cargo.dto.StockInfoDto;
import kr.bb.store.domain.cargo.dto.StockModifyDto;
import kr.bb.store.domain.cargo.entity.FlowerCargo;
import kr.bb.store.domain.cargo.entity.FlowerCargoId;
import kr.bb.store.domain.cargo.exception.FlowerCargoNotFoundException;
import kr.bb.store.domain.cargo.exception.StockCannotBeNegativeException;
import kr.bb.store.domain.cargo.repository.FlowerCargoRepository;
import kr.bb.store.domain.store.entity.Store;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CargoService {
    private final FlowerCargoRepository flowerCargoRepository;
    private static final Long EMPTY_COUNT = 0L;

    @Transactional
    public void modifyAllStocks(Long storeId, List<StockModifyDto> stockModifyDtos) {
        stockModifyDtos.forEach(stockModifyDto -> {
            if(stockModifyDto.getStock() < EMPTY_COUNT) {
                throw new StockCannotBeNegativeException();
            }
            flowerCargoRepository.modifyStock(storeId, stockModifyDto.getFlowerId(), stockModifyDto.getStock());
        });
    }

    @Transactional
    public Long plusStockCounts(Long storeId, List<StockDto> stockDtos) {
        return stockDtos.stream()
                .map(stockDto -> {
                    Long stock = stockDto.getStock();
                    FlowerCargoId flowerCargoId = makeId(storeId, stockDto.getFlowerId());
                    FlowerCargo flowerCargo = flowerCargoRepository.findById(flowerCargoId)
                            .orElseThrow(FlowerCargoNotFoundException::new);

                    if (flowerCargo.getStock() < -stock) {
                        throw new StockCannotBeNegativeException();
                    } else {
                        flowerCargoRepository.plusStock(flowerCargoId.getStoreId(), flowerCargoId.getFlowerId(), stock);
                        return flowerCargo.getStock() + stock;
                    }

                })
                .min(Long::compare)
                .orElse(Long.MAX_VALUE);
    }

    @Transactional
    public Long minusStockCounts(Long storeId, List<StockDto> stockDtos) {
        return stockDtos.stream()
                .map(stockDto -> {
                    Long stock = stockDto.getStock();
                    FlowerCargoId flowerCargoId = makeId(storeId, stockDto.getFlowerId());
                    FlowerCargo flowerCargo = flowerCargoRepository.findById(flowerCargoId)
                            .orElseThrow(FlowerCargoNotFoundException::new);

                    if (flowerCargo.getStock() < stock) {
                        throw new StockCannotBeNegativeException();
                    } else {
                        flowerCargoRepository.minusStock(flowerCargoId.getStoreId(), flowerCargoId.getFlowerId(), stock);
                        return flowerCargo.getStock() - stock;
                    }

                })
                .min(Long::compare)
                .orElse(Long.MAX_VALUE);
    }

    @Transactional(readOnly = true)
    public RemainingStocksResponse getAllStocks(Long storeId) {
        List<FlowerCargo> flowerCargos = flowerCargoRepository.findAllByStoreId(storeId);
        List<StockInfoDto> stockInfoDtos = flowerCargos.stream()
                .map(StockInfoDto::fromEntity)
                .collect(Collectors.toList());

        return RemainingStocksResponse.from(stockInfoDtos);
    }

    @Transactional
    public void createBasicCargo(Store store, List<FlowerDto> flowers) {
        List<FlowerCargo> flowerCargos = flowers.stream()
                .map(flowerDto -> FlowerCargo.builder()
                        .id(makeId(store.getId(), flowerDto.getFlowerId()))
                        .store(store)
                        .flowerName(flowerDto.getFlowerName())
                        .build()
                )
                .collect(Collectors.toList());
        flowerCargoRepository.saveAll(flowerCargos);
    }

    private FlowerCargoId makeId(Long storeId, Long flowerId) {
        return FlowerCargoId.builder()
                .storeId(storeId)
                .flowerId(flowerId)
                .build();
    }

    private String makeRedissonKey(Long storeId, Long flowerId) {
        return "s" + storeId + "f" + flowerId;
    }
}
