package kr.bb.store.domain.store.entity;

import kr.bb.store.domain.common.BaseEntity;
import lombok.AccessLevel;
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
    @Column(nullable = false, columnDefinition = "varchar(255) default ''")
    private Float averageRating;
    @Column(nullable = false, columnDefinition = "varchar(255) default ''")
    private String phoneNumber;
    @Column(nullable = false, columnDefinition = "varchar(255) default ''")
    private String accountNumber;
    @Column(nullable = false, columnDefinition = "varchar(255) default ''")
    private String bank;

}
