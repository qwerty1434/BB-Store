package kr.bb.store.domain.store.dto;

import kr.bb.store.domain.store.entity.address.Sido;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SidoDto {
    private String code;
    private String name;

    public static SidoDto fromEntity(Sido sido) {
        return SidoDto.builder()
                .code(sido.getCode())
                .name(sido.getName())
                .build();
    }

}
