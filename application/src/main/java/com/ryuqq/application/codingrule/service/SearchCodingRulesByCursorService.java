package com.ryuqq.application.codingrule.service;

import com.ryuqq.application.codingrule.assembler.CodingRuleAssembler;
import com.ryuqq.application.codingrule.dto.query.CodingRuleSearchParams;
import com.ryuqq.application.codingrule.dto.response.CodingRuleSliceResult;
import com.ryuqq.application.codingrule.factory.query.CodingRuleQueryFactory;
import com.ryuqq.application.codingrule.manager.CodingRuleReadManager;
import com.ryuqq.application.codingrule.port.in.SearchCodingRulesByCursorUseCase;
import com.ryuqq.domain.codingrule.aggregate.CodingRule;
import com.ryuqq.domain.codingrule.query.CodingRuleSliceCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * SearchCodingRulesByCursorService - CodingRule 복합 조건 조회 서비스 (커서 기반)
 *
 * <p>SearchCodingRulesByCursorUseCase를 구현합니다.
 *
 * <p>CodingRule 목록을 커서 기반으로 복합 조건(카테고리, 심각도, 검색)으로 조회합니다.
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
public class SearchCodingRulesByCursorService implements SearchCodingRulesByCursorUseCase {

    private final CodingRuleQueryFactory codingRuleQueryFactory;
    private final CodingRuleReadManager codingRuleReadManager;
    private final CodingRuleAssembler codingRuleAssembler;

    public SearchCodingRulesByCursorService(
            CodingRuleQueryFactory codingRuleQueryFactory,
            CodingRuleReadManager codingRuleReadManager,
            CodingRuleAssembler codingRuleAssembler) {
        this.codingRuleQueryFactory = codingRuleQueryFactory;
        this.codingRuleReadManager = codingRuleReadManager;
        this.codingRuleAssembler = codingRuleAssembler;
    }

    @Override
    public CodingRuleSliceResult execute(CodingRuleSearchParams searchParams) {
        // Factory에서 Criteria 생성 (필터 포함)
        CodingRuleSliceCriteria criteria = codingRuleQueryFactory.createSliceCriteria(searchParams);
        List<CodingRule> codingRules = codingRuleReadManager.findBySliceCriteria(criteria);
        return codingRuleAssembler.toSliceResult(codingRules, searchParams.size());
    }
}
