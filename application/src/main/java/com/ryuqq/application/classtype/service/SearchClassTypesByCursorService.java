package com.ryuqq.application.classtype.service;

import com.ryuqq.application.classtype.assembler.ClassTypeAssembler;
import com.ryuqq.application.classtype.dto.query.ClassTypeSearchParams;
import com.ryuqq.application.classtype.dto.response.ClassTypeSliceResult;
import com.ryuqq.application.classtype.factory.query.ClassTypeQueryFactory;
import com.ryuqq.application.classtype.manager.ClassTypeReadManager;
import com.ryuqq.application.classtype.port.in.SearchClassTypesByCursorUseCase;
import com.ryuqq.domain.classtype.aggregate.ClassType;
import com.ryuqq.domain.classtype.query.ClassTypeSliceCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * SearchClassTypesByCursorService - ClassType 커서 기반 검색 서비스
 *
 * <p>SearchClassTypesByCursorUseCase를 구현합니다.
 *
 * <p>SVC-001: @Service 어노테이션 필수.
 *
 * <p>SVC-002: UseCase(Port-In) 인터페이스 구현 필수.
 *
 * <p>SVC-006: @Transactional 금지 → Manager에서 처리.
 *
 * <p>SVC-007: Service에 비즈니스 로직 금지 → 오케스트레이션만.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Service
public class SearchClassTypesByCursorService implements SearchClassTypesByCursorUseCase {

    private final ClassTypeQueryFactory classTypeQueryFactory;
    private final ClassTypeReadManager classTypeReadManager;
    private final ClassTypeAssembler classTypeAssembler;

    public SearchClassTypesByCursorService(
            ClassTypeQueryFactory classTypeQueryFactory,
            ClassTypeReadManager classTypeReadManager,
            ClassTypeAssembler classTypeAssembler) {
        this.classTypeQueryFactory = classTypeQueryFactory;
        this.classTypeReadManager = classTypeReadManager;
        this.classTypeAssembler = classTypeAssembler;
    }

    @Override
    public ClassTypeSliceResult execute(ClassTypeSearchParams params) {
        ClassTypeSliceCriteria criteria = classTypeQueryFactory.createSliceCriteria(params);

        List<ClassType> classTypes = classTypeReadManager.findBySliceCriteria(criteria);

        return classTypeAssembler.toSliceResult(classTypes, params.size());
    }
}
