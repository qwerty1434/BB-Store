package kr.bb.store.domain.common.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GugunRepository extends JpaRepository<Gugun,Long> {
    Optional<Gugun> findByName(String name);
}
