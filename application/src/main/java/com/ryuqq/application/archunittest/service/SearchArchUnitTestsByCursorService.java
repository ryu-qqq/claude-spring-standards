package com.ryuqq.application.archunittest.service;

import com.ryuqq.application.archunittest.assembler.ArchUnitTestAssembler;
import com.ryuqq.application.archunittest.dto.query.ArchUnitTestSearchParams;
import com.ryuqq.application.archunittest.dto.response.ArchUnitTestSliceResult;
import com.ryuqq.application.archunittest.factory.query.ArchUnitTestQueryFactory;
import com.ryuqq.application.archunittest.manager.ArchUnitTestReadManager;
import com.ryuqq.application.archunittest.port.in.SearchArchUnitTestsByCursorUseCase;
import com.ryuqq.domain.archunittest.aggregate.ArchUnitTest;
import com.ryuqq.domain.archunittest.query.ArchUnitTestSliceCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * SearchArchUnitTestsByCursorService - ArchUnit 테스트 복합 조건 조회 서비스 (커서 기반)
 *
 * <p>SearchArchUnitTestsByCursorUseCase를 구현합니다.
 *
 * <p>SVC-006: @Transactional 금지 → Manager에서 처리.
 *
 * @author ryu-qqq
 */
@Service
public class SearchArchUnitTestsByCursorService implements SearchArchUnitTestsByCursorUseCase {

    private final ArchUnitTestQueryFactory archUnitTestQueryFactory;
    private final ArchUnitTestReadManager archUnitTestReadManager;
    private final ArchUnitTestAssembler archUnitTestAssembler;

    public SearchArchUnitTestsByCursorService(
            ArchUnitTestQueryFactory archUnitTestQueryFactory,
            ArchUnitTestReadManager archUnitTestReadManager,
            ArchUnitTestAssembler archUnitTestAssembler) {
        this.archUnitTestQueryFactory = archUnitTestQueryFactory;
        this.archUnitTestReadManager = archUnitTestReadManager;
        this.archUnitTestAssembler = archUnitTestAssembler;
    }

    @Override
    public ArchUnitTestSliceResult execute(ArchUnitTestSearchParams searchParams) {
        ArchUnitTestSliceCriteria criteria =
                archUnitTestQueryFactory.createSliceCriteria(searchParams);
        List<ArchUnitTest> archUnitTests = archUnitTestReadManager.findBySliceCriteria(criteria);
        return archUnitTestAssembler.toSliceResult(archUnitTests, searchParams.size());
    }
}
