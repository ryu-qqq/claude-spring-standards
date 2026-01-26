package com.ryuqq.application.architecture.validator;

import com.ryuqq.application.architecture.manager.ArchitectureReadManager;
import com.ryuqq.application.techstack.manager.TechStackReadManager;
import com.ryuqq.domain.architecture.aggregate.Architecture;
import com.ryuqq.domain.architecture.exception.ArchitectureDuplicateNameException;
import com.ryuqq.domain.architecture.exception.ArchitectureNotFoundException;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.architecture.vo.ArchitectureName;
import com.ryuqq.domain.techstack.exception.TechStackNotFoundException;
import com.ryuqq.domain.techstack.id.TechStackId;
import org.springframework.stereotype.Component;

/**
 * ArchitectureValidator - Architecture 검증기
 *
 * <p>Architecture 관련 비즈니스 검증을 수행합니다.
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
 * <p>MGR-001: 파라미터는 원시타입 대신 VO를 사용합니다.
 *
 * @author ryu-qqq
 */
@Component
public class ArchitectureValidator {

    private final ArchitectureReadManager architectureReadManager;
    private final TechStackReadManager techStackReadManager;

    public ArchitectureValidator(
            ArchitectureReadManager architectureReadManager,
            TechStackReadManager techStackReadManager) {
        this.architectureReadManager = architectureReadManager;
        this.techStackReadManager = techStackReadManager;
    }

    /**
     * Architecture 존재 여부 검증
     *
     * @param id Architecture ID (VO)
     * @throws ArchitectureNotFoundException 존재하지 않는 경우
     */
    public void validateExists(ArchitectureId id) {
        if (!architectureReadManager.existsById(id)) {
            throw new ArchitectureNotFoundException(id.value());
        }
    }

    /**
     * Architecture 조회 및 존재 여부 검증
     *
     * <p>APP-VAL-001: 검증 성공 시 조회한 Domain 객체를 반환합니다.
     *
     * @param id Architecture ID (VO)
     * @return Architecture 조회된 도메인 객체
     * @throws ArchitectureNotFoundException 존재하지 않는 경우
     */
    public Architecture findExistingOrThrow(ArchitectureId id) {
        return architectureReadManager
                .findById(id)
                .orElseThrow(() -> new ArchitectureNotFoundException(id.value()));
    }

    /**
     * TechStack 존재 여부 검증 (FK 유효성)
     *
     * @param techStackId TechStack ID (VO)
     * @throws TechStackNotFoundException 존재하지 않는 경우
     */
    public void validateTechStackExists(TechStackId techStackId) {
        if (!techStackReadManager.existsById(techStackId)) {
            throw new TechStackNotFoundException(techStackId.value());
        }
    }

    /**
     * 이름 중복 검증 (생성 시)
     *
     * @param name 검증할 이름 (VO)
     * @throws ArchitectureDuplicateNameException 이미 존재하는 경우
     */
    public void validateNameNotDuplicate(ArchitectureName name) {
        if (architectureReadManager.existsByName(name)) {
            throw new ArchitectureDuplicateNameException(name.value());
        }
    }

    /**
     * 이름 중복 검증 (수정 시, 자신 제외)
     *
     * @param name 검증할 이름 (VO)
     * @param excludeId 제외할 ID (VO)
     * @throws ArchitectureDuplicateNameException 다른 Architecture에서 이미 사용 중인 경우
     */
    public void validateNameNotDuplicateExcluding(ArchitectureName name, ArchitectureId excludeId) {
        if (architectureReadManager.existsByNameAndIdNot(name, excludeId)) {
            throw new ArchitectureDuplicateNameException(name.value());
        }
    }
}
