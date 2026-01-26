package com.ryuqq.adapter.in.rest.zerotolerance.mapper;

import com.ryuqq.adapter.in.rest.checklistitem.dto.response.ChecklistItemApiResponse;
import com.ryuqq.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.adapter.in.rest.ruleexample.dto.response.RuleExampleApiResponse;
import com.ryuqq.adapter.in.rest.zerotolerance.dto.request.SearchZeroToleranceRulesCursorApiRequest;
import com.ryuqq.adapter.in.rest.zerotolerance.dto.response.ZeroToleranceRuleDetailApiResponse;
import com.ryuqq.adapter.in.rest.zerotolerance.dto.response.ZeroToleranceRuleSliceApiResponse;
import com.ryuqq.application.checklistitem.dto.response.ChecklistItemResult;
import com.ryuqq.application.codingrule.dto.response.CodingRuleResult;
import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.application.ruleexample.dto.response.RuleExampleResult;
import com.ryuqq.application.zerotolerance.dto.query.ZeroToleranceRuleSearchParams;
import com.ryuqq.application.zerotolerance.dto.response.ZeroToleranceRuleDetailResult;
import com.ryuqq.application.zerotolerance.dto.response.ZeroToleranceRuleSliceResult;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ZeroToleranceRuleQueryApiMapper - ZeroToleranceRule Query API 변환 매퍼
 *
 * <p>API Request/Response와 Application Query/Result 간 변환을 담당합니다.
 *
 * <p>MAP-001: Mapper는 @Component로 등록.
 *
 * <p>MAP-003: Application Result -> API Response 변환.
 *
 * <p>MAP-004: Mapper는 필드 매핑만 수행.
 *
 * <p>MAP-006: Mapper에서 Domain 객체 직접 사용 금지.
 *
 * <p>DTO-016: Response DTO는 String 타입으로 날짜/시간 표현.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ZeroToleranceRuleQueryApiMapper {

    private static final int DEFAULT_SIZE = 20;

    /**
     * SearchZeroToleranceRulesCursorApiRequest -> ZeroToleranceRuleSearchParams 변환
     *
     * <p>CTR-001/CTR-007: Controller 비즈니스 로직 금지 -> Mapper에서 변환 처리.
     *
     * <p>DTO-015: Request DTO Compact Constructor 기본값 설정 금지 -> Mapper에서 처리.
     *
     * @param request 조회 요청 DTO
     * @return ZeroToleranceRuleSearchParams 객체
     */
    public ZeroToleranceRuleSearchParams toSearchParams(
            SearchZeroToleranceRulesCursorApiRequest request) {
        int size = request.size() != null && request.size() > 0 ? request.size() : DEFAULT_SIZE;
        CommonCursorParams cursorParams = CommonCursorParams.of(request.cursor(), size);
        return ZeroToleranceRuleSearchParams.of(
                cursorParams,
                request.conventionIds(),
                request.detectionTypes(),
                request.searchField(),
                request.searchWord(),
                request.autoRejectPr());
    }

    /**
     * ZeroToleranceRuleDetailResult -> ZeroToleranceRuleDetailApiResponse 변환
     *
     * <p>DTO-016: Response DTO는 String 타입으로 날짜/시간 표현.
     *
     * @param result ZeroToleranceRuleDetailResult
     * @return ZeroToleranceRuleDetailApiResponse
     */
    public ZeroToleranceRuleDetailApiResponse toApiResponse(ZeroToleranceRuleDetailResult result) {
        CodingRuleResult codingRule = result.codingRule();
        List<RuleExampleApiResponse> exampleResponses = toRuleExampleResponses(result.examples());
        List<ChecklistItemApiResponse> checklistResponses =
                toChecklistItemResponses(result.checklistItems());

        return new ZeroToleranceRuleDetailApiResponse(
                codingRule.id(),
                codingRule.code(),
                codingRule.name(),
                codingRule.severity().name(),
                codingRule.category().name(),
                codingRule.description(),
                codingRule.rationale(),
                codingRule.autoFixable(),
                codingRule.appliesTo(),
                exampleResponses,
                checklistResponses,
                DateTimeFormatUtils.formatIso8601(codingRule.createdAt()),
                DateTimeFormatUtils.formatIso8601(codingRule.updatedAt()));
    }

    /**
     * ZeroToleranceRuleSliceResult -> ZeroToleranceRuleSliceApiResponse 변환
     *
     * @param sliceResult Application 슬라이스 결과 DTO
     * @return API 슬라이스 응답 DTO
     */
    public ZeroToleranceRuleSliceApiResponse toSliceApiResponse(
            ZeroToleranceRuleSliceResult sliceResult) {
        List<ZeroToleranceRuleDetailApiResponse> responses =
                sliceResult.rules().stream().map(this::toApiResponse).toList();

        return new ZeroToleranceRuleSliceApiResponse(
                responses, sliceResult.hasNext(), sliceResult.nextCursorId());
    }

    /**
     * RuleExampleResult 목록 -> RuleExampleApiResponse 목록 변환
     *
     * @param results RuleExampleResult 목록
     * @return RuleExampleApiResponse 목록
     */
    private List<RuleExampleApiResponse> toRuleExampleResponses(List<RuleExampleResult> results) {
        return results.stream().map(this::toRuleExampleResponse).toList();
    }

    /**
     * 단일 RuleExampleResult -> RuleExampleApiResponse 변환
     *
     * @param result RuleExampleResult
     * @return RuleExampleApiResponse
     */
    private RuleExampleApiResponse toRuleExampleResponse(RuleExampleResult result) {
        return new RuleExampleApiResponse(
                result.id(),
                result.ruleId(),
                result.exampleType(),
                result.code(),
                result.language(),
                result.explanation(),
                result.highlightLines(),
                result.source(),
                result.feedbackId(),
                DateTimeFormatUtils.formatIso8601(result.createdAt()),
                DateTimeFormatUtils.formatIso8601(result.updatedAt()));
    }

    /**
     * ChecklistItemResult 목록 -> ChecklistItemApiResponse 목록 변환
     *
     * @param results ChecklistItemResult 목록
     * @return ChecklistItemApiResponse 목록
     */
    private List<ChecklistItemApiResponse> toChecklistItemResponses(
            List<ChecklistItemResult> results) {
        return results.stream().map(this::toChecklistItemResponse).toList();
    }

    /**
     * 단일 ChecklistItemResult -> ChecklistItemApiResponse 변환
     *
     * @param result ChecklistItemResult
     * @return ChecklistItemApiResponse
     */
    private ChecklistItemApiResponse toChecklistItemResponse(ChecklistItemResult result) {
        return new ChecklistItemApiResponse(
                result.id(),
                result.ruleId(),
                result.sequenceOrder(),
                result.checkDescription(),
                result.checkType(),
                result.automationTool(),
                result.automationRuleId(),
                result.critical(),
                result.source(),
                result.feedbackId(),
                DateTimeFormatUtils.formatIso8601(result.createdAt()),
                DateTimeFormatUtils.formatIso8601(result.updatedAt()));
    }
}
