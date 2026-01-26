package com.ryuqq.application.configfiletemplate.manager;

import com.ryuqq.application.configfiletemplate.port.out.ConfigFileTemplateQueryPort;
import com.ryuqq.domain.configfiletemplate.aggregate.ConfigFileTemplate;
import com.ryuqq.domain.configfiletemplate.id.ConfigFileTemplateId;
import com.ryuqq.domain.configfiletemplate.query.ConfigFileTemplateSliceCriteria;
import com.ryuqq.domain.techstack.id.TechStackId;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * ConfigFileTemplateReadManager - ConfigFileTemplate 조회 관리자
 *
 * <p>QueryPort를 래핑하여 트랜잭션 일관성을 보장합니다.
 *
 * <p>C-004: @Transactional(readOnly=true)은 Manager에서만 메서드 단위로 사용합니다.
 *
 * <p>C-005: Port를 직접 노출하지 않고 Manager로 래핑합니다.
 *
 * <p>MGR-001: 파라미터는 원시타입 대신 VO를 사용합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ConfigFileTemplateReadManager {

    private final ConfigFileTemplateQueryPort configFileTemplateQueryPort;

    public ConfigFileTemplateReadManager(ConfigFileTemplateQueryPort configFileTemplateQueryPort) {
        this.configFileTemplateQueryPort = configFileTemplateQueryPort;
    }

    /**
     * ID로 ConfigFileTemplate 조회
     *
     * @param id ConfigFileTemplate ID (VO)
     * @return ConfigFileTemplate (Optional)
     */
    @Transactional(readOnly = true)
    public Optional<ConfigFileTemplate> findById(ConfigFileTemplateId id) {
        return configFileTemplateQueryPort.findById(id);
    }

    /**
     * ID로 존재 여부 확인
     *
     * @param id ConfigFileTemplate ID (VO)
     * @return 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean existsById(ConfigFileTemplateId id) {
        return configFileTemplateQueryPort.existsById(id);
    }

    /**
     * 커서 기반 슬라이스 조건으로 ConfigFileTemplate 목록 조회
     *
     * @param criteria 슬라이스 조건 (커서 기반)
     * @return ConfigFileTemplate 목록
     */
    @Transactional(readOnly = true)
    public List<ConfigFileTemplate> findBySliceCriteria(ConfigFileTemplateSliceCriteria criteria) {
        return configFileTemplateQueryPort.findBySliceCriteria(criteria);
    }

    /**
     * TechStack에 속한 ConfigFileTemplate 존재 여부 확인
     *
     * @param techStackId TechStack ID (VO)
     * @return 자식 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean existsByTechStackId(TechStackId techStackId) {
        return configFileTemplateQueryPort.existsByTechStackId(techStackId);
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
    @Transactional(readOnly = true)
    public List<ConfigFileTemplate> findForMcp(
            TechStackId techStackId,
            Long architectureId,
            List<com.ryuqq.domain.configfiletemplate.vo.ToolType> toolTypes) {
        return configFileTemplateQueryPort.findForMcp(techStackId, architectureId, toolTypes);
    }
}
