package com.ryuqq.application.classtype.assembler;

import com.ryuqq.application.classtype.dto.response.ClassTypeResult;
import com.ryuqq.application.classtype.dto.response.ClassTypeSliceResult;
import com.ryuqq.domain.classtype.aggregate.ClassType;
import com.ryuqq.domain.common.vo.SliceMeta;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ClassTypeAssembler - ClassType Domain → Response DTO 변환 Assembler
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
public class ClassTypeAssembler {

    /**
     * ClassType을 ClassTypeResult로 변환
     *
     * @param classType 도메인 객체
     * @return ClassTypeResult
     */
    public ClassTypeResult toResult(ClassType classType) {
        return new ClassTypeResult(
                classType.idValue(),
                classType.categoryIdValue(),
                classType.codeValue(),
                classType.nameValue(),
                classType.description(),
                classType.orderIndex(),
                classType.createdAt(),
                classType.updatedAt());
    }

    /**
     * ClassType 목록을 ClassTypeSliceResult로 변환
     *
     * @param classTypes 도메인 객체 목록
     * @param size 페이지 크기
     * @return ClassTypeSliceResult
     */
    public ClassTypeSliceResult toSliceResult(List<ClassType> classTypes, int size) {
        boolean hasNext = classTypes.size() > size;
        List<ClassType> content = hasNext ? classTypes.subList(0, size) : classTypes;

        List<ClassTypeResult> results = content.stream().map(this::toResult).toList();

        Long nextCursor =
                hasNext && !content.isEmpty() ? content.get(content.size() - 1).idValue() : null;

        SliceMeta sliceMeta = SliceMeta.withCursor(nextCursor, size, hasNext);

        return new ClassTypeSliceResult(results, sliceMeta);
    }
}
