package com.ryuqq.application.packagepurpose.factory.query;

import com.ryuqq.application.packagepurpose.dto.query.PackagePurposeSearchParams;
import com.ryuqq.domain.common.vo.CursorPageRequest;
import com.ryuqq.domain.packagepurpose.query.PackagePurposeSliceCriteria;
import com.ryuqq.domain.packagepurpose.vo.PackagePurposeSearchField;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * PackagePurposeQueryFactory - 패키지 목적 쿼리 팩토리
 *
 * <p>조회 관련 도메인 객체 변환을 담당합니다.
 *
 * @author ryu-qqq
 */
@Component
public class PackagePurposeQueryFactory {

    /**
     * PackagePurposeSearchParams로부터 PackagePurposeSliceCriteria 생성
     *
     * @param searchParams 검색 파라미터
     * @return PackagePurposeSliceCriteria
     */
    public PackagePurposeSliceCriteria createSliceCriteria(
            PackagePurposeSearchParams searchParams) {
        CursorPageRequest<Long> cursorPageRequest;
        if (searchParams.isFirstPage()) {
            cursorPageRequest = CursorPageRequest.first(searchParams.size());
        } else {
            Long cursorId = Long.parseLong(searchParams.cursor());
            cursorPageRequest = CursorPageRequest.afterId(cursorId, searchParams.size());
        }

        List<PackageStructureId> structureIds = null;
        if (searchParams.hasStructureIds()) {
            structureIds =
                    searchParams.structureIds().stream()
                            .map(PackageStructureId::of)
                            .collect(Collectors.toList());
        }

        PackagePurposeSearchField searchField = null;
        String searchWord = null;
        if (searchParams.hasSearch()) {
            searchField = PackagePurposeSearchField.valueOf(searchParams.searchField());
            searchWord = searchParams.searchWord();
        }

        return PackagePurposeSliceCriteria.of(
                cursorPageRequest, structureIds, searchField, searchWord);
    }
}
