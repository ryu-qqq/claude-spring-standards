package com.ryuqq.application.classtemplate.validator;

import com.ryuqq.application.classtemplate.manager.ClassTemplateReadManager;
import com.ryuqq.domain.classtemplate.aggregate.ClassTemplate;
import com.ryuqq.domain.classtemplate.exception.ClassTemplateDuplicateCodeException;
import com.ryuqq.domain.classtemplate.exception.ClassTemplateNotFoundException;
import com.ryuqq.domain.classtemplate.id.ClassTemplateId;
import com.ryuqq.domain.classtemplate.vo.TemplateCode;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import org.springframework.stereotype.Component;

/**
 * ClassTemplateValidator - 클래스 템플릿 검증기
 *
 * <p>클래스 템플릿 비즈니스 규칙을 검증합니다.
 *
 * <p>VLD-001: Validator는 ReadManager만 의존.
 *
 * @author ryu-qqq
 */
@Component
public class ClassTemplateValidator {

    private final ClassTemplateReadManager classTemplateReadManager;

    public ClassTemplateValidator(ClassTemplateReadManager classTemplateReadManager) {
        this.classTemplateReadManager = classTemplateReadManager;
    }

    /**
     * 클래스 템플릿 존재 여부 검증 후 반환 (조회 + 검증 통합)
     *
     * <p>APP-VAL-001: Validator.findExistingOrThrow()로 조회 + 검증 통합.
     *
     * @param classTemplateId 클래스 템플릿 ID
     * @return 존재하는 ClassTemplate
     * @throws ClassTemplateNotFoundException 클래스 템플릿이 존재하지 않으면
     */
    public ClassTemplate findExistingOrThrow(ClassTemplateId classTemplateId) {
        return classTemplateReadManager.getById(classTemplateId);
    }

    /**
     * 템플릿 코드 중복 검증 (생성 시)
     *
     * @param structureId 패키지 구조 ID
     * @param templateCode 템플릿 코드
     * @throws ClassTemplateDuplicateCodeException 동일 코드의 템플릿이 존재하면
     */
    public void validateNotDuplicate(PackageStructureId structureId, TemplateCode templateCode) {
        if (classTemplateReadManager.existsByStructureIdAndTemplateCode(
                structureId, templateCode)) {
            throw new ClassTemplateDuplicateCodeException(structureId, templateCode);
        }
    }

    /**
     * 템플릿 코드 중복 검증 (수정 시, 자신 제외)
     *
     * @param structureId 패키지 구조 ID
     * @param templateCode 템플릿 코드
     * @param excludeClassTemplateId 제외할 클래스 템플릿 ID
     * @throws ClassTemplateDuplicateCodeException 동일 코드의 다른 템플릿이 존재하면
     */
    public void validateNotDuplicateExcluding(
            PackageStructureId structureId,
            TemplateCode templateCode,
            ClassTemplateId excludeClassTemplateId) {
        if (classTemplateReadManager.existsByStructureIdAndTemplateCodeExcluding(
                structureId, templateCode, excludeClassTemplateId)) {
            throw new ClassTemplateDuplicateCodeException(structureId, templateCode);
        }
    }
}
