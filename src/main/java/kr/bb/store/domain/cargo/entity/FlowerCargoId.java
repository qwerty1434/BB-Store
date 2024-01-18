package kr.bb.store.domain.cargo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class FlowerCargoId implements Serializable{
    private Long storeId;

    private Long flowerId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlowerCargoId that = (FlowerCargoId) o;
        return Objects.equals(storeId, that.storeId) && Objects.equals(flowerId, that.flowerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(storeId, flowerId);
    }

}
