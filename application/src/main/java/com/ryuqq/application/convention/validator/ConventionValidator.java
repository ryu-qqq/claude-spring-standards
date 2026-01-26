package com.ryuqq.application.convention.validator;

import com.ryuqq.application.convention.manager.ConventionReadManager;
import com.ryuqq.domain.convention.aggregate.Convention;
import com.ryuqq.domain.convention.exception.ConventionDuplicateException;
import com.ryuqq.domain.convention.exception.ConventionNotFoundException;
import com.ryuqq.domain.convention.id.ConventionId;
import com.ryuqq.domain.convention.vo.ConventionVersion;
import com.ryuqq.domain.module.id.ModuleId;
import org.springframework.stereotype.Component;

/**
 * ConventionValidator - Convention 검증기
 *
 * <p>Convention 관련 비즈니스 검증을 수행합니다.
 *
 * <p>VAL-001: Validator는 @Component 어노테이션 사용.
 *
 * <p>VAL-002: Validator는 {Domain}Validator 네이밍 사용.
 *
 * <p>VAL-003: Validator는 ReadManager만 의존.
 *
 * <p>APP-VAL-001: Validator의 findExistingOrThrow 메서드는 검증 성공 시 조회한 Domain 객체를 반환합니다.
 *
 * <p>VAL-005: Validator 메서드는 validateXxx() 또는 findExistingOrThrow() 사용.
 *
 * <p>MGR-001: 파라미터는 원시타입 대신 VO를 사용합니다.
 *
 * @author ryu-qqq
 */
@Component
public class ConventionValidator {

    private final ConventionReadManager conventionReadManager;

    public ConventionValidator(ConventionReadManager conventionReadManager) {
        this.conventionReadManager = conventionReadManager;
    }

    /**
     * Convention 조회 및 존재 여부 검증
     *
     * <p>APP-VAL-001: 검증 성공 시 조회한 Domain 객체를 반환합니다.
     *
     * @param id Convention ID (VO)
     * @return Convention 조회된 도메인 객체
     * @throws ConventionNotFoundException 존재하지 않는 경우
     */
    public Convention findExistingOrThrow(ConventionId id) {
        return conventionReadManager
                .findById(id)
                .orElseThrow(() -> new ConventionNotFoundException(id.value()));
    }

    /**
     * Convention 존재 여부 검증
     *
     * @param id Convention ID (VO)
     * @throws ConventionNotFoundException 존재하지 않는 경우
     */
    public void validateExists(ConventionId id) {
        if (!conventionReadManager.existsById(id)) {
            throw new ConventionNotFoundException(id.value());
        }
    }

    /**
     * 모듈+버전 중복 검증 (생성 시)
     *
     * @param moduleId 모듈 ID (VO)
     * @param version 컨벤션 버전 (VO)
     * @throws ConventionDuplicateException 이미 존재하는 경우
     */
    public void validateNotDuplicate(ModuleId moduleId, ConventionVersion version) {
        if (conventionReadManager.existsByModuleIdAndVersion(moduleId, version.value())) {
            throw new ConventionDuplicateException(moduleId, version.value());
        }
    }

    /**
     * 모듈+버전 중복 검증 (수정 시, 자신 제외)
     *
     * @param moduleId 모듈 ID (VO)
     * @param version 컨벤션 버전 (VO)
     * @param excludeId 제외할 ID (VO)
     * @throws ConventionDuplicateException 다른 Convention에서 이미 사용 중인 경우
     */
    public void validateNotDuplicateExcluding(
            ModuleId moduleId, ConventionVersion version, ConventionId excludeId) {
        if (conventionReadManager.existsByModuleIdAndVersionAndIdNot(
                moduleId, version.value(), excludeId)) {
            throw new ConventionDuplicateException(moduleId, version.value());
        }
    }
}
