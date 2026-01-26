package com.ryuqq.application.configfiletemplate.assembler;

import com.ryuqq.application.configfiletemplate.dto.response.ConfigFileTemplateResult;
import com.ryuqq.application.configfiletemplate.dto.response.ConfigFileTemplateSliceResult;
import com.ryuqq.domain.common.vo.SliceMeta;
import com.ryuqq.domain.configfiletemplate.aggregate.ConfigFileTemplate;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ConfigFileTemplateAssembler - ConfigFileTemplate Domain → Response DTO 변환
 *
 * <p>Domain 객체를 Response DTO로 변환합니다.
 *
 * <p>SVC-005: Domain 객체 직접 반환 금지 → Assembler를 통해 변환.
 *
 * <p>RDTO-008: Response DTO는 Domain 타입 의존 금지 → Assembler에서 값 추출.
 *
 * <p>C-002: 변환기에서 null 체크 금지.
 *
 * <p>C-003: 변환기에서 기본값 할당 금지.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ConfigFileTemplateAssembler {

    /**
     * ConfigFileTemplate Domain을 ConfigFileTemplateResult로 변환
     *
     * @param configFileTemplate ConfigFileTemplate 도메인 객체
     * @return ConfigFileTemplateResult
     */
    public ConfigFileTemplateResult toResult(ConfigFileTemplate configFileTemplate) {
        return new ConfigFileTemplateResult(
                configFileTemplate.id().value(),
                configFileTemplate.techStackId().value(),
                toNullableLong(configFileTemplate.architectureId()),
                configFileTemplate.toolType().name(),
                configFileTemplate.filePath().value(),
                configFileTemplate.fileName().value(),
                configFileTemplate.content().value(),
                toNullableString(configFileTemplate.category()),
                toNullableDescription(configFileTemplate.description()),
                toNullableVariables(configFileTemplate.variables()),
                configFileTemplate.displayOrderValue(),
                configFileTemplate.isRequired(),
                configFileTemplate.isDeleted(),
                configFileTemplate.createdAt(),
                configFileTemplate.updatedAt());
    }

    /**
     * ConfigFileTemplate Domain 목록을 ConfigFileTemplateResult 목록으로 변환
     *
     * @param configFileTemplates ConfigFileTemplate 도메인 객체 목록
     * @return ConfigFileTemplateResult 목록
     */
    public List<ConfigFileTemplateResult> toResults(List<ConfigFileTemplate> configFileTemplates) {
        return configFileTemplates.stream().map(this::toResult).toList();
    }

    /**
     * ConfigFileTemplate Domain 목록을 ConfigFileTemplateSliceResult로 변환
     *
     * <p>RDTO-009: List 직접 반환 금지 → SliceMeta와 함께 반환합니다.
     *
     * @param configFileTemplates ConfigFileTemplate 도메인 객체 목록
     * @param size 페이지 크기
     * @return ConfigFileTemplateSliceResult
     */
    public ConfigFileTemplateSliceResult toSliceResult(
            List<ConfigFileTemplate> configFileTemplates, int size) {
        List<ConfigFileTemplateResult> content = toResults(configFileTemplates);
        boolean hasNext = content.size() > size;

        if (hasNext) {
            content = content.subList(0, size);
        }

        return new ConfigFileTemplateSliceResult(content, SliceMeta.of(size, hasNext));
    }

    // ==================== Private Helper Methods ====================

    private Long toNullableLong(com.ryuqq.domain.architecture.id.ArchitectureId architectureId) {
        return architectureId != null ? architectureId.value() : null;
    }

    private String toNullableString(
            com.ryuqq.domain.configfiletemplate.vo.TemplateCategory category) {
        return category != null ? category.name() : null;
    }

    private String toNullableDescription(
            com.ryuqq.domain.configfiletemplate.vo.TemplateDescription description) {
        return description != null ? description.value() : null;
    }

    private String toNullableVariables(
            com.ryuqq.domain.configfiletemplate.vo.TemplateVariables variables) {
        return variables != null ? variables.value() : null;
    }
}
