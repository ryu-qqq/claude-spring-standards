package com.ryuqq.application.classtypecategory.validator;

import com.ryuqq.application.classtypecategory.manager.ClassTypeCategoryReadManager;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.classtypecategory.aggregate.ClassTypeCategory;
import com.ryuqq.domain.classtypecategory.exception.ClassTypeCategoryDuplicateCodeException;
import com.ryuqq.domain.classtypecategory.exception.ClassTypeCategoryNotFoundException;
import com.ryuqq.domain.classtypecategory.id.ClassTypeCategoryId;
import com.ryuqq.domain.classtypecategory.vo.CategoryCode;
import org.springframework.stereotype.Component;

/**
 * ClassTypeCategoryValidator - ClassTypeCategory 검증기
 *
 * <p>ClassTypeCategory 생성/수정 시 비즈니스 규칙을 검증합니다.
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
public class ClassTypeCategoryValidator {

    private final ClassTypeCategoryReadManager classTypeCategoryReadManager;

    public ClassTypeCategoryValidator(ClassTypeCategoryReadManager classTypeCategoryReadManager) {
        this.classTypeCategoryReadManager = classTypeCategoryReadManager;
    }

    /**
     * ClassTypeCategory 조회 및 존재 여부 검증
     *
     * <p>APP-VAL-001: 검증 성공 시 조회한 Domain 객체를 반환합니다.
     *
     * @param id ClassTypeCategory ID (VO)
     * @return ClassTypeCategory 조회된 도메인 객체
     * @throws ClassTypeCategoryNotFoundException 존재하지 않는 경우
     */
    public ClassTypeCategory findExistingOrThrow(ClassTypeCategoryId id) {
        return classTypeCategoryReadManager
                .findById(id)
                .orElseThrow(() -> new ClassTypeCategoryNotFoundException(id.value()));
    }

    /**
     * 아키텍처 내 코드 중복 검증 (생성 시)
     *
     * <p>MGR-001: 파라미터는 원시타입 대신 VO를 사용합니다.
     *
     * @param architectureId 아키텍처 ID (VO)
     * @param code 카테고리 코드 (VO)
     * @throws ClassTypeCategoryDuplicateCodeException 코드가 중복된 경우
     */
    public void validateCodeNotDuplicated(ArchitectureId architectureId, CategoryCode code) {
        if (classTypeCategoryReadManager.existsByArchitectureIdAndCode(architectureId, code)) {
            throw new ClassTypeCategoryDuplicateCodeException(code.value(), architectureId.value());
        }
    }

    /**
     * 아키텍처 내 코드 중복 검증 (수정 시, 자신 제외)
     *
     * <p>MGR-001: 파라미터는 원시타입 대신 VO를 사용합니다.
     *
     * @param architectureId 아키텍처 ID (VO)
     * @param code 카테고리 코드 (VO)
     * @param excludeId 제외할 카테고리 ID (VO)
     * @throws ClassTypeCategoryDuplicateCodeException 코드가 중복된 경우
     */
    public void validateCodeNotDuplicatedExcluding(
            ArchitectureId architectureId, CategoryCode code, ClassTypeCategoryId excludeId) {
        if (classTypeCategoryReadManager.existsByArchitectureIdAndCodeAndIdNot(
                architectureId, code, excludeId)) {
            throw new ClassTypeCategoryDuplicateCodeException(code.value(), architectureId.value());
        }
    }
}
