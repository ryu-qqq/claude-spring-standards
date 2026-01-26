package com.ryuqq.domain.classtemplate.query;

import com.ryuqq.domain.classtype.id.ClassTypeId;
import com.ryuqq.domain.common.vo.CursorPageRequest;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import java.util.List;

/**
 * ClassTemplateSliceCriteria - ClassTemplate 슬라이스 조회 조건 (커서 기반)
 *
 * <p>ClassTemplate 목록을 커서 기반으로 조회할 때 사용하는 조건입니다.
 *
 * <p>패키지 구조 ID 및 클래스 타입 ID 필터링을 지원합니다.
 *
 * <p><strong>커서 전략:</strong>
 *
 * <ul>
 *   <li>ID 기반: 마지막 항목의 ClassTemplate ID를 커서로 사용
 *   <li>정렬: ID 내림차순 (DESC)으로 조회
 * </ul>
 *
 * @param structureIds 패키지 구조 ID 필터 목록 (필터링 용도, nullable)
 * @param classTypeIds 클래스 타입 ID 필터 목록 (필터링 용도, nullable)
 * @param cursorPageRequest 커서 기반 페이징 요청 (ID 기반: Long)
 * @author ryu-qqq
 */
public record ClassTemplateSliceCriteria(
        List<PackageStructureId> structureIds,
        List<ClassTypeId> classTypeIds,
        CursorPageRequest<Long> cursorPageRequest) {

    public ClassTemplateSliceCriteria {
        if (cursorPageRequest == null) {
            throw new IllegalArgumentException("cursorPageRequest must not be null");
        }
    }

    /**
     * 기본 슬라이스 조건 생성 (첫 페이지, 필터 없음)
     *
     * @param size 슬라이스 크기
     * @return ClassTemplateSliceCriteria
     */
    public static ClassTemplateSliceCriteria first(int size) {
        return new ClassTemplateSliceCriteria(null, null, CursorPageRequest.first(size));
    }

    /**
     * ID 기반 커서로 슬라이스 조건 생성 (필터 없음)
     *
     * @param cursorId 커서 ID (마지막 항목의 ID)
     * @param size 슬라이스 크기
     * @return ClassTemplateSliceCriteria
     */
    public static ClassTemplateSliceCriteria afterId(Long cursorId, int size) {
        return new ClassTemplateSliceCriteria(
                null, null, CursorPageRequest.afterId(cursorId, size));
    }

    /**
     * ClassTemplateSliceCriteria 생성 (static factory method)
     *
     * @param cursorPageRequest 커서 기반 페이징 요청
     * @return ClassTemplateSliceCriteria 인스턴스
     */
    public static ClassTemplateSliceCriteria of(CursorPageRequest<Long> cursorPageRequest) {
        return new ClassTemplateSliceCriteria(null, null, cursorPageRequest);
    }

    /**
     * ClassTemplateSliceCriteria 생성 (필터 포함)
     *
     * @param cursorPageRequest 커서 기반 페이징 요청
     * @param structureIds 패키지 구조 ID 필터 목록
     * @param classTypeIds 클래스 타입 ID 필터 목록
     * @return ClassTemplateSliceCriteria 인스턴스
     */
    public static ClassTemplateSliceCriteria of(
            CursorPageRequest<Long> cursorPageRequest,
            List<PackageStructureId> structureIds,
            List<ClassTypeId> classTypeIds) {
        return new ClassTemplateSliceCriteria(structureIds, classTypeIds, cursorPageRequest);
    }

    /**
     * 패키지 구조 ID 필터가 있는지 확인
     *
     * @return structureIds가 null이 아니고 비어있지 않으면 true
     */
    public boolean hasStructureIds() {
        return structureIds != null && !structureIds.isEmpty();
    }

    /**
     * 클래스 타입 ID 필터가 있는지 확인
     *
     * @return classTypeIds가 null이 아니고 비어있지 않으면 true
     */
    public boolean hasClassTypeIds() {
        return classTypeIds != null && !classTypeIds.isEmpty();
    }

    /**
     * 첫 페이지 요청인지 확인
     *
     * @return cursor가 null이면 true
     */
    public boolean isFirstPage() {
        return cursorPageRequest.cursor() == null;
    }

    /**
     * 커서가 있는지 확인
     *
     * @return 커서가 있으면 true
     */
    public boolean hasCursor() {
        return cursorPageRequest.cursor() != null;
    }

    /**
     * 슬라이스 크기 반환 (편의 메서드)
     *
     * @return size
     */
    public int size() {
        return cursorPageRequest.size();
    }

    /**
     * 실제 조회 크기 반환 (hasNext 판단용 +1)
     *
     * @return size + 1
     */
    public int fetchSize() {
        return cursorPageRequest.fetchSize();
    }
}
