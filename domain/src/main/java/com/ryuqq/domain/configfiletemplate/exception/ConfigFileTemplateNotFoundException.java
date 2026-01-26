package com.ryuqq.domain.configfiletemplate.exception;

import com.ryuqq.domain.common.exception.DomainException;
import java.util.Map;

/**
 * ConfigFileTemplateNotFoundException - 설정 파일 템플릿 미존재 예외
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public class ConfigFileTemplateNotFoundException extends DomainException {

    public ConfigFileTemplateNotFoundException(Long configFileTemplateId) {
        super(
                ConfigFileTemplateErrorCode.CONFIG_FILE_TEMPLATE_NOT_FOUND,
                String.format("Config file template not found: %d", configFileTemplateId),
                Map.of("configFileTemplateId", configFileTemplateId));
    }
}
