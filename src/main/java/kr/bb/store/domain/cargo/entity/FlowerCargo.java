package kr.bb.store.domain.cargo.entity;

import kr.bb.store.domain.common.entity.BaseEntity;
import kr.bb.store.domain.store.entity.Store;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class FlowerCargo extends BaseEntity {
    @EmbeddedId
    private FlowerCargoId id;

    @MapsId("storeId")
    @ManyToOne
    @JoinColumn(name="store_id")
    private Store store;

    private Long stock;

    private String flowerName;

    public void modifyStock(Long stock) {
        this.stock = (stock < 0) ? 0L : stock;
    }

    public void updateStock(Long stock) {
        this.stock = (this.stock + stock < 0) ? 0L : this.stock + stock;
    }
}
