package com.ryuqq.adapter.in.rest.fixture.response;

import com.ryuqq.adapter.in.rest.configfiletemplate.dto.response.ConfigFileTemplateApiResponse;

/**
 * ConfigFileTemplateApiResponse Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class ConfigFileTemplateApiResponseFixture {

    private ConfigFileTemplateApiResponseFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static ConfigFileTemplateApiResponse valid() {
        return new ConfigFileTemplateApiResponse(
                1L,
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
                true,
                "2024-01-15T10:30:00Z",
                "2024-01-15T10:30:00Z");
    }

    public static ConfigFileTemplateApiResponse validWithoutOptionalFields() {
        return new ConfigFileTemplateApiResponse(
                2L,
                1L,
                null,
                "CURSOR",
                ".cursor/rules/main.md",
                "main.md",
                "# Cursor Rules",
                null,
                null,
                null,
                1,
                false,
                "2024-01-15T10:30:00Z",
                "2024-01-15T10:30:00Z");
    }
}
