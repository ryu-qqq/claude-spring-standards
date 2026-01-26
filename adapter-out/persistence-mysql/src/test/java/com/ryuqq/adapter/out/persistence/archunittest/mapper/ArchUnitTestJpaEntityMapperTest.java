package com.ryuqq.adapter.out.persistence.archunittest.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.out.persistence.archunittest.entity.ArchUnitTestJpaEntity;
import com.ryuqq.adapter.out.persistence.common.MapperTestSupport;
import com.ryuqq.domain.archunittest.aggregate.ArchUnitTest;
import com.ryuqq.domain.archunittest.id.ArchUnitTestId;
import com.ryuqq.domain.archunittest.vo.ArchUnitTestDescription;
import com.ryuqq.domain.archunittest.vo.ArchUnitTestName;
import com.ryuqq.domain.archunittest.vo.ArchUnitTestSeverity;
import com.ryuqq.domain.archunittest.vo.TestCode;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ArchUnitTestJpaEntityMapper 단위 테스트
 *
 * @author Development Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("mapper")
@Tag("persistence-layer")
@DisplayName("ArchUnitTestJpaEntityMapper 단위 테스트")
class ArchUnitTestJpaEntityMapperTest extends MapperTestSupport {

    private ArchUnitTestJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ArchUnitTestJpaEntityMapper();
    }

    @Nested
    @DisplayName("toDomain()")
    class ToDomain {

        @Test
        @DisplayName("성공 - Entity를 Domain으로 변환")
        void success() {
            // Given
            Instant now = Instant.now();
            ArchUnitTestJpaEntity entity =
                    ArchUnitTestJpaEntity.of(
                            1L,
                            100L,
                            "TEST-001",
                            "Test Name",
                            "Test Description",
                            "TestClass",
                            "testMethod",
                            "test code content",
                            "BLOCKER",
                            now,
                            now,
                            null);

            // When
            ArchUnitTest domain = mapper.toDomain(entity);

            // Then
            assertThat(domain).isNotNull();
            assertThat(domain.idValue()).isEqualTo(1L);
            assertThat(domain.structureIdValue()).isEqualTo(100L);
        }

        @Test
        @DisplayName("성공 - null Entity는 null 반환")
        void nullEntity() {
            // Given
            ArchUnitTestJpaEntity entity = null;

            // When
            ArchUnitTest domain = mapper.toDomain(entity);

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
            ArchUnitTest domain =
                    ArchUnitTest.reconstitute(
                            ArchUnitTestId.of(1L),
                            PackageStructureId.of(100L),
                            "TEST-001",
                            ArchUnitTestName.of("Test Name"),
                            ArchUnitTestDescription.of("Test Description"),
                            "TestClass",
                            "testMethod",
                            TestCode.of("test code"),
                            ArchUnitTestSeverity.BLOCKER,
                            DeletionStatus.active(),
                            now,
                            now);

            // When
            ArchUnitTestJpaEntity entity = mapper.toEntity(domain);

            // Then
            assertThat(entity).isNotNull();
            assertThat(entity.getId()).isEqualTo(1L);
            assertThat(entity.getStructureId()).isEqualTo(100L);
        }
    }
}
