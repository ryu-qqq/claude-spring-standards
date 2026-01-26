package com.ryuqq.application.layer.assembler;

import com.ryuqq.application.layer.dto.response.LayerResult;
import com.ryuqq.application.layer.dto.response.LayerSliceResult;
import com.ryuqq.domain.common.vo.SliceMeta;
import com.ryuqq.domain.layer.aggregate.Layer;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * LayerAssembler - Layer Domain -> Response DTO 변환
 *
 * <p>Domain 객체를 Response DTO로 변환합니다.
 *
 * <p>SVC-005: Domain 객체 직접 반환 금지 -> Assembler를 통해 변환.
 *
 * <p>RDTO-008: Response DTO는 Domain 타입 의존 금지 -> Assembler에서 값 추출.
 *
 * <p>C-002: 변환기에서 null 체크 금지.
 *
 * <p>C-003: 변환기에서 기본값 할당 금지.
 *
 * @author ryu-qqq
 */
@Component
public class LayerAssembler {

    /**
     * Layer Domain을 LayerResult로 변환
     *
     * @param layer Layer 도메인 객체
     * @return LayerResult DTO
     */
    public LayerResult toResult(Layer layer) {
        return new LayerResult(
                layer.idValue(),
                layer.architectureIdValue(),
                layer.codeValue(),
                layer.nameValue(),
                layer.description(),
                layer.orderIndex(),
                layer.createdAt(),
                layer.updatedAt());
    }

    /**
     * Layer Domain 목록을 LayerResult 목록으로 변환
     *
     * @param layers Layer 도메인 객체 목록
     * @return LayerResult 목록
     */
    public List<LayerResult> toResults(List<Layer> layers) {
        return layers.stream().map(this::toResult).toList();
    }

    /**
     * Layer Domain 목록을 LayerSliceResult로 변환
     *
     * <p>RDTO-009: List 직접 반환 금지 → SliceMeta와 함께 반환합니다.
     *
     * @param layers Layer 도메인 객체 목록
     * @param size 페이지 크기
     * @return LayerSliceResult
     */
    public LayerSliceResult toSliceResult(List<Layer> layers, int size) {
        List<LayerResult> content = toResults(layers);
        boolean hasNext = content.size() > size;

        if (hasNext) {
            content = content.subList(0, size);
        }

        return new LayerSliceResult(content, SliceMeta.of(size, hasNext));
    }
}
