package com.ryuqq.application.classtemplate.manager;

import com.ryuqq.application.classtemplate.port.out.ClassTemplateQueryPort;
import com.ryuqq.domain.classtemplate.aggregate.ClassTemplate;
import com.ryuqq.domain.classtemplate.exception.ClassTemplateNotFoundException;
import com.ryuqq.domain.classtemplate.id.ClassTemplateId;
import com.ryuqq.domain.classtemplate.query.ClassTemplateSliceCriteria;
import com.ryuqq.domain.classtemplate.vo.TemplateCode;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * ClassTemplateReadManager - 클래스 템플릿 조회 관리자
 *
 * <p>클래스 템플릿 조회 트랜잭션을 관리합니다.
 *
 * <p>MNG-001: Manager는 @Transactional을 메서드에 적용.
 *
 * <p>MNG-003: Manager 파라미터는 VO(Value Object)만 사용.
 *
 * @author ryu-qqq
 */
@Component
public class ClassTemplateReadManager {

    private final ClassTemplateQueryPort classTemplateQueryPort;

    public ClassTemplateReadManager(ClassTemplateQueryPort classTemplateQueryPort) {
        this.classTemplateQueryPort = classTemplateQueryPort;
    }

    /**
     * ID로 클래스 템플릿 조회 (존재하지 않으면 예외)
     *
     * @param classTemplateId 클래스 템플릿 ID
     * @return 클래스 템플릿
     * @throws ClassTemplateNotFoundException 클래스 템플릿이 존재하지 않으면
     */
    @Transactional(readOnly = true)
    public ClassTemplate getById(ClassTemplateId classTemplateId) {
        return classTemplateQueryPort
                .findById(classTemplateId)
                .orElseThrow(() -> new ClassTemplateNotFoundException(classTemplateId.value()));
    }

    /**
     * ID로 클래스 템플릿 존재 여부 확인 후 반환
     *
     * @param classTemplateId 클래스 템플릿 ID
     * @return 클래스 템플릿 (nullable)
     */
    @Transactional(readOnly = true)
    public ClassTemplate findById(ClassTemplateId classTemplateId) {
        return classTemplateQueryPort.findById(classTemplateId).orElse(null);
    }

    /**
     * 슬라이스 조건으로 클래스 템플릿 목록 조회
     *
     * @param criteria 슬라이스 조회 조건
     * @return 클래스 템플릿 목록
     */
    @Transactional(readOnly = true)
    public List<ClassTemplate> findBySliceCriteria(ClassTemplateSliceCriteria criteria) {
        return classTemplateQueryPort.findBySliceCriteria(criteria);
    }

    /**
     * 패키지 구조 내 템플릿 코드 존재 여부 확인
     *
     * @param structureId 패키지 구조 ID
     * @param templateCode 템플릿 코드
     * @return 존재하면 true
     */
    @Transactional(readOnly = true)
    public boolean existsByStructureIdAndTemplateCode(
            PackageStructureId structureId, TemplateCode templateCode) {
        return classTemplateQueryPort.existsByStructureIdAndTemplateCode(structureId, templateCode);
    }

    /**
     * 패키지 구조 내 템플릿 코드 존재 여부 확인 (특정 템플릿 제외)
     *
     * @param structureId 패키지 구조 ID
     * @param templateCode 템플릿 코드
     * @param excludeClassTemplateId 제외할 클래스 템플릿 ID
     * @return 존재하면 true
     */
    @Transactional(readOnly = true)
    public boolean existsByStructureIdAndTemplateCodeExcluding(
            PackageStructureId structureId,
            TemplateCode templateCode,
            ClassTemplateId excludeClassTemplateId) {
        return classTemplateQueryPort.existsByStructureIdAndTemplateCodeExcluding(
                structureId, templateCode, excludeClassTemplateId);
    }

    /**
     * 패키지 구조 ID로 클래스 템플릿 목록 조회
     *
     * @param structureId 패키지 구조 ID
     * @return 클래스 템플릿 목록
     */
    @Transactional(readOnly = true)
    public List<ClassTemplate> findByStructureId(Long structureId) {
        return classTemplateQueryPort.findByStructureId(structureId);
    }

    /**
     * 키워드로 클래스 템플릿 검색
     *
     * <p>templateCode, classType, description 필드에서 키워드를 검색합니다.
     *
     * @param keyword 검색 키워드
     * @param structureId 패키지 구조 ID (nullable)
     * @return 검색된 클래스 템플릿 목록
     */
    @Transactional(readOnly = true)
    public List<ClassTemplate> searchByKeyword(String keyword, Long structureId) {
        return classTemplateQueryPort.searchByKeyword(keyword, structureId);
    }
}
