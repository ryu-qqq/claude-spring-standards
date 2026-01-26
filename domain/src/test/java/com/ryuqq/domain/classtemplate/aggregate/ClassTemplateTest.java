package com.ryuqq.domain.classtemplate.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.domain.classtemplate.fixture.ClassTemplateFixture;
import com.ryuqq.domain.classtemplate.id.ClassTemplateId;
import com.ryuqq.domain.classtemplate.vo.NamingPattern;
import com.ryuqq.domain.classtemplate.vo.TemplateCode;
import com.ryuqq.domain.classtemplate.vo.TemplateDescription;
import com.ryuqq.domain.classtype.id.ClassTypeId;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * ClassTemplate Aggregate 단위 테스트
 *
 * @author development-team
 */
@DisplayName("ClassTemplate Aggregate")
class ClassTemplateTest {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneId.of("UTC"));

    @Nested
    @DisplayName("생성")
    class CreateClassTemplate {

        @Test
        @DisplayName("신규 ClassTemplate 생성 성공")
        void forNew_WithValidData_ShouldSucceed() {
            // given
            PackageStructureId structureId = PackageStructureId.of(1L);
            ClassTypeId classTypeId = ClassTypeId.of(1L);
            TemplateCode templateCode = TemplateCode.of("public class {ClassName} { ... }");
            NamingPattern namingPattern = NamingPattern.of(".*Aggregate");
            TemplateDescription description = TemplateDescription.of("Aggregate Root 클래스 템플릿");
            List<String> requiredAnnotations = List.of("@Entity");
            List<String> forbiddenAnnotations = List.of("@Data");
            List<String> requiredInterfaces = List.of();
            List<String> forbiddenInheritance = List.of();
            List<String> requiredMethods = List.of();
            Instant now = FIXED_CLOCK.instant();

            // when
            ClassTemplate classTemplate =
                    ClassTemplate.forNew(
                            structureId,
                            classTypeId,
                            templateCode,
                            namingPattern,
                            description,
                            requiredAnnotations,
                            forbiddenAnnotations,
                            requiredInterfaces,
                            forbiddenInheritance,
                            requiredMethods,
                            now);

            // then
            assertThat(classTemplate.isNew()).isTrue();
            assertThat(classTemplate.structureId()).isEqualTo(structureId);
            assertThat(classTemplate.classTypeId()).isEqualTo(classTypeId);
            assertThat(classTemplate.templateCode()).isEqualTo(templateCode);
            assertThat(classTemplate.namingPattern()).isEqualTo(namingPattern);
            assertThat(classTemplate.description()).isEqualTo(description);
            assertThat(classTemplate.requiredAnnotations()).isEqualTo(requiredAnnotations);
            assertThat(classTemplate.forbiddenAnnotations()).isEqualTo(forbiddenAnnotations);
            assertThat(classTemplate.deletionStatus().isDeleted()).isFalse();
            assertThat(classTemplate.createdAt()).isEqualTo(now);
            assertThat(classTemplate.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("신규 ClassTemplate은 null ID를 가짐")
        void forNew_ShouldHaveNullId() {
            // when
            ClassTemplate classTemplate = ClassTemplateFixture.defaultNewClassTemplate();

            // then
            assertThat(classTemplate.id().isNew()).isTrue();
        }

        @Test
        @DisplayName("structureId가 null이면 예외 발생")
        void forNew_WithNullStructureId_ShouldThrow() {
            // given
            ClassTypeId classTypeId = ClassTypeId.of(1L);
            TemplateCode templateCode = TemplateCode.of("public class {ClassName} { ... }");
            TemplateDescription description = TemplateDescription.of("템플릿");
            Instant now = FIXED_CLOCK.instant();

            // when & then
            assertThatThrownBy(
                            () ->
                                    ClassTemplate.forNew(
                                            null,
                                            classTypeId,
                                            templateCode,
                                            null,
                                            description,
                                            List.of(),
                                            List.of(),
                                            List.of(),
                                            List.of(),
                                            List.of(),
                                            now))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("structureId must not be null");
        }

        @Test
        @DisplayName("classTypeId가 null이면 예외 발생")
        void forNew_WithNullClassTypeId_ShouldThrow() {
            // given
            PackageStructureId structureId = PackageStructureId.of(1L);
            TemplateCode templateCode = TemplateCode.of("public class {ClassName} { ... }");
            TemplateDescription description = TemplateDescription.of("템플릿");
            Instant now = FIXED_CLOCK.instant();

            // when & then
            assertThatThrownBy(
                            () ->
                                    ClassTemplate.forNew(
                                            structureId,
                                            null,
                                            templateCode,
                                            null,
                                            description,
                                            List.of(),
                                            List.of(),
                                            List.of(),
                                            List.of(),
                                            List.of(),
                                            now))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("classTypeId must not be null");
        }

        @Test
        @DisplayName("templateCode가 null이면 예외 발생")
        void forNew_WithNullTemplateCode_ShouldThrow() {
            // given
            PackageStructureId structureId = PackageStructureId.of(1L);
            ClassTypeId classTypeId = ClassTypeId.of(1L);
            TemplateDescription description = TemplateDescription.of("템플릿");
            Instant now = FIXED_CLOCK.instant();

            // when & then
            assertThatThrownBy(
                            () ->
                                    ClassTemplate.forNew(
                                            structureId,
                                            classTypeId,
                                            null,
                                            null,
                                            description,
                                            List.of(),
                                            List.of(),
                                            List.of(),
                                            List.of(),
                                            List.of(),
                                            now))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("templateCode must not be null");
        }
    }

    @Nested
    @DisplayName("ID 할당")
    class AssignId {

        @Test
        @DisplayName("신규 엔티티에 ID 할당 성공")
        void assignId_WhenNew_ShouldSucceed() {
            // given
            ClassTemplate classTemplate = ClassTemplateFixture.defaultNewClassTemplate();
            ClassTemplateId id = ClassTemplateId.of(1L);

            // when
            classTemplate.assignId(id);

            // then
            assertThat(classTemplate.id()).isEqualTo(id);
            assertThat(classTemplate.isNew()).isFalse();
        }

        @Test
        @DisplayName("이미 ID가 할당된 엔티티에 ID 할당 시 예외")
        void assignId_WhenAlreadyAssigned_ShouldThrow() {
            // given
            ClassTemplate classTemplate = ClassTemplateFixture.defaultExistingClassTemplate();
            ClassTemplateId newId = ClassTemplateId.of(2L);

            // when & then
            assertThatThrownBy(() -> classTemplate.assignId(newId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("id already assigned");
        }
    }

    @Nested
    @DisplayName("수정")
    class UpdateClassTemplate {

        @Test
        @DisplayName("ClassTemplate 정보 수정 성공")
        void update_WithValidData_ShouldSucceed() {
            // given
            ClassTemplate classTemplate = ClassTemplateFixture.defaultExistingClassTemplate();
            ClassTemplateUpdateData updateData =
                    new ClassTemplateUpdateData(
                            ClassTypeId.of(2L),
                            TemplateCode.of("public record {ClassName} { ... }"),
                            NamingPattern.of(".*VO"),
                            TemplateDescription.of("업데이트된 템플릿"),
                            List.of("@Value"),
                            List.of("@Data"),
                            List.of(),
                            List.of(),
                            List.of());
            Instant updateTime = FIXED_CLOCK.instant().plusSeconds(3600);

            // when
            classTemplate.update(updateData, updateTime);

            // then
            assertThat(classTemplate.classTypeId()).isEqualTo(updateData.classTypeId());
            assertThat(classTemplate.templateCode()).isEqualTo(updateData.templateCode());
            assertThat(classTemplate.namingPattern()).isEqualTo(updateData.namingPattern());
            assertThat(classTemplate.description()).isEqualTo(updateData.description());
            assertThat(classTemplate.requiredAnnotations())
                    .isEqualTo(updateData.requiredAnnotations());
            assertThat(classTemplate.forbiddenAnnotations())
                    .isEqualTo(updateData.forbiddenAnnotations());
            assertThat(classTemplate.updatedAt()).isEqualTo(updateTime);
        }

        @Test
        @DisplayName("null 필드는 기존 값 유지")
        void update_WithNullFields_ShouldKeepExistingValues() {
            // given
            ClassTemplate classTemplate = ClassTemplateFixture.defaultExistingClassTemplate();
            ClassTypeId originalClassTypeId = classTemplate.classTypeId();
            TemplateCode originalTemplateCode = classTemplate.templateCode();
            ClassTemplateUpdateData updateData =
                    new ClassTemplateUpdateData(
                            null,
                            null,
                            NamingPattern.of(".*Updated"),
                            null,
                            null,
                            null,
                            null,
                            null,
                            null);
            Instant updateTime = FIXED_CLOCK.instant().plusSeconds(3600);

            // when
            classTemplate.update(updateData, updateTime);

            // then
            assertThat(classTemplate.classTypeId()).isEqualTo(originalClassTypeId);
            assertThat(classTemplate.templateCode()).isEqualTo(originalTemplateCode);
            assertThat(classTemplate.namingPattern()).isEqualTo(updateData.namingPattern());
        }
    }

    @Nested
    @DisplayName("Helper 메서드")
    class HelperMethods {

        @Test
        @DisplayName("네이밍 패턴 여부 확인")
        void hasNamingPattern_ShouldReturnCorrectStatus() {
            // given
            ClassTemplate withPattern = ClassTemplateFixture.defaultExistingClassTemplate();
            ClassTemplate withoutPattern = ClassTemplateFixture.classTemplateWithoutNamingPattern();

            // when & then
            assertThat(withPattern.hasNamingPattern()).isTrue();
            assertThat(withoutPattern.hasNamingPattern()).isFalse();
        }

        @Test
        @DisplayName("필수 어노테이션 여부 확인")
        void hasRequiredAnnotations_ShouldReturnCorrectStatus() {
            // given
            ClassTemplate withAnnotations =
                    ClassTemplateFixture.classTemplateWithRequiredAnnotations();
            ClassTemplate withoutAnnotations = ClassTemplateFixture.defaultExistingClassTemplate();

            // when & then
            assertThat(withAnnotations.hasRequiredAnnotations()).isTrue();
            assertThat(withoutAnnotations.hasRequiredAnnotations())
                    .isTrue(); // defaultExisting에는 있음
        }

        @Test
        @DisplayName("금지 어노테이션 여부 확인")
        void hasForbiddenAnnotations_ShouldReturnCorrectStatus() {
            // given
            ClassTemplate withForbidden =
                    ClassTemplateFixture.classTemplateWithForbiddenAnnotations();
            ClassTemplate withoutForbidden =
                    ClassTemplateFixture.classTemplateWithRequiredAnnotations();

            // when & then
            assertThat(withForbidden.hasForbiddenAnnotations()).isTrue();
            assertThat(withoutForbidden.hasForbiddenAnnotations()).isFalse();
        }

        @Test
        @DisplayName("필수 인터페이스 여부 확인")
        void hasRequiredInterfaces_ShouldReturnCorrectStatus() {
            // given
            ClassTemplate withInterfaces =
                    ClassTemplateFixture.classTemplateWithRequiredInterfaces();
            ClassTemplate withoutInterfaces = ClassTemplateFixture.defaultExistingClassTemplate();

            // when & then
            assertThat(withInterfaces.hasRequiredInterfaces()).isTrue();
            assertThat(withoutInterfaces.hasRequiredInterfaces()).isFalse();
        }
    }

    @Nested
    @DisplayName("삭제")
    class DeleteClassTemplate {

        @Test
        @DisplayName("Soft Delete 성공")
        void delete_ShouldMarkAsDeleted() {
            // given
            ClassTemplate classTemplate = ClassTemplateFixture.defaultExistingClassTemplate();
            Instant deleteTime = FIXED_CLOCK.instant().plusSeconds(3600);

            // when
            classTemplate.delete(deleteTime);

            // then
            assertThat(classTemplate.isDeleted()).isTrue();
            assertThat(classTemplate.deletionStatus().isDeleted()).isTrue();
            assertThat(classTemplate.deletionStatus().deletedAt()).isEqualTo(deleteTime);
            assertThat(classTemplate.updatedAt()).isEqualTo(deleteTime);
        }

        @Test
        @DisplayName("삭제된 ClassTemplate 복원 성공")
        void restore_WhenDeleted_ShouldRestore() {
            // given
            ClassTemplate classTemplate = ClassTemplateFixture.deletedClassTemplate();
            Instant restoreTime = FIXED_CLOCK.instant().plusSeconds(7200);

            // when
            classTemplate.restore(restoreTime);

            // then
            assertThat(classTemplate.isDeleted()).isFalse();
            assertThat(classTemplate.deletionStatus().isActive()).isTrue();
            assertThat(classTemplate.updatedAt()).isEqualTo(restoreTime);
        }

        @Test
        @DisplayName("삭제 여부 확인")
        void isDeleted_ShouldReturnCorrectStatus() {
            // given
            ClassTemplate activeTemplate = ClassTemplateFixture.defaultExistingClassTemplate();
            ClassTemplate deletedTemplate = ClassTemplateFixture.deletedClassTemplate();

            // when & then
            assertThat(activeTemplate.isDeleted()).isFalse();
            assertThat(deletedTemplate.isDeleted()).isTrue();
        }
    }

    @Nested
    @DisplayName("복원")
    class ReconstituteClassTemplate {

        @Test
        @DisplayName("영속성에서 복원 성공")
        void reconstitute_WithValidData_ShouldSucceed() {
            // given
            ClassTemplateId id = ClassTemplateId.of(1L);
            PackageStructureId structureId = PackageStructureId.of(1L);
            ClassTypeId classTypeId = ClassTypeId.of(1L);
            TemplateCode templateCode = TemplateCode.of("public class {ClassName} { ... }");
            NamingPattern namingPattern = NamingPattern.of(".*Aggregate");
            TemplateDescription description = TemplateDescription.of("Aggregate Root 클래스 템플릿");
            List<String> requiredAnnotations = List.of("@Entity");
            List<String> forbiddenAnnotations = List.of("@Data");
            List<String> requiredInterfaces = List.of();
            List<String> forbiddenInheritance = List.of();
            List<String> requiredMethods = List.of();
            DeletionStatus deletionStatus = DeletionStatus.active();
            Instant createdAt = FIXED_CLOCK.instant();
            Instant updatedAt = FIXED_CLOCK.instant();

            // when
            ClassTemplate classTemplate =
                    ClassTemplate.of(
                            id,
                            structureId,
                            classTypeId,
                            templateCode,
                            namingPattern,
                            description,
                            requiredAnnotations,
                            forbiddenAnnotations,
                            requiredInterfaces,
                            forbiddenInheritance,
                            requiredMethods,
                            deletionStatus,
                            createdAt,
                            updatedAt);

            // then
            assertThat(classTemplate.id()).isEqualTo(id);
            assertThat(classTemplate.structureId()).isEqualTo(structureId);
            assertThat(classTemplate.classTypeId()).isEqualTo(classTypeId);
            assertThat(classTemplate.templateCode()).isEqualTo(templateCode);
            assertThat(classTemplate.namingPattern()).isEqualTo(namingPattern);
            assertThat(classTemplate.description()).isEqualTo(description);
            assertThat(classTemplate.createdAt()).isEqualTo(createdAt);
            assertThat(classTemplate.updatedAt()).isEqualTo(updatedAt);
        }
    }

    @Nested
    @DisplayName("클래스 타입별 생성")
    class ClassTypeCreation {

        @Test
        @DisplayName("AGGREGATE 타입 ClassTemplate 생성")
        void forNew_WithAggregateType_ShouldSucceed() {
            // given
            PackageStructureId structureId = PackageStructureId.of(1L);
            ClassTypeId classTypeId = ClassTypeId.of(1L); // AGGREGATE
            TemplateCode templateCode = TemplateCode.of("public class {ClassName} { ... }");
            TemplateDescription description = TemplateDescription.of("Aggregate 템플릿");
            Instant now = FIXED_CLOCK.instant();

            // when
            ClassTemplate classTemplate =
                    ClassTemplate.forNew(
                            structureId,
                            classTypeId,
                            templateCode,
                            null,
                            description,
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            now);

            // then
            assertThat(classTemplate.classTypeId()).isEqualTo(ClassTypeId.of(1L));
        }

        @Test
        @DisplayName("REST_CONTROLLER 타입 ClassTemplate 생성")
        void forNew_WithRestControllerType_ShouldSucceed() {
            // given
            PackageStructureId structureId = PackageStructureId.of(1L);
            ClassTypeId classTypeId = ClassTypeId.of(10L); // REST_CONTROLLER
            TemplateCode templateCode =
                    TemplateCode.of("@RestController\npublic class {ClassName} { ... }");
            TemplateDescription description = TemplateDescription.of("REST Controller 템플릿");
            Instant now = FIXED_CLOCK.instant();

            // when
            ClassTemplate classTemplate =
                    ClassTemplate.forNew(
                            structureId,
                            classTypeId,
                            templateCode,
                            null,
                            description,
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            now);

            // then
            assertThat(classTemplate.classTypeId()).isEqualTo(ClassTypeId.of(10L));
        }
    }
}
