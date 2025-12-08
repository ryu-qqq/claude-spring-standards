package com.ryuqq.adapter.in.rest.auth.component;

import java.util.List;
import java.util.UUID;

/**
 * Gateway에서 전달된 사용자 정보.
 *
 * <p>Gateway Only 아키텍처에서 Gateway가 JWT를 검증한 후
 * 헤더(X-User-Id, X-User-Roles)로 전달한 사용자 정보를 담는 불변 객체입니다.
 *
 * <p>주요 기능:
 *
 * <ul>
 *   <li>사용자 ID 및 역할 정보 보관
 *   <li>역할 기반 권한 확인 메서드 제공
 *   <li>Spring Security Authentication 변환 지원
 * </ul>
 *
 * @param userId 사용자 ID (UUIDv7, Gateway에서 X-User-Id 헤더로 전달)
 * @param roles 역할 목록 (Gateway에서 X-User-Roles 헤더로 전달, 쉼표 구분)
 * @author development-team
 * @since 1.0.0
 * @see GatewayUserResolver
 * @see <a href="../../security/gateway-only-architecture.md">Gateway Only Architecture Guide</a>
 */
public record GatewayUser(
    UUID userId,
    List<String> roles
) {

    /**
     * GatewayUser 생성자.
     *
     * @param userId 사용자 ID (UUIDv7, null 불가)
     * @param roles 역할 목록 (null인 경우 빈 리스트로 변환)
     * @throws IllegalArgumentException userId가 null인 경우
     */
    public GatewayUser {
        if (userId == null) {
            throw new IllegalArgumentException("userId must not be null");
        }
        if (roles == null) {
            roles = List.of();
        } else {
            roles = List.copyOf(roles);
        }
    }

    /**
     * 특정 역할을 가지고 있는지 확인합니다.
     *
     * @param role 확인할 역할 (예: "ROLE_ADMIN", "ROLE_USER")
     * @return 해당 역할을 보유하면 true
     */
    public boolean hasRole(String role) {
        return roles.contains(role);
    }

    /**
     * 관리자 역할을 가지고 있는지 확인합니다.
     *
     * @return ROLE_ADMIN 역할을 보유하면 true
     */
    public boolean isAdmin() {
        return hasRole("ROLE_ADMIN");
    }

    /**
     * 주어진 역할 중 하나라도 보유하고 있는지 확인합니다.
     *
     * @param requiredRoles 확인할 역할 목록
     * @return 하나라도 보유하면 true
     */
    public boolean hasAnyRole(String... requiredRoles) {
        for (String role : requiredRoles) {
            if (hasRole(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 주어진 역할을 모두 보유하고 있는지 확인합니다.
     *
     * @param requiredRoles 확인할 역할 목록
     * @return 모두 보유하면 true
     */
    public boolean hasAllRoles(String... requiredRoles) {
        for (String role : requiredRoles) {
            if (!hasRole(role)) {
                return false;
            }
        }
        return true;
    }
}
