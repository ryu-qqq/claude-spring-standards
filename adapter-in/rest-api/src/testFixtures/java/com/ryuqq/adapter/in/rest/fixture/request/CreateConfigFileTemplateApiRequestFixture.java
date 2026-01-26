package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.configfiletemplate.dto.request.CreateConfigFileTemplateApiRequest;

/**
 * CreateConfigFileTemplateApiRequest Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class CreateConfigFileTemplateApiRequestFixture {

    private CreateConfigFileTemplateApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static CreateConfigFileTemplateApiRequest valid() {
        return new CreateConfigFileTemplateApiRequest(
                1L,
                1L,
                "CLAUDE",
                ".claude/CLAUDE.md",
                "CLAUDE.md",
                "# Project Configuration\n\nThis is the main configuration file.",
                "MAIN_CONFIG",
                "Claude Code 메인 설정 파일",
                "{\"project_name\": \"string\", \"version\": \"string\"}",
                0,
                true);
    }

    public static CreateConfigFileTemplateApiRequest validWithoutArchitecture() {
        return new CreateConfigFileTemplateApiRequest(
                1L,
                null,
                "CURSOR",
                ".cursor/rules/main.md",
                "main.md",
                "# Cursor Rules",
                "RULE",
                "Cursor 규칙 파일",
                null,
                1,
                false);
    }

    public static CreateConfigFileTemplateApiRequest invalidWithBlankToolType() {
        return new CreateConfigFileTemplateApiRequest(
                1L,
                1L,
                "",
                ".claude/CLAUDE.md",
                "CLAUDE.md",
                "# Content",
                null,
                null,
                null,
                0,
                true);
    }

    public static CreateConfigFileTemplateApiRequest invalidWithNullTechStackId() {
        return new CreateConfigFileTemplateApiRequest(
                null,
                1L,
                "CLAUDE",
                ".claude/CLAUDE.md",
                "CLAUDE.md",
                "# Content",
                null,
                null,
                null,
                0,
                true);
    }
}
