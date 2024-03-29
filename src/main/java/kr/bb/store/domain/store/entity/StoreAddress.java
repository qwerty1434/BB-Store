package kr.bb.store.domain.store.entity;

import kr.bb.store.domain.common.entity.BaseEntity;
import kr.bb.store.domain.store.entity.address.Gugun;
import kr.bb.store.domain.store.entity.address.Sido;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class StoreAddress extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="store_id")
    private Store store;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="sido_code")
    private Sido sido;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="gugun_code")
    private Gugun gugun;

    @NotNull
    private String address;

    @NotNull
    private String detailAddress;

    @NotNull
    private String zipCode;

    @NotNull
    private Double lat;

    @NotNull
    private Double lon;

    @Builder
    public StoreAddress(Store store, Sido sido, Gugun gugun, String address,
                        String detailAddress, String zipCode, Double lat, Double lon) {
        this.store = store;
        this.sido = sido;
        this.gugun = gugun;
        this.address = address;
        this.detailAddress = detailAddress;
        this.zipCode = zipCode;
        this.lat = lat;
        this.lon = lon;
    }

    public void update(Sido sido, Gugun gugun, String address, String detailAddress,
                       String zipCode, Double lat, Double lon) {
        this.sido = sido;
        this.gugun = gugun;
        this.address = address;
        this.detailAddress = detailAddress;
        this.zipCode = zipCode;
        this.lat = lat;
        this.lon = lon;
    }

}
