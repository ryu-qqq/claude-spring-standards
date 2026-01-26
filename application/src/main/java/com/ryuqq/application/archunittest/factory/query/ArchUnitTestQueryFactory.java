package com.ryuqq.application.archunittest.factory.query;

import com.ryuqq.application.archunittest.dto.query.ArchUnitTestSearchParams;
import com.ryuqq.domain.archunittest.id.ArchUnitTestId;
import com.ryuqq.domain.archunittest.query.ArchUnitTestSliceCriteria;
import com.ryuqq.domain.archunittest.vo.ArchUnitTestSearchField;
import com.ryuqq.domain.archunittest.vo.ArchUnitTestSeverity;
import com.ryuqq.domain.common.vo.CursorPageRequest;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * ArchUnitTestQueryFactory - ArchUnit 테스트 쿼리 팩토리
 *
 * <p>조회에 필요한 도메인 객체를 생성합니다.
 *
 * @author ryu-qqq
 */
@Component
public class ArchUnitTestQueryFactory {

    /**
     * ArchUnitTestSearchParams로부터 ArchUnitTestSliceCriteria 생성
     *
     * @param searchParams 조회 SearchParams
     * @return ArchUnitTestSliceCriteria
     */
    public ArchUnitTestSliceCriteria createSliceCriteria(ArchUnitTestSearchParams searchParams) {
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

        ArchUnitTestSearchField searchField = null;
        String searchWord = null;
        if (searchParams.hasSearch()) {
            searchField = ArchUnitTestSearchField.valueOf(searchParams.searchField());
            searchWord = searchParams.searchWord();
        }

        List<ArchUnitTestSeverity> severities = null;
        if (searchParams.hasSeverities()) {
            severities =
                    searchParams.severities().stream().map(ArchUnitTestSeverity::valueOf).toList();
        }

        return ArchUnitTestSliceCriteria.of(
                structureIds, searchField, searchWord, severities, cursorPageRequest);
    }

    /**
     * Long ID를 ArchUnitTestId로 변환
     *
     * @param archUnitTestId ArchUnit 테스트 ID
     * @return ArchUnitTestId
     */
    public ArchUnitTestId toArchUnitTestId(Long archUnitTestId) {
        return ArchUnitTestId.of(archUnitTestId);
    }

    /**
     * Long ID를 PackageStructureId로 변환
     *
     * @param structureId 패키지 구조 ID
     * @return PackageStructureId
     */
    public PackageStructureId toStructureId(Long structureId) {
        return PackageStructureId.of(structureId);
    }
}
