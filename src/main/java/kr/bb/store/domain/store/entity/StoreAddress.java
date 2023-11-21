package kr.bb.store.domain.store.entity;

import kr.bb.store.domain.common.BaseEntity;
import kr.bb.store.domain.common.Gugun;
import kr.bb.store.domain.common.Sido;
import lombok.AccessLevel;
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

    @OneToOne
    @JoinColumn(name="store_id")
    private Store store;

    @OneToOne
    @JoinColumn(name="sido_code")
    private Sido sido;

    @OneToOne
    @JoinColumn(name="gugun_code")
    private Gugun gugun;

    @NotNull
    private String address;

    @NotNull
    private String detailAddress;

    @NotNull
    private String zipCode;

    @NotNull
    private Float lat;

    @NotNull
    private Float lon;

}
