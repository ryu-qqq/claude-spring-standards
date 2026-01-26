package com.ryuqq.adapter.in.rest.common.controller;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.adapter.in.rest.common.ApiPaths;
import com.ryuqq.adapter.in.rest.common.RestDocsTestSupport;
import com.ryuqq.adapter.in.rest.common.error.ErrorMapperRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * ApiDocsController REST Docs 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest(ApiDocsController.class)
@DisplayName("ApiDocsController REST Docs")
class ApiDocsControllerRestDocsTest extends RestDocsTestSupport {

    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Test
    @DisplayName("GET /api/v1/templates/docs - API 문서 메인 페이지로 리다이렉트")
    void getDocs_ShouldRedirectToIndex() throws Exception {
        // When & Then
        mockMvc.perform(get(ApiPaths.DOCS))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ApiPaths.DOCS + "/index.html"))
                .andDo(document("api-docs-redirect"));
    }
}
