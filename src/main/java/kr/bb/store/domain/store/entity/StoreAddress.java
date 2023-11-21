package kr.bb.store.domain.store.entity;

import kr.bb.store.domain.common.BaseEntity;
import kr.bb.store.domain.common.Gugun;
import kr.bb.store.domain.common.Sido;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
    private String address;
    private String detailAddress;
    private String zipCode;
    private Float lat;
    private Float lon;

}
