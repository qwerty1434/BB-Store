package kr.bb.store.domain.store.dto;

import kr.bb.store.domain.store.entity.address.Gugun;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GugunDto {
    private String value;
    private String label;
    public static GugunDto fromEntity(Gugun gugun) {
        return GugunDto.builder()
                .value(gugun.getCode())
                .label(gugun.getName())
                .build();
    }

}
