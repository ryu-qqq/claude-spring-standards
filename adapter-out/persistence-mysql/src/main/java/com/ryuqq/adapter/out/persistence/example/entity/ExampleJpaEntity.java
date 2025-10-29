package com.ryuqq.adapter.out.persistence.example.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.ryuqq.adapter.out.persistence.common.entity.BaseAuditEntity;
import com.ryuqq.domain.example.ExampleStatus;

import java.time.LocalDateTime;

/**
 * ExampleJpaEntity - Example JPA Entity
 *
 * <p>Persistence Layer의 JPA Entity로서 데이터베이스 테이블과 매핑됩니다.</p>
 *
 * <p><strong>BaseAuditEntity 상속:</strong></p>
 * <ul>
 *   <li>공통 감사 필드 상속: createdAt, updatedAt</li>
 *   <li>markAsUpdated() 메서드로 수정 일시 자동 갱신</li>
 *   <li>감사 필드 중복 코드 제거</li>
 * </ul>
 *
 * <p><strong>Long FK 전략:</strong></p>
 * <ul>
 *   <li>JPA 관계 어노테이션 사용 금지 (@ManyToOne, @OneToMany 등)</li>
 *   <li>모든 외래키는 Long 타입으로 직접 관리</li>
 *   <li>연관 관계는 Application Layer에서 조합</li>
 * </ul>
 *
 * <p><strong>Lombok 금지:</strong></p>
 * <ul>
 *   <li>Plain Java getter/setter 사용</li>
 *   <li>명시적 생성자 제공</li>
 *   <li>JPA protected 기본 생성자 필수</li>
 * </ul>
 *
 * <p><strong>필드 불변성 전략 (final 제거):</strong></p>
 * <ul>
 *   <li>JPA 프록시 생성을 위해 final 사용 안 함</li>
 *   <li>불변성은 비즈니스 로직에서 보장 (setter 미제공)</li>
 *   <li>변경이 필요한 경우 명시적 메서드 제공</li>
 * </ul>
 *
 * <p><strong>테이블 설계:</strong></p>
 * <ul>
 *   <li>테이블명: example</li>
 *   <li>ID 생성 전략: IDENTITY (AUTO_INCREMENT)</li>
 *   <li>상태: Enum 타입 (ACTIVE, INACTIVE, DELETED)</li>
 *   <li>감사 정보: created_at, updated_at (상속)</li>
 * </ul>
 *
 * @author windsurf
 * @since 1.0.0
 */
@Entity
@Table(name = "example")
public class ExampleJpaEntity extends BaseAuditEntity {

    /**
     * 기본 키 - AUTO_INCREMENT
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 메시지 내용
     *
     * <p>final 제거: JPA 프록시 생성을 위해 필수</p>
     * <p>불변성은 setter 미제공으로 보장</p>
     */
    @Column(name = "message", nullable = false, length = 500)
    private String message;

    /**
     * 상태 - ACTIVE, INACTIVE, DELETED
     *
     * <p>final 제거: JPA 프록시 생성을 위해 필수</p>
     * <p>상태 변경은 updateStatus() 메서드로만 가능</p>
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ExampleStatus status;

    /**
     * JPA 기본 생성자 (protected)
     *
     * <p>JPA 스펙 요구사항으로 반드시 필요합니다.</p>
     * <p>BaseAuditEntity의 protected 생성자 호출</p>
     */
    protected ExampleJpaEntity() {
        super();
    }

    /**
     * 전체 필드 생성자
     *
     * @param id 기본 키
     * @param message 메시지 내용
     * @param status 상태
     * @param createdAt 생성 일시
     * @param updatedAt 수정 일시
     */
    public ExampleJpaEntity(
        Long id,
        String message,
        ExampleStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        super(createdAt, updatedAt);
        this.id = id;
        this.message = message;
        this.status = status;
    }

    /**
     * 신규 생성용 생성자 (ID 제외)
     *
     * @param message 메시지 내용
     * @param status 상태
     * @param createdAt 생성 일시
     * @param updatedAt 수정 일시
     */
    public ExampleJpaEntity(
        String message,
        ExampleStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        this(null, message, status, createdAt, updatedAt);
    }

    /**
     * ID 조회
     *
     * @return 기본 키
     */
    public Long getId() {
        return id;
    }

    /**
     * 메시지 조회
     *
     * @return 메시지 내용
     */
    public String getMessage() {
        return message;
    }

    /**
     * 상태 조회
     *
     * @return 상태 Enum
     */
    public ExampleStatus getStatus() {
        return status;
    }

}
