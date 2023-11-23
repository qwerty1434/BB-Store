package kr.bb.store.domain.cargo.dto;

import kr.bb.store.domain.cargo.entity.FlowerCargo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockInfoDto {
    private Long flowerId;
    private String name;
    private List<Long> data;

    public static StockInfoDto fromEntity(FlowerCargo flowerCargo) {
        return StockInfoDto
                .builder()
                .flowerId(flowerCargo.getId().getFlowerId())
                .name(flowerCargo.getFlowerName())
                .data(List.of(flowerCargo.getStock()))
                .build();
    }
}
