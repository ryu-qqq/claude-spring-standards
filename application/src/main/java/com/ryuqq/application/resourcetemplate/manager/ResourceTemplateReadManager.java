package com.ryuqq.application.resourcetemplate.manager;

import com.ryuqq.application.resourcetemplate.port.out.ResourceTemplateQueryPort;
import com.ryuqq.domain.module.id.ModuleId;
import com.ryuqq.domain.resourcetemplate.aggregate.ResourceTemplate;
import com.ryuqq.domain.resourcetemplate.exception.ResourceTemplateNotFoundException;
import com.ryuqq.domain.resourcetemplate.id.ResourceTemplateId;
import com.ryuqq.domain.resourcetemplate.query.ResourceTemplateSliceCriteria;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * ResourceTemplateReadManager - 리소스 템플릿 조회 관리자
 *
 * <p>리소스 템플릿 조회 트랜잭션을 관리합니다.
 *
 * <p>MNG-001: Manager는 @Transactional을 메서드에 적용.
 *
 * <p>MNG-003: Manager 파라미터는 VO(Value Object)만 사용.
 *
 * @author ryu-qqq
 */
@Component
public class ResourceTemplateReadManager {

    private final ResourceTemplateQueryPort resourceTemplateQueryPort;

    public ResourceTemplateReadManager(ResourceTemplateQueryPort resourceTemplateQueryPort) {
        this.resourceTemplateQueryPort = resourceTemplateQueryPort;
    }

    /**
     * ID로 리소스 템플릿 조회 (존재하지 않으면 예외)
     *
     * @param resourceTemplateId 리소스 템플릿 ID
     * @return 리소스 템플릿
     * @throws ResourceTemplateNotFoundException 리소스 템플릿이 존재하지 않으면
     */
    @Transactional(readOnly = true)
    public ResourceTemplate getById(ResourceTemplateId resourceTemplateId) {
        return resourceTemplateQueryPort
                .findById(resourceTemplateId)
                .orElseThrow(
                        () -> new ResourceTemplateNotFoundException(resourceTemplateId.value()));
    }

    /**
     * ID로 리소스 템플릿 존재 여부 확인 후 반환
     *
     * @param resourceTemplateId 리소스 템플릿 ID
     * @return 리소스 템플릿 (nullable)
     */
    @Transactional(readOnly = true)
    public ResourceTemplate findById(ResourceTemplateId resourceTemplateId) {
        return resourceTemplateQueryPort.findById(resourceTemplateId).orElse(null);
    }

    /**
     * 슬라이스 조건으로 리소스 템플릿 목록 조회
     *
     * @param criteria 슬라이스 조회 조건
     * @return 리소스 템플릿 목록
     */
    @Transactional(readOnly = true)
    public List<ResourceTemplate> findBySliceCriteria(ResourceTemplateSliceCriteria criteria) {
        return resourceTemplateQueryPort.findBySliceCriteria(criteria);
    }

    /**
     * 모듈 ID로 리소스 템플릿 목록 조회
     *
     * @param moduleId 모듈 ID
     * @return 리소스 템플릿 목록
     */
    @Transactional(readOnly = true)
    public List<ResourceTemplate> findByModuleId(ModuleId moduleId) {
        return resourceTemplateQueryPort.findByModuleId(moduleId);
    }
}
