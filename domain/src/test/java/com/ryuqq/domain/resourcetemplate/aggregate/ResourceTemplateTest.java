package com.ryuqq.domain.resourcetemplate.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.domain.common.vo.CursorPageRequest;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.module.id.ModuleId;
import com.ryuqq.domain.resourcetemplate.exception.ResourceTemplateErrorCode;
import com.ryuqq.domain.resourcetemplate.exception.ResourceTemplateNotFoundException;
import com.ryuqq.domain.resourcetemplate.fixture.ResourceTemplateFixture;
import com.ryuqq.domain.resourcetemplate.id.ResourceTemplateId;
import com.ryuqq.domain.resourcetemplate.query.ResourceTemplateSliceCriteria;
import com.ryuqq.domain.resourcetemplate.vo.FileType;
import com.ryuqq.domain.resourcetemplate.vo.TemplateCategory;
import com.ryuqq.domain.resourcetemplate.vo.TemplateContent;
import com.ryuqq.domain.resourcetemplate.vo.TemplatePath;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * ResourceTemplate Aggregate 단위 테스트
 *
 * @author development-team
 */
@DisplayName("ResourceTemplate Aggregate")
class ResourceTemplateTest {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneId.of("UTC"));

    @Nested
    @DisplayName("생성")
    class CreateResourceTemplate {

        @Test
        @DisplayName("신규 ResourceTemplate 생성 성공")
        void forNew_WithValidData_ShouldSucceed() {
            // given
            ModuleId moduleId = ResourceTemplateFixture.fixedModuleId();
            TemplateCategory category = TemplateCategory.CONFIG;
            TemplatePath filePath = TemplatePath.of("src/main/resources/application.yml");
            FileType fileType = FileType.YAML;
            String description = "기본 설정 파일";
            TemplateContent templateContent =
                    TemplateContent.of("spring:\n  application:\n    name: test");
            boolean required = true;
            Instant now = FIXED_CLOCK.instant();

            // when
            ResourceTemplate template =
                    ResourceTemplate.forNew(
                            moduleId,
                            category,
                            filePath,
                            fileType,
                            description,
                            templateContent,
                            required,
                            now);

            // then
            assertThat(template.isNew()).isTrue();
            assertThat(template.moduleId()).isEqualTo(moduleId);
            assertThat(template.category()).isEqualTo(category);
            assertThat(template.filePath()).isEqualTo(filePath);
            assertThat(template.fileType()).isEqualTo(fileType);
            assertThat(template.description()).isEqualTo(description);
            assertThat(template.templateContent()).isEqualTo(templateContent);
            assertThat(template.required()).isTrue();
            assertThat(template.deletionStatus().isDeleted()).isFalse();
            assertThat(template.createdAt()).isEqualTo(now);
            assertThat(template.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("신규 ResourceTemplate은 null ID를 가짐")
        void forNew_ShouldHaveNullId() {
            // when
            ResourceTemplate template = ResourceTemplateFixture.forNew();

            // then
            assertThat(template.id().isNew()).isTrue();
        }

        @Test
        @DisplayName("templateContent가 null이면 빈 콘텐츠로 설정")
        void forNew_WithNullContent_ShouldSetEmptyContent() {
            // given
            ModuleId moduleId = ResourceTemplateFixture.fixedModuleId();
            Instant now = FIXED_CLOCK.instant();

            // when
            ResourceTemplate template =
                    ResourceTemplate.forNew(
                            moduleId,
                            TemplateCategory.CONFIG,
                            TemplatePath.of("test.yml"),
                            FileType.YAML,
                            "test",
                            null,
                            true,
                            now);

            // then
            assertThat(template.templateContent()).isEqualTo(TemplateContent.empty());
            assertThat(template.hasContent()).isFalse();
        }
    }

    @Nested
    @DisplayName("ID 할당")
    class AssignId {

        @Test
        @DisplayName("신규 엔티티에 ID 할당 성공")
        void assignId_WhenNew_ShouldSucceed() {
            // given
            ResourceTemplate template = ResourceTemplateFixture.forNew();
            ResourceTemplateId id = ResourceTemplateFixture.nextResourceTemplateId();

            // when
            template.assignId(id);

            // then
            assertThat(template.id()).isEqualTo(id);
            assertThat(template.isNew()).isFalse();
        }

        @Test
        @DisplayName("이미 ID가 할당된 엔티티에 ID 할당 시 예외")
        void assignId_WhenAlreadyAssigned_ShouldThrow() {
            // given
            ResourceTemplate template = ResourceTemplateFixture.defaultExistingResourceTemplate();
            ResourceTemplateId newId = ResourceTemplateFixture.nextResourceTemplateId();

            // when & then
            assertThatThrownBy(() -> template.assignId(newId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("id already assigned");
        }
    }

    @Nested
    @DisplayName("비즈니스 로직")
    class BusinessLogic {

        @Test
        @DisplayName("설정 파일인지 확인")
        void isConfig_WithConfigCategory_ShouldReturnTrue() {
            // given
            ResourceTemplate template = ResourceTemplateFixture.configTemplate();

            // when & then
            assertThat(template.isConfig()).isTrue();
        }

        @Test
        @DisplayName("필수 템플릿인지 확인")
        void isRequired_WithRequiredTemplate_ShouldReturnTrue() {
            // given
            ResourceTemplate template = ResourceTemplateFixture.configTemplate();

            // when & then
            assertThat(template.isRequired()).isTrue();
        }

        @Test
        @DisplayName("선택적 템플릿 확인")
        void isRequired_WithOptionalTemplate_ShouldReturnFalse() {
            // given
            ResourceTemplate template = ResourceTemplateFixture.optionalTemplate();

            // when & then
            assertThat(template.isRequired()).isFalse();
        }

        @Test
        @DisplayName("내용 존재 여부 확인 - 내용 있음")
        void hasContent_WithContent_ShouldReturnTrue() {
            // given
            ResourceTemplate template = ResourceTemplateFixture.configTemplate();

            // when & then
            assertThat(template.hasContent()).isTrue();
        }

        @Test
        @DisplayName("내용 존재 여부 확인 - 내용 없음")
        void hasContent_WithoutContent_ShouldReturnFalse() {
            // given
            ResourceTemplate template = ResourceTemplateFixture.emptyContentTemplate();

            // when & then
            assertThat(template.hasContent()).isFalse();
        }
    }

    @Nested
    @DisplayName("업데이트")
    class UpdateResourceTemplate {

        @Test
        @DisplayName("부분 업데이트 성공")
        void update_WithPartialData_ShouldUpdateOnlyProvidedFields() {
            // given
            ResourceTemplate template = ResourceTemplateFixture.defaultExistingResourceTemplate();
            String originalDescription = template.description();
            Instant updateTime = FIXED_CLOCK.instant().plusSeconds(3600);

            ResourceTemplateUpdateData updateData =
                    ResourceTemplateUpdateData.builder().category(TemplateCategory.BUILD).build();

            // when
            template.update(updateData, updateTime);

            // then
            assertThat(template.category()).isEqualTo(TemplateCategory.BUILD);
            assertThat(template.description()).isEqualTo(originalDescription);
            assertThat(template.updatedAt()).isEqualTo(updateTime);
        }

        @Test
        @DisplayName("전체 업데이트 성공")
        void update_WithFullData_ShouldUpdateAllFields() {
            // given
            ResourceTemplate template = ResourceTemplateFixture.defaultExistingResourceTemplate();
            Instant updateTime = FIXED_CLOCK.instant().plusSeconds(3600);

            ResourceTemplateUpdateData updateData =
                    ResourceTemplateUpdateData.builder()
                            .category(TemplateCategory.BUILD)
                            .filePath(TemplatePath.of("build.gradle"))
                            .fileType(FileType.GRADLE)
                            .description("업데이트된 설명")
                            .templateContent(TemplateContent.of("plugins {}"))
                            .required(false)
                            .build();

            // when
            template.update(updateData, updateTime);

            // then
            assertThat(template.category()).isEqualTo(TemplateCategory.BUILD);
            assertThat(template.filePath()).isEqualTo(TemplatePath.of("build.gradle"));
            assertThat(template.fileType()).isEqualTo(FileType.GRADLE);
            assertThat(template.description()).isEqualTo("업데이트된 설명");
            assertThat(template.templateContent()).isEqualTo(TemplateContent.of("plugins {}"));
            assertThat(template.required()).isFalse();
            assertThat(template.updatedAt()).isEqualTo(updateTime);
        }

        @Test
        @DisplayName("null 업데이트 데이터는 무시")
        void update_WithNullData_ShouldNotUpdate() {
            // given
            ResourceTemplate template = ResourceTemplateFixture.defaultExistingResourceTemplate();
            Instant originalUpdatedAt = template.updatedAt();

            // when
            template.update(null, FIXED_CLOCK.instant().plusSeconds(3600));

            // then
            assertThat(template.updatedAt()).isEqualTo(originalUpdatedAt);
        }

        @Test
        @DisplayName("빈 업데이트 데이터는 무시")
        void update_WithEmptyData_ShouldNotUpdate() {
            // given
            ResourceTemplate template = ResourceTemplateFixture.defaultExistingResourceTemplate();
            Instant originalUpdatedAt = template.updatedAt();
            ResourceTemplateUpdateData emptyData = ResourceTemplateUpdateData.builder().build();

            // when
            template.update(emptyData, FIXED_CLOCK.instant().plusSeconds(3600));

            // then
            assertThat(template.updatedAt()).isEqualTo(originalUpdatedAt);
        }
    }

    @Nested
    @DisplayName("삭제")
    class DeleteResourceTemplate {

        @Test
        @DisplayName("Soft Delete 성공")
        void delete_ShouldMarkAsDeleted() {
            // given
            ResourceTemplate template = ResourceTemplateFixture.defaultExistingResourceTemplate();
            Instant deleteTime = FIXED_CLOCK.instant().plusSeconds(3600);

            // when
            template.delete(deleteTime);

            // then
            assertThat(template.isDeleted()).isTrue();
            assertThat(template.deletionStatus().isDeleted()).isTrue();
            assertThat(template.deletionStatus().deletedAt()).isEqualTo(deleteTime);
            assertThat(template.updatedAt()).isEqualTo(deleteTime);
        }

        @Test
        @DisplayName("삭제된 ResourceTemplate 복원 성공")
        void restore_WhenDeleted_ShouldRestore() {
            // given
            ResourceTemplate template = ResourceTemplateFixture.deletedResourceTemplate();
            Instant restoreTime = FIXED_CLOCK.instant().plusSeconds(7200);

            // when
            template.restore(restoreTime);

            // then
            assertThat(template.isDeleted()).isFalse();
            assertThat(template.deletionStatus().isActive()).isTrue();
            assertThat(template.updatedAt()).isEqualTo(restoreTime);
        }

        @Test
        @DisplayName("삭제 여부 확인")
        void isDeleted_ShouldReturnCorrectStatus() {
            // given
            ResourceTemplate activeTemplate =
                    ResourceTemplateFixture.defaultExistingResourceTemplate();
            ResourceTemplate deletedTemplate = ResourceTemplateFixture.deletedResourceTemplate();

            // when & then
            assertThat(activeTemplate.isDeleted()).isFalse();
            assertThat(deletedTemplate.isDeleted()).isTrue();
        }
    }

    @Nested
    @DisplayName("복원")
    class ReconstituteResourceTemplate {

        @Test
        @DisplayName("영속성에서 복원 성공")
        void reconstitute_WithValidData_ShouldSucceed() {
            // given
            ResourceTemplateId id = ResourceTemplateFixture.nextResourceTemplateId();
            ModuleId moduleId = ResourceTemplateFixture.fixedModuleId();
            TemplateCategory category = TemplateCategory.CONFIG;
            TemplatePath filePath = TemplatePath.of("src/main/resources/application.yml");
            FileType fileType = FileType.YAML;
            String description = "설정 파일";
            TemplateContent templateContent =
                    TemplateContent.of("spring:\n  profiles:\n    active: local");
            boolean required = true;
            DeletionStatus deletionStatus = DeletionStatus.active();
            Instant createdAt = FIXED_CLOCK.instant();
            Instant updatedAt = FIXED_CLOCK.instant();

            // when
            ResourceTemplate template =
                    ResourceTemplate.reconstitute(
                            id,
                            moduleId,
                            category,
                            filePath,
                            fileType,
                            description,
                            templateContent,
                            required,
                            deletionStatus,
                            createdAt,
                            updatedAt);

            // then
            assertThat(template.id()).isEqualTo(id);
            assertThat(template.moduleId()).isEqualTo(moduleId);
            assertThat(template.category()).isEqualTo(category);
            assertThat(template.filePath()).isEqualTo(filePath);
            assertThat(template.fileType()).isEqualTo(fileType);
            assertThat(template.description()).isEqualTo(description);
            assertThat(template.templateContent()).isEqualTo(templateContent);
            assertThat(template.required()).isEqualTo(required);
            assertThat(template.deletionStatus()).isEqualTo(deletionStatus);
            assertThat(template.createdAt()).isEqualTo(createdAt);
            assertThat(template.updatedAt()).isEqualTo(updatedAt);
        }
    }

    @Nested
    @DisplayName("Value Objects")
    class ValueObjectTests {

        @Test
        @DisplayName("TemplatePath 생성 및 검증")
        void templatePath_ShouldValidateAndExtractParts() {
            // given
            String path = "src/main/resources/application.yml";

            // when
            TemplatePath templatePath = TemplatePath.of(path);

            // then
            assertThat(templatePath.value()).isEqualTo(path);
            assertThat(templatePath.fileName()).isEqualTo("application.yml");
            assertThat(templatePath.directory()).isEqualTo("src/main/resources");
            assertThat(templatePath.extension()).isEqualTo(".yml");
        }

        @Test
        @DisplayName("TemplatePath 빈 값 검증 실패")
        void templatePath_WithBlankValue_ShouldThrow() {
            // when & then
            assertThatThrownBy(() -> TemplatePath.of(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("must not be blank");
        }

        @Test
        @DisplayName("TemplatePath 최대 길이 검증 실패")
        void templatePath_WithLongValue_ShouldThrow() {
            // given
            String longPath = "a".repeat(256);

            // when & then
            assertThatThrownBy(() -> TemplatePath.of(longPath))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("must not exceed");
        }

        @Test
        @DisplayName("TemplateContent 생성")
        void templateContent_ShouldCreateCorrectly() {
            // when
            TemplateContent content = TemplateContent.of("spring:\n  application:\n    name: test");
            TemplateContent empty = TemplateContent.empty();

            // then
            assertThat(content.hasContent()).isTrue();
            assertThat(content.isEmpty()).isFalse();
            assertThat(content.length()).isGreaterThan(0);

            assertThat(empty.hasContent()).isFalse();
            assertThat(empty.isEmpty()).isTrue();
            assertThat(empty.length()).isEqualTo(0);
        }

        @Test
        @DisplayName("TemplateCategory enum 테스트")
        void templateCategory_ShouldHaveCorrectValues() {
            // when & then
            assertThat(TemplateCategory.CONFIG.isConfig()).isTrue();
            assertThat(TemplateCategory.CONFIG.isI18n()).isFalse();
            assertThat(TemplateCategory.I18N.isI18n()).isTrue();
            assertThat(TemplateCategory.STATIC.isStatic()).isTrue();
            assertThat(TemplateCategory.BUILD.isBuild()).isTrue();
            assertThat(TemplateCategory.CONFIG.description()).isEqualTo("설정 파일");
        }

        @Test
        @DisplayName("FileType enum 테스트")
        void fileType_ShouldHaveCorrectValues() {
            // when & then
            assertThat(FileType.YAML.isYaml()).isTrue();
            assertThat(FileType.PROPERTIES.isProperties()).isTrue();
            assertThat(FileType.JSON.isJson()).isTrue();
            assertThat(FileType.GRADLE.isGradle()).isTrue();

            assertThat(FileType.fromExtension("yaml")).isEqualTo(FileType.YAML);
            assertThat(FileType.fromExtension("yml")).isEqualTo(FileType.YAML);
            assertThat(FileType.fromExtension("json")).isEqualTo(FileType.JSON);
            assertThat(FileType.fromExtension("gradle")).isEqualTo(FileType.GRADLE);
            assertThat(FileType.fromExtension("properties")).isEqualTo(FileType.PROPERTIES);
            assertThat(FileType.fromExtension("unknown")).isEqualTo(FileType.OTHER);
            assertThat(FileType.fromExtension(null)).isEqualTo(FileType.OTHER);
            assertThat(FileType.fromExtension("")).isEqualTo(FileType.OTHER);

            assertThat(FileType.YAML.extension()).isEqualTo("yaml");
            assertThat(FileType.YAML.displayName()).isEqualTo("YAML");
        }

        @Test
        @DisplayName("ResourceTemplateId 테스트")
        void resourceTemplateId_ShouldWorkCorrectly() {
            // when
            ResourceTemplateId newId = ResourceTemplateId.forNew();
            ResourceTemplateId existingId = ResourceTemplateId.of(123L);

            // then
            assertThat(newId.isNew()).isTrue();
            assertThat(newId.value()).isNull();

            assertThat(existingId.isNew()).isFalse();
            assertThat(existingId.value()).isEqualTo(123L);
        }

        @Test
        @DisplayName("ResourceTemplateId.of() null 검증")
        void resourceTemplateId_WithNullValue_ShouldThrow() {
            // when & then
            assertThatThrownBy(() -> ResourceTemplateId.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("must not be null");
        }
    }

    @Nested
    @DisplayName("SliceCriteria")
    class SliceCriteriaTests {

        @Test
        @DisplayName("기본 슬라이스 조건 생성")
        void first_ShouldCreateCorrectly() {
            // when
            ResourceTemplateSliceCriteria criteria = ResourceTemplateSliceCriteria.first(20);

            // then
            assertThat(criteria.moduleIds()).isNull();
            assertThat(criteria.categories()).isNull();
            assertThat(criteria.fileTypes()).isNull();
            assertThat(criteria.size()).isEqualTo(20);
            assertThat(criteria.isFirstPage()).isTrue();
            assertThat(criteria.hasCursor()).isFalse();
            assertThat(criteria.hasModuleFilter()).isFalse();
            assertThat(criteria.hasCategoryFilter()).isFalse();
            assertThat(criteria.fetchSize()).isEqualTo(21);
        }

        @Test
        @DisplayName("모듈 ID 목록으로 필터링된 슬라이스 조건")
        void of_WithModuleIds_ShouldCreateCorrectly() {
            // given
            ModuleId moduleId = ModuleId.of(100L);
            CursorPageRequest<Long> pageRequest = CursorPageRequest.first(10);

            // when
            ResourceTemplateSliceCriteria criteria =
                    ResourceTemplateSliceCriteria.of(List.of(moduleId), null, null, pageRequest);

            // then
            assertThat(criteria.moduleIds()).containsExactly(moduleId);
            assertThat(criteria.hasModuleFilter()).isTrue();
            assertThat(criteria.hasCategoryFilter()).isFalse();
        }

        @Test
        @DisplayName("카테고리 목록으로 필터링된 슬라이스 조건")
        void of_WithCategories_ShouldCreateCorrectly() {
            // given
            CursorPageRequest<Long> pageRequest = CursorPageRequest.first(10);

            // when
            ResourceTemplateSliceCriteria criteria =
                    ResourceTemplateSliceCriteria.of(
                            null, List.of(TemplateCategory.CONFIG), null, pageRequest);

            // then
            assertThat(criteria.categories()).containsExactly(TemplateCategory.CONFIG);
            assertThat(criteria.hasCategoryFilter()).isTrue();
            assertThat(criteria.hasModuleFilter()).isFalse();
        }

        @Test
        @DisplayName("모듈 ID 목록과 카테고리 목록으로 필터링된 슬라이스 조건")
        void of_WithModuleIdsAndCategories_ShouldCreateCorrectly() {
            // given
            ModuleId moduleId = ModuleId.of(100L);
            CursorPageRequest<Long> pageRequest = CursorPageRequest.first(15);

            // when
            ResourceTemplateSliceCriteria criteria =
                    ResourceTemplateSliceCriteria.of(
                            List.of(moduleId), List.of(TemplateCategory.BUILD), null, pageRequest);

            // then
            assertThat(criteria.moduleIds()).containsExactly(moduleId);
            assertThat(criteria.categories()).containsExactly(TemplateCategory.BUILD);
            assertThat(criteria.hasModuleFilter()).isTrue();
            assertThat(criteria.hasCategoryFilter()).isTrue();
        }

        @Test
        @DisplayName("커서 기반 슬라이스 조건")
        void afterId_ShouldCreateCorrectly() {
            // when
            ResourceTemplateSliceCriteria criteria = ResourceTemplateSliceCriteria.afterId(50L, 20);

            // then
            assertThat(criteria.cursorPageRequest().cursor()).isEqualTo(50L);
            assertThat(criteria.isFirstPage()).isFalse();
            assertThat(criteria.hasCursor()).isTrue();
        }

        @Test
        @DisplayName("of 팩토리 메서드로 슬라이스 조건 생성")
        void of_ShouldCreateCorrectly() {
            // given
            ModuleId moduleId = ModuleId.of(100L);
            CursorPageRequest<Long> pageRequest = CursorPageRequest.afterId(25L, 30);

            // when
            ResourceTemplateSliceCriteria criteria =
                    ResourceTemplateSliceCriteria.of(
                            List.of(moduleId), List.of(TemplateCategory.I18N), null, pageRequest);

            // then
            assertThat(criteria.moduleIds()).containsExactly(moduleId);
            assertThat(criteria.categories()).containsExactly(TemplateCategory.I18N);
            assertThat(criteria.cursorPageRequest()).isEqualTo(pageRequest);
        }

        @Test
        @DisplayName("cursorPageRequest null 검증 실패")
        void sliceCriteria_WithNullPageRequest_ShouldThrow() {
            // when & then
            assertThatThrownBy(() -> new ResourceTemplateSliceCriteria(null, null, null, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("cursorPageRequest must not be null");
        }
    }

    @Nested
    @DisplayName("Exception")
    class ExceptionTests {

        @Test
        @DisplayName("ResourceTemplateNotFoundException 생성")
        void notFoundException_ShouldCreateCorrectly() {
            // given
            Long resourceTemplateId = 123L;

            // when
            ResourceTemplateNotFoundException exception =
                    new ResourceTemplateNotFoundException(resourceTemplateId);

            // then
            assertThat(exception.getMessage()).contains("123");
            assertThat(exception.getErrorCode())
                    .isEqualTo(ResourceTemplateErrorCode.RESOURCE_TEMPLATE_NOT_FOUND);
        }

        @Test
        @DisplayName("ResourceTemplateErrorCode 값 확인")
        void errorCode_ShouldHaveCorrectValues() {
            // when
            ResourceTemplateErrorCode errorCode =
                    ResourceTemplateErrorCode.RESOURCE_TEMPLATE_NOT_FOUND;

            // then
            assertThat(errorCode.getCode()).isEqualTo("RESOURCE_TEMPLATE-001");
            assertThat(errorCode.getHttpStatus()).isEqualTo(404);
            assertThat(errorCode.getMessage()).isEqualTo("ResourceTemplate not found");
        }
    }
}
