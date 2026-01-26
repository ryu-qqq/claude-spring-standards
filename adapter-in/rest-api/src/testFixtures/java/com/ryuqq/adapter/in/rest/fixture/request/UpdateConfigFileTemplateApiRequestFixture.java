package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.configfiletemplate.dto.request.UpdateConfigFileTemplateApiRequest;

/**
 * UpdateConfigFileTemplateApiRequest Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class UpdateConfigFileTemplateApiRequestFixture {

    private UpdateConfigFileTemplateApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static UpdateConfigFileTemplateApiRequest valid() {
        return new UpdateConfigFileTemplateApiRequest(
                "CLAUDE",
                ".claude/CLAUDE.md",
                "CLAUDE.md",
                "# Updated Project Configuration\n\nUpdated content.",
                "MAIN_CONFIG",
                "Claude Code 메인 설정 파일 (수정됨)",
                "{\"project_name\": \"string\", \"version\": \"string\", \"author\": \"string\"}",
                0,
                true);
    }

    public static UpdateConfigFileTemplateApiRequest invalidWithBlankFilePath() {
        return new UpdateConfigFileTemplateApiRequest(
                "CLAUDE", "", "CLAUDE.md", "# Content", null, null, null, 0, true);
    }
}
