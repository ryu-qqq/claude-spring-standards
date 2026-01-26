package com.ryuqq.application.configfiletemplate.validator;

import com.ryuqq.application.configfiletemplate.manager.ConfigFileTemplateReadManager;
import com.ryuqq.application.techstack.manager.TechStackReadManager;
import com.ryuqq.domain.configfiletemplate.aggregate.ConfigFileTemplate;
import com.ryuqq.domain.configfiletemplate.exception.ConfigFileTemplateNotFoundException;
import com.ryuqq.domain.configfiletemplate.id.ConfigFileTemplateId;
import com.ryuqq.domain.techstack.exception.TechStackNotFoundException;
import com.ryuqq.domain.techstack.id.TechStackId;
import org.springframework.stereotype.Component;

/**
 * ConfigFileTemplateValidator - ConfigFileTemplate 검증기
 *
 * <p>ConfigFileTemplate 관련 비즈니스 검증을 수행합니다.
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
 * @since 1.0.0
 */
@Component
public class ConfigFileTemplateValidator {

    private final ConfigFileTemplateReadManager configFileTemplateReadManager;
    private final TechStackReadManager techStackReadManager;

    public ConfigFileTemplateValidator(
            ConfigFileTemplateReadManager configFileTemplateReadManager,
            TechStackReadManager techStackReadManager) {
        this.configFileTemplateReadManager = configFileTemplateReadManager;
        this.techStackReadManager = techStackReadManager;
    }

    /**
     * ConfigFileTemplate 존재 여부 검증
     *
     * @param id ConfigFileTemplate ID (VO)
     * @throws ConfigFileTemplateNotFoundException 존재하지 않는 경우
     */
    public void validateExists(ConfigFileTemplateId id) {
        if (!configFileTemplateReadManager.existsById(id)) {
            throw new ConfigFileTemplateNotFoundException(id.value());
        }
    }

    /**
     * ConfigFileTemplate 조회 및 존재 여부 검증
     *
     * <p>APP-VAL-001: 검증 성공 시 조회한 Domain 객체를 반환합니다.
     *
     * @param id ConfigFileTemplate ID (VO)
     * @return ConfigFileTemplate 조회된 도메인 객체
     * @throws ConfigFileTemplateNotFoundException 존재하지 않는 경우
     */
    public ConfigFileTemplate findExistingOrThrow(ConfigFileTemplateId id) {
        return configFileTemplateReadManager
                .findById(id)
                .orElseThrow(() -> new ConfigFileTemplateNotFoundException(id.value()));
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
}
