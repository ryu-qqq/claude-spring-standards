package com.ryuqq.application.architecture.assembler;

import com.ryuqq.application.architecture.dto.response.ArchitectureResult;
import com.ryuqq.application.architecture.dto.response.ArchitectureSliceResult;
import com.ryuqq.domain.architecture.aggregate.Architecture;
import com.ryuqq.domain.common.vo.SliceMeta;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ArchitectureAssembler - Architecture Domain → Response DTO 변환
 *
 * <p>Domain 객체를 Response DTO로 변환합니다.
 *
 * <p>SVC-005: Domain 객체 직접 반환 금지 → Assembler를 통해 변환.
 *
 * <p>RDTO-008: Response DTO는 Domain 타입 의존 금지 → Assembler에서 값 추출.
 *
 * <p>C-002: 변환기에서 null 체크 금지.
 *
 * <p>C-003: 변환기에서 기본값 할당 금지.
 *
 * @author ryu-qqq
 */
@Component
public class ArchitectureAssembler {

    /**
     * Architecture Domain을 ArchitectureResult로 변환
     *
     * @param architecture Architecture 도메인 객체
     * @return ArchitectureResult
     */
    public ArchitectureResult toResult(Architecture architecture) {
        return new ArchitectureResult(
                architecture.id().value(),
                architecture.techStackId().value(),
                architecture.name().value(),
                architecture.patternType().name(),
                architecture.patternDescription().value(),
                architecture.patternPrinciples().values(),
                architecture.referenceLinks().values(),
                architecture.isDeleted(),
                architecture.createdAt(),
                architecture.updatedAt());
    }

    /**
     * Architecture Domain 목록을 ArchitectureResult 목록으로 변환
     *
     * @param architectures Architecture 도메인 객체 목록
     * @return ArchitectureResult 목록
     */
    public List<ArchitectureResult> toResults(List<Architecture> architectures) {
        return architectures.stream().map(this::toResult).toList();
    }

    /**
     * Architecture Domain 목록을 ArchitectureSliceResult로 변환
     *
     * <p>RDTO-009: List 직접 반환 금지 → SliceMeta와 함께 반환합니다.
     *
     * @param architectures Architecture 도메인 객체 목록
     * @param size 페이지 크기
     * @return ArchitectureSliceResult
     */
    public ArchitectureSliceResult toSliceResult(List<Architecture> architectures, int size) {
        List<ArchitectureResult> content = toResults(architectures);
        boolean hasNext = content.size() > size;

        if (hasNext) {
            content = content.subList(0, size);
        }

        return new ArchitectureSliceResult(content, SliceMeta.of(size, hasNext));
    }
}
