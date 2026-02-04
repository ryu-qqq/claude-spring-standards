package com.ryuqq.adapter.out.persistence.mcp.repository;

import static com.ryuqq.adapter.out.persistence.architecture.entity.QArchitectureJpaEntity.architectureJpaEntity;
import static com.ryuqq.adapter.out.persistence.archunittest.entity.QArchUnitTestJpaEntity.archUnitTestJpaEntity;
import static com.ryuqq.adapter.out.persistence.checklistitem.entity.QChecklistItemJpaEntity.checklistItemJpaEntity;
import static com.ryuqq.adapter.out.persistence.classtemplate.entity.QClassTemplateJpaEntity.classTemplateJpaEntity;
import static com.ryuqq.adapter.out.persistence.classtype.entity.QClassTypeJpaEntity.classTypeJpaEntity;
import static com.ryuqq.adapter.out.persistence.codingrule.entity.QCodingRuleJpaEntity.codingRuleJpaEntity;
import static com.ryuqq.adapter.out.persistence.convention.entity.QConventionJpaEntity.conventionJpaEntity;
import static com.ryuqq.adapter.out.persistence.layer.entity.QLayerJpaEntity.layerJpaEntity;
import static com.ryuqq.adapter.out.persistence.module.entity.QModuleJpaEntity.moduleJpaEntity;
import static com.ryuqq.adapter.out.persistence.packagepurpose.entity.QPackagePurposeJpaEntity.packagePurposeJpaEntity;
import static com.ryuqq.adapter.out.persistence.packagestructure.entity.QPackageStructureJpaEntity.packageStructureJpaEntity;
import static com.ryuqq.adapter.out.persistence.ruleexample.entity.QRuleExampleJpaEntity.ruleExampleJpaEntity;
import static com.ryuqq.adapter.out.persistence.techstack.entity.QTechStackJpaEntity.techStackJpaEntity;
import static com.ryuqq.adapter.out.persistence.zerotolerance.entity.QZeroToleranceRuleJpaEntity.zeroToleranceRuleJpaEntity;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.adapter.out.persistence.mcp.dto.ArchUnitTestRow;
import com.ryuqq.adapter.out.persistence.mcp.dto.ChecklistItemRow;
import com.ryuqq.adapter.out.persistence.mcp.dto.ClassTemplateRow;
import com.ryuqq.adapter.out.persistence.mcp.dto.CodingRuleRow;
import com.ryuqq.adapter.out.persistence.mcp.dto.LayerModuleStructureRow;
import com.ryuqq.adapter.out.persistence.mcp.dto.ModuleLayerConventionRow;
import com.ryuqq.adapter.out.persistence.mcp.dto.PackagePurposeRow;
import com.ryuqq.adapter.out.persistence.mcp.dto.PackageStructureRow;
import com.ryuqq.adapter.out.persistence.mcp.dto.RuleExampleRow;
import com.ryuqq.adapter.out.persistence.mcp.dto.TechStackArchitectureRow;
import com.ryuqq.adapter.out.persistence.mcp.dto.ValidationChecklistRow;
import com.ryuqq.adapter.out.persistence.mcp.dto.ValidationZeroToleranceRow;
import com.ryuqq.adapter.out.persistence.mcp.dto.ZeroToleranceRow;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * McpContextQueryDslRepository - MCP Context 조회용 QueryDSL Repository
 *
 * <p>MCP 서비스에서 사용하는 복합 데이터를 JOIN 쿼리로 조회합니다.
 *
 * <p>예외적으로 JOIN을 허용하여 N+1 문제를 해결합니다.
 *
 * <p>REP-002: QueryDSL Repository 명명 규칙 준수.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Repository
