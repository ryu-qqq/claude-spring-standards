package com.ryuqq.application.classtemplate.service;

import com.ryuqq.application.classtemplate.assembler.ClassTemplateAssembler;
import com.ryuqq.application.classtemplate.dto.query.ClassTemplateSearchParams;
import com.ryuqq.application.classtemplate.dto.response.ClassTemplateSliceResult;
import com.ryuqq.application.classtemplate.factory.query.ClassTemplateQueryFactory;
import com.ryuqq.application.classtemplate.manager.ClassTemplateReadManager;
import com.ryuqq.application.classtemplate.port.in.SearchClassTemplatesByCursorUseCase;
import com.ryuqq.domain.classtemplate.aggregate.ClassTemplate;
import com.ryuqq.domain.classtemplate.query.ClassTemplateSliceCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * SearchClassTemplatesByCursorService - ClassTemplate 복합 조건 조회 서비스 (커서 기반)
 *
 * <p>SearchClassTemplatesByCursorUseCase를 구현합니다.
 *
 * <p>ClassTemplate 목록을 커서 기반으로 복합 조건(패키지 구조 ID, 클래스 타입)으로 조회합니다.
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
 */
@Service
public class SearchClassTemplatesByCursorService implements SearchClassTemplatesByCursorUseCase {

    private final ClassTemplateQueryFactory classTemplateQueryFactory;
    private final ClassTemplateReadManager classTemplateReadManager;
    private final ClassTemplateAssembler classTemplateAssembler;

    public SearchClassTemplatesByCursorService(
            ClassTemplateQueryFactory classTemplateQueryFactory,
            ClassTemplateReadManager classTemplateReadManager,
            ClassTemplateAssembler classTemplateAssembler) {
        this.classTemplateQueryFactory = classTemplateQueryFactory;
        this.classTemplateReadManager = classTemplateReadManager;
        this.classTemplateAssembler = classTemplateAssembler;
    }

    @Override
    public ClassTemplateSliceResult execute(ClassTemplateSearchParams searchParams) {
        // Factory에서 Criteria 생성 (필터 포함)
        ClassTemplateSliceCriteria criteria =
                classTemplateQueryFactory.createSliceCriteria(searchParams);
        List<ClassTemplate> classTemplates = classTemplateReadManager.findBySliceCriteria(criteria);
        return classTemplateAssembler.toSliceResult(classTemplates, searchParams.size());
    }
}
