package com.ryuqq.application.packagepurpose.dto.response;

import com.ryuqq.domain.packagepurpose.aggregate.PackagePurpose;
import java.time.Instant;

/**
 * PackagePurposeResult - 패키지 목적 조회 결과 DTO
 *
 * <p>Application Layer의 결과 DTO입니다.
 *
 * @param id 패키지 목적 ID
 * @param structureId 패키지 구조 ID
 * @param code 목적 코드
 * @param name 목적 이름
 * @param description 설명 (nullable)
 * @param createdAt 생성 시각
 * @param updatedAt 수정 시각
 * @author ryu-qqq
 * @since 1.0.0
 */
public record PackagePurposeResult(
        Long id,
        Long structureId,
        String code,
        String name,
        String description,
        Instant createdAt,
        Instant updatedAt) {

    /**
     * Domain 객체로부터 Result 생성
     *
     * @param packagePurpose PackagePurpose 도메인 객체
     * @return PackagePurposeResult
     */
    public static PackagePurposeResult from(PackagePurpose packagePurpose) {
        return new PackagePurposeResult(
                packagePurpose.idValue(),
                packagePurpose.structureIdValue(),
                packagePurpose.codeValue(),
                packagePurpose.nameValue(),
                packagePurpose.description(),
                packagePurpose.createdAt(),
                packagePurpose.updatedAt());
    }
}
