package com.ryuqq.adapter.out.persistence.techstack.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.out.persistence.common.MapperTestSupport;
import com.ryuqq.adapter.out.persistence.config.PersistenceObjectMapper;
import com.ryuqq.adapter.out.persistence.techstack.entity.TechStackJpaEntity;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.common.vo.ReferenceLinks;
import com.ryuqq.domain.techstack.aggregate.TechStack;
import com.ryuqq.domain.techstack.id.TechStackId;
import com.ryuqq.domain.techstack.vo.BuildConfigFile;
import com.ryuqq.domain.techstack.vo.BuildToolType;
import com.ryuqq.domain.techstack.vo.FrameworkModules;
import com.ryuqq.domain.techstack.vo.FrameworkType;
import com.ryuqq.domain.techstack.vo.FrameworkVersion;
import com.ryuqq.domain.techstack.vo.LanguageFeatures;
import com.ryuqq.domain.techstack.vo.LanguageType;
import com.ryuqq.domain.techstack.vo.LanguageVersion;
import com.ryuqq.domain.techstack.vo.PlatformType;
import com.ryuqq.domain.techstack.vo.RuntimeEnvironment;
import com.ryuqq.domain.techstack.vo.TechStackName;
import com.ryuqq.domain.techstack.vo.TechStackStatus;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * TechStackEntityMapper 단위 테스트
 *
 * @author Development Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("mapper")
@Tag("persistence-layer")
@DisplayName("TechStackEntityMapper 단위 테스트")
class TechStackEntityMapperTest extends MapperTestSupport {

    @Mock private PersistenceObjectMapper objectMapper;

    private TechStackEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TechStackEntityMapper(objectMapper);
    }

    @Nested
    @DisplayName("toDomain()")
    class ToDomain {

        @Test
        @DisplayName("성공 - Entity를 Domain으로 변환")
        void success() {
            // Given
            Instant now = Instant.now();
            TechStackJpaEntity entity = createTestEntity(now);

            // When
            TechStack domain = mapper.toDomain(entity);

            // Then
            assertThat(domain).isNotNull();
            assertThat(domain.idValue()).isEqualTo(1L);
            assertThat(domain.nameValue()).isEqualTo("Spring Boot");
            assertThat(domain.status()).isEqualTo(TechStackStatus.ACTIVE);
            assertThat(domain.languageType()).isEqualTo(LanguageType.JAVA);
        }

        @Test
        @DisplayName("성공 - null Entity는 null 반환")
        void nullEntity() {
            // Given
            TechStackJpaEntity entity = null;

            // When
            TechStack domain = mapper.toDomain(entity);

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
            TechStack domain = createTestDomain(now);

            // When
            TechStackJpaEntity entity = mapper.toEntity(domain);

            // Then
            assertThat(entity).isNotNull();
            assertThat(entity.getId()).isEqualTo(1L);
            assertThat(entity.getName()).isEqualTo("Spring Boot");
            assertThat(entity.getStatus()).isEqualTo("ACTIVE");
            assertThat(entity.getLanguageType()).isEqualTo("JAVA");
        }

        @Test
        @DisplayName("성공 - null Domain은 null 반환")
        void nullDomain() {
            // Given
            TechStack domain = null;

            // When
            TechStackJpaEntity entity = mapper.toEntity(domain);

            // Then
            assertThat(entity).isNull();
        }
    }

    // Helper methods
    private TechStackJpaEntity createTestEntity(Instant now) {
        return TechStackJpaEntity.of(
                1L,
                "Spring Boot",
                "ACTIVE",
                "JAVA",
                "21",
                "[]",
                "SPRING_BOOT",
                "3.5.0",
                "[]",
                "BACKEND",
                "JVM",
                "GRADLE",
                "build.gradle",
                "[]",
                now,
                now,
                null);
    }

    private TechStack createTestDomain(Instant now) {
        return TechStack.reconstitute(
                TechStackId.of(1L),
                TechStackName.of("Spring Boot"),
                TechStackStatus.ACTIVE,
                LanguageType.JAVA,
                LanguageVersion.of("21"),
                LanguageFeatures.empty(),
                FrameworkType.SPRING_BOOT,
                FrameworkVersion.of("3.5.0"),
                FrameworkModules.empty(),
                PlatformType.BACKEND,
                RuntimeEnvironment.JVM,
                BuildToolType.GRADLE,
                BuildConfigFile.of("build.gradle"),
                ReferenceLinks.empty(),
                DeletionStatus.active(),
                now,
                now);
    }
}
