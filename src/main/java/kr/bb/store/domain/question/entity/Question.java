package kr.bb.store.domain.question.entity;

import kr.bb.store.domain.common.BaseEntity;
import kr.bb.store.domain.store.entity.Store;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Question extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="store_id")
    private Store store;

    @NotNull
    private Long userId;

    @NotNull
    private Long productId;

    @NotNull
    private String title;

    @NotNull
    private String content;

    @NotNull
    private Boolean isSecret;

    @NotNull
    private Boolean isRead;
}
