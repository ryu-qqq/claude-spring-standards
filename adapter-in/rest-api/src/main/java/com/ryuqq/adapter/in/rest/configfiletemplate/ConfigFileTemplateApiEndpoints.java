package com.ryuqq.adapter.in.rest.configfiletemplate;

import com.ryuqq.adapter.in.rest.common.ApiPaths;

/**
 * ConfigFileTemplateApiEndpoints - ConfigFileTemplate API Endpoint 상수
 *
 * <p>ConfigFileTemplate 도메인 전용 API Endpoint 경로를 중앙 관리합니다.
 *
 * <p><strong>API 구조:</strong>
 *
 * <pre>{@code
 * /api/v1/templates/config-files
 *   ├── GET    /                           # 목록 조회
 *   ├── POST   /                           # 생성
 *   └── PUT    /{configFileTemplateId}     # 수정
 * }</pre>
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class ConfigFileTemplateApiEndpoints {

    private ConfigFileTemplateApiEndpoints() {
        // Utility class - prevent instantiation
    }

    // ============================================
    // Base Paths
    // ============================================

    /** Standards API 기본 경로 */
    public static final String STANDARDS_BASE = ApiPaths.SERVICE_BASE;

    // ============================================
    // ConfigFileTemplate Endpoints
    // ============================================

    /** ConfigFileTemplate 기본 경로 */
    public static final String CONFIG_FILES = STANDARDS_BASE + "/config-files";

    /** ConfigFileTemplate 단일 경로 */
    public static final String CONFIG_FILE_DETAIL = CONFIG_FILES + "/{configFileTemplateId}";

    // ============================================
    // Relative Paths (for @GetMapping, @PutMapping, etc.)
    // ============================================

    /** ID 경로 (상대경로) */
    public static final String ID = "/{configFileTemplateId}";

    // ============================================
    // Path Variable Names
    // ============================================

    /** ConfigFileTemplate ID 경로 변수명 */
    public static final String PATH_CONFIG_FILE_TEMPLATE_ID = "configFileTemplateId";
}
