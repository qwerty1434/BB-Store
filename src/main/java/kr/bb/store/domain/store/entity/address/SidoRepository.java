package kr.bb.store.domain.store.entity.address;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SidoRepository extends JpaRepository<Sido,String> {
    Optional<Sido> findByName(String name);
}