@SuppressWarnings("PMD.ExcessiveImports") // MCP Context 복합 쿼리를 위해 다수 Entity import 필요
public class McpContextQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public McpContextQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * Query 1: Module + Layer + Convention (Active) 조회
     *
     * <p>Module을 기준으로 Layer와 활성 Convention을 LEFT JOIN하여 조회합니다.
     *
     * @param moduleId 모듈 ID
     * @return Module + Layer + Convention 정보
     */
    public Optional<ModuleLayerConventionRow> findModuleWithLayerAndConvention(Long moduleId) {
        ModuleLayerConventionRow result =
                queryFactory
                        .select(
                                Projections.constructor(
                                        ModuleLayerConventionRow.class,
                                        moduleJpaEntity.id,
                                        moduleJpaEntity.name,
                                        moduleJpaEntity.description,
                                        layerJpaEntity.id,
                                        layerJpaEntity.code,
                                        layerJpaEntity.name,
                                        conventionJpaEntity.id,
                                        conventionJpaEntity.version,
                                        conventionJpaEntity.description))
                        .from(moduleJpaEntity)
                        .join(layerJpaEntity)
                        .on(moduleJpaEntity.layerId.eq(layerJpaEntity.id))
                        .leftJoin(conventionJpaEntity)
                        .on(
                                conventionJpaEntity
                                        .moduleId
                                        .eq(moduleJpaEntity.id)
                                        .and(conventionJpaEntity.isActive.isTrue())
                                        .and(conventionJpaEntity.deletedAt.isNull()))
                        .where(
                                moduleJpaEntity.id.eq(moduleId),
                                moduleJpaEntity.deletedAt.isNull(),
                                layerJpaEntity.deletedAt.isNull())
                        .fetchOne();

        return Optional.ofNullable(result);
    }

    /**
     * Query 2-1: CodingRule 기본 정보 조회 (classTypeCode 필터 적용)
     *
     * @param conventionId 컨벤션 ID
     * @param classTypeCode 클래스 타입 코드 (appliesTo 필터링, nullable이면 전체 조회)
     * @return CodingRule 목록
     */
    public List<CodingRuleRow> findCodingRulesByConventionId(
            Long conventionId, String classTypeCode) {
        var query =
                queryFactory
                        .select(
                                Projections.constructor(
                                        CodingRuleRow.class,
                                        codingRuleJpaEntity.id,
                                        codingRuleJpaEntity.code,
                                        codingRuleJpaEntity.name,
                                        codingRuleJpaEntity.description,
                                        codingRuleJpaEntity.severity.stringValue(),
                                        codingRuleJpaEntity.appliesTo))
                        .from(codingRuleJpaEntity)
                        .where(
                                codingRuleJpaEntity.conventionId.eq(conventionId),
                                codingRuleJpaEntity.deletedAt.isNull());

        if (classTypeCode != null && !classTypeCode.isEmpty()) {
            query.where(codingRuleJpaEntity.appliesTo.contains(classTypeCode));
        }

        return query.fetch();
    }

    /**
     * classTypeId → code 변환 조회
     *
     * @param classTypeId 클래스 타입 ID
     * @return 클래스 타입 코드 (없으면 null)
     */
    public String findClassTypeCodeById(Long classTypeId) {
        if (classTypeId == null) {
            return null;
        }
        return queryFactory
                .select(classTypeJpaEntity.code)
                .from(classTypeJpaEntity)
                .where(classTypeJpaEntity.id.eq(classTypeId), classTypeJpaEntity.deletedAt.isNull())
                .fetchOne();
    }

    /**
     * Query 2-2: RuleExample 조회 (IN절 사용)
     *
     * @param ruleIds 규칙 ID 목록
     * @return RuleExample 목록
     */
    public List<RuleExampleRow> findRuleExamplesByRuleIds(List<Long> ruleIds) {
        if (ruleIds.isEmpty()) {
            return List.of();
        }

        return queryFactory
                .select(
                        Projections.constructor(
                                RuleExampleRow.class,
                                ruleExampleJpaEntity.ruleId,
                                ruleExampleJpaEntity.exampleType,
                                ruleExampleJpaEntity.code,
                                ruleExampleJpaEntity.explanation))
                .from(ruleExampleJpaEntity)
                .where(
                        ruleExampleJpaEntity.ruleId.in(ruleIds),
                        ruleExampleJpaEntity.deletedAt.isNull())
                .fetch();
    }

    /**
     * Query 2-3: ZeroToleranceRule 조회 (IN절 사용)
     *
     * @param ruleIds 규칙 ID 목록
     * @return ZeroToleranceRule 목록
     */
    public List<ZeroToleranceRow> findZeroTolerancesByRuleIds(List<Long> ruleIds) {
        if (ruleIds.isEmpty()) {
            return List.of();
        }

        return queryFactory
                .select(
                        Projections.constructor(
                                ZeroToleranceRow.class,
                                zeroToleranceRuleJpaEntity.ruleId,
                                zeroToleranceRuleJpaEntity.detectionPattern,
                                zeroToleranceRuleJpaEntity.detectionType,
                                zeroToleranceRuleJpaEntity.autoRejectPr))
                .from(zeroToleranceRuleJpaEntity)
                .where(
                        zeroToleranceRuleJpaEntity.ruleId.in(ruleIds),
                        zeroToleranceRuleJpaEntity.deletedAt.isNull())
                .fetch();
    }

    /**
     * Query 2-4: ChecklistItem 조회 (IN절 사용)
     *
     * @param ruleIds 규칙 ID 목록
     * @return ChecklistItem 목록
     */
    public List<ChecklistItemRow> findChecklistItemsByRuleIds(List<Long> ruleIds) {
        if (ruleIds.isEmpty()) {
            return List.of();
        }

        return queryFactory
                .select(
                        Projections.constructor(
                                ChecklistItemRow.class,
                                checklistItemJpaEntity.ruleId,
                                checklistItemJpaEntity.checkDescription,
                                checklistItemJpaEntity.automationTool))
                .from(checklistItemJpaEntity)
                .where(
                        checklistItemJpaEntity.ruleId.in(ruleIds),
                        checklistItemJpaEntity.deletedAt.isNull())
                .fetch();
    }

    /**
     * Query 3-1: PackageStructure 기본 정보 조회
     *
     * @param moduleId 모듈 ID
     * @return PackageStructure 목록
     */
    public List<PackageStructureRow> findPackageStructuresByModuleId(Long moduleId) {
        return queryFactory
                .select(
                        Projections.constructor(
                                PackageStructureRow.class,
                                packageStructureJpaEntity.id,
                                packageStructureJpaEntity.pathPattern,
                                packageStructureJpaEntity.description))
                .from(packageStructureJpaEntity)
                .where(
                        packageStructureJpaEntity.moduleId.eq(moduleId),
                        packageStructureJpaEntity.deletedAt.isNull())
                .fetch();
    }

    /**
     * Query 3-2: PackagePurpose 조회 (IN절 사용)
     *
     * @param structureIds 패키지 구조 ID 목록
     * @return PackagePurpose 목록
     */
    public List<PackagePurposeRow> findPackagePurposesByStructureIds(List<Long> structureIds) {
        if (structureIds.isEmpty()) {
            return List.of();
        }

        return queryFactory
                .select(
                        Projections.constructor(
                                PackagePurposeRow.class,
                                packagePurposeJpaEntity.structureId,
                                packagePurposeJpaEntity.code,
                                packagePurposeJpaEntity.description))
                .from(packagePurposeJpaEntity)
                .where(
                        packagePurposeJpaEntity.structureId.in(structureIds),
                        packagePurposeJpaEntity.deletedAt.isNull())
                .fetch();
    }

    /**
     * Query 4-1: ClassTemplate 조회 (IN절 사용 + classType 필터)
     *
     * @param structureIds 패키지 구조 ID 목록
     * @param classTypeId 클래스 타입 ID 필터 (nullable)
     * @return ClassTemplate 목록
     */
    public List<ClassTemplateRow> findClassTemplatesByStructureIds(
            List<Long> structureIds, Long classTypeId) {
        if (structureIds.isEmpty()) {
            return List.of();
        }

        var query =
                queryFactory
                        .select(
                                Projections.constructor(
                                        ClassTemplateRow.class,
                                        classTemplateJpaEntity.structureId,
                                        classTemplateJpaEntity.id,
                                        classTemplateJpaEntity.classTypeId,
                                        classTemplateJpaEntity.templateCode,
                                        classTemplateJpaEntity.description))
                        .from(classTemplateJpaEntity)
                        .where(
                                classTemplateJpaEntity.structureId.in(structureIds),
                                classTemplateJpaEntity.deletedAt.isNull());

        if (classTypeId != null) {
            query.where(classTemplateJpaEntity.classTypeId.eq(classTypeId));
        }

        return query.fetch();
    }

    /**
     * Query 4-2: ArchUnitTest 조회 (IN절 사용)
     *
     * @param structureIds 패키지 구조 ID 목록
     * @return ArchUnitTest 목록
     */
    public List<ArchUnitTestRow> findArchUnitTestsByStructureIds(List<Long> structureIds) {
        if (structureIds.isEmpty()) {
            return List.of();
        }

        return queryFactory
                .select(
                        Projections.constructor(
                                ArchUnitTestRow.class,
                                archUnitTestJpaEntity.structureId,
                                archUnitTestJpaEntity.id,
                                archUnitTestJpaEntity.name,
                                archUnitTestJpaEntity.description,
                                archUnitTestJpaEntity.testCode))
                .from(archUnitTestJpaEntity)
                .where(
                        archUnitTestJpaEntity.structureId.in(structureIds),
                        archUnitTestJpaEntity.deletedAt.isNull())
                .fetch();
    }

    // ========== Planning Context 조회 메서드 ==========

    /**
     * Query P-1: TechStack + Architecture 조회 (Planning Context용)
     *
     * <p>TechStack과 연결된 Architecture를 JOIN하여 조회합니다.
     *
     * @param techStackId 기술 스택 ID (nullable - null이면 활성 스택 조회)
     * @return TechStack + Architecture 정보
     */
    public Optional<TechStackArchitectureRow> findTechStackWithArchitecture(Long techStackId) {
        var query =
                queryFactory
                        .select(
                                Projections.constructor(
                                        TechStackArchitectureRow.class,
                                        techStackJpaEntity.id,
                                        techStackJpaEntity.name,
                                        techStackJpaEntity.languageType,
                                        techStackJpaEntity.languageVersion,
                                        techStackJpaEntity.frameworkType,
                                        techStackJpaEntity.frameworkVersion,
                                        architectureJpaEntity.id,
                                        architectureJpaEntity.name,
                                        architectureJpaEntity.patternDescription))
                        .from(techStackJpaEntity)
                        .join(architectureJpaEntity)
                        .on(architectureJpaEntity.techStackId.eq(techStackJpaEntity.id))
                        .where(techStackJpaEntity.deletedAt.isNull())
                        .where(architectureJpaEntity.deletedAt.isNull());

        if (techStackId != null) {
            query.where(techStackJpaEntity.id.eq(techStackId));
        } else {
            // 활성 스택 조회 (status = 'ACTIVE')
            query.where(techStackJpaEntity.status.eq("ACTIVE"));
        }

        TechStackArchitectureRow result = query.fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * Query P-2: Layer + Module + PackageStructure + 통계 조회 (Planning Context용)
     *
     * <p>Architecture의 모든 Layer, Module, PackageStructure를 JOIN하여 조회합니다.
     *
     * <p>각 PackageStructure별 템플릿 개수, 규칙 개수를 서브쿼리로 계산합니다.
     *
     * @param architectureId 아키텍처 ID
     * @param layerCodes 레이어 코드 필터 목록 (empty면 전체 조회)
     * @return Flat 구조의 조회 결과 (메모리에서 그룹핑 필요)
     */
    public List<LayerModuleStructureRow> findLayerModuleStructures(
            Long architectureId, List<String> layerCodes) {
        var query =
                queryFactory
                        .select(
                                Projections.constructor(
                                        LayerModuleStructureRow.class,
                                        layerJpaEntity.code,
                                        layerJpaEntity.name,
                                        layerJpaEntity.description,
                                        moduleJpaEntity.id,
                                        moduleJpaEntity.name,
                                        moduleJpaEntity.description,
                                        packageStructureJpaEntity.id,
                                        packageStructureJpaEntity.pathPattern,
                                        packageStructureJpaEntity.description,
                                        // allowedClassTypes: PackagePurpose의 code를 GROUP_CONCAT
                                        Expressions.stringTemplate(
                                                "GROUP_CONCAT(DISTINCT {0})",
                                                packagePurposeJpaEntity.code),
                                        // templateCount 서브쿼리
                                        JPAExpressions.select(
                                                        classTemplateJpaEntity.count().intValue())
                                                .from(classTemplateJpaEntity)
                                                .where(
                                                        classTemplateJpaEntity.structureId.eq(
                                                                packageStructureJpaEntity.id),
                                                        classTemplateJpaEntity.deletedAt.isNull()),
                                        // ruleCount 서브쿼리 (Convention → CodingRule)
                                        JPAExpressions.select(
                                                        codingRuleJpaEntity.count().intValue())
                                                .from(codingRuleJpaEntity)
                                                .join(conventionJpaEntity)
                                                .on(
                                                        codingRuleJpaEntity.conventionId.eq(
                                                                conventionJpaEntity.id))
                                                .where(
                                                        conventionJpaEntity.moduleId.eq(
                                                                moduleJpaEntity.id),
                                                        conventionJpaEntity.isActive.isTrue(),
                                                        conventionJpaEntity.deletedAt.isNull(),
                                                        codingRuleJpaEntity.deletedAt.isNull())))
                        .from(layerJpaEntity)
                        .join(moduleJpaEntity)
                        .on(moduleJpaEntity.layerId.eq(layerJpaEntity.id))
                        .leftJoin(packageStructureJpaEntity)
                        .on(packageStructureJpaEntity.moduleId.eq(moduleJpaEntity.id))
                        .leftJoin(packagePurposeJpaEntity)
                        .on(packagePurposeJpaEntity.structureId.eq(packageStructureJpaEntity.id))
                        .where(
                                layerJpaEntity.architectureId.eq(architectureId),
                                layerJpaEntity.deletedAt.isNull(),
                                moduleJpaEntity.deletedAt.isNull())
                        .groupBy(
                                layerJpaEntity.code,
                                layerJpaEntity.name,
                                layerJpaEntity.description,
                                moduleJpaEntity.id,
                                moduleJpaEntity.name,
                                moduleJpaEntity.description,
                                packageStructureJpaEntity.id,
                                packageStructureJpaEntity.pathPattern,
                                packageStructureJpaEntity.description)
                        .orderBy(layerJpaEntity.id.asc(), moduleJpaEntity.id.asc());

        // 레이어 코드 필터
        if (layerCodes != null && !layerCodes.isEmpty()) {
            query.where(layerJpaEntity.code.in(layerCodes));
        }

        return query.fetch();
    }

    // ========== Validation Context 조회 메서드 ==========

    /**
     * Query V-1: ZeroToleranceRule 조회 (Validation Context용)
     *
     * <p>Layer → Module → Convention(Active) → CodingRule → ZeroToleranceRule JOIN.
     *
     * <p>layerCodes, classTypes 필터를 지원합니다.
     *
     * @param architectureId 아키텍처 ID
     * @param layerCodes 레이어 코드 필터 목록 (empty면 전체 조회)
     * @param classTypes 클래스 타입 필터 목록 (empty면 전체 조회)
     * @return ZeroTolerance + CodingRule + Layer 정보
     */
    public List<ValidationZeroToleranceRow> findZeroToleranceRulesForValidation(
            Long architectureId, List<String> layerCodes, List<String> classTypes) {
        var query =
                queryFactory
                        .select(
                                Projections.constructor(
                                        ValidationZeroToleranceRow.class,
                                        layerJpaEntity.code,
                                        codingRuleJpaEntity.code,
                                        codingRuleJpaEntity.name,
                                        codingRuleJpaEntity.appliesTo,
                                        codingRuleJpaEntity.severity.stringValue(),
                                        zeroToleranceRuleJpaEntity.detectionPattern,
                                        zeroToleranceRuleJpaEntity.detectionType,
                                        zeroToleranceRuleJpaEntity.autoRejectPr))
                        .from(zeroToleranceRuleJpaEntity)
                        .join(codingRuleJpaEntity)
                        .on(zeroToleranceRuleJpaEntity.ruleId.eq(codingRuleJpaEntity.id))
                        .join(conventionJpaEntity)
                        .on(codingRuleJpaEntity.conventionId.eq(conventionJpaEntity.id))
                        .join(moduleJpaEntity)
                        .on(conventionJpaEntity.moduleId.eq(moduleJpaEntity.id))
                        .join(layerJpaEntity)
                        .on(moduleJpaEntity.layerId.eq(layerJpaEntity.id))
                        .where(
                                layerJpaEntity.architectureId.eq(architectureId),
                                conventionJpaEntity.isActive.isTrue(),
                                zeroToleranceRuleJpaEntity.deletedAt.isNull(),
                                codingRuleJpaEntity.deletedAt.isNull(),
                                conventionJpaEntity.deletedAt.isNull(),
                                moduleJpaEntity.deletedAt.isNull(),
                                layerJpaEntity.deletedAt.isNull());

        // 레이어 코드 필터
        if (layerCodes != null && !layerCodes.isEmpty()) {
            query.where(layerJpaEntity.code.in(layerCodes));
        }

        // 클래스 타입 필터 (appliesTo에 포함된 경우)
        if (classTypes != null && !classTypes.isEmpty()) {
            var classTypeCondition =
                    classTypes.stream()
                            .map(ct -> codingRuleJpaEntity.appliesTo.contains(ct))
                            .reduce((a, b) -> a.or(b))
                            .orElse(null);
            if (classTypeCondition != null) {
                query.where(classTypeCondition);
            }
        }

        return query.orderBy(layerJpaEntity.id.asc(), codingRuleJpaEntity.id.asc()).fetch();
    }

    /**
     * Query V-2: ChecklistItem 조회 (Validation Context용)
     *
     * <p>Layer → Module → Convention(Active) → CodingRule → ChecklistItem JOIN.
     *
     * <p>layerCodes, classTypes 필터를 지원합니다.
     *
     * @param architectureId 아키텍처 ID
     * @param layerCodes 레이어 코드 필터 목록 (empty면 전체 조회)
     * @param classTypes 클래스 타입 필터 목록 (empty면 전체 조회)
     * @return ChecklistItem + CodingRule + Layer 정보
     */
    public List<ValidationChecklistRow> findChecklistItemsForValidation(
            Long architectureId, List<String> layerCodes, List<String> classTypes) {
        var query =
                queryFactory
                        .select(
                                Projections.constructor(
                                        ValidationChecklistRow.class,
                                        layerJpaEntity.code,
                                        codingRuleJpaEntity.code,
                                        checklistItemJpaEntity.checkDescription,
                                        codingRuleJpaEntity.severity.stringValue(),
                                        checklistItemJpaEntity.automationTool))
                        .from(checklistItemJpaEntity)
                        .join(codingRuleJpaEntity)
                        .on(checklistItemJpaEntity.ruleId.eq(codingRuleJpaEntity.id))
                        .join(conventionJpaEntity)
                        .on(codingRuleJpaEntity.conventionId.eq(conventionJpaEntity.id))
                        .join(moduleJpaEntity)
                        .on(conventionJpaEntity.moduleId.eq(moduleJpaEntity.id))
                        .join(layerJpaEntity)
                        .on(moduleJpaEntity.layerId.eq(layerJpaEntity.id))
                        .where(
                                layerJpaEntity.architectureId.eq(architectureId),
                                conventionJpaEntity.isActive.isTrue(),
                                checklistItemJpaEntity.deletedAt.isNull(),
                                codingRuleJpaEntity.deletedAt.isNull(),
                                conventionJpaEntity.deletedAt.isNull(),
                                moduleJpaEntity.deletedAt.isNull(),
                                layerJpaEntity.deletedAt.isNull());

        // 레이어 코드 필터
        if (layerCodes != null && !layerCodes.isEmpty()) {
            query.where(layerJpaEntity.code.in(layerCodes));
        }

        // 클래스 타입 필터 (appliesTo에 포함된 경우)
        if (classTypes != null && !classTypes.isEmpty()) {
            var classTypeCondition =
                    classTypes.stream()
                            .map(ct -> codingRuleJpaEntity.appliesTo.contains(ct))
                            .reduce((a, b) -> a.or(b))
                            .orElse(null);
            if (classTypeCondition != null) {
                query.where(classTypeCondition);
            }
        }

        return query.orderBy(layerJpaEntity.id.asc(), codingRuleJpaEntity.id.asc()).fetch();
    }
}
