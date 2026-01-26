package com.ryuqq.adapter.out.persistence.configfiletemplate.adapter;

import com.ryuqq.adapter.out.persistence.configfiletemplate.entity.ConfigFileTemplateJpaEntity;
import com.ryuqq.adapter.out.persistence.configfiletemplate.mapper.ConfigFileTemplateEntityMapper;
import com.ryuqq.adapter.out.persistence.configfiletemplate.repository.ConfigFileTemplateQueryDslRepository;
import com.ryuqq.application.configfiletemplate.port.out.ConfigFileTemplateQueryPort;
import com.ryuqq.domain.configfiletemplate.aggregate.ConfigFileTemplate;
import com.ryuqq.domain.configfiletemplate.id.ConfigFileTemplateId;
import com.ryuqq.domain.configfiletemplate.query.ConfigFileTemplateSliceCriteria;
import com.ryuqq.domain.configfiletemplate.vo.ToolType;
import com.ryuqq.domain.techstack.id.TechStackId;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * ConfigFileTemplateQueryAdapter - ConfigFileTemplate 조회 어댑터
 *
 * <p>ConfigFileTemplateQueryPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>QPRT-002: 표준 메서드를 구현합니다.
 *
 * <p><strong>주의사항:</strong>
 *
 * <ul>
 *   <li>QueryAdapter는 QueryDslRepository에만 의존합니다 (JpaRepository 금지)
 *   <li>JpaRepository는 Command 작업을 수행할 수 있어 QueryAdapter에서 사용하면 안 됩니다
 * </ul>
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ConfigFileTemplateQueryAdapter implements ConfigFileTemplateQueryPort {

    private final ConfigFileTemplateQueryDslRepository queryDslRepository;
    private final ConfigFileTemplateEntityMapper mapper;

    public ConfigFileTemplateQueryAdapter(
            ConfigFileTemplateQueryDslRepository queryDslRepository,
            ConfigFileTemplateEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    /**
     * ID로 ConfigFileTemplate 조회
     *
     * @param id ConfigFileTemplate ID (VO)
     * @return ConfigFileTemplate (Optional)
     */
    @Override
    public Optional<ConfigFileTemplate> findById(ConfigFileTemplateId id) {
        return queryDslRepository.findById(id.value()).map(mapper::toDomain);
    }

    /**
     * ID로 존재 여부 확인
     *
     * @param id ConfigFileTemplate ID (VO)
     * @return 존재 여부
     */
    @Override
    public boolean existsById(ConfigFileTemplateId id) {
        return queryDslRepository.existsById(id.value());
    }

    /**
     * 커서 기반 슬라이스 조건으로 ConfigFileTemplate 목록 조회
     *
     * @param criteria 슬라이스 조건 (커서 기반)
     * @return ConfigFileTemplate 목록
     */
    @Override
    public List<ConfigFileTemplate> findBySliceCriteria(ConfigFileTemplateSliceCriteria criteria) {
        List<ConfigFileTemplateJpaEntity> entities =
                queryDslRepository.findBySliceCriteria(criteria);
        return entities.stream().map(mapper::toDomain).toList();
    }

    /**
     * TechStack에 속한 ConfigFileTemplate 존재 여부 확인
     *
     * <p>TechStack 삭제 시 자식 확인을 위해 사용합니다.
     *
     * @param techStackId TechStack ID (VO)
     * @return 자식 존재 여부
     */
    @Override
    public boolean existsByTechStackId(TechStackId techStackId) {
        return queryDslRepository.existsByTechStackId(techStackId.value());
    }

    /**
     * MCP Tool용 조건 기반 ConfigFileTemplate 목록 조회
     *
     * <p>init_project Tool에서 사용. 페이지네이션 없이 조건에 맞는 전체 목록 반환.
     *
     * @param techStackId TechStack ID (필수)
     * @param architectureId Architecture ID (nullable)
     * @param toolTypes Tool Type 목록 (nullable)
     * @return ConfigFileTemplate 목록 (priority 오름차순 정렬)
     */
    @Override
    public List<ConfigFileTemplate> findForMcp(
            TechStackId techStackId, Long architectureId, List<ToolType> toolTypes) {
        List<String> toolTypeStrings =
                toolTypes != null ? toolTypes.stream().map(ToolType::name).toList() : null;

        List<ConfigFileTemplateJpaEntity> entities =
                queryDslRepository.findByTechStackAndTools(
                        techStackId.value(), architectureId, toolTypeStrings);
        return entities.stream().map(mapper::toDomain).toList();
    }
}
