package kr.bb.store.domain.store.handler;

import kr.bb.store.domain.store.entity.DeliveryPolicy;
import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.handler.request.DeliveryPolicyRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class DeliveryPolicyCreatorTest {
    @Autowired
    private DeliveryPolicyCreator deliveryPolicyCreator;

    @DisplayName("배송 정책 정보를 전달받아 배송 정책을 생성한다")
    @Test
    void createDeliveryPolicy() {
        // given
        DeliveryPolicyRequest deliveryPolicyRequest = createDeliveryPolicyRequest();
        Store store = createStore();

        // when
        DeliveryPolicy deliveryPolicy = deliveryPolicyCreator.create(store, deliveryPolicyRequest);

        // then
        assertThat(deliveryPolicy.getId()).isNotNull();
    }



    private DeliveryPolicyRequest createDeliveryPolicyRequest() {
        return DeliveryPolicyRequest.builder()
                .minOrderPrice(10_000L)
                .deliveryPrice(5_000L)
                .freeDeliveryMinPrice(10_000L)
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