package kr.bb.store.domain.subscription.handler;

import kr.bb.store.client.ProductFeignClient;
import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.repository.StoreRepository;
import kr.bb.store.domain.subscription.entity.Subscription;
import kr.bb.store.domain.subscription.repository.SubscriptionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest
@Transactional
class SubscriptionManagerTest {
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private SubscriptionManager subscriptionManager;
    @MockBean
    private ProductFeignClient productFeignClient;

    @DisplayName("구독을 취소한다")
    @Test
    void deleteCoupon() {
        // given
        Store store = createStore();
        storeRepository.save(store);

        Subscription subscription = makeSubscription(store);
        subscriptionRepository.save(subscription);

        // when
        subscriptionManager.softDelete(subscription);

        // then
        assertThat(subscription.getIsDeleted()).isTrue();

    }


    private Subscription makeSubscription(Store store) {
        return Subscription.builder()
                .store(store)
                .orderSubscriptionId(1L)
                .userId(1L)
                .subscriptionProductId("1")
                .subscriptionCode("Code")
                .deliveryDate(LocalDate.now())
                .build();
    }

    private Store createStore() {
        return Store.builder()
                .storeManagerId(1L)
                .storeCode("가게코드")
                .storeName("가게1")
                .detailInfo("가게 상세정보")
                .storeThumbnailImage("가게 썸네일")
                .phoneNumber("가게 전화번호")
                .accountNumber("가게 계좌정보")
                .bank("가게 계좌 은행정보")
                .build();
    }
}