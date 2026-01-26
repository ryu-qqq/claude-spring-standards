package com.ryuqq.application.module.validator;

import com.ryuqq.application.module.manager.ModuleReadManager;
import com.ryuqq.domain.layer.id.LayerId;
import com.ryuqq.domain.module.aggregate.Module;
import com.ryuqq.domain.module.exception.ModuleDuplicateNameException;
import com.ryuqq.domain.module.exception.ModuleNotFoundException;
import com.ryuqq.domain.module.id.ModuleId;
import com.ryuqq.domain.module.vo.ModuleName;
import org.springframework.stereotype.Component;

/**
 * ModuleValidator - Module 검증기
 *
 * <p>Module 생성/수정 시 비즈니스 규칙을 검증합니다.
 *
 * <p>VAL-001: Validator는 @Component 어노테이션 사용.
 *
 * <p>VAL-002: Validator는 {Domain}Validator 네이밍 사용.
 *
 * <p>VAL-003: Validator는 ReadManager만 의존.
 *
 * <p>VAL-004: Validator는 void 반환, 실패 시 DomainException.
 *
 * <p>VAL-005: Validator 메서드는 validateXxx() 또는 checkXxx() 사용.
 *
 * <p>APP-VAL-001: Validator의 findExistingOrThrow 메서드로 Domain 객체를 조회합니다.
 *
 * <p>MGR-001: 파라미터는 원시타입 대신 VO를 사용합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ModuleValidator {

    private final ModuleReadManager moduleReadManager;

    public ModuleValidator(ModuleReadManager moduleReadManager) {
        this.moduleReadManager = moduleReadManager;
    }

    /**
     * Module 조회 및 존재 여부 검증
     *
     * <p>APP-VAL-001: 검증 성공 시 조회한 Domain 객체를 반환합니다.
     *
     * @param id Module ID (VO)
     * @return Module 조회된 도메인 객체
     * @throws ModuleNotFoundException 존재하지 않는 경우
     */
    public Module findExistingOrThrow(ModuleId id) {
        return moduleReadManager.getById(id);
    }

    /**
     * 모듈 이름 중복 검증 (생성 시)
     *
     * @param layerId 레이어 ID
     * @param name 모듈 이름
     * @throws ModuleDuplicateNameException 동일 이름의 모듈이 존재하면
     */
    public void validateNotDuplicate(LayerId layerId, ModuleName name) {
        if (moduleReadManager.existsByLayerIdAndName(layerId, name)) {
            throw new ModuleDuplicateNameException(layerId, name);
        }
    }

    /**
     * 모듈 이름 중복 검증 (수정 시, 자신 제외)
     *
     * @param layerId 레이어 ID
     * @param name 모듈 이름
     * @param excludeModuleId 제외할 모듈 ID
     * @throws ModuleDuplicateNameException 동일 이름의 다른 모듈이 존재하면
     */
    public void validateNotDuplicateExcluding(
            LayerId layerId, ModuleName name, ModuleId excludeModuleId) {
        if (moduleReadManager.existsByLayerIdAndNameExcluding(layerId, name, excludeModuleId)) {
            throw new ModuleDuplicateNameException(layerId, name);
        }
    }

    /**
     * 모듈 삭제 가능 여부 검증
     *
     * @param moduleId 모듈 ID
     * @throws IllegalStateException 자식 모듈이 존재하면
     */
    public void validateDeletable(ModuleId moduleId) {
        if (moduleReadManager.hasChildren(moduleId)) {
            throw new IllegalStateException(
                    "Cannot delete module with children. Module ID: " + moduleId.value());
        }
    }
}
