package com.ryuqq.application.mcp.assembler;

import com.ryuqq.application.mcp.dto.context.ValidationChecklistDto;
import com.ryuqq.application.mcp.dto.context.ValidationZeroToleranceDto;
import com.ryuqq.application.mcp.dto.response.ChecklistItemResult;
import com.ryuqq.application.mcp.dto.response.LayerValidationStatsResult;
import com.ryuqq.application.mcp.dto.response.ValidationContextResult;
import com.ryuqq.application.mcp.dto.response.ValidationContextSummaryResult;
import com.ryuqq.application.mcp.dto.response.ZeroToleranceRuleResult;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;

/**
 * ValidationContextAssembler - Validation Context 응답 조립 담당
 *
 * <p>ZeroToleranceRules, ChecklistItems를 ValidationContextResult로 조립합니다.
 *
 * <p>ASM-001: Assembler 명명 규칙 준수.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ValidationContextAssembler {

    /**
     * ValidationContextResult 조립
     *
     * @param zeroToleranceRules Zero-Tolerance 규칙 DTO 목록
     * @param checklistItems 체크리스트 항목 DTO 목록
     * @return ValidationContextResult
     */
    public ValidationContextResult assemble(
            List<ValidationZeroToleranceDto> zeroToleranceRules,
            List<ValidationChecklistDto> checklistItems) {

        // ZeroTolerance 결과 변환 + 레이어별 통계
        Map<String, Integer> zeroToleranceCountByLayer = new HashMap<>();
        List<ZeroToleranceRuleResult> zeroToleranceResults =
                toZeroToleranceRuleResults(zeroToleranceRules, zeroToleranceCountByLayer);

        // Checklist 결과 변환 + 레이어별 통계 + 자동화 가능 항목 카운트
        Map<String, Integer> checklistCountByLayer = new HashMap<>();
        List<ChecklistItemResult> checklistResults =
                toChecklistItemResults(checklistItems, checklistCountByLayer);

        int autoCheckableCount = countAutoCheckable(checklistResults);

        // Summary 생성
        Map<String, LayerValidationStatsResult> byLayer =
                buildLayerStats(zeroToleranceCountByLayer, checklistCountByLayer);

        ValidationContextSummaryResult summary =
                new ValidationContextSummaryResult(
                        zeroToleranceResults.size(),
                        checklistResults.size(),
                        autoCheckableCount,
                        byLayer);

        return new ValidationContextResult(zeroToleranceResults, checklistResults, summary);
    }

    /**
     * ZeroToleranceRuleResult 목록 변환
     *
     * @param zeroToleranceRules 원본 DTO 목록
     * @param countByLayer 레이어별 카운트 맵 (side-effect로 채워짐)
     * @return ZeroToleranceRuleResult 목록
     */
    private List<ZeroToleranceRuleResult> toZeroToleranceRuleResults(
            List<ValidationZeroToleranceDto> zeroToleranceRules,
            Map<String, Integer> countByLayer) {

        return zeroToleranceRules.stream()
                .map(
                        zt -> {
                            countByLayer.merge(
                                    zt.layerCode(), 1, (Integer a, Integer b) -> Integer.sum(a, b));
                            return toZeroToleranceRuleResult(zt);
                        })
                .toList();
    }

    /**
     * 단일 ZeroToleranceRuleResult 변환
     *
     * @param zt ValidationZeroToleranceDto
     * @return ZeroToleranceRuleResult
     */
    private ZeroToleranceRuleResult toZeroToleranceRuleResult(ValidationZeroToleranceDto zt) {
        return new ZeroToleranceRuleResult(
                zt.ruleCode(),
                zt.ruleName(),
                zt.layerCode(),
                zt.appliesTo(),
                zt.detectionPattern(),
                zt.detectionType(),
                zt.autoRejectPr(),
                buildErrorMessage(zt));
    }

    /**
     * 에러 메시지 생성
     *
     * @param zt ValidationZeroToleranceDto
     * @return 포맷된 에러 메시지
     */
    private String buildErrorMessage(ValidationZeroToleranceDto zt) {
        return String.format(
                "[%s] %s violation detected. Rule: %s",
                zt.severity(), zt.ruleName(), zt.ruleCode());
    }

    /**
     * ChecklistItemResult 목록 변환
     *
     * @param checklistItems 원본 DTO 목록
     * @param countByLayer 레이어별 카운트 맵 (side-effect로 채워짐)
     * @return ChecklistItemResult 목록
     */
    private List<ChecklistItemResult> toChecklistItemResults(
            List<ValidationChecklistDto> checklistItems, Map<String, Integer> countByLayer) {

        return new ArrayList<>(
                checklistItems.stream()
                        .map(
                                cl -> {
                                    countByLayer.merge(
                                            cl.layerCode(),
                                            1,
                                            (Integer a, Integer b) -> Integer.sum(a, b));
                                    return toChecklistItemResult(cl);
                                })
                        .toList());
    }

    /**
     * 단일 ChecklistItemResult 변환
     *
     * @param cl ValidationChecklistDto
     * @return ChecklistItemResult
     */
    private ChecklistItemResult toChecklistItemResult(ValidationChecklistDto cl) {
        return new ChecklistItemResult(
                cl.ruleCode(), cl.checkDescription(), cl.severity(), cl.hasAutomation());
    }

    /**
     * 자동 체크 가능 항목 수 계산
     *
     * @param checklistResults 체크리스트 결과 목록
     * @return 자동 체크 가능 항목 수
     */
    private int countAutoCheckable(List<ChecklistItemResult> checklistResults) {
        int count = 0;
        for (ChecklistItemResult result : checklistResults) {
            if (result.autoCheckable()) {
                count++;
            }
        }
        return count;
    }

    /**
     * 레이어별 통계 생성
     *
     * @param zeroToleranceCountByLayer ZeroTolerance 레이어별 카운트
     * @param checklistCountByLayer Checklist 레이어별 카운트
     * @return 레이어별 통계 맵
     */
    private Map<String, LayerValidationStatsResult> buildLayerStats(
            Map<String, Integer> zeroToleranceCountByLayer,
            Map<String, Integer> checklistCountByLayer) {

        Map<String, LayerValidationStatsResult> result = new HashMap<>();

        // 모든 레이어 코드 수집
        Set<String> allLayerCodes = new HashSet<>();
        allLayerCodes.addAll(zeroToleranceCountByLayer.keySet());
        allLayerCodes.addAll(checklistCountByLayer.keySet());

        for (String layerCode : allLayerCodes) {
            int zeroTolerance = zeroToleranceCountByLayer.getOrDefault(layerCode, 0);
            int checklist = checklistCountByLayer.getOrDefault(layerCode, 0);
            result.put(layerCode, new LayerValidationStatsResult(zeroTolerance, checklist));
        }

        return result;
    }
}
