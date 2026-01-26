package com.ryuqq.domain.onboardingcontext.aggregate;

import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.onboardingcontext.id.OnboardingContextId;
import com.ryuqq.domain.onboardingcontext.vo.ContextContent;
import com.ryuqq.domain.onboardingcontext.vo.ContextTitle;
import com.ryuqq.domain.onboardingcontext.vo.ContextType;
import com.ryuqq.domain.onboardingcontext.vo.Priority;
import com.ryuqq.domain.techstack.id.TechStackId;
import java.time.Instant;

/**
 * OnboardingContext - 온보딩 컨텍스트 Aggregate Root
 *
 * <p>Serena 온보딩 시 제공할 컨텍스트 정보를 관리합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public class OnboardingContext {

    private OnboardingContextId id;
    private TechStackId techStackId;
    private ArchitectureId architectureId;
    private ContextType contextType;
    private ContextTitle title;
    private ContextContent content;
    private Priority priority;
    private DeletionStatus deletionStatus;

    // Audit
    private final Instant createdAt;
    private Instant updatedAt;

    /** 프레임워크 호환용 기본 생성자 */
    protected OnboardingContext() {
        this.createdAt = null;
    }

    private OnboardingContext(
            OnboardingContextId id,
            TechStackId techStackId,
            ArchitectureId architectureId,
            ContextType contextType,
            ContextTitle title,
            ContextContent content,
            Priority priority,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.techStackId = techStackId;
        this.architectureId = architectureId;
        this.contextType = contextType;
        this.title = title;
        this.content = content != null ? content : ContextContent.empty();
        this.priority = priority != null ? priority : Priority.defaultPriority();
        this.deletionStatus = deletionStatus != null ? deletionStatus : DeletionStatus.active();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 신규 생성용 팩토리 메서드
     *
     * @param techStackId 기술 스택 ID
     * @param contextType 컨텍스트 유형
     * @param title 컨텍스트 제목
     * @param content 컨텍스트 내용
     * @param priority 우선순위
     * @param now 현재 시각 (Instant.now() 외부 주입)
     * @return 새로운 OnboardingContext 인스턴스
     */
    public static OnboardingContext forNew(
            TechStackId techStackId,
            ContextType contextType,
            ContextTitle title,
            ContextContent content,
            Priority priority,
            Instant now) {
        return new OnboardingContext(
                OnboardingContextId.forNew(),
                techStackId,
                null,
                contextType,
                title,
                content,
                priority,
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * 신규 생성용 팩토리 메서드 (아키텍처 포함)
     *
     * @param techStackId 기술 스택 ID
     * @param architectureId 아키텍처 ID (nullable)
     * @param contextType 컨텍스트 유형
     * @param title 컨텍스트 제목
     * @param content 컨텍스트 내용
     * @param priority 우선순위
     * @param now 현재 시각 (Instant.now() 외부 주입)
     * @return 새로운 OnboardingContext 인스턴스
     */
    public static OnboardingContext forNew(
            TechStackId techStackId,
            ArchitectureId architectureId,
            ContextType contextType,
            ContextTitle title,
            ContextContent content,
            Priority priority,
            Instant now) {
        return new OnboardingContext(
                OnboardingContextId.forNew(),
                techStackId,
                architectureId,
                contextType,
                title,
                content,
                priority,
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * 영속성에서 복원용 팩토리 메서드
     *
     * @return 복원된 OnboardingContext 인스턴스
     */
    public static OnboardingContext reconstitute(
            OnboardingContextId id,
            TechStackId techStackId,
            ArchitectureId architectureId,
            ContextType contextType,
            ContextTitle title,
            ContextContent content,
            Priority priority,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        return new OnboardingContext(
                id,
                techStackId,
                architectureId,
                contextType,
                title,
                content,
                priority,
                deletionStatus,
                createdAt,
                updatedAt);
    }

    /**
     * 신규 엔티티 여부 확인
     *
     * @return ID가 null이면 true
     */
    public boolean isNew() {
        return id.isNew();
    }

    /**
     * ID 할당 (영속화 후 호출)
     *
     * @param id 할당할 ID
     */
    public void assignId(OnboardingContextId id) {
        if (!this.id.isNew()) {
            throw new IllegalStateException("id already assigned");
        }
        this.id = id;
    }

    /**
     * 온보딩 컨텍스트 정보 수정
     *
     * @param data 수정 데이터
     * @param now 현재 시각 (Instant.now() 외부 주입)
     */
    public void update(OnboardingContextUpdateData data, Instant now) {
        this.contextType = data.contextType();
        this.title = data.title();
        this.content = data.content();
        this.priority = data.priority();
        this.updatedAt = now;
    }

    /**
     * 온보딩 컨텍스트 삭제 (Soft Delete)
     *
     * @param now 삭제 발생 시각
     */
    public void delete(Instant now) {
        this.deletionStatus = DeletionStatus.deletedAt(now);
        this.updatedAt = now;
    }

    /**
     * 삭제된 온보딩 컨텍스트 복원
     *
     * @param now 복원 시각
     */
    public void restore(Instant now) {
        this.deletionStatus = DeletionStatus.active();
        this.updatedAt = now;
    }

    /**
     * 삭제 여부 확인
     *
     * @return 삭제되었으면 true
     */
    public boolean isDeleted() {
        return deletionStatus.isDeleted();
    }

    // Getters
    public OnboardingContextId id() {
        return id;
    }

    public TechStackId techStackId() {
        return techStackId;
    }

    public ArchitectureId architectureId() {
        return architectureId;
    }

    public ContextType contextType() {
        return contextType;
    }

    public ContextTitle title() {
        return title;
    }

    public ContextContent content() {
        return content;
    }

    public Priority priority() {
        return priority;
    }

    public DeletionStatus deletionStatus() {
        return deletionStatus;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    // Value Object 위임 메서드 (Law of Demeter 준수)

    /**
     * ID 원시값 반환
     *
     * @return ID 값 (신규인 경우 null)
     */
    public Long idValue() {
        return id != null ? id.value() : null;
    }

    /**
     * Tech Stack ID 원시값 반환
     *
     * @return Tech Stack ID 값
     */
    public Long techStackIdValue() {
        return techStackId.value();
    }

    /**
     * Architecture ID 원시값 반환
     *
     * @return Architecture ID 값 (nullable)
     */
    public Long architectureIdValue() {
        return architectureId != null ? architectureId.value() : null;
    }

    /**
     * Context Type 이름 반환
     *
     * @return Context Type enum 이름
     */
    public String contextTypeName() {
        return contextType.name();
    }

    /**
     * Title 원시값 반환
     *
     * @return 제목 문자열
     */
    public String titleValue() {
        return title.value();
    }

    /**
     * Content 원시값 반환
     *
     * @return 내용 문자열
     */
    public String contentValue() {
        return content.value();
    }

    /**
     * Priority 원시값 반환
     *
     * @return 우선순위 정수
     */
    public Integer priorityValue() {
        return priority != null ? priority.value() : 0;
    }

    /**
     * 삭제 시각 반환
     *
     * @return 삭제 시각 (활성 상태인 경우 null)
     */
    public Instant deletedAt() {
        return deletionStatus.deletedAt();
    }
}
