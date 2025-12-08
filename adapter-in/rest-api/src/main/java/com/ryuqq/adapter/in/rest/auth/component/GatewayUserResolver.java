package com.ryuqq.adapter.in.rest.auth.component;

import com.ryuqq.adapter.in.rest.auth.config.SecurityProperties;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * Gateway 헤더에서 사용자 정보를 추출하는 컴포넌트.
 *
 * <p>Gateway Only 아키텍처에서 Gateway가 JWT 검증 후 설정한
 * X-User-Id, X-User-Roles 헤더를 파싱하여 {@link GatewayUser}를 생성합니다.
 *
 * <p>헤더 형식:
 *
 * <ul>
 *   <li>X-User-Id: UUIDv7 형태의 사용자 ID (예: "0191d38a-1c00-7000-8000-000000000001")
 *   <li>X-User-Roles: 쉼표로 구분된 역할 목록 (예: "ROLE_ADMIN,ROLE_USER")
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 * @see GatewayUser
 * @see com.ryuqq.adapter.in.rest.auth.config.GatewayProperties
 */
@Component
public class GatewayUserResolver {

    private final SecurityProperties securityProperties;

    public GatewayUserResolver(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    /**
     * HTTP 요청에서 Gateway 사용자 정보를 추출합니다.
     *
     * <p>Gateway가 설정한 헤더(X-User-Id, X-User-Roles)를 읽어
     * {@link GatewayUser} 객체를 생성합니다.
     *
     * @param request HTTP 요청
     * @return GatewayUser 또는 null (헤더가 없거나 유효하지 않은 경우)
     */
    public GatewayUser resolve(HttpServletRequest request) {
        String userIdHeader = securityProperties.getGateway().getUserIdHeader();
        String userRolesHeader = securityProperties.getGateway().getUserRolesHeader();

        String userIdValue = request.getHeader(userIdHeader);

        if (userIdValue == null || userIdValue.isBlank()) {
            return null;
        }

        UUID userId = parseUserId(userIdValue);
        if (userId == null) {
            return null;
        }

        String rolesValue = request.getHeader(userRolesHeader);
        List<String> roles = parseRoles(rolesValue);

        return new GatewayUser(userId, roles);
    }

    /**
     * 사용자 ID 문자열을 UUID로 파싱합니다.
     *
     * @param userIdValue 사용자 ID 문자열 (UUIDv7 형식)
     * @return UUID 타입 사용자 ID 또는 null (파싱 실패 시)
     */
    private UUID parseUserId(String userIdValue) {
        try {
            return UUID.fromString(userIdValue.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * 역할 헤더 문자열을 역할 목록으로 파싱합니다.
     *
     * <p>쉼표로 구분된 역할 문자열을 파싱합니다. (예: "ROLE_ADMIN,ROLE_USER")
     *
     * @param rolesValue 역할 헤더 값
     * @return 역할 목록 (빈 문자열이거나 null인 경우 빈 리스트 반환)
     */
    private List<String> parseRoles(String rolesValue) {
        if (rolesValue == null || rolesValue.isBlank()) {
            return Collections.emptyList();
        }

        return Arrays.stream(rolesValue.split(","))
                .map(String::trim)
                .filter(role -> !role.isEmpty())
                .toList();
    }
}
