package com.ryuqq.domain.zerotolerance.fixture;

import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.zerotolerance.aggregate.ZeroToleranceRule;
import com.ryuqq.domain.zerotolerance.aggregate.ZeroToleranceRuleUpdateData;
import com.ryuqq.domain.zerotolerance.id.ZeroToleranceRuleId;
import com.ryuqq.domain.zerotolerance.vo.DetectionPattern;
import com.ryuqq.domain.zerotolerance.vo.DetectionType;
import com.ryuqq.domain.zerotolerance.vo.ErrorMessage;
import com.ryuqq.domain.zerotolerance.vo.ZeroToleranceType;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ZeroToleranceRule Aggregate Test Fixture
 *
 * <p>모든 레이어에서 재사용 가능한 ZeroToleranceRule 객체 생성 유틸리티
 *
 * @author ryu-qqq
 */
public final class ZeroToleranceRuleFixture {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneId.of("UTC"));

    private static final AtomicLong ID_SEQUENCE = new AtomicLong(1L);

    private ZeroToleranceRuleFixture() {
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
     * 다음 ZeroToleranceRuleId 생성
     *
     * @return ZeroToleranceRuleId
     */
    public static ZeroToleranceRuleId nextZeroToleranceRuleId() {
        return ZeroToleranceRuleId.of(ID_SEQUENCE.getAndIncrement());
    }

    /**
     * 신규 ZeroToleranceRule 생성 (ID 미할당)
     *
     * @return 신규 ZeroToleranceRule
     */
    public static ZeroToleranceRule forNew() {
        return ZeroToleranceRule.forNew(
                fixedCodingRuleId(),
                ZeroToleranceType.lombokInDomain(),
                DetectionPattern.of("@Data|@Getter|@Setter"),
                DetectionType.REGEX,
                false,
                ErrorMessage.of("Lombok 사용 금지"),
                FIXED_CLOCK.instant());
    }

    /**
     * 기존 ZeroToleranceRule 복원 (기본 설정)
     *
     * @return 복원된 ZeroToleranceRule
     */
    public static ZeroToleranceRule reconstitute() {
        return reconstitute(nextZeroToleranceRuleId());
    }

    /**
     * 지정된 ID로 ZeroToleranceRule 복원
     *
     * @param id ZeroToleranceRuleId
     * @return 복원된 ZeroToleranceRule
     */
    public static ZeroToleranceRule reconstitute(ZeroToleranceRuleId id) {
        Instant now = FIXED_CLOCK.instant();
        return ZeroToleranceRule.reconstitute(
                id,
                fixedCodingRuleId(),
                ZeroToleranceType.lombokInDomain(),
                DetectionPattern.of("@Data|@Getter|@Setter"),
                DetectionType.REGEX,
                false,
                ErrorMessage.of("Lombok 사용 금지"),
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * 기본 기존 ZeroToleranceRule (저장된 상태)
     *
     * @return 기존 ZeroToleranceRule
     */
    public static ZeroToleranceRule defaultExistingZeroToleranceRule() {
        Instant now = FIXED_CLOCK.instant();
        return ZeroToleranceRule.of(
                nextZeroToleranceRuleId(),
                fixedCodingRuleId(),
                ZeroToleranceType.lombokInDomain(),
                DetectionPattern.of("@Data|@Getter|@Setter"),
                DetectionType.REGEX,
                false,
                ErrorMessage.of("Lombok 사용 금지"),
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * PR 자동 거부 활성화된 ZeroToleranceRule
     *
     * @return PR 자동 거부 활성화된 ZeroToleranceRule
     */
    public static ZeroToleranceRule withAutoRejectPr() {
        Instant now = FIXED_CLOCK.instant();
        return ZeroToleranceRule.reconstitute(
                nextZeroToleranceRuleId(),
                fixedCodingRuleId(),
                ZeroToleranceType.lombokInDomain(),
                DetectionPattern.of("@Data|@Getter|@Setter"),
                DetectionType.REGEX,
                true,
                ErrorMessage.of("Lombok 사용 금지 - PR 자동 거부"),
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * 삭제된 ZeroToleranceRule
     *
     * @return 삭제된 ZeroToleranceRule
     */
    public static ZeroToleranceRule deletedZeroToleranceRule() {
        Instant now = FIXED_CLOCK.instant();
        return ZeroToleranceRule.reconstitute(
                nextZeroToleranceRuleId(),
                fixedCodingRuleId(),
                ZeroToleranceType.lombokInDomain(),
                DetectionPattern.of("@Data|@Getter|@Setter"),
                DetectionType.REGEX,
                false,
                ErrorMessage.of("Lombok 사용 금지"),
                DeletionStatus.deletedAt(now),
                now,
                now);
    }

    /**
     * 기본 ZeroToleranceRuleUpdateData
     *
     * @return ZeroToleranceRuleUpdateData
     */
    public static ZeroToleranceRuleUpdateData defaultUpdateData() {
        return new ZeroToleranceRuleUpdateData(
                ZeroToleranceType.setterUsage(),
                DetectionPattern.of("@Setter"),
                DetectionType.REGEX,
                true,
                ErrorMessage.of("Setter 사용 금지 - PR 자동 거부"));
    }
}
