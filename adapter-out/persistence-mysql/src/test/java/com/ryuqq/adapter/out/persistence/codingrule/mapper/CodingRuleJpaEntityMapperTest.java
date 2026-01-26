package com.ryuqq.adapter.out.persistence.codingrule.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.out.persistence.codingrule.entity.CodingRuleJpaEntity;
import com.ryuqq.adapter.out.persistence.common.MapperTestSupport;
import com.ryuqq.domain.codingrule.aggregate.CodingRule;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.codingrule.vo.AppliesTo;
import com.ryuqq.domain.codingrule.vo.RuleCategory;
import com.ryuqq.domain.codingrule.vo.RuleCode;
import com.ryuqq.domain.codingrule.vo.RuleName;
import com.ryuqq.domain.codingrule.vo.RuleSeverity;
import com.ryuqq.domain.codingrule.vo.SdkConstraint;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.convention.id.ConventionId;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * CodingRuleJpaEntityMapper 단위 테스트
 *
 * @author Development Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("mapper")
@Tag("persistence-layer")
@DisplayName("CodingRuleJpaEntityMapper 단위 테스트")
class CodingRuleJpaEntityMapperTest extends MapperTestSupport {

    private CodingRuleJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CodingRuleJpaEntityMapper();
    }

    @Nested
    @DisplayName("toDomain()")
    class ToDomain {

        @Test
        @DisplayName("성공 - Entity를 Domain으로 변환")
        void success() {
            // Given
            Instant now = Instant.now();
            CodingRuleJpaEntity entity =
                    CodingRuleJpaEntity.ofInstant(
                            1L,
                            100L,
                            "DOM-001",
                            "Lombok 금지",
                            RuleSeverity.BLOCKER,
                            RuleCategory.ANNOTATION,
                            "Domain Layer에서 Lombok 사용 금지",
                            "Pure Java 원칙",
                            false,
                            "AGGREGATE,VALUE_OBJECT",
                            now,
                            now,
                            null);

            // When
            CodingRule domain = mapper.toDomain(entity);

            // Then
            assertThat(domain.idValue()).isEqualTo(1L);
            assertThat(domain.conventionIdValue()).isEqualTo(100L);
            assertThat(domain.codeValue()).isEqualTo("DOM-001");
            assertThat(domain.nameValue()).isEqualTo("Lombok 금지");
            assertThat(domain.severity()).isEqualTo(RuleSeverity.BLOCKER);
            assertThat(domain.category()).isEqualTo(RuleCategory.ANNOTATION);
        }

        @Test
        @DisplayName("성공 - null Entity는 null 반환")
        void nullEntity() {
            // Given
            CodingRuleJpaEntity entity = null;

            // When
            CodingRule domain = mapper.toDomain(entity);

            // Then
            assertThat(domain).isNull();
        }

        @Test
        @DisplayName("성공 - AppliesTo 파싱")
        void appliesToParsing() {
            // Given
            Instant now = Instant.now();
            CodingRuleJpaEntity entity =
                    CodingRuleJpaEntity.ofInstant(
                            1L,
                            100L,
                            "DOM-001",
                            "Test Rule",
                            RuleSeverity.BLOCKER,
                            RuleCategory.ANNOTATION,
                            "Description",
                            "Rationale",
                            false,
                            "AGGREGATE,VALUE_OBJECT,ENTITY",
                            now,
                            now,
                            null);

            // When
            CodingRule domain = mapper.toDomain(entity);

            // Then
            assertThat(domain.appliesTo().targets()).hasSize(3);
            assertThat(domain.appliesTo().targets())
                    .containsExactly("AGGREGATE", "VALUE_OBJECT", "ENTITY");
        }
    }

    @Nested
    @DisplayName("toEntity()")
    class ToEntity {

        @Test
        @DisplayName("성공 - 신규 Domain을 Entity로 변환")
        void newDomain() {
            // Given
            Instant now = Instant.now();
            CodingRule domain =
                    CodingRule.reconstitute(
                            CodingRuleId.forNew(),
                            ConventionId.of(100L),
                            null,
                            RuleCode.of("DOM-001"),
                            RuleName.of("Lombok 금지"),
                            RuleSeverity.BLOCKER,
                            RuleCategory.ANNOTATION,
                            "Description",
                            "Rationale",
                            false,
                            AppliesTo.of(List.of("AGGREGATE", "VALUE_OBJECT")),
                            SdkConstraint.empty(),
                            DeletionStatus.active(),
                            now,
                            now);

            // When
            CodingRuleJpaEntity entity = mapper.toEntity(domain);

            // Then
            assertThat(entity.getId()).isNull(); // 신규이므로 ID 없음
            assertThat(entity.getConventionId()).isEqualTo(100L);
            assertThat(entity.getCode()).isEqualTo("DOM-001");
            assertThat(entity.getName()).isEqualTo("Lombok 금지");
        }

        @Test
        @DisplayName("성공 - 기존 Domain을 Entity로 변환 (ID 유지)")
        void existingDomain() {
            // Given
            Instant now = Instant.now();
            CodingRule domain =
                    CodingRule.reconstitute(
                            CodingRuleId.of(1L),
                            ConventionId.of(100L),
                            null,
                            RuleCode.of("DOM-001"),
                            RuleName.of("Lombok 금지"),
                            RuleSeverity.BLOCKER,
                            RuleCategory.ANNOTATION,
                            "Description",
                            "Rationale",
                            false,
                            AppliesTo.of(List.of("AGGREGATE")),
                            SdkConstraint.empty(),
                            DeletionStatus.active(),
                            now,
                            now);

            // When
            CodingRuleJpaEntity entity = mapper.toEntity(domain);

            // Then
            assertThat(entity.getId()).isEqualTo(1L);
            assertThat(entity.getCode()).isEqualTo("DOM-001");
        }
    }
}
