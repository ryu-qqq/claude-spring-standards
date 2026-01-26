package com.ryuqq.application.resourcetemplate.manager;

import com.ryuqq.application.resourcetemplate.port.out.ResourceTemplateCommandPort;
import com.ryuqq.domain.resourcetemplate.aggregate.ResourceTemplate;
import com.ryuqq.domain.resourcetemplate.id.ResourceTemplateId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * ResourceTemplatePersistenceManager - 리소스 템플릿 영속화 관리자
 *
 * <p>리소스 템플릿 저장 트랜잭션을 관리합니다.
 *
 * <p>MNG-001: Manager는 @Transactional을 메서드에 적용.
 *
 * @author ryu-qqq
 */
@Component
public class ResourceTemplatePersistenceManager {

    private final ResourceTemplateCommandPort resourceTemplateCommandPort;

    public ResourceTemplatePersistenceManager(
            ResourceTemplateCommandPort resourceTemplateCommandPort) {
        this.resourceTemplateCommandPort = resourceTemplateCommandPort;
    }

    /**
     * 리소스 템플릿 영속화 (생성 또는 수정)
     *
     * @param resourceTemplate 영속화할 리소스 템플릿
     * @return 영속화된 리소스 템플릿 ID
     */
    @Transactional
    public ResourceTemplateId persist(ResourceTemplate resourceTemplate) {
        return resourceTemplateCommandPort.persist(resourceTemplate);
    }
}
