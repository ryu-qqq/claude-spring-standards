package com.ryuqq.domain.classtypecategory.exception;

import com.ryuqq.domain.common.exception.DomainException;
import java.util.Map;

/**
 * ClassTypeCategoryNotFoundException - 클래스 타입 카테고리 미존재 예외
 *
 * @author ryu-qqq
 */
public class ClassTypeCategoryNotFoundException extends DomainException {

    public ClassTypeCategoryNotFoundException(Long categoryId) {
        super(
                ClassTypeCategoryErrorCode.CLASS_TYPE_CATEGORY_NOT_FOUND,
                String.format("ClassTypeCategory not found: %d", categoryId),
                Map.of("categoryId", categoryId));
    }
}
