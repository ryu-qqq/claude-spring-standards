package com.ryuqq.adapter.out.persistence.ruleexample.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.out.persistence.common.MapperTestSupport;
import com.ryuqq.adapter.out.persistence.ruleexample.entity.RuleExampleJpaEntity;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.ruleexample.aggregate.RuleExample;
import com.ryuqq.domain.ruleexample.id.RuleExampleId;
import com.ryuqq.domain.ruleexample.vo.ExampleCode;
import com.ryuqq.domain.ruleexample.vo.ExampleLanguage;
import com.ryuqq.domain.ruleexample.vo.ExampleSource;
import com.ryuqq.domain.ruleexample.vo.ExampleType;
import com.ryuqq.domain.ruleexample.vo.HighlightLines;
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
 * RuleExampleJpaEntityMapper 단위 테스트
 *
 * @author Development Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("mapper")
@Tag("persistence-layer")
@DisplayName("RuleExampleJpaEntityMapper 단위 테스트")
class RuleExampleJpaEntityMapperTest extends MapperTestSupport {

    private RuleExampleJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new RuleExampleJpaEntityMapper();
    }

    @Nested
    @DisplayName("toDomain()")
    class ToDomain {

        @Test
        @DisplayName("성공 - Entity를 Domain으로 변환")
        void success() {
            // Given
            Instant now = Instant.now();
            RuleExampleJpaEntity entity = createTestEntity(now);

            // When
            RuleExample domain = mapper.toDomain(entity);

            // Then
            assertThat(domain).isNotNull();
            assertThat(domain.idValue()).isEqualTo(1L);
            assertThat(domain.ruleIdValue()).isEqualTo(100L);
            assertThat(domain.exampleType()).isEqualTo(ExampleType.GOOD);
            assertThat(domain.codeValue()).isEqualTo("public class Test { }");
        }

        @Test
        @DisplayName("성공 - null Entity는 null 반환")
        void nullEntity() {
            // Given
            RuleExampleJpaEntity entity = null;

            // When
            RuleExample domain = mapper.toDomain(entity);

            // Then
            assertThat(domain).isNull();
        }
    }

    @Nested
    @DisplayName("toEntity()")
    class ToEntity {

        @Test
        @DisplayName("성공 - Domain을 Entity로 변환")
        void success() {
            // Given
            Instant now = Instant.now();
            RuleExample domain = createTestDomain(now);

            // When
            RuleExampleJpaEntity entity = mapper.toEntity(domain);

            // Then
            assertThat(entity).isNotNull();
            assertThat(entity.getId()).isEqualTo(1L);
            assertThat(entity.getRuleId()).isEqualTo(100L);
            assertThat(entity.getExampleType()).isEqualTo("GOOD");
            assertThat(entity.getCode()).isEqualTo("public class Test { }");
        }

        @Test
        @DisplayName("성공 - null Domain은 null 반환")
        void nullDomain() {
            // Given
            RuleExample domain = null;

            // When
            RuleExampleJpaEntity entity = mapper.toEntity(domain);

            // Then
            assertThat(entity).isNull();
        }
    }

    // Helper methods
    private RuleExampleJpaEntity createTestEntity(Instant now) {
        return RuleExampleJpaEntity.of(
                1L,
                100L,
                "GOOD",
                "public class Test { }",
                "JAVA",
                "Test Explanation",
                "[1,2]",
                "MANUAL",
                null,
                now,
                now,
                null);
    }

    private RuleExample createTestDomain(Instant now) {
        return RuleExample.reconstitute(
                RuleExampleId.of(1L),
                CodingRuleId.of(100L),
                ExampleType.GOOD,
                ExampleCode.of("public class Test { }"),
                ExampleLanguage.JAVA,
                "Test Explanation",
                HighlightLines.of(List.of(1, 2)),
                ExampleSource.MANUAL,
                null,
                DeletionStatus.active(),
                now,
                now);
    }
}
