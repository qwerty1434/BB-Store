package kr.bb.store.domain.common.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SidoRepository extends JpaRepository<Sido,Long> {
    Optional<Sido> findByName(String name);
}
