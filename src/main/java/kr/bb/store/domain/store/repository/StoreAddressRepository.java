package kr.bb.store.domain.store.repository;

import kr.bb.store.domain.store.entity.StoreAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreAddressRepository extends JpaRepository<StoreAddress,Long>,StoreAddressRepositoryCustom {
}
