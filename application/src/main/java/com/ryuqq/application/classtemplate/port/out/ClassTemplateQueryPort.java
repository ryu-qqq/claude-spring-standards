package com.ryuqq.application.classtemplate.port.out;

import com.ryuqq.domain.classtemplate.aggregate.ClassTemplate;
import com.ryuqq.domain.classtemplate.id.ClassTemplateId;
import com.ryuqq.domain.classtemplate.query.ClassTemplateSliceCriteria;
import com.ryuqq.domain.classtemplate.vo.TemplateCode;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import java.util.List;
import java.util.Optional;

/**
 * ClassTemplateQueryPort - 클래스 템플릿 조회 아웃바운드 포트
 *
 * <p>영속성 계층에서 구현합니다.
 *
 * @author ryu-qqq
 */
public interface ClassTemplateQueryPort {

    /**
     * ID로 클래스 템플릿 조회
     *
     * @param id 클래스 템플릿 ID
     * @return 클래스 템플릿 Optional
     */
    Optional<ClassTemplate> findById(Long id);

    /**
     * 패키지 구조 ID로 클래스 템플릿 목록 조회
     *
     * @param structureId 패키지 구조 ID
     * @return 클래스 템플릿 목록
     */
    List<ClassTemplate> findByStructureId(Long structureId);

    /**
     * ClassTemplateId로 클래스 템플릿 조회
     *
     * @param classTemplateId 클래스 템플릿 ID
     * @return 클래스 템플릿 Optional
     */
    Optional<ClassTemplate> findById(ClassTemplateId classTemplateId);

    /**
     * 슬라이스 조건으로 클래스 템플릿 목록 조회
     *
     * @param criteria 슬라이스 조회 조건
     * @return 클래스 템플릿 목록
     */
    List<ClassTemplate> findBySliceCriteria(ClassTemplateSliceCriteria criteria);

    /**
     * 패키지 구조 내 템플릿 코드 존재 여부 확인
     *
     * @param structureId 패키지 구조 ID
     * @param templateCode 템플릿 코드
     * @return 존재하면 true
     */
    boolean existsByStructureIdAndTemplateCode(
            PackageStructureId structureId, TemplateCode templateCode);

    /**
     * 패키지 구조 내 템플릿 코드 존재 여부 확인 (특정 템플릿 제외)
     *
     * @param structureId 패키지 구조 ID
     * @param templateCode 템플릿 코드
     * @param excludeClassTemplateId 제외할 클래스 템플릿 ID
     * @return 존재하면 true
     */
    boolean existsByStructureIdAndTemplateCodeExcluding(
            PackageStructureId structureId,
            TemplateCode templateCode,
            ClassTemplateId excludeClassTemplateId);

    /**
     * 키워드 검색
     *
     * <p>templateCode, classType, description 필드에서 키워드를 검색합니다.
     *
     * @param keyword 검색 키워드
     * @param structureId 패키지 구조 ID (nullable)
     * @return 검색된 클래스 템플릿 목록
     */
    List<ClassTemplate> searchByKeyword(String keyword, Long structureId);
}
