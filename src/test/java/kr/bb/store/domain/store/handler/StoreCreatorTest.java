package kr.bb.store.domain.store.handler;

import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.exception.CannotOwnMultipleStoreException;
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

        // when
        Store store = storeCreator.create(userId);

        // then
        assertThat(store.getId()).isNotNull();
        assertThat(store.getStoreManagerId()).isEqualTo(userId);
    }

    @DisplayName("처음 생성된 가게의 가게명, 가게상세정보, 가게 이미지, 평균별점, " +
            "가게 연락처, 계좌번호, 계좌은행은 null이 아닌 기본값이 들어간다")
    @Test
    void storeHasBasicProperties() {
        // given
        Long userId = 1L;

        // when
        Store store = storeCreator.create(userId);
        em.flush();
        em.clear();

        Store savedStore = storeRepository.findById(store.getId()).get();

        // then
        assertThat(savedStore.getStoreName()).isNotNull().isEqualTo("");
        assertThat(savedStore.getDetailInfo()).isNotNull().isEqualTo("");
        assertThat(savedStore.getStoreThumbnailImage()).isNotNull().isEqualTo("");
        assertThat(savedStore.getPhoneNumber()).isNotNull().isEqualTo("");
        assertThat(savedStore.getAccountNumber()).isNotNull().isEqualTo("");
        assertThat(savedStore.getBank()).isNotNull().isEqualTo("");
        assertThat(savedStore.getAverageRating()).isNotNull().isEqualTo(0.0F);

    }

    @DisplayName("가게사장은 둘 이상의 가게를 가질 수 없다")
    @Test
    void userCanCreateOnlyOneStore() {
        // given
        Long userId = 1L;

        // when // then
        assertThatThrownBy(() -> {
            storeCreator.create(userId);
            storeCreator.create(userId);
        }).isInstanceOf(CannotOwnMultipleStoreException.class)
            .hasMessage("둘 이상의 가게를 생성할 수 없습니다.");

    }
}