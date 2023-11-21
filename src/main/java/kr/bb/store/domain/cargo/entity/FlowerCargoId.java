package kr.bb.store.domain.cargo.entity;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class FlowerCargoId implements Serializable {
    private Long storeId;

    private Long flowerId;
}
