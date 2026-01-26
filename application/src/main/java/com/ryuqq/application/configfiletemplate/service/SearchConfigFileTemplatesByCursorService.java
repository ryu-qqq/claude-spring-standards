package com.ryuqq.application.configfiletemplate.service;

import com.ryuqq.application.configfiletemplate.assembler.ConfigFileTemplateAssembler;
import com.ryuqq.application.configfiletemplate.dto.query.ConfigFileTemplateSearchParams;
import com.ryuqq.application.configfiletemplate.dto.response.ConfigFileTemplateSliceResult;
import com.ryuqq.application.configfiletemplate.factory.query.ConfigFileTemplateQueryFactory;
import com.ryuqq.application.configfiletemplate.manager.ConfigFileTemplateReadManager;
import com.ryuqq.application.configfiletemplate.port.in.SearchConfigFileTemplatesByCursorUseCase;
import com.ryuqq.domain.configfiletemplate.aggregate.ConfigFileTemplate;
import com.ryuqq.domain.configfiletemplate.query.ConfigFileTemplateSliceCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * SearchConfigFileTemplatesByCursorService - ConfigFileTemplate 복합 조건 조회 서비스 (커서 기반)
 *
 * <p>SearchConfigFileTemplatesByCursorUseCase를 구현합니다.
 *
 * <p>SVC-001: @Service 어노테이션 필수.
 *
 * <p>SVC-002: UseCase(Port-In) 인터페이스 구현 필수.
 *
 * <p>SVC-003: Domain 객체 직접 생성 금지 → Factory 사용.
 *
 * <p>SVC-005: Domain 객체 직접 반환 금지 → Assembler 사용.
 *
 * <p>SVC-006: @Transactional 금지 → Manager에서 처리.
 *
 * <p>SVC-008: Port(Out) 직접 주입 금지 → Manager 사용.
 *
 * <p>FAC-001: Criteria 생성도 Factory에서 담당합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Service
public class SearchConfigFileTemplatesByCursorService
        implements SearchConfigFileTemplatesByCursorUseCase {

    private final ConfigFileTemplateReadManager configFileTemplateReadManager;
    private final ConfigFileTemplateQueryFactory configFileTemplateQueryFactory;
    private final ConfigFileTemplateAssembler configFileTemplateAssembler;

    public SearchConfigFileTemplatesByCursorService(
            ConfigFileTemplateReadManager configFileTemplateReadManager,
            ConfigFileTemplateQueryFactory configFileTemplateQueryFactory,
            ConfigFileTemplateAssembler configFileTemplateAssembler) {
        this.configFileTemplateReadManager = configFileTemplateReadManager;
        this.configFileTemplateQueryFactory = configFileTemplateQueryFactory;
        this.configFileTemplateAssembler = configFileTemplateAssembler;
    }

    @Override
    public ConfigFileTemplateSliceResult execute(ConfigFileTemplateSearchParams searchParams) {
        ConfigFileTemplateSliceCriteria criteria =
                configFileTemplateQueryFactory.createSliceCriteria(searchParams);

        List<ConfigFileTemplate> configFileTemplates =
                configFileTemplateReadManager.findBySliceCriteria(criteria);
        return configFileTemplateAssembler.toSliceResult(configFileTemplates, searchParams.size());
    }
}
