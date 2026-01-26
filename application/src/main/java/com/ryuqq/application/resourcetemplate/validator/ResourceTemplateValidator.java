package com.ryuqq.application.resourcetemplate.validator;

import com.ryuqq.application.resourcetemplate.manager.ResourceTemplateReadManager;
import com.ryuqq.domain.resourcetemplate.aggregate.ResourceTemplate;
import com.ryuqq.domain.resourcetemplate.exception.ResourceTemplateNotFoundException;
import com.ryuqq.domain.resourcetemplate.id.ResourceTemplateId;
import org.springframework.stereotype.Component;

/**
 * ResourceTemplateValidator - 리소스 템플릿 검증기
 *
 * <p>리소스 템플릿 비즈니스 규칙을 검증합니다.
 *
 * <p>VLD-001: Validator는 ReadManager만 의존.
 *
 * @author ryu-qqq
 */
@Component
public class ResourceTemplateValidator {

    private final ResourceTemplateReadManager resourceTemplateReadManager;

    public ResourceTemplateValidator(ResourceTemplateReadManager resourceTemplateReadManager) {
        this.resourceTemplateReadManager = resourceTemplateReadManager;
    }

    /**
     * 리소스 템플릿 존재 여부 검증 후 반환 (조회 + 검증 통합)
     *
     * <p>APP-VAL-001: Validator.findExistingOrThrow()로 조회 + 검증 통합.
     *
     * @param resourceTemplateId 리소스 템플릿 ID
     * @return 존재하는 ResourceTemplate
     * @throws ResourceTemplateNotFoundException 리소스 템플릿이 존재하지 않으면
     */
    public ResourceTemplate findExistingOrThrow(ResourceTemplateId resourceTemplateId) {
        return resourceTemplateReadManager.getById(resourceTemplateId);
    }

    /**
     * 리소스 템플릿 존재 여부 검증
     *
     * @param resourceTemplateId 리소스 템플릿 ID
     * @throws ResourceTemplateNotFoundException 리소스 템플릿이 존재하지 않으면
     */
    public void validateExists(ResourceTemplateId resourceTemplateId) {
        resourceTemplateReadManager.getById(resourceTemplateId);
    }
}
