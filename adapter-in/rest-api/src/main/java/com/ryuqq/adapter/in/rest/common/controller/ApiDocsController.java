package com.ryuqq.adapter.in.rest.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * API 문서 접근용 Controller
 *
 * <p>Spring REST Docs로 생성된 HTML 문서를 서빙합니다.
 *
 * <p><strong>접근 경로:</strong>
 * <ul>
 *   <li>{@code /docs} - API 문서 메인 페이지</li>
 *   <li>{@code /docs/index.html} - API 문서 메인 페이지 (직접 접근)</li>
 * </ul>
 *
 * <p><strong>문서 위치:</strong>
 * <ul>
 *   <li>소스: {@code src/docs/asciidoc/}</li>
 *   <li>빌드 결과: {@code build/docs/asciidoc/}</li>
 *   <li>배포 위치: {@code static/docs/} (bootJar 내)</li>
 * </ul>
 *
 * <p><strong>빌드 방법:</strong>
 * <pre>{@code
 * ./gradlew :bootstrap:bootstrap-web-api:asciidoctor
 * }</pre>
 *
 * @author Development Team
 * @since 1.0.0
 * @see <a href="https://docs.spring.io/spring-restdocs/docs/current/reference/htmlsingle/">Spring REST Docs</a>
 */
@Controller
public class ApiDocsController {

    /**
     * API 문서 메인 페이지로 리다이렉트
     *
     * <p>{@code /docs} 접근 시 {@code /docs/index.html}로 리다이렉트합니다.
     *
     * @return 리다이렉트 경로
     */
    @GetMapping("/docs")
    public String redirectToApiDocs() {
        return "redirect:/docs/index.html";
    }
}
