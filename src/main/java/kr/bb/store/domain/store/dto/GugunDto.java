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
    private String code;
    private String name;
    public static GugunDto fromEntity(Gugun gugun) {
        return GugunDto.builder()
                .code(gugun.getCode())
                .name(gugun.getName())
                .build();
    }

}
