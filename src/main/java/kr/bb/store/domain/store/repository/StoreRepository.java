package kr.bb.store.domain.store.repository;

import kr.bb.store.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store,Long>,StoreRepositoryCustom {
}
