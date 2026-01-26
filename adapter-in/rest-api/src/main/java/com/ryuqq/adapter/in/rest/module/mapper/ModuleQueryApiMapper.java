package com.ryuqq.adapter.in.rest.module.mapper;

import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.adapter.in.rest.module.dto.request.SearchModulesCursorApiRequest;
import com.ryuqq.adapter.in.rest.module.dto.response.ModuleApiResponse;
import com.ryuqq.adapter.in.rest.module.dto.response.ModuleTreeApiResponse;
import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.application.module.dto.query.ModuleSearchParams;
import com.ryuqq.application.module.dto.response.ModuleResult;
import com.ryuqq.application.module.dto.response.ModuleSliceResult;
import com.ryuqq.application.module.dto.response.ModuleTreeResult;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ModuleQueryApiMapper - Module Query API 변환 매퍼
 *
 * <p>API Request/Response와 Application Query/Result 간 변환을 담당합니다.
 *
 * <p>MAPPER-001: Mapper는 @Component로 등록.
 *
 * <p>MAPPER-003: Application Result -> API Response 변환.
 *
 * <p>MAPPER-004: Domain 타입 직접 의존 금지.
 *
 * <p>CQRS 분리: Query 전용 Mapper (CommandApiMapper와 분리).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ModuleQueryApiMapper {

    /**
     * SearchModulesCursorApiRequest -> ModuleSearchParams 변환
     *
     * <p>CTR-001/CTR-007: Controller 비즈니스 로직 금지 → Mapper에서 변환 로직 처리.
     *
     * <p>CommonCursorParams 내부에서 기본값 처리를 수행하므로 Mapper는 단순 변환만 담당합니다.
     *
     * @param request 조회 요청 DTO
     * @return ModuleSearchParams 객체
     */
    public ModuleSearchParams toSearchParams(SearchModulesCursorApiRequest request) {
        CommonCursorParams cursorParams = CommonCursorParams.of(request.cursor(), request.size());
        return ModuleSearchParams.of(cursorParams, request.layerIds());
    }

    /**
     * 단일 Result -> Response 변환
     *
     * <p>MAPPER-005: DateTimeFormatUtils 사용하여 날짜 포맷 변환.
     *
     * @param result ModuleResult
     * @return ModuleApiResponse
     */
    public ModuleApiResponse toResponse(ModuleResult result) {
        return new ModuleApiResponse(
                result.moduleId(),
                result.layerId(),
                result.parentModuleId(),
                result.name(),
                result.description(),
                result.modulePath(),
                result.buildIdentifier(),
                DateTimeFormatUtils.formatIso8601(result.createdAt()),
                DateTimeFormatUtils.formatIso8601(result.updatedAt()));
    }

    /**
     * Result 목록 -> Response 목록 변환
     *
     * @param results ModuleResult 목록
     * @return ModuleApiResponse 목록
     */
    public List<ModuleApiResponse> toResponses(List<ModuleResult> results) {
        return results.stream().map(this::toResponse).toList();
    }

    /**
     * ModuleSliceResult -> SliceApiResponse 변환
     *
     * <p>RDTO-009: List 직접 반환 금지 -> SliceApiResponse 페이징 필수.
     *
     * @param sliceResult Application 슬라이스 결과 DTO
     * @return API 슬라이스 응답 DTO
     */
    public SliceApiResponse<ModuleApiResponse> toSliceResponse(ModuleSliceResult sliceResult) {
        List<ModuleApiResponse> responses = toResponses(sliceResult.modules());
        String nextCursor =
                sliceResult.nextCursor() != null ? sliceResult.nextCursor().toString() : null;
        return SliceApiResponse.of(
                responses, sliceResult.size(), sliceResult.hasNext(), nextCursor);
    }

    /**
     * ModuleTreeResult -> ModuleTreeApiResponse 변환
     *
     * <p>트리 구조를 재귀적으로 변환합니다.
     *
     * @param treeResult Application 트리 결과 DTO
     * @return API 트리 응답 DTO
     */
    public ModuleTreeApiResponse toTreeResponse(ModuleTreeResult treeResult) {
        List<ModuleTreeApiResponse> children =
                treeResult.children().stream().map(this::toTreeResponse).toList();

        return new ModuleTreeApiResponse(
                treeResult.moduleId(),
                treeResult.layerId(),
                treeResult.parentModuleId(),
                treeResult.name(),
                treeResult.description(),
                treeResult.modulePath(),
                treeResult.buildIdentifier(),
                DateTimeFormatUtils.formatIso8601(treeResult.createdAt()),
                DateTimeFormatUtils.formatIso8601(treeResult.updatedAt()),
                children);
    }

    /**
     * ModuleTreeResult 목록 -> ModuleTreeApiResponse 목록 변환
     *
     * @param treeResults Application 트리 결과 DTO 목록
     * @return API 트리 응답 DTO 목록
     */
    public List<ModuleTreeApiResponse> toTreeResponses(List<ModuleTreeResult> treeResults) {
        return treeResults.stream().map(this::toTreeResponse).toList();
    }
}
