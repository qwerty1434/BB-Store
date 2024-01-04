package kr.bb.store.domain.store.repository;

import kr.bb.store.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store,Long>,StoreRepositoryCustom {
    Optional<Store> findByStoreManagerId(Long userId);

    List<Store> findAllByIdIn(List<Long> storeIds);

}
