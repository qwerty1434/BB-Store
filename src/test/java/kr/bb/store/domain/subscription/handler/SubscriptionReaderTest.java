package kr.bb.store.domain.subscription.handler;

import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.repository.StoreRepository;
import kr.bb.store.domain.subscription.entity.Subscription;
import kr.bb.store.domain.subscription.exception.SubscriptionNotFoundException;
import kr.bb.store.domain.subscription.repository.SubscriptionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@Transactional
class SubscriptionReaderTest {
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private SubscriptionReader subscriptionReader;
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private EntityManager em;

    @DisplayName("주문 구독 아이디로 구독 정보를 읽어온다")
    @Test
    public void readByOrderSubscriptionId() {
        // given
        Long orderSubscriptionId = 1L;

        Store store = createStore();
        storeRepository.save(store);

        Subscription subscription = makeSubscription(store, orderSubscriptionId);
        subscriptionRepository.save(subscription);
        em.flush();
        em.clear();

        // when
        Subscription result = subscriptionReader.readByOrderSubscriptionId(orderSubscriptionId);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getOrderSubscriptionId()).isEqualTo(orderSubscriptionId);

    }

    @DisplayName("주문 구독 아이디와 일치하는 정보가 없으면 에러가 발생한다")
    @Test
    public void OccurExceptionWhenInvalidateOrderSubscriptionId() {
        // given
        Long orderSubscriptionId = 1L;

        Store store = createStore();
        storeRepository.save(store);

        Subscription subscription = makeSubscription(store, orderSubscriptionId);
        subscriptionRepository.save(subscription);
        em.flush();
        em.clear();

        Long notExistOrderSubscriptionId = 100L;

        // when // then
        assertThatThrownBy(() -> subscriptionReader.readByOrderSubscriptionId(notExistOrderSubscriptionId))
                .isInstanceOf(SubscriptionNotFoundException.class)
                .hasMessage("구독정보가 존재하지 않습니다.");

    }


    private Subscription makeSubscription(Store store, Long orderSubscriptionId) {
        return Subscription.builder()
                .store(store)
                .orderSubscriptionId(orderSubscriptionId)
                .userId(1L)
                .subscriptionProductId(1L)
                .subscriptionCode("Code")
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