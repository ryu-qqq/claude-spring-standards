package com.ryuqq.domain.classtypecategory.exception;

import com.ryuqq.domain.common.exception.DomainException;
import java.util.Map;

/**
 * ClassTypeCategoryDuplicateCodeException - 클래스 타입 카테고리 코드 중복 예외
 *
 * @author ryu-qqq
 */
public class ClassTypeCategoryDuplicateCodeException extends DomainException {

    public ClassTypeCategoryDuplicateCodeException(String code, Long architectureId) {
        super(
                ClassTypeCategoryErrorCode.CLASS_TYPE_CATEGORY_DUPLICATE_CODE,
                String.format(
                        "ClassTypeCategory code '%s' already exists for architecture: %d",
                        code, architectureId),
                Map.of("code", code, "architectureId", architectureId));
    }
}
