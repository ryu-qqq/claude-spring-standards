package com.ryuqq.application.classtemplate.assembler;

import com.ryuqq.application.classtemplate.dto.response.ClassTemplateResult;
import com.ryuqq.application.classtemplate.dto.response.ClassTemplateSliceResult;
import com.ryuqq.domain.classtemplate.aggregate.ClassTemplate;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ClassTemplateAssembler - 클래스 템플릿 응답 조립기
 *
 * <p>도메인 객체를 Application DTO로 변환합니다.
 *
 * <p>ASM-001: Assembler는 Domain -> Application DTO 변환만 담당.
 *
 * @author ryu-qqq
 */
@Component
public class ClassTemplateAssembler {

    /**
     * ClassTemplate 도메인 객체를 ClassTemplateResult로 변환
     *
     * @param classTemplate 클래스 템플릿 도메인 객체
     * @return ClassTemplateResult
     */
    public ClassTemplateResult toResult(ClassTemplate classTemplate) {
        return ClassTemplateResult.from(classTemplate);
    }

    /**
     * ClassTemplate 목록을 ClassTemplateResult 목록으로 변환
     *
     * @param classTemplates 클래스 템플릿 도메인 객체 목록
     * @return ClassTemplateResult 목록
     */
    public List<ClassTemplateResult> toResults(List<ClassTemplate> classTemplates) {
        return classTemplates.stream().map(this::toResult).toList();
    }

    /**
     * ClassTemplate 목록을 ClassTemplateSliceResult로 변환
     *
     * @param classTemplates 클래스 템플릿 도메인 객체 목록
     * @param requestedSize 요청한 페이지 크기
     * @return ClassTemplateSliceResult
     */
    public ClassTemplateSliceResult toSliceResult(
            List<ClassTemplate> classTemplates, int requestedSize) {
        boolean hasNext = classTemplates.size() > requestedSize;
        List<ClassTemplate> resultClassTemplates =
                hasNext ? classTemplates.subList(0, requestedSize) : classTemplates;
        List<ClassTemplateResult> results = toResults(resultClassTemplates);
        return ClassTemplateSliceResult.of(results, hasNext);
    }
}
