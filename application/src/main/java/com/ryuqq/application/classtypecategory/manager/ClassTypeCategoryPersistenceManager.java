package com.ryuqq.application.classtypecategory.manager;

import com.ryuqq.application.classtypecategory.port.out.ClassTypeCategoryCommandPort;
import com.ryuqq.domain.classtypecategory.aggregate.ClassTypeCategory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * ClassTypeCategoryPersistenceManager - ClassTypeCategory 영속성 관리자
 *
 * <p>CommandPort를 래핑하여 트랜잭션 일관성을 보장합니다.
 *
 * <p>C-004: @Transactional은 Manager에서만 메서드 단위로 사용합니다.
 *
 * <p>C-005: Port를 직접 노출하지 않고 Manager로 래핑합니다.
 *
 * @author ryu-qqq
 */
@Component
public class ClassTypeCategoryPersistenceManager {

    private final ClassTypeCategoryCommandPort classTypeCategoryCommandPort;

    public ClassTypeCategoryPersistenceManager(
            ClassTypeCategoryCommandPort classTypeCategoryCommandPort) {
        this.classTypeCategoryCommandPort = classTypeCategoryCommandPort;
    }

    /**
     * ClassTypeCategory 영속화
     *
     * @param classTypeCategory 영속화할 ClassTypeCategory
     * @return 영속화된 ClassTypeCategory ID
     */
    @Transactional
    public Long persist(ClassTypeCategory classTypeCategory) {
        return classTypeCategoryCommandPort.persist(classTypeCategory);
    }
}
