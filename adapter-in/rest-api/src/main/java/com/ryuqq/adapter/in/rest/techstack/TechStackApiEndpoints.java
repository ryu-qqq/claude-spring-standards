package com.ryuqq.adapter.in.rest.techstack;

import com.ryuqq.adapter.in.rest.common.ApiPaths;

/**
 * TechStackApiEndpoints - TechStack API Endpoint 상수
 *
 * <p>TechStack 도메인 전용 API Endpoint 경로를 중앙 관리합니다.
 *
 * <p><strong>API 구조:</strong>
 *
 * <pre>{@code
 * /api/v1/templates/tech-stacks
 *   ├── GET    /                           # 커서 기반 목록 조회
 *   ├── POST   /                           # 생성
 *   ├── PUT    /{techStackId}              # 수정
 *   └── PATCH  /{techStackId}/archive      # 아카이브 (Soft Delete)
 * }</pre>
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class TechStackApiEndpoints {

    private TechStackApiEndpoints() {
        // Utility class - prevent instantiation
    }

    /** TechStack 기본 경로 */
    public static final String TECH_STACKS = ApiPaths.SERVICE_BASE + "/tech-stacks";

    /** ID 경로 (상대경로) */
    public static final String ID = "/{techStackId}";

    /** TechStack ID 경로 변수명 */
    public static final String PATH_TECH_STACK_ID = "techStackId";
}
