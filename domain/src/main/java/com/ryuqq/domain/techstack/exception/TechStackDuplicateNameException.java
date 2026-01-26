package com.ryuqq.domain.techstack.exception;

import com.ryuqq.domain.common.exception.DomainException;
import java.util.Map;

/**
 * TechStackDuplicateNameException - 기술 스택 이름 중복 예외
 *
 * @author ryu-qqq
 */
public class TechStackDuplicateNameException extends DomainException {

    public TechStackDuplicateNameException(String name) {
        super(
                TechStackErrorCode.TECH_STACK_DUPLICATE_NAME,
                String.format("TechStack name already exists: %s", name),
                Map.of("name", name));
    }
}
