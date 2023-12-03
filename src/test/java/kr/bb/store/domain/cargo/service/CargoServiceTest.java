package kr.bb.store.domain.cargo.service;

import kr.bb.store.domain.cargo.controller.response.RemainingStocksResponse;
import kr.bb.store.domain.cargo.dto.FlowerDto;
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
import static org.assertj.core.groups.Tuple.tuple;

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

        FlowerCargo fc1 = createFlowerCargo(flowerCargoId1, 100L, "장미", store);
        FlowerCargo fc2 = createFlowerCargo(flowerCargoId2, 100L, "장미", store);
        FlowerCargo fc3 = createFlowerCargo(flowerCargoId3, 100L, "장미", store);

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

    @DisplayName("특정 가게, 특정 꽃의 재고를 더한다")
    @Test
    void addStock() {
        // given
        Store store = createStore();
        storeRepository.save(store);
        FlowerCargoId flowerCargoId = createFlowerCargoId(store.getId(),1L);
        FlowerCargo flowerCargo = createFlowerCargo(flowerCargoId, 100L, "장미", store);
        flowerCargoRepository.save(flowerCargo);

        // when
        cargoService.plusStockCount(store.getId(), 1L, 10L);

        em.flush();
        em.clear();

        FlowerCargo flowerCargoFromDB = flowerCargoRepository.findById(flowerCargoId).get();

        // then
        assertThat(flowerCargoFromDB.getStock()).isEqualTo(110L);

    }
    
    @DisplayName("특정 가게, 특정 꽃의 재고를 차감한다")
    @Test
    void subtractStock() {
        // given
        Store store = createStore();
        storeRepository.save(store);
        FlowerCargoId flowerCargoId = createFlowerCargoId(store.getId(),1L);
        FlowerCargo flowerCargo = createFlowerCargo(flowerCargoId, 100L, "장미", store);
        flowerCargoRepository.save(flowerCargo);

        // when
        cargoService.minusStockCount(store.getId(), 1L, 10L);

        em.flush();
        em.clear();

        FlowerCargo flowerCargoFromDB = flowerCargoRepository.findById(flowerCargoId).get();

        // then
        assertThat(flowerCargoFromDB.getStock()).isEqualTo(90L);

    }

    @DisplayName("재고는 음수가 될 수 없다")
    @Test
    void stockCannotBeNegative() {
        // given
        Store store = createStore();
        storeRepository.save(store);
        FlowerCargoId flowerCargoId = createFlowerCargoId(store.getId(),1L);
        FlowerCargo flowerCargo = createFlowerCargo(flowerCargoId, 100L, "장미", store);
        flowerCargoRepository.save(flowerCargo);

        // when
        cargoService.plusStockCount(store.getId(), 1L, -10000L);

        em.flush();
        em.clear();

        FlowerCargo flowerCargoFromDB = flowerCargoRepository.findById(flowerCargoId).get();
        // then
        assertThat(flowerCargoFromDB.getStock()).isEqualTo(0L);

    }
    @DisplayName("재고는 음수가 될 수 없다")
    @Test
    void stockCannotBeNegative2() {
        // given
        Store store = createStore();
        storeRepository.save(store);

        FlowerCargoId flowerCargoId = createFlowerCargoId(store.getId(),1L);
        FlowerCargo fc1 = createFlowerCargo(flowerCargoId, 100L, "장미", store);

        flowerCargoRepository.saveAll(List.of(fc1));

        StockModifyDto s1 = createStockModifyDto(1L, -3L);

        // when
        cargoService.modifyAllStocks(store.getId(), List.of(s1));

        em.flush();
        em.clear();

        FlowerCargo flowerCargoFromDB = flowerCargoRepository.findById(flowerCargoId).get();

        // then
        assertThat(flowerCargoFromDB.getStock()).isEqualTo(0L);

    }

    @DisplayName("해당 가게의 모든 재고정보를 가져온다")
    @Test
    void getAllStocks() {
        // given
        Store store = createStore();
        storeRepository.save(store);

        FlowerCargoId flowerCargoId1 = createFlowerCargoId(store.getId(),2L);
        FlowerCargoId flowerCargoId2 = createFlowerCargoId(store.getId(),1L);
        FlowerCargoId flowerCargoId3 = createFlowerCargoId(store.getId(),3L);

        FlowerCargo fc1 = createFlowerCargo(flowerCargoId1, 100L, "장미", store);
        FlowerCargo fc2 = createFlowerCargo(flowerCargoId2, 100L, "튤립", store);
        FlowerCargo fc3 = createFlowerCargo(flowerCargoId3, 100L, "백합", store);

        flowerCargoRepository.saveAll(List.of(fc1,fc2,fc3));

        // when
        RemainingStocksResponse stocks = cargoService.getAllStocks(store.getId());

        // then
        assertThat(stocks.getStockInfoDtos()).hasSize(3)
                .extracting("flowerId","name")
                .containsExactlyInAnyOrder(
                        tuple(2L,"장미"),
                        tuple(1L,"튤립"),
                        tuple(3L,"백합")
                );

    }

    @DisplayName("꽃 종류를 입력받아 수량이 0인 기본 정보를 생성한다")
    @Test
    void createBasicCargo() {
        // given
        Store store = createStore();
        storeRepository.save(store);

        FlowerDto dto1 = createFlowerDto(1L, "장미");
        FlowerDto dto2 = createFlowerDto(2L, "국화");
        List<FlowerDto> flowers = List.of(dto1,dto2);

        cargoService.createBasicCargo(store,flowers);
        em.flush();
        em.clear();

        // when
        List<FlowerCargo> result = flowerCargoRepository.findAllByStoreId(store.getId());

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getStock()).isEqualTo(0);
    }

    private FlowerDto createFlowerDto(Long flowerId, String flowerName) {
        return FlowerDto.builder()
                .flowerId(flowerId)
                .flowerName(flowerName)
                .build();
    }
    private FlowerCargo createFlowerCargo(FlowerCargoId flowerCargoId, Long stock, String name, Store store) {
        return FlowerCargo.builder()
                .id(flowerCargoId)
                .store(store)
                .stock(stock)
                .flowerName(name)
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