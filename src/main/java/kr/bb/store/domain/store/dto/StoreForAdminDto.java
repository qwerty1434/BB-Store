package kr.bb.store.domain.store.dto;

import kr.bb.store.domain.store.entity.Store;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreForAdminDto {
    private Long key;
    private String storeCode;
    private String storeName;
    private String phoneNumber;
    private String bank;
    private String accountNumber;
    private Double averageRating;
    private Long totalAmount;
    private LocalDate regDate;

    public static StoreForAdminDto fromEntity(Store store) {
        return StoreForAdminDto.builder()
                .key(store.getId())
                .storeCode(store.getStoreCode())
                .storeName(store.getStoreName())
                .phoneNumber(store.getPhoneNumber())
                .bank(store.getBank())
                .accountNumber(store.getAccountNumber())
                .averageRating(store.getAverageRating())
                .totalAmount(store.getMonthlySalesRevenue())
                .regDate(store.getCreatedAt().toLocalDate())
                .build();
    }
}
