package com.ryuqq.domain.classtemplate.exception;

import com.ryuqq.domain.classtemplate.vo.TemplateCode;
import com.ryuqq.domain.common.exception.DomainException;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;

/**
 * ClassTemplateDuplicateCodeException - 클래스 템플릿 코드 중복 예외
 *
 * <p>동일한 패키지 구조 내에서 템플릿 코드가 이미 존재할 때 발생합니다.
 *
 * @author ryu-qqq
 */
public class ClassTemplateDuplicateCodeException extends DomainException {

    public ClassTemplateDuplicateCodeException(
            PackageStructureId structureId, TemplateCode templateCode) {
        super(
                ClassTemplateErrorCode.CLASS_TEMPLATE_DUPLICATE_CODE,
                String.format(
                        "Template code '%s' already exists in package structure: %d",
                        templateCode.value(), structureId.value()));
    }
}
