package com.ryuqq.domain.architecture.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.domain.architecture.fixture.ArchitectureFixture;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.architecture.vo.ArchitectureName;
import com.ryuqq.domain.architecture.vo.PatternDescription;
import com.ryuqq.domain.architecture.vo.PatternPrinciples;
import com.ryuqq.domain.architecture.vo.PatternType;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.common.vo.ReferenceLinks;
import com.ryuqq.domain.techstack.id.TechStackId;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Architecture Aggregate 단위 테스트
 *
 * @author development-team
 */
@DisplayName("Architecture Aggregate")
class ArchitectureTest {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneId.of("UTC"));

    @Nested
    @DisplayName("생성")
    class CreateArchitecture {

        @Test
        @DisplayName("신규 Architecture 생성 성공")
        void forNew_WithValidData_ShouldSucceed() {
            // given
            TechStackId techStackId = TechStackId.of(1L);
            ArchitectureName name = ArchitectureName.of("hexagonal-architecture");
            PatternType patternType = PatternType.HEXAGONAL;
            PatternDescription patternDescription = PatternDescription.empty();
            PatternPrinciples patternPrinciples = PatternPrinciples.empty();
            Instant now = FIXED_CLOCK.instant();

            // when
            Architecture architecture =
                    Architecture.forNew(
                            techStackId,
                            name,
                            patternType,
                            patternDescription,
                            patternPrinciples,
                            ReferenceLinks.empty(),
                            now);

            // then
            assertThat(architecture.isNew()).isTrue();
            assertThat(architecture.techStackId()).isEqualTo(techStackId);
            assertThat(architecture.name()).isEqualTo(name);
            assertThat(architecture.patternType()).isEqualTo(patternType);
            assertThat(architecture.patternDescription()).isEqualTo(patternDescription);
            assertThat(architecture.patternPrinciples()).isEqualTo(patternPrinciples);
            assertThat(architecture.deletionStatus().isDeleted()).isFalse();
            assertThat(architecture.createdAt()).isEqualTo(now);
            assertThat(architecture.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("신규 Architecture는 null ID를 가짐")
        void forNew_ShouldHaveNullId() {
            // when
            Architecture architecture = ArchitectureFixture.defaultNewArchitecture();

            // then
            assertThat(architecture.id().isNew()).isTrue();
        }

        @Test
        @DisplayName("설명과 원칙이 포함된 Architecture 생성 성공")
        void forNew_WithDescriptionAndPrinciples_ShouldSucceed() {
            // given
            TechStackId techStackId = TechStackId.of(1L);
            ArchitectureName name = ArchitectureName.of("clean-architecture");
            PatternType patternType = PatternType.CLEAN;
            PatternDescription description = PatternDescription.of("클린 아키텍처 패턴");
            PatternPrinciples principles =
                    PatternPrinciples.of(java.util.List.of("DIP", "SRP", "OCP"));
            Instant now = FIXED_CLOCK.instant();

            // when
            Architecture architecture =
                    Architecture.forNew(
                            techStackId,
                            name,
                            patternType,
                            description,
                            principles,
                            ReferenceLinks.empty(),
                            now);

            // then
            assertThat(architecture.patternDescription()).isEqualTo(description);
            assertThat(architecture.patternPrinciples()).isEqualTo(principles);
            assertThat(architecture.patternPrinciples().contains("DIP")).isTrue();
        }
    }

    @Nested
    @DisplayName("ID 할당")
    class AssignId {

        @Test
        @DisplayName("신규 엔티티에 ID 할당 성공")
        void assignId_WhenNew_ShouldSucceed() {
            // given
            Architecture architecture = ArchitectureFixture.defaultNewArchitecture();
            ArchitectureId id = ArchitectureId.of(1L);

            // when
            architecture.assignId(id);

            // then
            assertThat(architecture.id()).isEqualTo(id);
            assertThat(architecture.isNew()).isFalse();
        }

        @Test
        @DisplayName("이미 ID가 할당된 엔티티에 ID 할당 시 예외")
        void assignId_WhenAlreadyAssigned_ShouldThrow() {
            // given
            Architecture architecture = ArchitectureFixture.defaultExistingArchitecture();
            ArchitectureId newId = ArchitectureId.of(2L);

            // when & then
            assertThatThrownBy(() -> architecture.assignId(newId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("id already assigned");
        }
    }

    @Nested
    @DisplayName("수정")
    class UpdateArchitecture {

        @Test
        @DisplayName("Architecture 정보 수정 성공")
        void update_WithValidData_ShouldSucceed() {
            // given
            Architecture architecture = ArchitectureFixture.defaultExistingArchitecture();
            ArchitectureUpdateData updateData =
                    new ArchitectureUpdateData(
                            ArchitectureName.of("updated-architecture"),
                            PatternType.CLEAN,
                            PatternDescription.of("업데이트된 설명"),
                            PatternPrinciples.of(java.util.List.of("DIP", "SRP")),
                            ReferenceLinks.empty());
            Instant updateTime = FIXED_CLOCK.instant().plusSeconds(3600);

            // when
            architecture.update(updateData, updateTime);

            // then
            assertThat(architecture.name()).isEqualTo(updateData.name());
            assertThat(architecture.patternType()).isEqualTo(updateData.patternType());
            assertThat(architecture.patternDescription())
                    .isEqualTo(updateData.patternDescription());
            assertThat(architecture.patternPrinciples()).isEqualTo(updateData.patternPrinciples());
            assertThat(architecture.updatedAt()).isEqualTo(updateTime);
        }

        @Test
        @DisplayName("빈 설명과 원칙으로 수정 성공")
        void update_WithEmptyDescriptionAndPrinciples_ShouldSucceed() {
            // given
            Architecture architecture =
                    ArchitectureFixture.architectureWithDescriptionAndPrinciples();
            ArchitectureUpdateData updateData =
                    new ArchitectureUpdateData(
                            ArchitectureName.of("updated-architecture"),
                            PatternType.HEXAGONAL,
                            PatternDescription.empty(),
                            PatternPrinciples.empty(),
                            ReferenceLinks.empty());
            Instant updateTime = FIXED_CLOCK.instant().plusSeconds(3600);

            // when
            architecture.update(updateData, updateTime);

            // then
            assertThat(architecture.patternDescription().isEmpty()).isTrue();
            assertThat(architecture.patternPrinciples().isEmpty()).isTrue();
        }
    }

    @Nested
    @DisplayName("삭제")
    class DeleteArchitecture {

        @Test
        @DisplayName("Soft Delete 성공")
        void delete_ShouldMarkAsDeleted() {
            // given
            Architecture architecture = ArchitectureFixture.defaultExistingArchitecture();
            Instant deleteTime = FIXED_CLOCK.instant().plusSeconds(3600);

            // when
            architecture.delete(deleteTime);

            // then
            assertThat(architecture.isDeleted()).isTrue();
            assertThat(architecture.deletionStatus().isDeleted()).isTrue();
            assertThat(architecture.deletionStatus().deletedAt()).isEqualTo(deleteTime);
            assertThat(architecture.updatedAt()).isEqualTo(deleteTime);
        }

        @Test
        @DisplayName("삭제된 Architecture 복원 성공")
        void restore_WhenDeleted_ShouldRestore() {
            // given
            Architecture architecture = ArchitectureFixture.deletedArchitecture();
            Instant restoreTime = FIXED_CLOCK.instant().plusSeconds(7200);

            // when
            architecture.restore(restoreTime);

            // then
            assertThat(architecture.isDeleted()).isFalse();
            assertThat(architecture.deletionStatus().isActive()).isTrue();
            assertThat(architecture.updatedAt()).isEqualTo(restoreTime);
        }

        @Test
        @DisplayName("삭제 여부 확인")
        void isDeleted_ShouldReturnCorrectStatus() {
            // given
            Architecture activeArchitecture = ArchitectureFixture.defaultExistingArchitecture();
            Architecture deletedArchitecture = ArchitectureFixture.deletedArchitecture();

            // when & then
            assertThat(activeArchitecture.isDeleted()).isFalse();
            assertThat(deletedArchitecture.isDeleted()).isTrue();
        }
    }

    @Nested
    @DisplayName("복원")
    class ReconstituteArchitecture {

        @Test
        @DisplayName("영속성에서 복원 성공")
        void reconstitute_WithValidData_ShouldSucceed() {
            // given
            ArchitectureId id = ArchitectureId.of(1L);
            TechStackId techStackId = TechStackId.of(1L);
            ArchitectureName name = ArchitectureName.of("hexagonal-architecture");
            PatternType patternType = PatternType.HEXAGONAL;
            PatternDescription patternDescription = PatternDescription.empty();
            PatternPrinciples patternPrinciples = PatternPrinciples.empty();
            DeletionStatus deletionStatus = DeletionStatus.active();
            Instant createdAt = FIXED_CLOCK.instant();
            Instant updatedAt = FIXED_CLOCK.instant();

            ReferenceLinks referenceLinks = ReferenceLinks.empty();

            // when
            Architecture architecture =
                    Architecture.reconstitute(
                            id,
                            techStackId,
                            name,
                            patternType,
                            patternDescription,
                            patternPrinciples,
                            referenceLinks,
                            deletionStatus,
                            createdAt,
                            updatedAt);

            // then
            assertThat(architecture.id()).isEqualTo(id);
            assertThat(architecture.techStackId()).isEqualTo(techStackId);
            assertThat(architecture.name()).isEqualTo(name);
            assertThat(architecture.patternType()).isEqualTo(patternType);
            assertThat(architecture.createdAt()).isEqualTo(createdAt);
            assertThat(architecture.updatedAt()).isEqualTo(updatedAt);
        }
    }

    @Nested
    @DisplayName("패턴 타입별 생성")
    class PatternTypeCreation {

        @Test
        @DisplayName("HEXAGONAL 패턴 타입 Architecture 생성")
        void forNew_WithHexagonalPattern_ShouldSucceed() {
            // when
            Architecture architecture =
                    ArchitectureFixture.architectureWithPatternType(PatternType.HEXAGONAL);

            // then
            assertThat(architecture.patternType()).isEqualTo(PatternType.HEXAGONAL);
        }

        @Test
        @DisplayName("CLEAN 패턴 타입 Architecture 생성")
        void forNew_WithCleanPattern_ShouldSucceed() {
            // when
            Architecture architecture =
                    ArchitectureFixture.architectureWithPatternType(PatternType.CLEAN);

            // then
            assertThat(architecture.patternType()).isEqualTo(PatternType.CLEAN);
        }

        @Test
        @DisplayName("LAYERED 패턴 타입 Architecture 생성")
        void forNew_WithLayeredPattern_ShouldSucceed() {
            // when
            Architecture architecture =
                    ArchitectureFixture.architectureWithPatternType(PatternType.LAYERED);

            // then
            assertThat(architecture.patternType()).isEqualTo(PatternType.LAYERED);
        }
    }
}
