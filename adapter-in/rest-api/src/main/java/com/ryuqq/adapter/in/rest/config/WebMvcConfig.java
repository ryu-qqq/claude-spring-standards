package com.ryuqq.adapter.in.rest.config;

import com.ryuqq.adapter.in.rest.common.ApiPaths;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 설정
 *
 * <p>REST Docs 등 정적 리소스 경로를 설정합니다.
 *
 * <p><strong>API Gateway 라우팅:</strong>
 *
 * <ul>
 *   <li>Gateway: /api/v1/templates/** → 이 서버 (prefix strip 없음)
 *   <li>REST Docs: /api/v1/templates/docs/** → static/docs/
 * </ul>
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private static final String DOCS_PATH = ApiPaths.DOCS_PATTERN;

    /**
     * REST Docs 정적 리소스 핸들러 등록
     *
     * @param registry ResourceHandlerRegistry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(DOCS_PATH).addResourceLocations("classpath:/static/docs/");
    }
}
