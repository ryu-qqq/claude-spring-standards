package com.ryuqq.application.convention.assembler;

import com.ryuqq.application.convention.dto.response.ConventionResult;
import com.ryuqq.application.convention.dto.response.ConventionSliceResult;
import com.ryuqq.domain.common.vo.SliceMeta;
import com.ryuqq.domain.convention.aggregate.Convention;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ConventionAssembler - Convention Domain -> Response DTO 변환
 *
 * <p>Domain 객체를 Response DTO로 변환합니다.
 *
 * <p>SVC-005: Domain 객체 직접 반환 금지 -> Assembler를 통해 변환.
 *
 * <p>RDTO-008: Response DTO는 Domain 타입 의존 금지 -> Assembler에서 값 추출.
 *
 * <p>C-002: 변환기에서 null 체크 금지.
 *
 * <p>C-003: 변환기에서 기본값 할당 금지.
 *
 * @author ryu-qqq
 */
@Component
public class ConventionAssembler {

    /**
     * Convention Domain을 ConventionResult로 변환
     *
     * @param convention Convention 도메인 객체
     * @return ConventionResult
     */
    public ConventionResult toResult(Convention convention) {
        return new ConventionResult(
                convention.idValue(),
                convention.moduleIdValue(),
                convention.versionValue(),
                convention.description(),
                convention.isActive(),
                convention.isDeleted(),
                convention.createdAt(),
                convention.updatedAt());
    }

    /**
     * Convention Domain 목록을 ConventionResult 목록으로 변환
     *
     * @param conventions Convention 도메인 객체 목록
     * @return ConventionResult 목록
     */
    public List<ConventionResult> toResults(List<Convention> conventions) {
        return conventions.stream().map(this::toResult).toList();
    }

    /**
     * Convention Domain 목록을 ConventionSliceResult로 변환
     *
     * <p>RDTO-009: List 직접 반환 금지 → SliceMeta와 함께 반환합니다.
     *
     * @param conventions Convention 도메인 객체 목록
     * @param size 페이지 크기
     * @return ConventionSliceResult
     */
    public ConventionSliceResult toSliceResult(List<Convention> conventions, int size) {
        List<ConventionResult> content = toResults(conventions);
        boolean hasNext = content.size() > size;

        if (hasNext) {
            content = content.subList(0, size);
        }

        return new ConventionSliceResult(content, SliceMeta.of(size, hasNext));
    }
}
