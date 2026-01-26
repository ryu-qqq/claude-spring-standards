package com.ryuqq.application.resourcetemplate.port.out;

import com.ryuqq.domain.module.id.ModuleId;
import com.ryuqq.domain.resourcetemplate.aggregate.ResourceTemplate;
import com.ryuqq.domain.resourcetemplate.id.ResourceTemplateId;
import com.ryuqq.domain.resourcetemplate.query.ResourceTemplateSliceCriteria;
import java.util.List;
import java.util.Optional;

/**
 * ResourceTemplateQueryPort - 리소스 템플릿 조회 아웃바운드 포트
 *
 * <p>영속성 계층에서 구현합니다.
 *
 * @author ryu-qqq
 */
public interface ResourceTemplateQueryPort {

    /**
     * ID로 리소스 템플릿 조회
     *
     * @param id 리소스 템플릿 ID
     * @return 리소스 템플릿 Optional
     */
    Optional<ResourceTemplate> findById(Long id);

    /**
     * ResourceTemplateId로 리소스 템플릿 조회
     *
     * @param resourceTemplateId 리소스 템플릿 ID
     * @return 리소스 템플릿 Optional
     */
    Optional<ResourceTemplate> findById(ResourceTemplateId resourceTemplateId);

    /**
     * 모듈 ID로 리소스 템플릿 목록 조회
     *
     * @param moduleId 모듈 ID
     * @return 리소스 템플릿 목록
     */
    List<ResourceTemplate> findByModuleId(Long moduleId);

    /**
     * ModuleId 값 객체로 리소스 템플릿 목록 조회
     *
     * @param moduleId 모듈 ID
     * @return 리소스 템플릿 목록
     */
    List<ResourceTemplate> findByModuleId(ModuleId moduleId);

    /**
     * 슬라이스 조건으로 리소스 템플릿 목록 조회
     *
     * @param criteria 슬라이스 조회 조건
     * @return 리소스 템플릿 목록
     */
    List<ResourceTemplate> findBySliceCriteria(ResourceTemplateSliceCriteria criteria);
}
