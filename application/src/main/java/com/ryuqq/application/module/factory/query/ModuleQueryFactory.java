package com.ryuqq.application.module.factory.query;

import com.ryuqq.application.module.dto.query.ModuleSearchParams;
import com.ryuqq.domain.common.vo.CursorPageRequest;
import com.ryuqq.domain.layer.id.LayerId;
import com.ryuqq.domain.module.query.ModuleSliceCriteria;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * ModuleQueryFactory - Module 쿼리 팩토리
 *
 * <p>조회에 필요한 도메인 객체를 생성합니다.
 *
 * <p>FAC-001: Criteria 생성도 Factory에서 담당합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ModuleQueryFactory {

    /**
     * ModuleSearchParams로부터 ModuleSliceCriteria 생성
     *
     * <p>FAC-001: Criteria 생성도 Factory에서 담당합니다.
     *
     * @param searchParams 조회 파라미터
     * @return ModuleSliceCriteria
     */
    public ModuleSliceCriteria createSliceCriteria(ModuleSearchParams searchParams) {
        CursorPageRequest<Long> cursorPageRequest;

        if (searchParams.isFirstPage()) {
            cursorPageRequest = CursorPageRequest.first(searchParams.size());
        } else {
            Long cursorId = Long.parseLong(searchParams.cursor());
            cursorPageRequest = CursorPageRequest.afterId(cursorId, searchParams.size());
        }

        List<LayerId> layerIds = null;
        if (searchParams.hasLayerIds()) {
            layerIds =
                    searchParams.layerIds().stream().map(LayerId::of).collect(Collectors.toList());
        }

        return ModuleSliceCriteria.of(cursorPageRequest, layerIds);
    }
}
