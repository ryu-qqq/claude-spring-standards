package com.ryuqq.application.mcp.service;

import com.ryuqq.application.configfiletemplate.manager.ConfigFileTemplateReadManager;
import com.ryuqq.application.mcp.assembler.ConfigFileResultAssembler;
import com.ryuqq.application.mcp.dto.query.GetConfigFilesQuery;
import com.ryuqq.application.mcp.dto.response.ConfigFilesResult;
import com.ryuqq.application.mcp.port.in.GetConfigFilesForMcpUseCase;
import com.ryuqq.domain.configfiletemplate.aggregate.ConfigFileTemplate;
import com.ryuqq.domain.configfiletemplate.vo.ToolType;
import com.ryuqq.domain.techstack.id.TechStackId;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * GetConfigFilesForMcpService - MCP init_project용 Config Files 조회 서비스
 *
 * <p>MCP Tool에서 설정 파일 템플릿을 조회할 때 사용하는 서비스입니다.
 *
 * <p>SVC-001: @Service 어노테이션 필수.
 *
 * <p>SVC-003: ReadManager를 통한 조회 (Port 직접 사용 금지).
 *
 * <p>SVC-005: Assembler를 통한 DTO 변환.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Service
public class GetConfigFilesForMcpService implements GetConfigFilesForMcpUseCase {

    private final ConfigFileTemplateReadManager configFileTemplateReadManager;
    private final ConfigFileResultAssembler configFileResultAssembler;

    /**
     * GetConfigFilesForMcpService 생성자
     *
     * @param configFileTemplateReadManager ConfigFileTemplate 조회 Manager
     * @param configFileResultAssembler ConfigFileResult 변환 Assembler
     */
    public GetConfigFilesForMcpService(
            ConfigFileTemplateReadManager configFileTemplateReadManager,
            ConfigFileResultAssembler configFileResultAssembler) {
        this.configFileTemplateReadManager = configFileTemplateReadManager;
        this.configFileResultAssembler = configFileResultAssembler;
    }

    /**
     * 설정 파일 템플릿 목록 조회
     *
     * @param query 조회 조건 (도구 타입, 기술 스택, 아키텍처)
     * @return 설정 파일 템플릿 목록
     */
    @Override
    public ConfigFilesResult execute(GetConfigFilesQuery query) {
        TechStackId techStackId = TechStackId.of(query.techStackId());

        List<ToolType> toolTypes =
                query.toolTypes() == null || query.toolTypes().isEmpty()
                        ? null
                        : query.toolTypes().stream().map(ToolType::valueOf).toList();

        List<ConfigFileTemplate> templates =
                configFileTemplateReadManager.findForMcp(
                        techStackId, query.architectureId(), toolTypes);

        return configFileResultAssembler.toConfigFilesResult(templates);
    }
}
