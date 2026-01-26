package com.ryuqq.application.classtypecategory.assembler;

import com.ryuqq.application.classtypecategory.dto.response.ClassTypeCategoryResult;
import com.ryuqq.application.classtypecategory.dto.response.ClassTypeCategorySliceResult;
import com.ryuqq.domain.classtypecategory.aggregate.ClassTypeCategory;
import com.ryuqq.domain.common.vo.SliceMeta;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ClassTypeCategoryAssembler - ClassTypeCategory Domain → Response DTO 변환 Assembler
 *
 * <p>Domain 객체를 Response DTO로 변환합니다.
 *
 * <p>ASSM-001: Assembler는 @Component 어노테이션 사용.
 *
 * <p>ASSM-002: Assembler는 {Domain}Assembler 네이밍 사용.
 *
 * <p>ASSM-003: toXxx() 메서드로 변환을 수행합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ClassTypeCategoryAssembler {

    /**
     * ClassTypeCategory를 ClassTypeCategoryResult로 변환
     *
     * @param classTypeCategory 도메인 객체
     * @return ClassTypeCategoryResult
     */
    public ClassTypeCategoryResult toResult(ClassTypeCategory classTypeCategory) {
        return new ClassTypeCategoryResult(
                classTypeCategory.idValue(),
                classTypeCategory.architectureIdValue(),
                classTypeCategory.codeValue(),
                classTypeCategory.nameValue(),
                classTypeCategory.description(),
                classTypeCategory.orderIndex(),
                classTypeCategory.createdAt(),
                classTypeCategory.updatedAt());
    }

    /**
     * ClassTypeCategory 목록을 ClassTypeCategorySliceResult로 변환
     *
     * @param categories 도메인 객체 목록
     * @param size 페이지 크기
     * @return ClassTypeCategorySliceResult
     */
    public ClassTypeCategorySliceResult toSliceResult(
            List<ClassTypeCategory> categories, int size) {
        boolean hasNext = categories.size() > size;
        List<ClassTypeCategory> content = hasNext ? categories.subList(0, size) : categories;

        List<ClassTypeCategoryResult> results = content.stream().map(this::toResult).toList();

        Long nextCursor =
                hasNext && !content.isEmpty() ? content.get(content.size() - 1).idValue() : null;

        SliceMeta sliceMeta = SliceMeta.withCursor(nextCursor, size, hasNext);

        return new ClassTypeCategorySliceResult(results, sliceMeta);
    }
}
