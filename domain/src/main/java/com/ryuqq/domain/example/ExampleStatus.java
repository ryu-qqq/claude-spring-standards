package com.ryuqq.domain.example;

import com.ryuqq.domain.example.exception.ExampleInvalidStatusException;

/**
 * ExampleStatus - Example 상태 Enum
 *
 * <p>Example의 상태를 나타내는 불변 Enum입니다.</p>
 *
 * <p><strong>DDD 원칙:</strong></p>
 * <ul>
 *   <li>불변성 (Immutability): enum의 불변성 보장</li>
 *   <li>자가 검증 (Self-Validation): 유효한 상태만 존재 가능</li>
 *   <li>도메인 개념 표현: 단순 String이 아닌 타입으로 표현</li>
 * </ul>
 *
 * <p><strong>상태 전이 규칙:</strong></p>
 * <ul>
 *   <li>ACTIVE → INACTIVE: 가능</li>
 *   <li>INACTIVE → ACTIVE: 가능</li>
 *   <li>ACTIVE/INACTIVE → DELETED: 가능</li>
 *   <li>DELETED → ACTIVE/INACTIVE: 불가능 (예외 발생)</li>
 * </ul>
 *
 * @author windsurf
 * @since 1.0.0
 */
public enum ExampleStatus {
    /**
     * 활성 상태 - 정상 사용 가능
     */
    ACTIVE,

    /**
     * 비활성 상태 - 일시적으로 사용 중지
     */
    INACTIVE,

    /**
     * 삭제 상태 - 논리적 삭제 (복구 불가)
     */
    DELETED;

    /**
     * 기본 상태 반환 (ACTIVE)
     *
     * @return ACTIVE 상태
     */
    public static ExampleStatus createDefault() {
        return ACTIVE;
    }

    /**
     * 문자열로부터 생성
     *
     * @param statusString 상태 문자열 ("ACTIVE", "INACTIVE", "DELETED")
     * @return ExampleStatus
     * @throws IllegalArgumentException 유효하지 않은 상태 문자열인 경우
     */
    public static ExampleStatus fromString(String statusString) {
        if (statusString == null || statusString.isBlank()) {
            throw new IllegalArgumentException("Status string cannot be null or blank");
        }
        try {
            return ExampleStatus.valueOf(statusString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "Invalid status: " + statusString + ". Valid values: ACTIVE, INACTIVE, DELETED",
                e
            );
        }
    }

    /**
     * ACTIVE 상태 여부 확인
     *
     * @return ACTIVE면 true
     */
    public boolean isActive() {
        return this == ACTIVE;
    }

    /**
     * INACTIVE 상태 여부 확인
     *
     * @return INACTIVE면 true
     */
    public boolean isInactive() {
        return this == INACTIVE;
    }

    /**
     * DELETED 상태 여부 확인
     *
     * @return DELETED면 true
     */
    public boolean isDeleted() {
        return this == DELETED;
    }

    /**
     * 활성화 가능 여부 확인
     *
     * <p>DELETED 상태에서는 활성화 불가능</p>
     *
     * @return 활성화 가능하면 true
     */
    public boolean canActivate() {
        return this != DELETED;
    }

    /**
     * 비활성화 가능 여부 확인
     *
     * <p>DELETED 상태에서는 비활성화 불가능</p>
     *
     * @return 비활성화 가능하면 true
     */
    public boolean canDeactivate() {
        return this != DELETED;
    }

    /**
     * 삭제 가능 여부 확인
     *
     * <p>모든 상태에서 삭제 가능</p>
     *
     * @return 항상 true
     */
    public boolean canDelete() {
        return true;
    }

    /**
     * 상태 전이 - ACTIVE로 변경
     *
     * @return ACTIVE 상태
     * @throws ExampleInvalidStatusException DELETED 상태에서 호출 시
     */
    public ExampleStatus activate() {
        if (!canActivate()) {
            throw new ExampleInvalidStatusException(
                this.name(),
                ACTIVE.name()
            );
        }
        return ACTIVE;
    }

    /**
     * 상태 전이 - INACTIVE로 변경
     *
     * @return INACTIVE 상태
     * @throws ExampleInvalidStatusException DELETED 상태에서 호출 시
     */
    public ExampleStatus deactivate() {
        if (!canDeactivate()) {
            throw new ExampleInvalidStatusException(
                this.name(),
                INACTIVE.name()
            );
        }
        return INACTIVE;
    }

    /**
     * 상태 전이 - DELETED로 변경
     *
     * @return DELETED 상태
     */
    public ExampleStatus delete() {
        return DELETED;
    }

    /**
     * 문자열 변환
     *
     * @return 상태 문자열 ("ACTIVE", "INACTIVE", "DELETED")
     */
    public String asString() {
        return this.name();
    }
}
