package kr.bb.store.domain.store.entity.address;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GugunRepository extends JpaRepository<Gugun,String> {

    List<Gugun> findGugunBySidoCode(String sidoCode);
    Optional<Gugun> findBySidoAndName(Sido sido, String name);
}
