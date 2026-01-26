package com.ryuqq.application.packagestructure.factory.query;

import com.ryuqq.application.packagestructure.dto.query.PackageStructureSearchParams;
import com.ryuqq.domain.common.vo.CursorPageRequest;
import com.ryuqq.domain.module.id.ModuleId;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import com.ryuqq.domain.packagestructure.query.PackageStructureSliceCriteria;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * PackageStructureQueryFactory - 패키지 구조 쿼리 팩토리
 *
 * <p>조회에 필요한 도메인 객체를 생성합니다.
 *
 * @author ryu-qqq
 */
@Component
public class PackageStructureQueryFactory {

    /**
     * PackageStructureSearchParams로부터 PackageStructureSliceCriteria 생성
     *
     * <p>FAC-001: Criteria 생성도 Factory에서 담당합니다.
     *
     * @param searchParams 검색 파라미터
     * @return PackageStructureSliceCriteria
     */
    public PackageStructureSliceCriteria createSliceCriteria(
            PackageStructureSearchParams searchParams) {
        CursorPageRequest<Long> cursorPageRequest;

        if (searchParams.isFirstPage()) {
            cursorPageRequest = CursorPageRequest.first(searchParams.size());
        } else {
            Long cursorId = Long.parseLong(searchParams.cursor());
            cursorPageRequest = CursorPageRequest.afterId(cursorId, searchParams.size());
        }

        List<ModuleId> moduleIds = null;
        if (searchParams.hasModuleIds()) {
            moduleIds =
                    searchParams.moduleIds().stream()
                            .map(ModuleId::of)
                            .collect(Collectors.toList());
        }

        return PackageStructureSliceCriteria.of(cursorPageRequest, moduleIds);
    }

    /**
     * Long ID를 PackageStructureId로 변환
     *
     * @param packageStructureId 패키지 구조 ID
     * @return PackageStructureId
     */
    public PackageStructureId toPackageStructureId(Long packageStructureId) {
        return PackageStructureId.of(packageStructureId);
    }

    /**
     * Long ID를 ModuleId로 변환
     *
     * @param moduleId 모듈 ID
     * @return ModuleId
     */
    public ModuleId toModuleId(Long moduleId) {
        return ModuleId.of(moduleId);
    }
}
