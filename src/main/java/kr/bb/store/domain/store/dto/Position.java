package kr.bb.store.domain.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Position {
    private Double lat;
    private Double lon;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return Objects.equals(lat, position.lat) && Objects.equals(lon, position.lon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lat, lon);
    }
}
