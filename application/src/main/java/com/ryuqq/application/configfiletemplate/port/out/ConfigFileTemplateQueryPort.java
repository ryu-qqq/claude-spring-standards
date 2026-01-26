package com.ryuqq.application.configfiletemplate.port.out;

import com.ryuqq.domain.configfiletemplate.aggregate.ConfigFileTemplate;
import com.ryuqq.domain.configfiletemplate.id.ConfigFileTemplateId;
import com.ryuqq.domain.configfiletemplate.query.ConfigFileTemplateSliceCriteria;
import com.ryuqq.domain.configfiletemplate.vo.ToolType;
import com.ryuqq.domain.techstack.id.TechStackId;
import java.util.List;
import java.util.Optional;

/**
 * ConfigFileTemplateQueryPort - ConfigFileTemplate 조회 Port
 *
 * <p>영속성 계층으로의 ConfigFileTemplate 조회 아웃바운드 포트입니다.
 *
 * <p>QPRT-002: 표준 메서드를 제공합니다.
 *
 * <p>QPRT-004: 원시타입 대신 VO를 파라미터로 사용합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public interface ConfigFileTemplateQueryPort {

    /**
     * ID로 ConfigFileTemplate 조회
     *
     * @param id ConfigFileTemplate ID (VO)
     * @return ConfigFileTemplate (Optional)
     */
    Optional<ConfigFileTemplate> findById(ConfigFileTemplateId id);

    /**
     * ID로 존재 여부 확인
     *
     * @param id ConfigFileTemplate ID (VO)
     * @return 존재 여부
     */
    boolean existsById(ConfigFileTemplateId id);

    /**
     * 커서 기반 슬라이스 조건으로 ConfigFileTemplate 목록 조회
     *
     * @param criteria 슬라이스 조건 (커서 기반)
     * @return ConfigFileTemplate 목록
     */
    List<ConfigFileTemplate> findBySliceCriteria(ConfigFileTemplateSliceCriteria criteria);

    /**
     * TechStack에 속한 ConfigFileTemplate 존재 여부 확인
     *
     * @param techStackId TechStack ID (VO)
     * @return 자식 존재 여부
     */
    boolean existsByTechStackId(TechStackId techStackId);

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
    List<ConfigFileTemplate> findForMcp(
            TechStackId techStackId, Long architectureId, List<ToolType> toolTypes);
}
