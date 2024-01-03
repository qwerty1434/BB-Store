package kr.bb.store.domain.cargo.facade;

import bloomingblooms.domain.flower.StockChangeDto;
import bloomingblooms.domain.flower.StockDto;
import kr.bb.store.domain.RedisContainerTestEnv;
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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
class CargoFacadeTest extends RedisContainerTestEnv {
    @Autowired
    private CargoFacade cargoFacade;

    @Autowired
    private FlowerCargoRepository flowerCargoRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private PlatformTransactionManager txManager;


    @DisplayName("꽃 아이디와 수량을 입력받아 재고를 변경한다")
    @Transactional
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
        cargoFacade.modifyAllStocksWithLock(store.getId(), List.of(s1,s2,s3));

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


    @DisplayName("재고 증가는 여러 쓰레드에서 동시에 요청해도 정상적으로 동작한다")
    @Test
    void StockPlusRedissonLockTest() throws InterruptedException, ExecutionException {
        // given
        final int concurrentRequestCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(concurrentRequestCount);
        final Long flowerId = 1L;

        Future<Long> storeCreate = executorService.submit(() -> {
            TransactionStatus status = txManager.getTransaction(new DefaultTransactionAttribute());
            Store store = createStore();
            storeRepository.save(store);
            FlowerCargoId flowerCargoId = createFlowerCargoId(store.getId(), flowerId);
            FlowerCargo flowerCargo = createFlowerCargo(flowerCargoId, 0L, "장미", store);
            flowerCargoRepository.save(flowerCargo);
            txManager.commit(status);
            return store.getId();
        });

        final Long storeId = storeCreate.get();

        // when
        LongStream.rangeClosed(1L, concurrentRequestCount)
                .forEach( idx -> executorService.submit(() -> {
                    try {
                        StockDto stockDto = StockDto.builder()
                                .flowerId(flowerId)
                                .stock(1L)
                                .build();
                        StockChangeDto stockChangeDto = StockChangeDto
                                .builder()
                                .storeId(storeId)
                                .stockDtos(List.of(stockDto))
                                .build();

                        cargoFacade.plusStocksWithLock(idx, List.of(stockChangeDto));
                    } catch (Exception ignored) {
                    } finally {
                        latch.countDown();
                    }
                }));

        latch.await();
        FlowerCargoId flowerCargoId = createFlowerCargoId(storeId, flowerId);
        FlowerCargo result = flowerCargoRepository.findById(flowerCargoId).get();

        // then
        assertThat(result.getStock()).isEqualTo(concurrentRequestCount);

    }

    @DisplayName("재고 감소는 여러 쓰레드에서 동시에 요청해도 정상적으로 동작한다")
    @Test
    void StockMinusRedissonLockTest() throws InterruptedException, ExecutionException {
        // given
        final int concurrentRequestCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(concurrentRequestCount);
        final Long flowerId1 = 1L;
        final Long flowerId2 = 2L;

        Future<Long> storeCreate = executorService.submit(() -> {
            TransactionStatus status = txManager.getTransaction(new DefaultTransactionAttribute());
            Store store = createStore();
            storeRepository.save(store);
            FlowerCargoId flowerCargoId1 = createFlowerCargoId(store.getId(), flowerId1);
            FlowerCargo flowerCargo1 = createFlowerCargo(flowerCargoId1, 100L, "장미", store);
            FlowerCargoId flowerCargoId2 = createFlowerCargoId(store.getId(), flowerId2);
            FlowerCargo flowerCargo2 = createFlowerCargo(flowerCargoId2, 100L, "해바라기", store);
            flowerCargoRepository.saveAll(List.of(flowerCargo1, flowerCargo2));
            txManager.commit(status);
            return store.getId();
        });

        final Long storeId = storeCreate.get();

        // when
        LongStream.rangeClosed(1L, concurrentRequestCount)
                .forEach( idx -> executorService.submit(() -> {
                    try {
                        StockDto stockDto1 = StockDto.builder()
                                .flowerId(flowerId1)
                                .stock(1L)
                                .build();
                        StockDto stockDto2 = StockDto.builder()
                                .flowerId(flowerId2)
                                .stock(1L)
                                .build();
                        StockChangeDto stockChangeDto = StockChangeDto
                                .builder()
                                .storeId(storeId)
                                .stockDtos(List.of(stockDto1,stockDto2))
                                .build();

                        cargoFacade.minusStocksWithLock(idx, List.of(stockChangeDto));
                    } catch (Exception ignored) {
                    } finally {
                        latch.countDown();
                    }
                }));

        latch.await();
        FlowerCargoId flowerCargoId1 = createFlowerCargoId(storeId, flowerId1);
        FlowerCargo result1 = flowerCargoRepository.findById(flowerCargoId1).get();

        FlowerCargoId flowerCargoId2 = createFlowerCargoId(storeId, flowerId2);
        FlowerCargo result2 = flowerCargoRepository.findById(flowerCargoId2).get();

        // then
        assertThat(result1.getStock()).isEqualTo(0);
        assertThat(result2.getStock()).isEqualTo(0);
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