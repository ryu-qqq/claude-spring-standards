package com.ryuqq.application.packagestructure.dto.response;

import com.ryuqq.domain.packagestructure.aggregate.PackageStructure;
import java.time.Instant;

/**
 * PackageStructureResult - 패키지 구조 조회 결과 DTO
 *
 * <p>Application Layer의 결과 DTO입니다.
 *
 * @param id 패키지 구조 ID
 * @param moduleId 모듈 ID
 * @param pathPattern 경로 패턴
 * @param description 설명 (nullable)
 * @param createdAt 생성 시각
 * @param updatedAt 수정 시각
 * @author ryu-qqq
 * @since 1.0.0
 */
public record PackageStructureResult(
        Long id,
        Long moduleId,
        String pathPattern,
        String description,
        Instant createdAt,
        Instant updatedAt) {

    /**
     * Domain 객체로부터 Result 생성
     *
     * @param packageStructure PackageStructure 도메인 객체
     * @return PackageStructureResult
     */
    public static PackageStructureResult from(PackageStructure packageStructure) {
        return new PackageStructureResult(
                packageStructure.id().value(),
                packageStructure.moduleId().value(),
                packageStructure.pathPattern().value(),
                packageStructure.description(),
                packageStructure.createdAt(),
                packageStructure.updatedAt());
    }
}
