package com.ryuqq.application.classtemplate.manager;

import com.ryuqq.application.classtemplate.port.out.ClassTemplateCommandPort;
import com.ryuqq.domain.classtemplate.aggregate.ClassTemplate;
import com.ryuqq.domain.classtemplate.id.ClassTemplateId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * ClassTemplatePersistenceManager - 클래스 템플릿 영속화 관리자
 *
 * <p>클래스 템플릿 저장 트랜잭션을 관리합니다.
 *
 * <p>MNG-001: Manager는 @Transactional을 메서드에 적용.
 *
 * @author ryu-qqq
 */
@Component
public class ClassTemplatePersistenceManager {

    private final ClassTemplateCommandPort classTemplateCommandPort;

    public ClassTemplatePersistenceManager(ClassTemplateCommandPort classTemplateCommandPort) {
        this.classTemplateCommandPort = classTemplateCommandPort;
    }

    /**
     * 클래스 템플릿 영속화 (생성 또는 수정)
     *
     * @param classTemplate 영속화할 클래스 템플릿
     * @return 영속화된 클래스 템플릿 ID
     */
    @Transactional
    public ClassTemplateId persist(ClassTemplate classTemplate) {
        return classTemplateCommandPort.persist(classTemplate);
    }
}
