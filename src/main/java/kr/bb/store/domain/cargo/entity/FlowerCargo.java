package kr.bb.store.domain.cargo.entity;

import kr.bb.store.domain.common.entity.BaseEntity;
import kr.bb.store.domain.store.entity.Store;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

@Entity
@Getter
@Builder
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class FlowerCargo extends BaseEntity {
    @EmbeddedId
    private FlowerCargoId id;

    @MapsId("storeId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="store_id")
    private Store store;

    @Column(nullable = false, columnDefinition = "bigint default 0")
    private Long stock;

    private String flowerName;

}
