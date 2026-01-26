package com.ryuqq.adapter.out.persistence.classtemplate.adapter;

import com.ryuqq.adapter.out.persistence.classtemplate.entity.ClassTemplateJpaEntity;
import com.ryuqq.adapter.out.persistence.classtemplate.mapper.ClassTemplateJpaEntityMapper;
import com.ryuqq.adapter.out.persistence.classtemplate.repository.ClassTemplateQueryDslRepository;
import com.ryuqq.application.classtemplate.port.out.ClassTemplateQueryPort;
import com.ryuqq.domain.classtemplate.aggregate.ClassTemplate;
import com.ryuqq.domain.classtemplate.id.ClassTemplateId;
import com.ryuqq.domain.classtemplate.query.ClassTemplateSliceCriteria;
import com.ryuqq.domain.classtemplate.vo.TemplateCode;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * ClassTemplateQueryAdapter - 클래스 템플릿 조회 어댑터
 *
 * <p>ClassTemplateQueryPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>QADP-001: QueryDslRepository 위임만
 *
 * <p>QADP-002: QueryAdapter에서 @Transactional 금지
 *
 * <p>QADP-006: Domain 반환 (DTO 반환 금지)
 *
 * <p>QADP-007: Entity -> Domain 변환 (Mapper 사용)
 *
 * <p>QADP-008: QueryAdapter에 비즈니스 로직 금지
 *
 * @author ryu-qqq
 */
@Component
public class ClassTemplateQueryAdapter implements ClassTemplateQueryPort {

    private final ClassTemplateQueryDslRepository queryDslRepository;
    private final ClassTemplateJpaEntityMapper mapper;

    /**
     * 생성자 주입
     *
     * <p>QADP-005: Mapper + QueryDslRepository 의존
     *
     * @param queryDslRepository QueryDSL 레포지토리
     * @param mapper Entity-Domain 매퍼
     */
    public ClassTemplateQueryAdapter(
            ClassTemplateQueryDslRepository queryDslRepository,
            ClassTemplateJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    /**
     * ID로 클래스 템플릿 조회
     *
     * @param id 클래스 템플릿 ID
     * @return 클래스 템플릿 Optional
     */
    @Override
    public Optional<ClassTemplate> findById(Long id) {
        return queryDslRepository.findById(id).map(mapper::toDomain);
    }

    /**
     * 패키지 구조 ID로 클래스 템플릿 목록 조회
     *
     * @param structureId 패키지 구조 ID
     * @return 클래스 템플릿 목록
     */
    @Override
    public List<ClassTemplate> findByStructureId(Long structureId) {
        List<ClassTemplateJpaEntity> entities = queryDslRepository.findByStructureId(structureId);
        return entities.stream().map(mapper::toDomain).toList();
    }

    /**
     * ClassTemplateId로 클래스 템플릿 조회
     *
     * @param classTemplateId 클래스 템플릿 ID
     * @return 클래스 템플릿 Optional
     */
    @Override
    public Optional<ClassTemplate> findById(ClassTemplateId classTemplateId) {
        return queryDslRepository.findById(classTemplateId.value()).map(mapper::toDomain);
    }

    /**
     * 슬라이스 조건으로 클래스 템플릿 목록 조회
     *
     * @param criteria 슬라이스 조회 조건
     * @return 클래스 템플릿 목록
     */
    @Override
    public List<ClassTemplate> findBySliceCriteria(ClassTemplateSliceCriteria criteria) {
        List<ClassTemplateJpaEntity> entities = queryDslRepository.findBySliceCriteria(criteria);
        return entities.stream().map(mapper::toDomain).toList();
    }

    /**
     * 패키지 구조 내 템플릿 코드 존재 여부 확인
     *
     * @param structureId 패키지 구조 ID
     * @param templateCode 템플릿 코드
     * @return 존재하면 true
     */
    @Override
    public boolean existsByStructureIdAndTemplateCode(
            PackageStructureId structureId, TemplateCode templateCode) {
        return queryDslRepository.existsByStructureIdAndTemplateCode(
                structureId.value(), templateCode.value());
    }

    /**
     * 패키지 구조 내 템플릿 코드 존재 여부 확인 (특정 템플릿 제외)
     *
     * @param structureId 패키지 구조 ID
     * @param templateCode 템플릿 코드
     * @param excludeClassTemplateId 제외할 클래스 템플릿 ID
     * @return 존재하면 true
     */
    @Override
    public boolean existsByStructureIdAndTemplateCodeExcluding(
            PackageStructureId structureId,
            TemplateCode templateCode,
            ClassTemplateId excludeClassTemplateId) {
        return queryDslRepository.existsByStructureIdAndTemplateCodeExcluding(
                structureId.value(), templateCode.value(), excludeClassTemplateId.value());
    }

    /**
     * 키워드 검색
     *
     * @param keyword 검색 키워드
     * @param structureId 패키지 구조 ID (nullable)
     * @return 검색된 클래스 템플릿 목록
     */
    @Override
    public List<ClassTemplate> searchByKeyword(String keyword, Long structureId) {
        List<ClassTemplateJpaEntity> entities =
                queryDslRepository.searchByKeyword(keyword, structureId);
        return entities.stream().map(mapper::toDomain).toList();
    }
}
