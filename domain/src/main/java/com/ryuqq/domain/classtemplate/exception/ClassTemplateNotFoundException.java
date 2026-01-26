package com.ryuqq.domain.classtemplate.exception;

import com.ryuqq.domain.common.exception.DomainException;

/**
 * ClassTemplateNotFoundException - 클래스 템플릿을 찾을 수 없을 때 발생
 *
 * @author ryu-qqq
 */
public class ClassTemplateNotFoundException extends DomainException {

    public ClassTemplateNotFoundException(Long id) {
        super(ClassTemplateErrorCode.CLASS_TEMPLATE_NOT_FOUND, "id: " + id);
    }
}
