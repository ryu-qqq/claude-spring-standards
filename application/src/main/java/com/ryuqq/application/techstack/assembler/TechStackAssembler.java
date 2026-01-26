package com.ryuqq.application.techstack.assembler;

import com.ryuqq.application.techstack.dto.response.TechStackResult;
import com.ryuqq.application.techstack.dto.response.TechStackSliceResult;
import com.ryuqq.domain.common.vo.SliceMeta;
import com.ryuqq.domain.techstack.aggregate.TechStack;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * TechStackAssembler - TechStack Domain → Response DTO 변환
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
public class TechStackAssembler {

    /**
     * TechStack Domain을 TechStackResult로 변환
     *
     * @param techStack TechStack 도메인 객체
     * @return TechStackResult
     */
    public TechStackResult toResult(TechStack techStack) {
        return new TechStackResult(
                techStack.id().value(),
                techStack.name().value(),
                techStack.status().name(),
                techStack.languageType().name(),
                techStack.languageVersion().value(),
                techStack.languageFeatures().values(),
                techStack.frameworkType().name(),
                techStack.frameworkVersion().value(),
                techStack.frameworkModules().values(),
                techStack.platformType().name(),
                techStack.runtimeEnvironment().name(),
                techStack.buildToolType().name(),
                techStack.buildConfigFile().value(),
                techStack.referenceLinks().values(),
                techStack.isDeleted(),
                techStack.createdAt(),
                techStack.updatedAt());
    }

    /**
     * TechStack Domain 목록을 TechStackResult 목록으로 변환
     *
     * @param techStacks TechStack 도메인 객체 목록
     * @return TechStackResult 목록
     */
    public List<TechStackResult> toResults(List<TechStack> techStacks) {
        return techStacks.stream().map(this::toResult).toList();
    }

    /**
     * TechStack Domain 목록을 TechStackSliceResult로 변환
     *
     * <p>RDTO-009: List 직접 반환 금지 → SliceMeta와 함께 반환합니다.
     *
     * @param techStacks TechStack 도메인 객체 목록
     * @param size 페이지 크기
     * @return TechStackSliceResult
     */
    public TechStackSliceResult toSliceResult(List<TechStack> techStacks, int size) {
        List<TechStackResult> content = toResults(techStacks);
        boolean hasNext = content.size() > size;

        if (hasNext) {
            content = content.subList(0, size);
        }

        return new TechStackSliceResult(content, SliceMeta.of(size, hasNext));
    }
}
