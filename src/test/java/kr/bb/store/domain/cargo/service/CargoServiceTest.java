package kr.bb.store.domain.cargo.service;

import kr.bb.store.domain.cargo.dto.StockModifyDto;
import kr.bb.store.domain.cargo.entity.FlowerCargo;
import kr.bb.store.domain.cargo.entity.FlowerCargoId;
import kr.bb.store.domain.cargo.repository.FlowerCargoRepository;
import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.repository.StoreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CargoServiceTest {

    @Autowired
    private CargoService cargoService;

    @Autowired
    private FlowerCargoRepository flowerCargoRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private EntityManager em;

    @DisplayName("꽃 아이디와 수량을 입력받아 재고를 변경한다")
    @Test
    void modifyStock() {
        Store store = createStore();
        storeRepository.save(store);
        FlowerCargo fc1 = createFlowerCargo(store.getId(), 2L, 100L, store);
        FlowerCargo fc2 = createFlowerCargo(store.getId(), 1L, 100L, store);
        FlowerCargo fc3 = createFlowerCargo(store.getId(), 3L, 100L, store);

        flowerCargoRepository.saveAll(List.of(fc1,fc2,fc3));

        StockModifyDto s1 = createStockModifyDto(1L, 3L);
        StockModifyDto s2 = createStockModifyDto(2L, 6L);
        StockModifyDto s3 = createStockModifyDto(3L, 2L);

        cargoService.modifyStock(store.getId(), List.of(s1,s2,s3));

        em.flush();
        em.clear();

        List<FlowerCargo> flowerStocks = flowerCargoRepository.findAllByStoreId(store.getId());
        assertThat(flowerStocks).hasSize(3)
                .extracting("stock")
                .containsExactlyInAnyOrder(
                        3L,6L,2L
                );
    }

    private FlowerCargo createFlowerCargo(Long storeId, Long flowerId, Long stock, Store store) {
        return FlowerCargo.builder()
                .id(createFlowerCargoId(storeId,flowerId))
                .store(store)
                .stock(stock)
                .build();
    }

    private Store createStore() {
        return Store.builder()
                .storeManagerId(1L)
                .storeCode("가게코드")
                .storeName("가게명")
                .detailInfo("가게 상세정보")
                .storeThumbnailImage("가게 썸네일")
                .phoneNumber("가게 전화번호")
                .accountNumber("가게 계좌정보")
                .bank("가게 계좌 은행정보")
                .build();
    }

    private FlowerCargoId createFlowerCargoId(Long storeId, Long flowerId) {
        return FlowerCargoId.builder()
                .storeId(storeId)
                .flowerId(flowerId)
                .build();
    }

    private StockModifyDto createStockModifyDto(Long flowerId, Long stock) {
        return StockModifyDto.builder()
                .flowerId(flowerId)
                .stock(stock)
                .build();
    }

}