package com.ryuqq.adapter.out.persistence.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import java.time.LocalDateTime;

/**
 * BaseAuditEntity - 감사 정보 공통 추상 클래스
 *
 * <p>모든 JPA 엔티티의 공통 감사 필드(생성일시, 수정일시)를 제공합니다.</p>
 *
 * <p><strong>설계 원칙:</strong></p>
 * <ul>
 *   <li>@MappedSuperclass: 엔티티가 아닌 매핑 정보만 제공</li>
 *   <li>추상 클래스로 직접 인스턴스화 불가</li>
 *   <li>protected 생성자로 상속 클래스만 접근 가능</li>
 * </ul>
 *
 * <p><strong>필드 불변성 전략:</strong></p>
 * <ul>
 *   <li>JPA 프록시 생성을 위해 final 사용 안 함</li>
 *   <li>불변성은 비즈니스 로직에서 보장 (setter 미제공)</li>
 *   <li>수정 일시는 markAsUpdated() 메서드로만 변경</li>
 * </ul>
 *
 * <p><strong>사용 예시:</strong></p>
 * <pre>{@code
 * @Entity
 * @Table(name = "example")
 * public class ExampleJpaEntity extends BaseAuditEntity {
 *     // 엔티티별 고유 필드
 * }
 * }</pre>
 *
 * @author windsurf
 * @since 1.0.0
 */
@MappedSuperclass
public abstract class BaseAuditEntity {

    /**
     * 생성 일시
     *
     * <p>엔티티 최초 생성 시각을 기록합니다.</p>
     * <p>updatable = false로 수정 불가능하게 설정합니다.</p>
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 수정 일시
     *
     * <p>엔티티 최종 수정 시각을 기록합니다.</p>
     * <p>markAsUpdated() 메서드로만 변경 가능합니다.</p>
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 기본 생성자 (protected)
     *
     * <p>JPA 스펙 요구사항 및 상속 클래스 전용 생성자입니다.</p>
     */
    protected BaseAuditEntity() {
    }

    /**
     * 감사 정보 생성자
     *
     * <p>상속 클래스에서 감사 필드를 초기화할 때 사용합니다.</p>
     *
     * @param createdAt 생성 일시
     * @param updatedAt 수정 일시
     */
    protected BaseAuditEntity(LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 현재 시각으로 감사 정보 초기화
     *
     * <p>신규 엔티티 생성 시 현재 시각으로 생성/수정 일시를 설정합니다.</p>
     */
    protected void initializeAuditFields() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    /**
     * 생성 일시 조회
     *
     * @return 생성 일시
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * 수정 일시 조회
     *
     * @return 수정 일시
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 엔티티 수정 시각 갱신
     *
     * <p>엔티티의 상태가 변경될 때 호출하여 수정 일시를 업데이트합니다.</p>
     * <p>도메인 의미를 가진 메서드로 setter를 대체합니다.</p>
     *
     * <p><strong>사용 시점:</strong></p>
     * <ul>
     *   <li>엔티티 필드 값 변경 후</li>
     *   <li>비즈니스 메서드 실행 후</li>
     *   <li>Repository save() 호출 전</li>
     * </ul>
     *
     * <p><strong>예시:</strong></p>
     * <pre>{@code
     * entity.updateMessage("new message");
     * entity.markAsUpdated();  // 수정 일시 갱신
     * repository.save(entity);
     * }</pre>
     */
    public void markAsUpdated() {
        this.updatedAt = LocalDateTime.now();
    }
}
