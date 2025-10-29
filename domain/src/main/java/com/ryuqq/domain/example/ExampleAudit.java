package com.ryuqq.domain.example;

import java.time.LocalDateTime;

/**
 * ExampleAudit - Example 감사 정보 Value Object
 *
 * <p>Example의 생성/수정 시각을 추적하는 불변 Value Object입니다.</p>
 *
 * <p><strong>DDD 원칙:</strong></p>
 * <ul>
 *   <li>불변성 (Immutability): record로 불변 객체 보장</li>
 *   <li>자가 검증 (Self-Validation): 유효한 시각만 저장</li>
 *   <li>도메인 개념 표현: 단순 날짜가 아닌 감사 정보 개념으로 표현</li>
 * </ul>
 *
 * <p><strong>불변성 원칙:</strong></p>
 * <ul>
 *   <li>생성 후 변경 불가</li>
 *   <li>수정 시 새로운 ExampleAudit 인스턴스 반환</li>
 *   <li>createdAt은 절대 변경 불가</li>
 * </ul>
 *
 * @param createdAt 생성 일시 (Not Null)
 * @param updatedAt 수정 일시 (Not Null)
 * @author windsurf
 * @since 1.0.0
 */
public record ExampleAudit(
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

    /**
     * Canonical Constructor - 유효성 검증
     *
     * @param createdAt 생성 일시
     * @param updatedAt 수정 일시
     * @throws IllegalArgumentException createdAt 또는 updatedAt이 null인 경우
     * @throws IllegalArgumentException updatedAt이 createdAt보다 이전인 경우
     */
    public ExampleAudit {
        if (createdAt == null) {
            throw new IllegalArgumentException("createdAt cannot be null");
        }
        if (updatedAt == null) {
            throw new IllegalArgumentException("updatedAt cannot be null");
        }
        if (updatedAt.isBefore(createdAt)) {
            throw new IllegalArgumentException(
                "updatedAt cannot be before createdAt. createdAt: " + createdAt + ", updatedAt: " + updatedAt
            );
        }
    }

    /**
     * 새로운 Example 생성 시 감사 정보 생성
     *
     * <p>현재 시각으로 createdAt과 updatedAt을 동일하게 설정합니다.</p>
     *
     * @return 새로운 ExampleAudit
     */
    public static ExampleAudit createNew() {
        LocalDateTime now = LocalDateTime.now();
        return new ExampleAudit(now, now);
    }

    /**
     * 기존 Example 로드 시 감사 정보 생성
     *
     * @param createdAt 생성 일시
     * @param updatedAt 수정 일시
     * @return ExampleAudit
     */
    public static ExampleAudit of(LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new ExampleAudit(createdAt, updatedAt);
    }

    /**
     * 수정 시각 갱신
     *
     * <p>updatedAt을 현재 시각으로 변경한 새로운 ExampleAudit을 반환합니다.</p>
     * <p>createdAt은 변경되지 않습니다.</p>
     *
     * @return updatedAt이 갱신된 새로운 ExampleAudit
     */
    public ExampleAudit updateNow() {
        return new ExampleAudit(this.createdAt, LocalDateTime.now());
    }

    /**
     * 특정 시각으로 수정 시각 설정
     *
     * <p>주로 테스트나 특수한 경우에 사용합니다.</p>
     *
     * @param newUpdatedAt 새로운 수정 일시
     * @return updatedAt이 설정된 새로운 ExampleAudit
     * @throws IllegalArgumentException newUpdatedAt이 createdAt보다 이전인 경우
     */
    public ExampleAudit updateAt(LocalDateTime newUpdatedAt) {
        return new ExampleAudit(this.createdAt, newUpdatedAt);
    }

    /**
     * 생성 이후 경과 시간 여부 확인
     *
     * @param hours 시간
     * @return 생성 이후 지정된 시간이 경과했으면 true
     */
    public boolean isCreatedBefore(int hours) {
        LocalDateTime threshold = LocalDateTime.now().minusHours(hours);
        return createdAt.isBefore(threshold);
    }

    /**
     * 최근 수정 여부 확인
     *
     * @param hours 시간
     * @return 지정된 시간 이내에 수정되었으면 true
     */
    public boolean isRecentlyUpdated(int hours) {
        LocalDateTime threshold = LocalDateTime.now().minusHours(hours);
        return updatedAt.isAfter(threshold);
    }

    /**
     * 생성 이후 수정 여부 확인
     *
     * @return 생성 이후 수정되었으면 true
     */
    public boolean isModified() {
        return !createdAt.equals(updatedAt);
    }
}
