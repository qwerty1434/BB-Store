package kr.bb.store.domain.store.entity.address;

import kr.bb.store.domain.store.dto.GugunDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GugunRepository extends JpaRepository<Gugun,Long> {
    Optional<Gugun> findByName(String name);

    List<Gugun> findGugunBySidoCode(String sidoCode);
}
