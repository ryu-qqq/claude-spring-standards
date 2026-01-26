package com.ryuqq.application.layer.validator;

import com.ryuqq.application.layer.manager.LayerReadManager;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.layer.aggregate.Layer;
import com.ryuqq.domain.layer.exception.LayerDuplicateCodeException;
import com.ryuqq.domain.layer.exception.LayerNotFoundException;
import com.ryuqq.domain.layer.id.LayerId;
import com.ryuqq.domain.layer.vo.LayerCode;
import org.springframework.stereotype.Component;

/**
 * LayerValidator - Layer 검증기
 *
 * <p>Layer 생성/수정 시 비즈니스 규칙을 검증합니다.
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
public class LayerValidator {

    private final LayerReadManager layerReadManager;

    public LayerValidator(LayerReadManager layerReadManager) {
        this.layerReadManager = layerReadManager;
    }

    /**
     * Layer 조회 및 존재 여부 검증
     *
     * <p>APP-VAL-001: 검증 성공 시 조회한 Domain 객체를 반환합니다.
     *
     * @param id Layer ID (VO)
     * @return Layer 조회된 도메인 객체
     * @throws LayerNotFoundException 존재하지 않는 경우
     */
    public Layer findExistingOrThrow(LayerId id) {
        return layerReadManager
                .findById(id)
                .orElseThrow(() -> new LayerNotFoundException(id.value()));
    }

    /**
     * 아키텍처 내 코드 중복 검증 (생성 시)
     *
     * <p>MGR-001: 파라미터는 원시타입 대신 VO를 사용합니다.
     *
     * @param architectureId 아키텍처 ID (VO)
     * @param code 레이어 코드 (VO)
     * @throws LayerDuplicateCodeException 코드가 중복된 경우
     */
    public void validateCodeNotDuplicated(ArchitectureId architectureId, LayerCode code) {
        if (layerReadManager.existsByArchitectureIdAndCode(architectureId, code)) {
            throw new LayerDuplicateCodeException(code.value(), architectureId.value());
        }
    }

    /**
     * 아키텍처 내 코드 중복 검증 (수정 시, 자신 제외)
     *
     * <p>MGR-001: 파라미터는 원시타입 대신 VO를 사용합니다.
     *
     * @param architectureId 아키텍처 ID (VO)
     * @param code 레이어 코드 (VO)
     * @param excludeId 제외할 레이어 ID (VO)
     * @throws LayerDuplicateCodeException 코드가 중복된 경우
     */
    public void validateCodeNotDuplicatedExcluding(
            ArchitectureId architectureId, LayerCode code, LayerId excludeId) {
        if (layerReadManager.existsByArchitectureIdAndCodeAndIdNot(
                architectureId, code, excludeId)) {
            throw new LayerDuplicateCodeException(code.value(), architectureId.value());
        }
    }
}
