package com.ryuqq.domain.example;

import com.ryuqq.domain.example.exception.ExampleInvalidStatusException;

import java.time.LocalDateTime;

/**
 * ExampleDomain - Example Aggregate Root
 *
 * <p>DDD 패턴의 Aggregate Root로서 Example 도메인을 표현합니다.</p>
 *
 * <p><strong>Aggregate Root 책임:</strong></p>
 * <ul>
 *   <li>도메인 불변성 유지</li>
 *   <li>비즈니스 규칙 검증</li>
 *   <li>상태 변경 관리</li>
 *   <li>Value Object 조합</li>
 * </ul>
 *
 * <p><strong>Value Object 구성:</strong></p>
 * <ul>
 *   <li>{@link ExampleId} - 식별자</li>
 *   <li>{@link ExampleContent} - 내용</li>
 *   <li>{@link ExampleStatus} - 상태</li>
 *   <li>{@link ExampleAudit} - 감사 정보</li>
 * </ul>
 *
 * <p><strong>불변성 원칙:</strong></p>
 * <ul>
 *   <li>모든 필드는 record로 불변</li>
 *   <li>상태 변경 시 새로운 인스턴스 반환</li>
 *   <li>비즈니스 로직은 도메인 메서드로 캡슐화</li>
 * </ul>
 *
 * @param exampleId 식별자 Value Object
 * @param content 내용 Value Object
 * @param status 상태 Value Object
 * @param audit 감사 정보 Value Object
 * @author windsurf
 * @since 1.0.0
 */
public record ExampleDomain(
    ExampleId exampleId,
    ExampleContent content,
    ExampleStatus status,
    ExampleAudit audit
) {

    /**
     * 새로운 Example 생성 (ID 없음)
     *
     * <p>신규 생성 시 사용하며, ID는 Persistence Layer에서 할당됩니다.</p>
     *
     * @param message 메시지 내용
     * @return ID가 없는 새로운 ExampleDomain
     */
    public static ExampleDomain create(String message) {
        return new ExampleDomain(
            null,  // ID는 저장 시 할당
            ExampleContent.of(message),
            ExampleStatus.createDefault(),  // ACTIVE
            ExampleAudit.createNew()  // 현재 시각
        );
    }

    /**
     * 기존 Example 재구성 (ID 있음)
     *
     * <p>Persistence Layer에서 로드 시 사용합니다.</p>
     *
     * @param id Example ID
     * @param message 메시지 내용
     * @param status 상태 문자열
     * @param createdAt 생성 일시
     * @param updatedAt 수정 일시
     * @return 기존 ExampleDomain
     */
    public static ExampleDomain of(
        Long id,
        String message,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        return new ExampleDomain(
            ExampleId.of(id),
            ExampleContent.of(message),
            ExampleStatus.fromString(status),
            ExampleAudit.of(createdAt, updatedAt)
        );
    }

    /**
     * 간단한 재구성 (하위 호환성)
     *
     * @param id Example ID
     * @param message 메시지 내용
     * @return 기본 상태(ACTIVE)와 현재 시각으로 ExampleDomain
     * @deprecated 전체 정보가 있는 경우 {@link #of(Long, String, String, LocalDateTime, LocalDateTime)} 사용 권장
     */
    @Deprecated(since = "1.0.0", forRemoval = true)
    public static ExampleDomain of(Long id, String message) {
        LocalDateTime now = LocalDateTime.now();
        return new ExampleDomain(
            ExampleId.of(id),
            ExampleContent.of(message),
            ExampleStatus.createDefault(),
            ExampleAudit.of(now, now)
        );
    }

    /**
     * ID 조회
     *
     * @return Example ID (신규 생성 시 null 가능)
     */
    public Long getId() {
        return exampleId != null ? exampleId.id() : null;
    }

    /**
     * 메시지 내용 조회
     *
     * @return 메시지 문자열
     */
    public String getMessage() {
        return content.message();
    }

    /**
     * 상태 문자열 조회
     *
     * @return 상태 문자열 ("ACTIVE", "INACTIVE", "DELETED")
     */
    public String getStatus() {
        return status.asString();
    }

    /**
     * 생성 일시 조회
     *
     * @return 생성 일시
     */
    public LocalDateTime getCreatedAt() {
        return audit.createdAt();
    }

    /**
     * 수정 일시 조회
     *
     * @return 수정 일시
     */
    public LocalDateTime getUpdatedAt() {
        return audit.updatedAt();
    }

    /**
     * 메시지 내용 변경
     *
     * <p>메시지를 변경하고 updatedAt을 갱신한 새로운 ExampleDomain을 반환합니다.</p>
     *
     * @param newMessage 새로운 메시지
     * @return 메시지가 변경된 새로운 ExampleDomain
     */
    public ExampleDomain changeMessage(String newMessage) {
        return new ExampleDomain(
            this.exampleId,
            ExampleContent.of(newMessage),
            this.status,
            this.audit.updateNow()
        );
    }

    /**
     * 활성화
     *
     * @return ACTIVE 상태로 변경된 새로운 ExampleDomain
     * @throws ExampleInvalidStatusException DELETED 상태에서 호출 시
     */
    public ExampleDomain activate() {
        return new ExampleDomain(
            this.exampleId,
            this.content,
            this.status.activate(),
            this.audit.updateNow()
        );
    }

    /**
     * 비활성화
     *
     * @return INACTIVE 상태로 변경된 새로운 ExampleDomain
     * @throws ExampleInvalidStatusException DELETED 상태에서 호출 시
     */
    public ExampleDomain deactivate() {
        return new ExampleDomain(
            this.exampleId,
            this.content,
            this.status.deactivate(),
            this.audit.updateNow()
        );
    }

    /**
     * 삭제 (논리적 삭제)
     *
     * @return DELETED 상태로 변경된 새로운 ExampleDomain
     */
    public ExampleDomain delete() {
        return new ExampleDomain(
            this.exampleId,
            this.content,
            this.status.delete(),
            this.audit.updateNow()
        );
    }

    /**
     * 활성 상태 여부
     *
     * @return ACTIVE면 true
     */
    public boolean isActive() {
        return status.isActive();
    }

    /**
     * 삭제 상태 여부
     *
     * @return DELETED면 true
     */
    public boolean isDeleted() {
        return status.isDeleted();
    }

    /**
     * ID 할당 (Persistence Layer에서 저장 후 호출)
     *
     * @param id 할당된 ID
     * @return ID가 설정된 새로운 ExampleDomain
     */
    public ExampleDomain withId(Long id) {
        return new ExampleDomain(
            ExampleId.of(id),
            this.content,
            this.status,
            this.audit
        );
    }
}
