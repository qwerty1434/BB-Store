package kr.bb.store.domain.store.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreListForMapResponse {
    List<StoreForMapResponse> stores;

    public void setLikes(Map<Long,Boolean> storeLikes) {
        stores.forEach(store -> store.setIsLiked(storeLikes.get(store.getStoreId())));
    }

    public List<Long> getStoreIds() {
        return stores.stream()
                .map(StoreForMapResponse::getStoreId)
                .collect(Collectors.toList());
    }

}
