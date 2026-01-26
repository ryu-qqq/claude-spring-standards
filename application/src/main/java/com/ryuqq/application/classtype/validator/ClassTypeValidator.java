package com.ryuqq.application.classtype.validator;

import com.ryuqq.application.classtype.manager.ClassTypeReadManager;
import com.ryuqq.domain.classtype.aggregate.ClassType;
import com.ryuqq.domain.classtype.exception.ClassTypeDuplicateCodeException;
import com.ryuqq.domain.classtype.exception.ClassTypeNotFoundException;
import com.ryuqq.domain.classtype.id.ClassTypeId;
import com.ryuqq.domain.classtype.vo.ClassTypeCode;
import com.ryuqq.domain.classtypecategory.id.ClassTypeCategoryId;
import org.springframework.stereotype.Component;

/**
 * ClassTypeValidator - ClassType 검증기
 *
 * <p>ClassType 생성/수정 시 비즈니스 규칙을 검증합니다.
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
public class ClassTypeValidator {

    private final ClassTypeReadManager classTypeReadManager;

    public ClassTypeValidator(ClassTypeReadManager classTypeReadManager) {
        this.classTypeReadManager = classTypeReadManager;
    }

    /**
     * ClassType 조회 및 존재 여부 검증
     *
     * <p>APP-VAL-001: 검증 성공 시 조회한 Domain 객체를 반환합니다.
     *
     * @param id ClassType ID (VO)
     * @return ClassType 조회된 도메인 객체
     * @throws ClassTypeNotFoundException 존재하지 않는 경우
     */
    public ClassType findExistingOrThrow(ClassTypeId id) {
        return classTypeReadManager
                .findById(id)
                .orElseThrow(() -> new ClassTypeNotFoundException(id.value()));
    }

    /**
     * 카테고리 내 코드 중복 검증 (생성 시)
     *
     * <p>MGR-001: 파라미터는 원시타입 대신 VO를 사용합니다.
     *
     * @param categoryId 카테고리 ID (VO)
     * @param code 클래스 타입 코드 (VO)
     * @throws ClassTypeDuplicateCodeException 코드가 중복된 경우
     */
    public void validateCodeNotDuplicated(ClassTypeCategoryId categoryId, ClassTypeCode code) {
        if (classTypeReadManager.existsByCategoryIdAndCode(categoryId, code)) {
            throw new ClassTypeDuplicateCodeException(code.value(), categoryId.value());
        }
    }

    /**
     * 카테고리 내 코드 중복 검증 (수정 시, 자신 제외)
     *
     * <p>MGR-001: 파라미터는 원시타입 대신 VO를 사용합니다.
     *
     * @param categoryId 카테고리 ID (VO)
     * @param code 클래스 타입 코드 (VO)
     * @param excludeId 제외할 클래스 타입 ID (VO)
     * @throws ClassTypeDuplicateCodeException 코드가 중복된 경우
     */
    public void validateCodeNotDuplicatedExcluding(
            ClassTypeCategoryId categoryId, ClassTypeCode code, ClassTypeId excludeId) {
        if (classTypeReadManager.existsByCategoryIdAndCodeAndIdNot(categoryId, code, excludeId)) {
            throw new ClassTypeDuplicateCodeException(code.value(), categoryId.value());
        }
    }
}
