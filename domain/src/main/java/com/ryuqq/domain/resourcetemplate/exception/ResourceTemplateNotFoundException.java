package com.ryuqq.domain.resourcetemplate.exception;

import com.ryuqq.domain.common.exception.DomainException;
import java.util.Map;

/**
 * ResourceTemplateNotFoundException - 리소스 템플릿 미존재 예외
 *
 * @author ryu-qqq
 */
public class ResourceTemplateNotFoundException extends DomainException {

    public ResourceTemplateNotFoundException(Long resourceTemplateId) {
        super(
                ResourceTemplateErrorCode.RESOURCE_TEMPLATE_NOT_FOUND,
                String.format("ResourceTemplate not found: %d", resourceTemplateId),
                Map.of("resourceTemplateId", resourceTemplateId));
    }
}
