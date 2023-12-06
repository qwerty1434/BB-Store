package kr.bb.store.domain.subscription.service;

import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.repository.StoreRepository;
import kr.bb.store.domain.subscription.controller.request.SubscriptionCreateRequest;
import kr.bb.store.domain.subscription.entity.Subscription;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class SubscriptionServiceTest {
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private StoreRepository storeRepository;

    @DisplayName("구독생성 정보를 받아 구독을 생성한다")
    @Test
    void createSubscription() {
        // given
        Store store = createStore();
        storeRepository.save(store);
        SubscriptionCreateRequest request = createRequest(store.getId());

        // when
        Subscription subscription = subscriptionService.createSubscription(request);

        // then
        assertThat(subscription.getId()).isNotNull();

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

    private SubscriptionCreateRequest createRequest(Long storeId) {
        return SubscriptionCreateRequest.builder()
                .orderSubscriptionId(1L)
                .storeId(storeId)
                .userId(1L)
                .subscriptionProductId("1")
                .deliveryDate(LocalDate.now())
                .build();
    }
}