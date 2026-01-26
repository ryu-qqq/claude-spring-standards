package com.ryuqq.domain.zerotolerance.aggregate;

import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.zerotolerance.id.ZeroToleranceRuleId;
import com.ryuqq.domain.zerotolerance.vo.DetectionPattern;
import com.ryuqq.domain.zerotolerance.vo.DetectionType;
import com.ryuqq.domain.zerotolerance.vo.ErrorMessage;
import com.ryuqq.domain.zerotolerance.vo.ZeroToleranceType;
import java.time.Instant;

/**
 * ZeroToleranceRule - Zero Tolerance 규칙 Aggregate Root
 *
 * <p>프로젝트에서 절대로 허용하지 않는 코딩 규칙을 정의합니다. 위반 시 PR 자동 거부 등의 강력한 제재가 적용됩니다.
 *
 * <p>Convention 정보는 ruleId를 통해 CodingRule → Convention 경로로 조회합니다.
 *
 * @author ryu-qqq
 */
public class ZeroToleranceRule {

    private ZeroToleranceRuleId id;
    private CodingRuleId ruleId;
    private ZeroToleranceType type;
    private DetectionPattern detectionPattern;
    private DetectionType detectionType;
    private boolean autoRejectPr;
    private ErrorMessage errorMessage;
    private DeletionStatus deletionStatus;

    // Audit
    private final Instant createdAt;
    private Instant updatedAt;

    /** 프레임워크 호환용 기본 생성자 */
    protected ZeroToleranceRule() {
        this.createdAt = null;
    }

    private ZeroToleranceRule(
            ZeroToleranceRuleId id,
            CodingRuleId ruleId,
            ZeroToleranceType type,
            DetectionPattern detectionPattern,
            DetectionType detectionType,
            boolean autoRejectPr,
            ErrorMessage errorMessage,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.ruleId = ruleId;
        this.type = type;
        this.detectionPattern = detectionPattern;
        this.detectionType = detectionType;
        this.autoRejectPr = autoRejectPr;
        this.errorMessage = errorMessage;
        this.deletionStatus = deletionStatus != null ? deletionStatus : DeletionStatus.active();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 신규 생성용 팩토리 메서드
     *
     * @param ruleId 코딩 규칙 ID
     * @param type Zero Tolerance 타입
     * @param detectionPattern 탐지 패턴
     * @param detectionType 탐지 방식
     * @param autoRejectPr PR 자동 거부 여부
     * @param errorMessage 에러 메시지
     * @param now 현재 시각 (Instant.now() 외부 주입)
     * @return 새로운 ZeroToleranceRule 인스턴스
     */
    public static ZeroToleranceRule forNew(
            CodingRuleId ruleId,
            ZeroToleranceType type,
            DetectionPattern detectionPattern,
            DetectionType detectionType,
            boolean autoRejectPr,
            ErrorMessage errorMessage,
            Instant now) {
        return new ZeroToleranceRule(
                ZeroToleranceRuleId.forNew(),
                ruleId,
                type,
                detectionPattern,
                detectionType,
                autoRejectPr,
                errorMessage,
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * 기존 데이터 래핑용 팩토리 메서드
     *
     * @param id 규칙 ID
     * @param ruleId 코딩 규칙 ID
     * @param type Zero Tolerance 타입
     * @param detectionPattern 탐지 패턴
     * @param detectionType 탐지 방식
     * @param autoRejectPr PR 자동 거부 여부
     * @param errorMessage 에러 메시지
     * @param deletionStatus 삭제 상태
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     * @return ZeroToleranceRule 인스턴스
     */
    public static ZeroToleranceRule of(
            ZeroToleranceRuleId id,
            CodingRuleId ruleId,
            ZeroToleranceType type,
            DetectionPattern detectionPattern,
            DetectionType detectionType,
            boolean autoRejectPr,
            ErrorMessage errorMessage,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        return new ZeroToleranceRule(
                id,
                ruleId,
                type,
                detectionPattern,
                detectionType,
                autoRejectPr,
                errorMessage,
                deletionStatus,
                createdAt,
                updatedAt);
    }

    /**
     * 영속성에서 복원용 팩토리 메서드
     *
     * @param id 규칙 ID
     * @param ruleId 코딩 규칙 ID
     * @param type Zero Tolerance 타입
     * @param detectionPattern 탐지 패턴
     * @param detectionType 탐지 방식
     * @param autoRejectPr PR 자동 거부 여부
     * @param errorMessage 에러 메시지
     * @param deletionStatus 삭제 상태
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     * @return 복원된 ZeroToleranceRule 인스턴스
     */
    public static ZeroToleranceRule reconstitute(
            ZeroToleranceRuleId id,
            CodingRuleId ruleId,
            ZeroToleranceType type,
            DetectionPattern detectionPattern,
            DetectionType detectionType,
            boolean autoRejectPr,
            ErrorMessage errorMessage,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        return of(
                id,
                ruleId,
                type,
                detectionPattern,
                detectionType,
                autoRejectPr,
                errorMessage,
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
    public void assignId(ZeroToleranceRuleId id) {
        if (!this.id.isNew()) {
            throw new IllegalStateException("id already assigned");
        }
        this.id = id;
    }

    /**
     * 규칙 정보 수정
     *
     * @param data 수정 데이터
     * @param now 현재 시각 (Instant.now() 외부 주입)
     */
    public void update(ZeroToleranceRuleUpdateData data, Instant now) {
        this.type = data.type();
        this.detectionPattern = data.detectionPattern();
        this.detectionType = data.detectionType();
        this.autoRejectPr = data.autoRejectPr();
        this.errorMessage = data.errorMessage();
        this.updatedAt = now;
    }

    /**
     * PR 자동 거부 활성화
     *
     * @param now 현재 시각
     */
    public void enableAutoReject(Instant now) {
        this.autoRejectPr = true;
        this.updatedAt = now;
    }

    /**
     * PR 자동 거부 비활성화
     *
     * @param now 현재 시각
     */
    public void disableAutoReject(Instant now) {
        this.autoRejectPr = false;
        this.updatedAt = now;
    }

    /**
     * 규칙 삭제 (Soft Delete)
     *
     * @param now 삭제 발생 시각
     */
    public void delete(Instant now) {
        this.deletionStatus = DeletionStatus.deletedAt(now);
        this.updatedAt = now;
    }

    /**
     * 삭제된 규칙 복원
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

    /**
     * 주어진 코드 텍스트에서 위반 여부 확인
     *
     * @param codeText 검사할 코드 텍스트
     * @return 위반이면 true
     */
    public boolean detectViolation(String codeText) {
        if (detectionType == DetectionType.REGEX) {
            return detectionPattern.matches(codeText);
        }
        // AST, ARCHUNIT은 별도의 탐지 로직 필요
        return false;
    }

    // Getters
    public ZeroToleranceRuleId id() {
        return id;
    }

    public CodingRuleId ruleId() {
        return ruleId;
    }

    public ZeroToleranceType type() {
        return type;
    }

    public DetectionPattern detectionPattern() {
        return detectionPattern;
    }

    public DetectionType detectionType() {
        return detectionType;
    }

    public boolean autoRejectPr() {
        return autoRejectPr;
    }

    public ErrorMessage errorMessage() {
        return errorMessage;
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
}
