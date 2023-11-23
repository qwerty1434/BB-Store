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
    void modifyAllStocks() {
        // given
        Store store = createStore();
        storeRepository.save(store);

        FlowerCargoId flowerCargoId1 = createFlowerCargoId(store.getId(),2L);
        FlowerCargoId flowerCargoId2 = createFlowerCargoId(store.getId(),1L);
        FlowerCargoId flowerCargoId3 = createFlowerCargoId(store.getId(),3L);

        FlowerCargo fc1 = createFlowerCargo(flowerCargoId1, 100L, store);
        FlowerCargo fc2 = createFlowerCargo(flowerCargoId2, 100L, store);
        FlowerCargo fc3 = createFlowerCargo(flowerCargoId3, 100L, store);

        flowerCargoRepository.saveAll(List.of(fc1,fc2,fc3));

        StockModifyDto s1 = createStockModifyDto(1L, 3L);
        StockModifyDto s2 = createStockModifyDto(2L, 6L);
        StockModifyDto s3 = createStockModifyDto(3L, 2L);

        // when
        cargoService.modifyAllStocks(store.getId(), List.of(s1,s2,s3));

        em.flush();
        em.clear();

        List<FlowerCargo> flowerStocks = flowerCargoRepository.findAllByStoreId(store.getId());

        // then
        assertThat(flowerStocks).hasSize(3)
                .extracting("stock")
                .containsExactlyInAnyOrder(
                        3L,6L,2L
                );
    }

    @DisplayName("특정 가게, 특정 꽃의 재고를 수정한다")
    @Test
    void modifyStock() {
        // given
        Store store = createStore();
        storeRepository.save(store);
        FlowerCargoId flowerCargoId = createFlowerCargoId(store.getId(),1L);
        FlowerCargo flowerCargo = createFlowerCargo(flowerCargoId, 100L, store);
        flowerCargoRepository.save(flowerCargo);

        // when
        cargoService.PlusStockCount(store.getId(), 1L, 10L);

        em.flush();
        em.clear();

        FlowerCargo flowerCargoFromDB = flowerCargoRepository.findById(flowerCargoId).get();
        // then
        assertThat(flowerCargoFromDB.getStock()).isEqualTo(110L);

    }
    
    @DisplayName("재고는 음수가 될 수 없다")
    @Test
    void stockCannotBeNegative() {
        // given
        Store store = createStore();
        storeRepository.save(store);
        FlowerCargoId flowerCargoId = createFlowerCargoId(store.getId(),1L);
        FlowerCargo flowerCargo = createFlowerCargo(flowerCargoId, 100L, store);
        flowerCargoRepository.save(flowerCargo);

        // when
        cargoService.PlusStockCount(store.getId(), 1L, -10000L);

        em.flush();
        em.clear();

        FlowerCargo flowerCargoFromDB = flowerCargoRepository.findById(flowerCargoId).get();
        // then
        assertThat(flowerCargoFromDB.getStock()).isEqualTo(0L);

    }

    private FlowerCargo createFlowerCargo(FlowerCargoId flowerCargoId, Long stock, Store store) {
        return FlowerCargo.builder()
                .id(flowerCargoId)
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