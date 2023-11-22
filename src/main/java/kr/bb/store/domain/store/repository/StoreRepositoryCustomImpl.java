package kr.bb.store.domain.store.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.bb.store.domain.store.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.List;

import static kr.bb.store.domain.store.entity.QStore.store;

public class StoreRepositoryCustomImpl implements StoreRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    public StoreRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Store> getStoresWithPaging(Pageable pageable) {
        List<Store> contents = queryFactory.selectFrom(store)
                .where(
                        store.isDeleted.isFalse()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        Long count = queryFactory
                .select(store.id.count())
                .from(store)
                .where(
                        store.isDeleted.isFalse()
                )
                .fetchOne();
        return new PageImpl<>(contents,pageable,count);
    }
}
