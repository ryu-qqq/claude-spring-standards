package com.ryuqq.application.packagepurpose.service;

import com.ryuqq.application.packagepurpose.assembler.PackagePurposeAssembler;
import com.ryuqq.application.packagepurpose.dto.query.PackagePurposeSearchParams;
import com.ryuqq.application.packagepurpose.dto.response.PackagePurposeSliceResult;
import com.ryuqq.application.packagepurpose.factory.query.PackagePurposeQueryFactory;
import com.ryuqq.application.packagepurpose.manager.PackagePurposeReadManager;
import com.ryuqq.application.packagepurpose.port.in.SearchPackagePurposesByCursorUseCase;
import com.ryuqq.domain.packagepurpose.aggregate.PackagePurpose;
import com.ryuqq.domain.packagepurpose.query.PackagePurposeSliceCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * SearchPackagePurposesByCursorService - PackagePurpose 복합 조건 조회 서비스 (커서 기반)
 *
 * @author ryu-qqq
 */
@Service
public class SearchPackagePurposesByCursorService implements SearchPackagePurposesByCursorUseCase {

    private final PackagePurposeReadManager packagePurposeReadManager;
    private final PackagePurposeQueryFactory packagePurposeQueryFactory;
    private final PackagePurposeAssembler packagePurposeAssembler;

    public SearchPackagePurposesByCursorService(
            PackagePurposeReadManager packagePurposeReadManager,
            PackagePurposeQueryFactory packagePurposeQueryFactory,
            PackagePurposeAssembler packagePurposeAssembler) {
        this.packagePurposeReadManager = packagePurposeReadManager;
        this.packagePurposeQueryFactory = packagePurposeQueryFactory;
        this.packagePurposeAssembler = packagePurposeAssembler;
    }

    @Override
    public PackagePurposeSliceResult execute(PackagePurposeSearchParams searchParams) {
        PackagePurposeSliceCriteria criteria =
                packagePurposeQueryFactory.createSliceCriteria(searchParams);
        List<PackagePurpose> packagePurposes =
                packagePurposeReadManager.findBySliceCriteria(criteria);
        return packagePurposeAssembler.toSliceResult(packagePurposes, searchParams.size());
    }
}
