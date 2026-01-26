package com.ryuqq.domain.resourcetemplate.fixture;

import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.module.id.ModuleId;
import com.ryuqq.domain.resourcetemplate.aggregate.ResourceTemplate;
import com.ryuqq.domain.resourcetemplate.id.ResourceTemplateId;
import com.ryuqq.domain.resourcetemplate.vo.FileType;
import com.ryuqq.domain.resourcetemplate.vo.TemplateCategory;
import com.ryuqq.domain.resourcetemplate.vo.TemplateContent;
import com.ryuqq.domain.resourcetemplate.vo.TemplatePath;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ResourceTemplate Aggregate Test Fixture
 *
 * <p>모든 레이어에서 재사용 가능한 ResourceTemplate 객체 생성 유틸리티
 *
 * @author ryu-qqq
 */
public final class ResourceTemplateFixture {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneId.of("UTC"));

    private static final AtomicLong ID_SEQUENCE = new AtomicLong(1L);

    private ResourceTemplateFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /**
     * 고정 Module ID
     *
     * @return ModuleId
     */
    public static ModuleId fixedModuleId() {
        return ModuleId.of(100L);
    }

    /**
     * 다음 ResourceTemplateId 생성
     *
     * @return ResourceTemplateId
     */
    public static ResourceTemplateId nextResourceTemplateId() {
        return ResourceTemplateId.of(ID_SEQUENCE.getAndIncrement());
    }

    /**
     * 신규 ResourceTemplate 생성 (ID 미할당)
     *
     * @return 신규 ResourceTemplate
     */
    public static ResourceTemplate forNew() {
        return ResourceTemplate.forNew(
                fixedModuleId(),
                TemplateCategory.CONFIG,
                TemplatePath.of("src/main/resources/application.yml"),
                FileType.YAML,
                "기본 설정 파일 템플릿",
                TemplateContent.of("spring:\n  application:\n    name: ${module.name}"),
                true,
                FIXED_CLOCK.instant());
    }

    /**
     * 기존 ResourceTemplate 복원 (기본 설정)
     *
     * @return 복원된 ResourceTemplate
     */
    public static ResourceTemplate reconstitute() {
        return reconstitute(nextResourceTemplateId());
    }

    /**
     * 지정된 ID로 ResourceTemplate 복원
     *
     * @param id ResourceTemplateId
     * @return 복원된 ResourceTemplate
     */
    public static ResourceTemplate reconstitute(ResourceTemplateId id) {
        Instant now = FIXED_CLOCK.instant();
        return ResourceTemplate.reconstitute(
                id,
                fixedModuleId(),
                TemplateCategory.CONFIG,
                TemplatePath.of("src/main/resources/application.yml"),
                FileType.YAML,
                "기본 설정 파일 템플릿",
                TemplateContent.of("spring:\n  application:\n    name: ${module.name}"),
                true,
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * 기본 기존 ResourceTemplate (저장된 상태)
     *
     * @return 기존 ResourceTemplate
     */
    public static ResourceTemplate defaultExistingResourceTemplate() {
        Instant now = FIXED_CLOCK.instant();
        return ResourceTemplate.of(
                nextResourceTemplateId(),
                fixedModuleId(),
                TemplateCategory.CONFIG,
                TemplatePath.of("src/main/resources/application.yml"),
                FileType.YAML,
                "기본 설정 파일 템플릿",
                TemplateContent.of("spring:\n  application:\n    name: ${module.name}"),
                true,
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * CONFIG 카테고리 템플릿
     *
     * @return CONFIG ResourceTemplate
     */
    public static ResourceTemplate configTemplate() {
        Instant now = FIXED_CLOCK.instant();
        return ResourceTemplate.reconstitute(
                nextResourceTemplateId(),
                fixedModuleId(),
                TemplateCategory.CONFIG,
                TemplatePath.of("src/main/resources/application.yml"),
                FileType.YAML,
                "설정 파일 템플릿",
                TemplateContent.of("spring:\n  profiles:\n    active: local"),
                true,
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * BUILD 카테고리 템플릿
     *
     * @return BUILD ResourceTemplate
     */
    public static ResourceTemplate buildTemplate() {
        Instant now = FIXED_CLOCK.instant();
        return ResourceTemplate.reconstitute(
                nextResourceTemplateId(),
                fixedModuleId(),
                TemplateCategory.BUILD,
                TemplatePath.of("build.gradle"),
                FileType.GRADLE,
                "Gradle 빌드 파일 템플릿",
                TemplateContent.of("plugins {\n    id 'java'\n}"),
                true,
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * I18N 카테고리 템플릿
     *
     * @return I18N ResourceTemplate
     */
    public static ResourceTemplate i18nTemplate() {
        Instant now = FIXED_CLOCK.instant();
        return ResourceTemplate.reconstitute(
                nextResourceTemplateId(),
                fixedModuleId(),
                TemplateCategory.I18N,
                TemplatePath.of("src/main/resources/messages.properties"),
                FileType.PROPERTIES,
                "다국어 메시지 파일 템플릿",
                TemplateContent.of("welcome.message=Welcome"),
                false,
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * 선택적 (required=false) 템플릿
     *
     * @return 선택적 ResourceTemplate
     */
    public static ResourceTemplate optionalTemplate() {
        Instant now = FIXED_CLOCK.instant();
        return ResourceTemplate.reconstitute(
                nextResourceTemplateId(),
                fixedModuleId(),
                TemplateCategory.STATIC,
                TemplatePath.of("src/main/resources/static/index.html"),
                FileType.OTHER,
                "정적 리소스 템플릿",
                TemplateContent.empty(),
                false,
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * 삭제된 ResourceTemplate
     *
     * @return 삭제된 ResourceTemplate
     */
    public static ResourceTemplate deletedResourceTemplate() {
        Instant now = FIXED_CLOCK.instant();
        return ResourceTemplate.reconstitute(
                nextResourceTemplateId(),
                fixedModuleId(),
                TemplateCategory.CONFIG,
                TemplatePath.of("src/main/resources/application.yml"),
                FileType.YAML,
                "삭제된 설정 파일 템플릿",
                TemplateContent.of("# deleted"),
                true,
                DeletionStatus.deletedAt(now),
                now,
                now);
    }

    /**
     * 내용이 없는 템플릿
     *
     * @return 내용 없는 ResourceTemplate
     */
    public static ResourceTemplate emptyContentTemplate() {
        Instant now = FIXED_CLOCK.instant();
        return ResourceTemplate.reconstitute(
                nextResourceTemplateId(),
                fixedModuleId(),
                TemplateCategory.CONFIG,
                TemplatePath.of("src/main/resources/empty.yml"),
                FileType.YAML,
                "내용 없는 템플릿",
                TemplateContent.empty(),
                false,
                DeletionStatus.active(),
                now,
                now);
    }
}
