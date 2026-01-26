package com.ryuqq.domain.classtype.exception;

import com.ryuqq.domain.common.exception.DomainException;
import java.util.Map;

/**
 * ClassTypeDuplicateCodeException - 클래스 타입 코드 중복 예외
 *
 * @author ryu-qqq
 */
public class ClassTypeDuplicateCodeException extends DomainException {

    public ClassTypeDuplicateCodeException(String code, Long categoryId) {
        super(
                ClassTypeErrorCode.CLASS_TYPE_DUPLICATE_CODE,
                String.format(
                        "ClassType code '%s' already exists for category: %d", code, categoryId),
                Map.of("code", code, "categoryId", categoryId));
    }
}
