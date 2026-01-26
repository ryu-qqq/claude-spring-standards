package com.ryuqq.application.classtypecategory.port.out;

import com.ryuqq.domain.classtypecategory.aggregate.ClassTypeCategory;

/**
 * ClassTypeCategoryCommandPort - ClassTypeCategory 명령 Port
 *
 * <p>영속성 계층으로의 ClassTypeCategory CUD 아웃바운드 포트입니다.
 *
 * <p>CPRT-002: CommandPort는 persist(Domain) 메서드만 제공합니다.
 *
 * @author ryu-qqq
 */
public interface ClassTypeCategoryCommandPort {

    /**
     * ClassTypeCategory 영속화 (생성/수정/삭제)
     *
     * @param classTypeCategory 영속화할 ClassTypeCategory
     * @return 영속화된 ClassTypeCategory ID
     */
    Long persist(ClassTypeCategory classTypeCategory);
}
