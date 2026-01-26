package com.ryuqq.domain.convention.aggregate;

import com.ryuqq.domain.convention.vo.ConventionVersion;
import com.ryuqq.domain.module.id.ModuleId;

/**
 * ConventionUpdateData - 컨벤션 수정 데이터
 *
 * <p>Convention 업데이트에 필요한 데이터를 담습니다.
 *
 * <p>AGG-001: Lombok 금지 - Record 사용.
 *
 * <p>Compact constructor에서 null 검증을 수행합니다.
 *
 * @param moduleId 모듈 ID
 * @param version 컨벤션 버전
 * @param description 컨벤션 설명
 * @param active 활성화 여부
 * @author ryu-qqq
 */
public record ConventionUpdateData(
        ModuleId moduleId, ConventionVersion version, String description, boolean active) {

    /**
     * Compact constructor - 필수 필드 검증
     *
     * @throws IllegalArgumentException 필수 필드가 null인 경우
     */
    public ConventionUpdateData {
        if (moduleId == null) {
            throw new IllegalArgumentException("moduleId must not be null");
        }
        if (version == null) {
            throw new IllegalArgumentException("version must not be null");
        }
        if (description == null) {
            throw new IllegalArgumentException("description must not be null");
        }
    }
}
