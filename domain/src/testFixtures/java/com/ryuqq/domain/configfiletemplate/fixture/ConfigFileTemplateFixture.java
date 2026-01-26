package com.ryuqq.domain.configfiletemplate.fixture;

import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.configfiletemplate.aggregate.ConfigFileTemplate;
import com.ryuqq.domain.configfiletemplate.id.ConfigFileTemplateId;
import com.ryuqq.domain.configfiletemplate.vo.DisplayOrder;
import com.ryuqq.domain.configfiletemplate.vo.FileName;
import com.ryuqq.domain.configfiletemplate.vo.FilePath;
import com.ryuqq.domain.configfiletemplate.vo.TemplateCategory;
import com.ryuqq.domain.configfiletemplate.vo.TemplateContent;
import com.ryuqq.domain.configfiletemplate.vo.TemplateDescription;
import com.ryuqq.domain.configfiletemplate.vo.TemplateVariables;
import com.ryuqq.domain.configfiletemplate.vo.ToolType;
import com.ryuqq.domain.techstack.id.TechStackId;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

/**
 * ConfigFileTemplate Aggregate Test Fixture
 *
 * <p>모든 레이어에서 재사용 가능한 ConfigFileTemplate 객체 생성 유틸리티
 *
 * @author development-team
 * @since 1.0.0
 */
public final class ConfigFileTemplateFixture {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneId.of("UTC"));

    private ConfigFileTemplateFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /** 기본 ConfigFileTemplate Fixture (저장된 상태) - Claude Tool */
    public static ConfigFileTemplate defaultExistingConfigFileTemplate() {
        Instant now = FIXED_CLOCK.instant();
        return ConfigFileTemplate.reconstitute(
                ConfigFileTemplateId.of(1L),
                TechStackId.of(1L),
                ArchitectureId.of(1L),
                ToolType.CLAUDE,
                FilePath.of(".claude/"),
                FileName.of("CLAUDE.md"),
                TemplateContent.of("# Claude Configuration\n\nProject-specific Claude settings."),
                TemplateCategory.MAIN_CONFIG,
                TemplateDescription.of("Claude Code 프로젝트 설정 파일"),
                TemplateVariables.empty(),
                DisplayOrder.of(1),
                true,
                DeletionStatus.active(),
                now,
                now);
    }

    /** Cursor Tool용 ConfigFileTemplate */
    public static ConfigFileTemplate cursorConfigFileTemplate() {
        Instant now = FIXED_CLOCK.instant();
        return ConfigFileTemplate.reconstitute(
                ConfigFileTemplateId.of(2L),
                TechStackId.of(1L),
                ArchitectureId.of(1L),
                ToolType.CURSOR,
                FilePath.of(".cursor/"),
                FileName.of("rules.json"),
                TemplateContent.of("{\"rules\": []}"),
                TemplateCategory.MAIN_CONFIG,
                TemplateDescription.of("Cursor 에디터 규칙 파일"),
                TemplateVariables.empty(),
                DisplayOrder.of(2),
                true,
                DeletionStatus.active(),
                now,
                now);
    }

    /** Copilot Tool용 ConfigFileTemplate */
    public static ConfigFileTemplate copilotConfigFileTemplate() {
        Instant now = FIXED_CLOCK.instant();
        return ConfigFileTemplate.reconstitute(
                ConfigFileTemplateId.of(3L),
                TechStackId.of(1L),
                null,
                ToolType.COPILOT,
                FilePath.of(".github/"),
                FileName.of("copilot-instructions.md"),
                TemplateContent.of("# Copilot Instructions\n\nProject coding guidelines."),
                TemplateCategory.SKILL,
                TemplateDescription.of("GitHub Copilot 지침 파일"),
                TemplateVariables.empty(),
                DisplayOrder.of(3),
                false,
                DeletionStatus.active(),
                now,
                now);
    }

    /** 삭제된 ConfigFileTemplate */
    public static ConfigFileTemplate deletedConfigFileTemplate() {
        Instant now = FIXED_CLOCK.instant();
        return ConfigFileTemplate.reconstitute(
                ConfigFileTemplateId.of(4L),
                TechStackId.of(1L),
                ArchitectureId.of(1L),
                ToolType.CLAUDE,
                FilePath.of(".claude/"),
                FileName.of("deleted.md"),
                TemplateContent.of("# Deleted"),
                TemplateCategory.SKILL,
                TemplateDescription.of("삭제된 파일"),
                TemplateVariables.empty(),
                DisplayOrder.of(99),
                false,
                DeletionStatus.deletedAt(now),
                now,
                now);
    }

    /** 커스텀 ConfigFileTemplate 생성 */
    public static ConfigFileTemplate customConfigFileTemplate(
            Long id,
            Long techStackId,
            Long architectureId,
            ToolType toolType,
            String filePath,
            String fileName,
            String content,
            int displayOrder) {
        Instant now = FIXED_CLOCK.instant();
        return ConfigFileTemplate.reconstitute(
                ConfigFileTemplateId.of(id),
                TechStackId.of(techStackId),
                architectureId != null ? ArchitectureId.of(architectureId) : null,
                toolType,
                FilePath.of(filePath),
                FileName.of(fileName),
                TemplateContent.of(content),
                TemplateCategory.MAIN_CONFIG,
                TemplateDescription.of("Custom template"),
                TemplateVariables.empty(),
                DisplayOrder.of(displayOrder),
                true,
                DeletionStatus.active(),
                now,
                now);
    }
}
