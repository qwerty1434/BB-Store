package kr.bb.store.domain.store.entity;

import kr.bb.store.domain.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@Getter
@Entity
public class Store extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long storeManagerId;

    @NotNull
    private String storeCode;

    @Column(nullable = false, columnDefinition = "varchar(255) default ''")
    private String storeName;

    @Column(nullable = false, columnDefinition = "varchar(255) default ''")
    private String detailInfo;

    @Column(nullable = false, columnDefinition = "varchar(255) default ''")
    private String storeThumbnailImage;

    @Column(nullable = false, columnDefinition = "float default 0.0")
    private Float averageRating;

    @Column(nullable = false, columnDefinition = "varchar(255) default ''")
    private String phoneNumber;

    @Column(nullable = false, columnDefinition = "varchar(255) default ''")
    private String accountNumber;

    @Column(nullable = false, columnDefinition = "varchar(255) default ''")
    private String bank;

    @Builder
    public Store(Long storeManagerId, String storeCode) {
        this.storeManagerId = storeManagerId;
        this.storeCode = storeCode;
    }
}
