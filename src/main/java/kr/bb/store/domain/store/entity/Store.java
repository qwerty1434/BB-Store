package kr.bb.store.domain.store.entity;

import kr.bb.store.domain.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import javax.annotation.Nullable;
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

    @NotNull
    private String storeName;

    @NotNull
    private String detailInfo;

    @NotNull
    private String storeThumbnailImage;

    @Column(nullable = false, columnDefinition = "float default 0.0")
    private Double averageRating;

    @NotNull
    private String phoneNumber;

    @NotNull
    private String accountNumber;

    @NotNull
    private String bank;

    @Column(nullable = false, columnDefinition = "bigint default 0")
    private Long monthlySalesRevenue;

    @Builder
    public Store(Long storeManagerId, String storeCode, String storeName, String detailInfo,
                 String storeThumbnailImage, String phoneNumber, String accountNumber, String bank) {
        this.storeManagerId = storeManagerId;
        this.storeCode = storeCode;
        this.storeName = storeName;
        this.detailInfo = detailInfo;
        this.storeThumbnailImage = storeThumbnailImage;
        this.phoneNumber = phoneNumber;
        this.accountNumber = accountNumber;
        this.bank = bank;
    }

    public void update(String storeName, String detailInfo, String storeThumbnailImage,
                       String phoneNumber, String accountNumber, String bank) {
        this.storeName = storeName;
        this.detailInfo = detailInfo;
        this.storeThumbnailImage = storeThumbnailImage;
        this.phoneNumber = phoneNumber;
        this.accountNumber = accountNumber;
        this.bank = bank;
    }

    public void updateAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }
}
