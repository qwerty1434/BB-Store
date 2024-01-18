package kr.bb.store.domain.cargo.repository;

import kr.bb.store.domain.cargo.entity.FlowerCargo;
import kr.bb.store.domain.cargo.entity.FlowerCargoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FlowerCargoRepository extends JpaRepository<FlowerCargo, FlowerCargoId> {
    List<FlowerCargo> findAllByStoreId(Long storeId);

    @Modifying
    @Query("update FlowerCargo f set f.stock = :stock " +
            "where f.id.storeId = :storeId and f.id.flowerId = :flowerId")
    void modifyStock(@Param("storeId") Long storeId, @Param("flowerId") Long flowerId, @Param("stock") long stock);

    @Modifying
    @Query("update FlowerCargo f set f.stock = f.stock + :stock " +
            "where f.id.storeId = :storeId and f.id.flowerId = :flowerId")
    void plusStock(@Param("storeId") Long storeId, @Param("flowerId") Long flowerId, @Param("stock") long stock);

    @Modifying
    @Query("update FlowerCargo f set f.stock = f.stock - :stock " +
            "where f.id.storeId = :storeId and f.id.flowerId = :flowerId")
    void minusStock(@Param("storeId") Long storeId, @Param("flowerId") Long flowerId, @Param("stock") long stock);


}
