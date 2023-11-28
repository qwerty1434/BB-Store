package kr.bb.store.domain.store.handler;

import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.exception.CannotOwnMultipleStoreException;
import kr.bb.store.domain.store.dto.StoreRequest;
import kr.bb.store.domain.store.repository.StoreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class StoreCreatorTest {
    @Autowired
    private StoreCreator storeCreator;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private EntityManager em;

    @DisplayName("회원 번호를 전달받아 가게를 생성한다")
    @Test
    void createStore() {
        // given
        Long userId = 1L;
        StoreRequest storeRequest = createStoreRequest();

        // when
        Store store = storeCreator.create(userId, storeRequest);

        // then
        assertThat(store.getId()).isNotNull();
        assertThat(store.getStoreManagerId()).isEqualTo(userId);
    }

    @DisplayName("처음 생성된 가게의 평균별점은 null이 아닌 기본값이 들어간다")
    @Test
    void storeHasBasicProperties() {
        // given
        Long userId = 1L;
        StoreRequest storeRequest = createStoreRequest();

        // when
        Store store = storeCreator.create(userId, storeRequest);
        em.flush();
        em.clear();

        Store savedStore = storeRepository.findById(store.getId()).get();

        // then
        assertThat(savedStore.getAverageRating()).isNotNull().isEqualTo(0.0F);

    }

    @DisplayName("가게사장은 둘 이상의 가게를 가질 수 없다")
    @Test
    void userCanCreateOnlyOneStore() {
        // given
        Long userId = 1L;
        StoreRequest storeRequest = createStoreRequest();

        // when // then
        assertThatThrownBy(() -> {
            storeCreator.create(userId, storeRequest);
            storeCreator.create(userId, storeRequest);
        }).isInstanceOf(CannotOwnMultipleStoreException.class)
            .hasMessage("둘 이상의 가게를 생성할 수 없습니다.");

    }

    private StoreRequest createStoreRequest() {
        return StoreRequest.builder()
                .storeName("가게1")
                .detailInfo("가게 상세정보")
                .storeThumbnailImage("가게 썸네일")
                .phoneNumber("가게 전화번호")
                .accountNumber("가게 계좌정보")
                .bank("가게 계좌 은행정보")
                .build();
    }
}