package com.ryuqq.domain.ruleexample.fixture;

import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.ruleexample.aggregate.RuleExample;
import com.ryuqq.domain.ruleexample.id.RuleExampleId;
import com.ryuqq.domain.ruleexample.vo.ExampleCode;
import com.ryuqq.domain.ruleexample.vo.ExampleLanguage;
import com.ryuqq.domain.ruleexample.vo.ExampleSource;
import com.ryuqq.domain.ruleexample.vo.ExampleType;
import com.ryuqq.domain.ruleexample.vo.HighlightLines;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * RuleExample Aggregate Test Fixture
 *
 * <p>모든 레이어에서 재사용 가능한 RuleExample 객체 생성 유틸리티
 *
 * @author ryu-qqq
 */
public final class RuleExampleFixture {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneId.of("UTC"));

    private static final AtomicLong ID_SEQUENCE = new AtomicLong(1L);

    private RuleExampleFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /**
     * 고정 CodingRule ID
     *
     * @return CodingRuleId
     */
    public static CodingRuleId fixedCodingRuleId() {
        return CodingRuleId.of(100L);
    }

    /**
     * 다음 RuleExampleId 생성
     *
     * @return RuleExampleId
     */
    public static RuleExampleId nextRuleExampleId() {
        return RuleExampleId.of(ID_SEQUENCE.getAndIncrement());
    }

    /**
     * 신규 RuleExample 생성 (ID 미할당)
     *
     * @return 신규 RuleExample
     */
    public static RuleExample forNew() {
        return RuleExample.forNew(
                fixedCodingRuleId(),
                ExampleType.GOOD,
                ExampleCode.of("public class Order { }"),
                ExampleLanguage.JAVA,
                "올바른 예시 설명",
                HighlightLines.of(List.of(1, 2)),
                FIXED_CLOCK.instant());
    }

    /**
     * 기존 RuleExample 복원 (기본 설정)
     *
     * @return 복원된 RuleExample
     */
    public static RuleExample reconstitute() {
        return reconstitute(nextRuleExampleId());
    }

    /**
     * 지정된 ID로 RuleExample 복원
     *
     * @param id RuleExampleId
     * @return 복원된 RuleExample
     */
    public static RuleExample reconstitute(RuleExampleId id) {
        Instant now = FIXED_CLOCK.instant();
        return RuleExample.reconstitute(
                id,
                fixedCodingRuleId(),
                ExampleType.GOOD,
                ExampleCode.of("public class Order { }"),
                ExampleLanguage.JAVA,
                "올바른 예시 설명",
                HighlightLines.of(List.of(1, 2)),
                ExampleSource.MANUAL,
                null,
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * 기본 기존 RuleExample (저장된 상태)
     *
     * @return 기존 RuleExample
     */
    public static RuleExample defaultExistingRuleExample() {
        Instant now = FIXED_CLOCK.instant();
        return RuleExample.of(
                nextRuleExampleId(),
                fixedCodingRuleId(),
                ExampleType.GOOD,
                ExampleCode.of("public class Order { }"),
                ExampleLanguage.JAVA,
                "올바른 예시 설명",
                HighlightLines.of(List.of(1, 2)),
                ExampleSource.MANUAL,
                null,
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * GOOD 예시
     *
     * @return GOOD RuleExample
     */
    public static RuleExample goodExample() {
        Instant now = FIXED_CLOCK.instant();
        return RuleExample.reconstitute(
                nextRuleExampleId(),
                fixedCodingRuleId(),
                ExampleType.GOOD,
                ExampleCode.of("public class Order { }"),
                ExampleLanguage.JAVA,
                "올바른 예시 설명",
                HighlightLines.of(List.of(1)),
                ExampleSource.MANUAL,
                null,
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * BAD 예시
     *
     * @return BAD RuleExample
     */
    public static RuleExample badExample() {
        Instant now = FIXED_CLOCK.instant();
        return RuleExample.reconstitute(
                nextRuleExampleId(),
                fixedCodingRuleId(),
                ExampleType.BAD,
                ExampleCode.of("public class order { }"),
                ExampleLanguage.JAVA,
                "잘못된 예시 설명",
                HighlightLines.of(List.of(1)),
                ExampleSource.MANUAL,
                null,
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * 피드백에서 승격된 예시
     *
     * @return 피드백에서 승격된 RuleExample
     */
    public static RuleExample fromFeedbackExample() {
        Instant now = FIXED_CLOCK.instant();
        return RuleExample.fromFeedback(
                fixedCodingRuleId(),
                ExampleType.GOOD,
                ExampleCode.of("public class Order { }"),
                ExampleLanguage.JAVA,
                "피드백에서 승격된 예시",
                HighlightLines.of(List.of(1)),
                999L,
                now);
    }

    /**
     * 삭제된 RuleExample
     *
     * @return 삭제된 RuleExample
     */
    public static RuleExample deletedRuleExample() {
        Instant now = FIXED_CLOCK.instant();
        return RuleExample.reconstitute(
                nextRuleExampleId(),
                fixedCodingRuleId(),
                ExampleType.GOOD,
                ExampleCode.of("public class Order { }"),
                ExampleLanguage.JAVA,
                "올바른 예시 설명",
                HighlightLines.of(List.of(1, 2)),
                ExampleSource.MANUAL,
                null,
                DeletionStatus.deletedAt(now),
                now,
                now);
    }
}
