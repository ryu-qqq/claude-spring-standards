package com.ryuqq.application.ruleexample.service;

import com.ryuqq.application.ruleexample.assembler.RuleExampleAssembler;
import com.ryuqq.application.ruleexample.dto.query.RuleExampleSearchParams;
import com.ryuqq.application.ruleexample.dto.response.RuleExampleSliceResult;
import com.ryuqq.application.ruleexample.factory.query.RuleExampleQueryFactory;
import com.ryuqq.application.ruleexample.manager.RuleExampleReadManager;
import com.ryuqq.application.ruleexample.port.in.SearchRuleExamplesByCursorUseCase;
import com.ryuqq.domain.ruleexample.aggregate.RuleExample;
import com.ryuqq.domain.ruleexample.query.RuleExampleSliceCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * SearchRuleExamplesByCursorService - RuleExample 복합 조건 조회 서비스 (커서 기반)
 *
 * <p>SearchRuleExamplesByCursorUseCase를 구현합니다.
 *
 * <p>RuleExample 목록을 커서 기반으로 복합 조건(코딩 규칙 ID, 예시 타입, 언어)으로 조회합니다.
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
public class SearchRuleExamplesByCursorService implements SearchRuleExamplesByCursorUseCase {

    private final RuleExampleQueryFactory ruleExampleQueryFactory;
    private final RuleExampleReadManager ruleExampleReadManager;
    private final RuleExampleAssembler ruleExampleAssembler;

    public SearchRuleExamplesByCursorService(
            RuleExampleQueryFactory ruleExampleQueryFactory,
            RuleExampleReadManager ruleExampleReadManager,
            RuleExampleAssembler ruleExampleAssembler) {
        this.ruleExampleQueryFactory = ruleExampleQueryFactory;
        this.ruleExampleReadManager = ruleExampleReadManager;
        this.ruleExampleAssembler = ruleExampleAssembler;
    }

    @Override
    public RuleExampleSliceResult execute(RuleExampleSearchParams searchParams) {
        // Factory에서 Criteria 생성 (필터 포함)
        RuleExampleSliceCriteria criteria =
                ruleExampleQueryFactory.createSliceCriteria(searchParams);
        List<RuleExample> ruleExamples = ruleExampleReadManager.findBySliceCriteria(criteria);
        return ruleExampleAssembler.toSliceResult(ruleExamples, searchParams.size());
    }
}
