package com.ryuqq.application.resourcetemplate.assembler;

import com.ryuqq.application.resourcetemplate.dto.response.ResourceTemplateResult;
import com.ryuqq.application.resourcetemplate.dto.response.ResourceTemplateSliceResult;
import com.ryuqq.domain.resourcetemplate.aggregate.ResourceTemplate;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ResourceTemplateAssembler - 리소스 템플릿 응답 조립기
 *
 * <p>도메인 객체를 Application DTO로 변환합니다.
 *
 * <p>ASM-001: Assembler는 Domain -> Application DTO 변환만 담당.
 *
 * @author ryu-qqq
 */
@Component
public class ResourceTemplateAssembler {

    /**
     * ResourceTemplate 도메인 객체를 ResourceTemplateResult로 변환
     *
     * @param resourceTemplate 리소스 템플릿 도메인 객체
     * @return ResourceTemplateResult
     */
    public ResourceTemplateResult toResult(ResourceTemplate resourceTemplate) {
        return ResourceTemplateResult.from(resourceTemplate);
    }

    /**
     * ResourceTemplate 목록을 ResourceTemplateResult 목록으로 변환
     *
     * @param resourceTemplates 리소스 템플릿 도메인 객체 목록
     * @return ResourceTemplateResult 목록
     */
    public List<ResourceTemplateResult> toResults(List<ResourceTemplate> resourceTemplates) {
        return resourceTemplates.stream().map(this::toResult).toList();
    }

    /**
     * ResourceTemplate 목록을 ResourceTemplateSliceResult로 변환
     *
     * @param resourceTemplates 리소스 템플릿 도메인 객체 목록
     * @param requestedSize 요청한 페이지 크기
     * @return ResourceTemplateSliceResult
     */
    public ResourceTemplateSliceResult toSliceResult(
            List<ResourceTemplate> resourceTemplates, int requestedSize) {
        boolean hasNext = resourceTemplates.size() > requestedSize;
        List<ResourceTemplate> resultResourceTemplates =
                hasNext ? resourceTemplates.subList(0, requestedSize) : resourceTemplates;
        List<ResourceTemplateResult> results = toResults(resultResourceTemplates);
        return ResourceTemplateSliceResult.of(results, hasNext);
    }
}
