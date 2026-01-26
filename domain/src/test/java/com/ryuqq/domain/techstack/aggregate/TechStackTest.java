package com.ryuqq.domain.techstack.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.common.vo.ReferenceLinks;
import com.ryuqq.domain.techstack.fixture.TechStackFixture;
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
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * TechStack Aggregate 단위 테스트
 *
 * @author development-team
 */
@DisplayName("TechStack Aggregate")
class TechStackTest {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneId.of("UTC"));

    @Nested
    @DisplayName("생성")
    class CreateTechStack {

        @Test
        @DisplayName("신규 TechStack 생성 성공")
        void forNew_WithValidData_ShouldSucceed() {
            // given
            TechStackName name = TechStackName.of("Spring Boot 3.5");
            LanguageType languageType = LanguageType.JAVA;
            LanguageVersion languageVersion = LanguageVersion.of("21");
            LanguageFeatures languageFeatures = LanguageFeatures.empty();
            FrameworkType frameworkType = FrameworkType.SPRING_BOOT;
            FrameworkVersion frameworkVersion = FrameworkVersion.of("3.5.0");
            FrameworkModules frameworkModules = FrameworkModules.empty();
            PlatformType platformType = PlatformType.BACKEND;
            RuntimeEnvironment runtimeEnvironment = RuntimeEnvironment.JVM;
            BuildToolType buildToolType = BuildToolType.GRADLE;
            BuildConfigFile buildConfigFile = BuildConfigFile.of("build.gradle");
            Instant now = FIXED_CLOCK.instant();

            // when
            TechStack techStack =
                    TechStack.forNew(
                            name,
                            languageType,
                            languageVersion,
                            languageFeatures,
                            frameworkType,
                            frameworkVersion,
                            frameworkModules,
                            platformType,
                            runtimeEnvironment,
                            buildToolType,
                            buildConfigFile,
                            ReferenceLinks.empty(),
                            now);

            // then
            assertThat(techStack.isNew()).isTrue();
            assertThat(techStack.name()).isEqualTo(name);
            assertThat(techStack.status()).isEqualTo(TechStackStatus.ACTIVE);
            assertThat(techStack.languageType()).isEqualTo(languageType);
            assertThat(techStack.languageVersion()).isEqualTo(languageVersion);
            assertThat(techStack.frameworkType()).isEqualTo(frameworkType);
            assertThat(techStack.frameworkVersion()).isEqualTo(frameworkVersion);
            assertThat(techStack.platformType()).isEqualTo(platformType);
            assertThat(techStack.runtimeEnvironment()).isEqualTo(runtimeEnvironment);
            assertThat(techStack.buildToolType()).isEqualTo(buildToolType);
            assertThat(techStack.buildConfigFile()).isEqualTo(buildConfigFile);
            assertThat(techStack.deletionStatus().isDeleted()).isFalse();
            assertThat(techStack.createdAt()).isEqualTo(now);
            assertThat(techStack.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("신규 TechStack은 null ID를 가짐")
        void forNew_ShouldHaveNullId() {
            // when
            TechStack techStack = TechStackFixture.defaultNewTechStack();

            // then
            assertThat(techStack.id().isNew()).isTrue();
        }
    }

    @Nested
    @DisplayName("ID 할당")
    class AssignId {

        @Test
        @DisplayName("신규 엔티티에 ID 할당 성공")
        void assignId_WhenNew_ShouldSucceed() {
            // given
            TechStack techStack = TechStackFixture.defaultNewTechStack();
            TechStackId id = TechStackId.of(1L);

            // when
            techStack.assignId(id);

            // then
            assertThat(techStack.id()).isEqualTo(id);
            assertThat(techStack.isNew()).isFalse();
        }

        @Test
        @DisplayName("이미 ID가 할당된 엔티티에 ID 할당 시 예외")
        void assignId_WhenAlreadyAssigned_ShouldThrow() {
            // given
            TechStack techStack = TechStackFixture.defaultExistingTechStack();
            TechStackId newId = TechStackId.of(2L);

            // when & then
            assertThatThrownBy(() -> techStack.assignId(newId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("id already assigned");
        }
    }

    @Nested
    @DisplayName("수정")
    class UpdateTechStack {

        @Test
        @DisplayName("TechStack 정보 수정 성공")
        void update_WithValidData_ShouldSucceed() {
            // given
            TechStack techStack = TechStackFixture.defaultExistingTechStack();
            TechStackUpdateData updateData =
                    new TechStackUpdateData(
                            TechStackName.of("Spring Boot 3.6"),
                            TechStackStatus.ACTIVE,
                            LanguageType.JAVA,
                            LanguageVersion.of("22"),
                            LanguageFeatures.of(java.util.List.of("VIRTUAL_THREAD")),
                            FrameworkType.SPRING_BOOT,
                            FrameworkVersion.of("3.6.0"),
                            FrameworkModules.of(java.util.List.of("WEB", "JPA")),
                            PlatformType.BACKEND,
                            RuntimeEnvironment.JVM,
                            BuildToolType.GRADLE,
                            BuildConfigFile.of("build.gradle.kts"),
                            ReferenceLinks.empty());
            Instant updateTime = FIXED_CLOCK.instant().plusSeconds(3600);

            // when
            techStack.update(updateData, updateTime);

            // then
            assertThat(techStack.name()).isEqualTo(updateData.name());
            assertThat(techStack.status()).isEqualTo(updateData.status());
            assertThat(techStack.languageVersion()).isEqualTo(updateData.languageVersion());
            assertThat(techStack.languageFeatures()).isEqualTo(updateData.languageFeatures());
            assertThat(techStack.frameworkVersion()).isEqualTo(updateData.frameworkVersion());
            assertThat(techStack.frameworkModules()).isEqualTo(updateData.frameworkModules());
            assertThat(techStack.buildConfigFile()).isEqualTo(updateData.buildConfigFile());
            assertThat(techStack.updatedAt()).isEqualTo(updateTime);
        }
    }

    @Nested
    @DisplayName("상태 변경")
    class StatusChange {

        @Test
        @DisplayName("비권장 상태로 변경 성공")
        void deprecate_ShouldChangeStatusToDeprecated() {
            // given
            TechStack techStack = TechStackFixture.defaultExistingTechStack();
            Instant updateTime = FIXED_CLOCK.instant().plusSeconds(3600);

            // when
            techStack.deprecate(updateTime);

            // then
            assertThat(techStack.status()).isEqualTo(TechStackStatus.DEPRECATED);
            assertThat(techStack.updatedAt()).isEqualTo(updateTime);
        }

        @Test
        @DisplayName("보관 상태로 변경 성공")
        void archive_ShouldChangeStatusToArchived() {
            // given
            TechStack techStack = TechStackFixture.defaultExistingTechStack();
            Instant updateTime = FIXED_CLOCK.instant().plusSeconds(3600);

            // when
            techStack.archive(updateTime);

            // then
            assertThat(techStack.status()).isEqualTo(TechStackStatus.ARCHIVED);
            assertThat(techStack.updatedAt()).isEqualTo(updateTime);
        }
    }

    @Nested
    @DisplayName("삭제")
    class DeleteTechStack {

        @Test
        @DisplayName("Soft Delete 성공")
        void delete_ShouldMarkAsDeleted() {
            // given
            TechStack techStack = TechStackFixture.defaultExistingTechStack();
            Instant deleteTime = FIXED_CLOCK.instant().plusSeconds(3600);

            // when
            techStack.delete(deleteTime);

            // then
            assertThat(techStack.isDeleted()).isTrue();
            assertThat(techStack.deletionStatus().isDeleted()).isTrue();
            assertThat(techStack.deletionStatus().deletedAt()).isEqualTo(deleteTime);
            assertThat(techStack.updatedAt()).isEqualTo(deleteTime);
        }

        @Test
        @DisplayName("삭제된 TechStack 복원 성공")
        void restore_WhenDeleted_ShouldRestore() {
            // given
            TechStack techStack = TechStackFixture.deletedTechStack();
            Instant restoreTime = FIXED_CLOCK.instant().plusSeconds(7200);

            // when
            techStack.restore(restoreTime);

            // then
            assertThat(techStack.isDeleted()).isFalse();
            assertThat(techStack.deletionStatus().isActive()).isTrue();
            assertThat(techStack.updatedAt()).isEqualTo(restoreTime);
        }

        @Test
        @DisplayName("삭제 여부 확인")
        void isDeleted_ShouldReturnCorrectStatus() {
            // given
            TechStack activeTechStack = TechStackFixture.defaultExistingTechStack();
            TechStack deletedTechStack = TechStackFixture.deletedTechStack();

            // when & then
            assertThat(activeTechStack.isDeleted()).isFalse();
            assertThat(deletedTechStack.isDeleted()).isTrue();
        }
    }

    @Nested
    @DisplayName("복원")
    class ReconstituteTechStack {

        @Test
        @DisplayName("영속성에서 복원 성공")
        void reconstitute_WithValidData_ShouldSucceed() {
            // given
            TechStackId id = TechStackId.of(1L);
            TechStackName name = TechStackName.of("Spring Boot 3.5");
            TechStackStatus status = TechStackStatus.ACTIVE;
            LanguageType languageType = LanguageType.JAVA;
            LanguageVersion languageVersion = LanguageVersion.of("21");
            LanguageFeatures languageFeatures = LanguageFeatures.empty();
            FrameworkType frameworkType = FrameworkType.SPRING_BOOT;
            FrameworkVersion frameworkVersion = FrameworkVersion.of("3.5.0");
            FrameworkModules frameworkModules = FrameworkModules.empty();
            PlatformType platformType = PlatformType.BACKEND;
            RuntimeEnvironment runtimeEnvironment = RuntimeEnvironment.JVM;
            BuildToolType buildToolType = BuildToolType.GRADLE;
            BuildConfigFile buildConfigFile = BuildConfigFile.of("build.gradle");
            DeletionStatus deletionStatus = DeletionStatus.active();
            Instant createdAt = FIXED_CLOCK.instant();
            Instant updatedAt = FIXED_CLOCK.instant();

            ReferenceLinks referenceLinks = ReferenceLinks.empty();

            // when
            TechStack techStack =
                    TechStack.reconstitute(
                            id,
                            name,
                            status,
                            languageType,
                            languageVersion,
                            languageFeatures,
                            frameworkType,
                            frameworkVersion,
                            frameworkModules,
                            platformType,
                            runtimeEnvironment,
                            buildToolType,
                            buildConfigFile,
                            referenceLinks,
                            deletionStatus,
                            createdAt,
                            updatedAt);

            // then
            assertThat(techStack.id()).isEqualTo(id);
            assertThat(techStack.name()).isEqualTo(name);
            assertThat(techStack.status()).isEqualTo(status);
            assertThat(techStack.createdAt()).isEqualTo(createdAt);
            assertThat(techStack.updatedAt()).isEqualTo(updatedAt);
        }
    }
}
