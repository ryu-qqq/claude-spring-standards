package com.ryuqq.application.configfiletemplate.dto.response;

import com.ryuqq.domain.common.vo.SliceMeta;
import java.util.List;

/**
 * ConfigFileTemplateSliceResult - ConfigFileTemplate 슬라이스 조회 결과
 *
 * <p>페이징된 ConfigFileTemplate 목록과 메타 정보를 담습니다.
 *
 * <p>RDTO-001: Application 반환 DTO는 Record로 작성합니다.
 *
 * <p>RDTO-009: List 직접 반환 금지 → SliceMeta와 함께 반환합니다.
 *
 * @param content ConfigFileTemplate 결과 목록
 * @param sliceMeta 슬라이스 메타 정보
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ConfigFileTemplateSliceResult(
        List<ConfigFileTemplateResult> content, SliceMeta sliceMeta) {

    public ConfigFileTemplateSliceResult {
        content = content != null ? List.copyOf(content) : List.of();
    }

    /**
     * 빈 결과 생성
     *
     * @param size 페이지 크기
     * @return 빈 ConfigFileTemplateSliceResult
     */
    public static ConfigFileTemplateSliceResult empty(int size) {
        return new ConfigFileTemplateSliceResult(List.of(), SliceMeta.empty(size));
    }
}
