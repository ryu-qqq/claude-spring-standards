package com.ryuqq.application.mcp.assembler;

import com.ryuqq.application.mcp.dto.response.ConfigFileResult;
import com.ryuqq.application.mcp.dto.response.ConfigFilesResult;
import com.ryuqq.domain.configfiletemplate.aggregate.ConfigFileTemplate;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ConfigFileResultAssembler - ConfigFileTemplate Domain → MCP Result DTO 변환
 *
 * <p>Domain 객체를 MCP Tool용 Response DTO로 변환합니다.
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
public class ConfigFileResultAssembler {

    /**
     * ConfigFileTemplate Domain을 ConfigFileResult로 변환
     *
     * @param template ConfigFileTemplate 도메인 객체
     * @return ConfigFileResult
     */
    public ConfigFileResult toResult(ConfigFileTemplate template) {
        return new ConfigFileResult(
                template.idValue(),
                template.toolTypeName(),
                template.filePathValue(),
                template.fileNameValue(),
                template.descriptionValue(),
                template.contentValue(),
                template.displayOrderValue());
    }

    /**
     * ConfigFileTemplate Domain 목록을 ConfigFileResult 목록으로 변환
     *
     * @param templates ConfigFileTemplate 도메인 객체 목록
     * @return ConfigFileResult 목록
     */
    public List<ConfigFileResult> toResults(List<ConfigFileTemplate> templates) {
        return templates.stream().map(this::toResult).toList();
    }

    /**
     * ConfigFileTemplate Domain 목록을 ConfigFilesResult로 변환
     *
     * @param templates ConfigFileTemplate 도메인 객체 목록
     * @return ConfigFilesResult (목록 + 총 개수)
     */
    public ConfigFilesResult toConfigFilesResult(List<ConfigFileTemplate> templates) {
        List<ConfigFileResult> results = toResults(templates);
        return new ConfigFilesResult(results, results.size());
    }
}
