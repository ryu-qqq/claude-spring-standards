package com.ryuqq.application.techstack.validator;

import com.ryuqq.application.techstack.manager.TechStackReadManager;
import com.ryuqq.domain.techstack.aggregate.TechStack;
import com.ryuqq.domain.techstack.exception.TechStackDuplicateNameException;
import com.ryuqq.domain.techstack.exception.TechStackNotFoundException;
import com.ryuqq.domain.techstack.id.TechStackId;
import com.ryuqq.domain.techstack.vo.TechStackName;
import org.springframework.stereotype.Component;

/**
 * TechStackValidator - TechStack 검증기
 *
 * <p>TechStack 관련 비즈니스 검증을 수행합니다.
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
public class TechStackValidator {

    private final TechStackReadManager techStackReadManager;

    public TechStackValidator(TechStackReadManager techStackReadManager) {
        this.techStackReadManager = techStackReadManager;
    }

    /**
     * TechStack 조회 및 존재 여부 검증
     *
     * <p>APP-VAL-001: 검증 성공 시 조회한 Domain 객체를 반환합니다.
     *
     * @param id TechStack ID (VO)
     * @return TechStack 조회된 도메인 객체
     * @throws TechStackNotFoundException 존재하지 않는 경우
     */
    public TechStack findExistingOrThrow(TechStackId id) {
        return techStackReadManager
                .findById(id)
                .orElseThrow(() -> new TechStackNotFoundException(id.value()));
    }

    /**
     * 이름 중복 검증 (생성 시)
     *
     * @param name 검증할 이름 (VO)
     * @throws TechStackDuplicateNameException 이미 존재하는 경우
     */
    public void validateNameNotDuplicate(TechStackName name) {
        if (techStackReadManager.existsByName(name)) {
            throw new TechStackDuplicateNameException(name.value());
        }
    }

    /**
     * 이름 중복 검증 (수정 시, 자신 제외)
     *
     * @param name 검증할 이름 (VO)
     * @param excludeId 제외할 ID (VO)
     * @throws TechStackDuplicateNameException 다른 TechStack에서 이미 사용 중인 경우
     */
    public void validateNameNotDuplicateExcluding(TechStackName name, TechStackId excludeId) {
        if (techStackReadManager.existsByNameAndIdNot(name, excludeId)) {
            throw new TechStackDuplicateNameException(name.value());
        }
    }
}
