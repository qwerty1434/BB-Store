package kr.bb.store.domain.question.entity;

import kr.bb.store.domain.common.entity.BaseEntity;
import kr.bb.store.domain.store.entity.Store;
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
public class Question extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="store_id")
    private Store store;

    @NotNull
    private Long userId;

    @NotNull
    private String nickname;

    @NotNull
    private String productId;

    @NotNull
    private String productName;

    @NotNull
    private String title;

    @NotNull
    private String content;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean isSecret;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean isRead;

    @Builder
    public Question(Store store, Long userId, String nickname, String productId, String productName, String title, String content, Boolean isSecret) {
        this.store = store;
        this.userId = userId;
        this.nickname = nickname;
        this.productId = productId;
        this.productName = productName;
        this.title = title;
        this.content = content;
        this.isSecret = isSecret;
    }

    public void check() {
        this.isRead = true;
    }
}
