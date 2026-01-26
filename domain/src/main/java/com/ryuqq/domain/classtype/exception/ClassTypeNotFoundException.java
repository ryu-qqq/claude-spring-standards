package com.ryuqq.domain.classtype.exception;

import com.ryuqq.domain.common.exception.DomainException;
import java.util.Map;

/**
 * ClassTypeNotFoundException - 클래스 타입 미존재 예외
 *
 * @author ryu-qqq
 */
public class ClassTypeNotFoundException extends DomainException {

    public ClassTypeNotFoundException(Long classTypeId) {
        super(
                ClassTypeErrorCode.CLASS_TYPE_NOT_FOUND,
                String.format("ClassType not found: %d", classTypeId),
                Map.of("classTypeId", classTypeId));
    }
}
