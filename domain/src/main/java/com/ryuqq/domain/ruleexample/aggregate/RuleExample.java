package com.ryuqq.domain.ruleexample.aggregate;

import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.ruleexample.id.RuleExampleId;
import com.ryuqq.domain.ruleexample.vo.ExampleCode;
import com.ryuqq.domain.ruleexample.vo.ExampleLanguage;
import com.ryuqq.domain.ruleexample.vo.ExampleSource;
import com.ryuqq.domain.ruleexample.vo.ExampleType;
import com.ryuqq.domain.ruleexample.vo.HighlightLines;
import java.time.Instant;

/**
 * RuleExample - 규칙 예시 Aggregate Root
 *
 * <p>코딩 규칙의 Good/Bad 예시를 정의합니다.
 *
 * @author ryu-qqq
 */
public class RuleExample {

    private RuleExampleId id;
    private CodingRuleId ruleId;
    private ExampleType exampleType;
    private ExampleCode code;
    private ExampleLanguage language;
    private String explanation;
    private HighlightLines highlightLines;
    private ExampleSource source;
    private Long feedbackId;
    private DeletionStatus deletionStatus;

    // Audit
    private final Instant createdAt;
    private Instant updatedAt;

    /** 프레임워크 호환용 기본 생성자 */
    protected RuleExample() {
        this.createdAt = null;
    }

