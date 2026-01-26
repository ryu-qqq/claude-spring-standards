package com.ryuqq.application.layer.service;

import com.ryuqq.application.layer.assembler.LayerAssembler;
import com.ryuqq.application.layer.dto.query.LayerSearchParams;
import com.ryuqq.application.layer.dto.response.LayerSliceResult;
import com.ryuqq.application.layer.factory.query.LayerQueryFactory;
import com.ryuqq.application.layer.manager.LayerReadManager;
import com.ryuqq.application.layer.port.in.SearchLayersByCursorUseCase;
import com.ryuqq.domain.layer.aggregate.Layer;
import com.ryuqq.domain.layer.query.LayerSliceCriteria;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * SearchLayersByCursorService - Layer 복합 조건 조회 서비스 (커서 기반)
 *
 * <p>SearchLayersByCursorUseCase 구현체입니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Service
@Transactional(readOnly = true)
public class SearchLayersByCursorService implements SearchLayersByCursorUseCase {

    private final LayerReadManager layerReadManager;
    private final LayerQueryFactory layerQueryFactory;
    private final LayerAssembler layerAssembler;

    public SearchLayersByCursorService(
            LayerReadManager layerReadManager,
            LayerQueryFactory layerQueryFactory,
            LayerAssembler layerAssembler) {
        this.layerReadManager = layerReadManager;
        this.layerQueryFactory = layerQueryFactory;
        this.layerAssembler = layerAssembler;
    }

    @Override
    public LayerSliceResult execute(LayerSearchParams searchParams) {
        LayerSliceCriteria criteria = layerQueryFactory.createSliceCriteria(searchParams);
        List<Layer> layers = layerReadManager.findBySliceCriteria(criteria);
        return layerAssembler.toSliceResult(layers, searchParams.size());
    }
}
