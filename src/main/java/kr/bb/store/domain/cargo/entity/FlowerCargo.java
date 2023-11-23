package kr.bb.store.domain.cargo.entity;

import kr.bb.store.domain.common.entity.BaseEntity;
import kr.bb.store.domain.store.entity.Store;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class FlowerCargo extends BaseEntity {
    @EmbeddedId
    private FlowerCargoId id;

    @MapsId("storeId")
    @ManyToOne
    @JoinColumn(name="store_id")
    private Store store;

    private Long stock;
}