    private RuleExample(
            RuleExampleId id,
            CodingRuleId ruleId,
            ExampleType exampleType,
            ExampleCode code,
            ExampleLanguage language,
            String explanation,
            HighlightLines highlightLines,
            ExampleSource source,
            Long feedbackId,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.ruleId = ruleId;
        this.exampleType = exampleType;
        this.code = code;
        this.language = language;
        this.explanation = explanation;
        this.highlightLines = highlightLines != null ? highlightLines : HighlightLines.empty();
        this.source = source != null ? source : ExampleSource.MANUAL;
        this.feedbackId = feedbackId;
        this.deletionStatus = deletionStatus != null ? deletionStatus : DeletionStatus.active();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 신규 생성용 팩토리 메서드
     *
     * @param ruleId 코딩 규칙 ID
     * @param exampleType 예시 타입
     * @param code 예시 코드
     * @param language 언어
     * @param explanation 설명
     * @param highlightLines 하이라이트 라인
     * @param now 현재 시각 (Instant.now() 외부 주입)
     * @return 새로운 RuleExample 인스턴스
     */
    public static RuleExample forNew(
            CodingRuleId ruleId,
            ExampleType exampleType,
            ExampleCode code,
            ExampleLanguage language,
            String explanation,
            HighlightLines highlightLines,
            Instant now) {
        return new RuleExample(
                RuleExampleId.forNew(),
                ruleId,
                exampleType,
                code,
                language,
                explanation,
                highlightLines,
                ExampleSource.MANUAL,
                null,
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * 에이전트 피드백에서 승격된 예시 생성
     *
     * @param ruleId 코딩 규칙 ID
     * @param exampleType 예시 타입
     * @param code 예시 코드
     * @param language 언어
     * @param explanation 설명
     * @param highlightLines 하이라이트 라인
     * @param feedbackId 피드백 ID
     * @param now 현재 시각 (Instant.now() 외부 주입)
     * @return 피드백에서 승격된 RuleExample 인스턴스
     */
    public static RuleExample fromFeedback(
            CodingRuleId ruleId,
            ExampleType exampleType,
            ExampleCode code,
            ExampleLanguage language,
            String explanation,
            HighlightLines highlightLines,
            Long feedbackId,
            Instant now) {
        return new RuleExample(
                RuleExampleId.forNew(),
                ruleId,
                exampleType,
                code,
                language,
                explanation,
                highlightLines,
                ExampleSource.AGENT_FEEDBACK,
                feedbackId,
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * 기존 데이터 래핑용 팩토리 메서드
     *
     * @param id 규칙 예시 ID
     * @param ruleId 코딩 규칙 ID
     * @param exampleType 예시 타입
     * @param code 예시 코드
     * @param language 언어
     * @param explanation 설명
     * @param highlightLines 하이라이트 라인
     * @param source 예시 소스
     * @param feedbackId 피드백 ID
     * @param deletionStatus 삭제 상태
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     * @return RuleExample 인스턴스
     */
    public static RuleExample of(
            RuleExampleId id,
            CodingRuleId ruleId,
            ExampleType exampleType,
            ExampleCode code,
            ExampleLanguage language,
            String explanation,
            HighlightLines highlightLines,
            ExampleSource source,
            Long feedbackId,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        return new RuleExample(
                id,
                ruleId,
                exampleType,
                code,
                language,
                explanation,
                highlightLines,
                source,
                feedbackId,
                deletionStatus,
                createdAt,
                updatedAt);
    }

    /**
     * 영속성에서 복원용 팩토리 메서드
     *
     * @param id 규칙 예시 ID
     * @param ruleId 코딩 규칙 ID
     * @param exampleType 예시 타입
     * @param code 예시 코드
     * @param language 언어
     * @param explanation 설명
     * @param highlightLines 하이라이트 라인
     * @param source 예시 소스
     * @param feedbackId 피드백 ID
     * @param deletionStatus 삭제 상태
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     * @return 복원된 RuleExample 인스턴스
     */
    public static RuleExample reconstitute(
            RuleExampleId id,
            CodingRuleId ruleId,
            ExampleType exampleType,
            ExampleCode code,
            ExampleLanguage language,
            String explanation,
            HighlightLines highlightLines,
            ExampleSource source,
            Long feedbackId,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        return of(
                id,
                ruleId,
                exampleType,
                code,
                language,
                explanation,
                highlightLines,
                source,
                feedbackId,
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
    public void assignId(RuleExampleId id) {
        if (!this.id.isNew()) {
            throw new IllegalStateException("id already assigned");
        }
        this.id = id;
    }

    /** 올바른 예시인지 확인 */
    public boolean isGoodExample() {
        return exampleType.isGood();
    }

    /** 잘못된 예시인지 확인 */
    public boolean isBadExample() {
        return exampleType.isBad();
    }

    /** 피드백에서 승격된 예시인지 확인 */
    public boolean isFromFeedback() {
        return source == ExampleSource.AGENT_FEEDBACK;
    }

    /**
     * 규칙 예시 삭제 (Soft Delete)
     *
     * @param now 삭제 발생 시각
     */
    public void delete(Instant now) {
        this.deletionStatus = DeletionStatus.deletedAt(now);
        this.updatedAt = now;
    }

    /**
     * 삭제된 규칙 예시 복원
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
     * 규칙 예시 업데이트
     *
     * <p>RuleExampleUpdateData의 Optional 필드 중 present인 항목만 업데이트합니다.
     *
     * @param updateData 업데이트 데이터
     * @param now 업데이트 시각
     */
    public void update(RuleExampleUpdateData updateData, Instant now) {
        if (updateData == null || !updateData.hasUpdates()) {
            return;
        }

        updateData.exampleType().ifPresent(value -> this.exampleType = value);
        updateData.code().ifPresent(value -> this.code = value);
        updateData.language().ifPresent(value -> this.language = value);
        updateData.explanation().ifPresent(value -> this.explanation = value);
        updateData.highlightLines().ifPresent(value -> this.highlightLines = value);

        this.updatedAt = now;
    }

    // Getters
    public RuleExampleId id() {
        return id;
    }

    public CodingRuleId ruleId() {
        return ruleId;
    }

    public ExampleType exampleType() {
        return exampleType;
    }

    public ExampleCode code() {
        return code;
    }

    public ExampleLanguage language() {
        return language;
    }

    public String explanation() {
        return explanation;
    }

    public HighlightLines highlightLines() {
        return highlightLines;
    }

    public ExampleSource source() {
        return source;
    }

    public Long feedbackId() {
        return feedbackId;
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
    // Persistence Layer Mapper에서 체이닝 방지: domain.id().value() 대신 domain.idValue() 사용

    /**
     * ID 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return ID 값 (신규인 경우 null)
     */
    public Long idValue() {
        return id != null ? id.value() : null;
    }

    /**
     * Rule ID 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return Rule ID 값
     */
    public Long ruleIdValue() {
        return ruleId.value();
    }

    /**
     * Example Code 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return 예시 코드 문자열
     */
    public String codeValue() {
        return code.value();
    }

    /**
     * Example Type 이름 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return Example Type enum 이름
     */
    public String exampleTypeName() {
        return exampleType.name();
    }

    /**
     * Language 이름 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return Language enum 이름
     */
    public String languageName() {
        return language.name();
    }

    /**
     * Source 이름 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return Source enum 이름 (nullable)
     */
    public String sourceName() {
        return source != null ? source.name() : null;
    }

    /**
     * 삭제 시각 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return 삭제 시각 (활성 상태인 경우 null)
     */
    public Instant deletedAt() {
        return deletionStatus.deletedAt();
    }
}
