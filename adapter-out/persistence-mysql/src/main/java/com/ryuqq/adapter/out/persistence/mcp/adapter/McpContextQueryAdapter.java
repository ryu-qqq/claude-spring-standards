package com.ryuqq.adapter.out.persistence.mcp.adapter;

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
import com.ryuqq.adapter.out.persistence.mcp.repository.McpContextQueryDslRepository;
import com.ryuqq.application.mcp.dto.context.ArchUnitTestDto;
import com.ryuqq.application.mcp.dto.context.ChecklistItemDto;
import com.ryuqq.application.mcp.dto.context.ClassTemplateDto;
import com.ryuqq.application.mcp.dto.context.CodingRuleWithDetailsDto;
import com.ryuqq.application.mcp.dto.context.ModuleWithLayerAndConventionDto;
import com.ryuqq.application.mcp.dto.context.PackagePurposeDto;
import com.ryuqq.application.mcp.dto.context.PackageStructureWithPurposesDto;
import com.ryuqq.application.mcp.dto.context.PlanningLayerModuleStructureDto;
import com.ryuqq.application.mcp.dto.context.PlanningTechStackArchitectureDto;
import com.ryuqq.application.mcp.dto.context.RuleExampleDto;
import com.ryuqq.application.mcp.dto.context.TemplateAndTestDto;
import com.ryuqq.application.mcp.dto.context.ValidationChecklistDto;
import com.ryuqq.application.mcp.dto.context.ValidationZeroToleranceDto;
import com.ryuqq.application.mcp.dto.context.ZeroToleranceDto;
import com.ryuqq.application.mcp.port.out.McpContextQueryPort;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * McpContextQueryAdapter - MCP Context 조회 Adapter
 *
 * <p>McpContextQueryPort를 구현하여 QueryDSL 기반 데이터 조회를 제공합니다.
 *
 * <p>N+1 문제 해결을 위해 IN절과 메모리 집계를 활용합니다.
 *
 * <p>ADP-001: Adapter 명명 규칙 준수.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
@SuppressWarnings("PMD.ExcessiveImports") // MCP Context 조회를 위해 다수 DTO import 필요
public class McpContextQueryAdapter implements McpContextQueryPort {

    private final McpContextQueryDslRepository repository;

    public McpContextQueryAdapter(McpContextQueryDslRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<ModuleWithLayerAndConventionDto> findModuleWithLayerAndConvention(
            Long moduleId) {
        return repository
                .findModuleWithLayerAndConvention(moduleId)
                .map(this::toModuleWithLayerAndConventionDto);
    }

    @Override
    public List<CodingRuleWithDetailsDto> findCodingRulesWithDetails(Long conventionId) {
        // 1. CodingRule 기본 정보 조회
        List<CodingRuleRow> rules = repository.findCodingRulesByConventionId(conventionId);
        if (rules.isEmpty()) {
            return List.of();
        }

        List<Long> ruleIds = rules.stream().map(CodingRuleRow::ruleId).toList();

        // 2. 관련 데이터 일괄 조회 (IN절 사용)
        List<RuleExampleRow> examples = repository.findRuleExamplesByRuleIds(ruleIds);
        List<ZeroToleranceRow> zeroTolerances = repository.findZeroTolerancesByRuleIds(ruleIds);
        List<ChecklistItemRow> checklistItems = repository.findChecklistItemsByRuleIds(ruleIds);

        // 3. ruleId 기준 그룹핑
        Map<Long, List<RuleExampleRow>> examplesByRuleId =
                examples.stream().collect(Collectors.groupingBy(RuleExampleRow::ruleId));

        Map<Long, ZeroToleranceRow> zeroToleranceByRuleId =
                zeroTolerances.stream()
                        .collect(
                                Collectors.toMap(
                                        ZeroToleranceRow::ruleId, row -> row, (a, b) -> a));

        Map<Long, ChecklistItemRow> checklistByRuleId =
                checklistItems.stream()
                        .collect(
                                Collectors.toMap(
                                        ChecklistItemRow::ruleId, row -> row, (a, b) -> a));

        // 4. 결과 조립
        return rules.stream()
                .map(
                        rule -> {
                            List<RuleExampleDto> exampleDtos =
                                    examplesByRuleId.getOrDefault(rule.ruleId(), List.of()).stream()
                                            .map(
                                                    ex ->
                                                            new RuleExampleDto(
                                                                    ex.exampleType(),
                                                                    ex.code(),
                                                                    ex.explanation()))
                                            .toList();

                            ZeroToleranceRow zt = zeroToleranceByRuleId.get(rule.ruleId());
                            ZeroToleranceDto zeroToleranceDto =
                                    zt != null
                                            ? new ZeroToleranceDto(
                                                    zt.detectionPattern(),
                                                    zt.detectionType(),
                                                    zt.autoRejectPr())
                                            : null;

                            ChecklistItemRow cl = checklistByRuleId.get(rule.ruleId());
                            ChecklistItemDto checklistItemDto =
                                    cl != null
                                            ? new ChecklistItemDto(
                                                    cl.checkDescription(),
                                                    cl.automationTool() != null)
                                            : null;

                            return new CodingRuleWithDetailsDto(
                                    rule.ruleId(),
                                    rule.ruleCode(),
                                    rule.ruleName(),
                                    rule.ruleDescription(),
                                    rule.severity(),
                                    rule.appliesTo(),
                                    exampleDtos,
                                    zeroToleranceDto,
                                    checklistItemDto);
                        })
                .toList();
    }

    @Override
    public List<PackageStructureWithPurposesDto> findPackageStructuresWithPurposes(Long moduleId) {
        // 1. PackageStructure 기본 정보 조회
        List<PackageStructureRow> structures = repository.findPackageStructuresByModuleId(moduleId);
        if (structures.isEmpty()) {
            return List.of();
        }

        List<Long> structureIds =
                structures.stream().map(PackageStructureRow::structureId).toList();

        // 2. PackagePurpose 일괄 조회
        List<PackagePurposeRow> purposes =
                repository.findPackagePurposesByStructureIds(structureIds);

        // 3. structureId 기준 그룹핑
        Map<Long, List<PackagePurposeRow>> purposesByStructureId =
                purposes.stream().collect(Collectors.groupingBy(PackagePurposeRow::structureId));

        // 4. 결과 조립
        return structures.stream()
                .map(
                        structure -> {
                            List<PackagePurposeDto> purposeDtos =
                                    purposesByStructureId
                                            .getOrDefault(structure.structureId(), List.of())
                                            .stream()
                                            .map(
                                                    p ->
                                                            new PackagePurposeDto(
                                                                    p.code(), p.description()))
                                            .toList();

                            return new PackageStructureWithPurposesDto(
                                    structure.structureId(),
                                    structure.pathPattern(),
                                    structure.description(),
                                    purposeDtos);
                        })
                .toList();
    }

    @Override
    public List<TemplateAndTestDto> findTemplatesAndTests(
            List<Long> structureIds, Long classTypeId) {
        if (structureIds.isEmpty()) {
            return List.of();
        }

        // 1. ClassTemplate 조회 (classTypeId 필터 적용)
        List<ClassTemplateRow> templates =
                repository.findClassTemplatesByStructureIds(structureIds, classTypeId);

        // 2. ArchUnitTest 조회
        List<ArchUnitTestRow> archTests = repository.findArchUnitTestsByStructureIds(structureIds);

        // 3. structureId 기준 그룹핑
        Map<Long, List<ClassTemplateRow>> templatesByStructureId =
                templates.stream().collect(Collectors.groupingBy(ClassTemplateRow::structureId));

        Map<Long, List<ArchUnitTestRow>> archTestsByStructureId =
                archTests.stream().collect(Collectors.groupingBy(ArchUnitTestRow::structureId));

        // 4. 결과 조립
        List<TemplateAndTestDto> results = new ArrayList<>();
        for (Long structureId : structureIds) {
            List<ClassTemplateDto> templateDtos =
                    templatesByStructureId.getOrDefault(structureId, List.of()).stream()
                            .map(
                                    t ->
                                            new ClassTemplateDto(
                                                    t.templateId(),
                                                    t.classTypeId(),
                                                    t.templateCode(),
                                                    t.description()))
                            .toList();

            List<ArchUnitTestDto> archTestDtos =
                    archTestsByStructureId.getOrDefault(structureId, List.of()).stream()
                            .map(
                                    a ->
                                            new ArchUnitTestDto(
                                                    a.testId(),
                                                    a.name(),
                                                    a.description(),
                                                    a.testCode()))
                            .toList();

            results.add(new TemplateAndTestDto(structureId, templateDtos, archTestDtos));
        }

        return results;
    }

    private ModuleWithLayerAndConventionDto toModuleWithLayerAndConventionDto(
            ModuleLayerConventionRow row) {
        return new ModuleWithLayerAndConventionDto(
                row.moduleId(),
                row.moduleName(),
                row.moduleDescription(),
                row.layerId(),
                row.layerCode(),
                row.layerName(),
                row.conventionId(),
                row.conventionVersion(),
                row.conventionDescription());
    }

    // ========== Planning Context 조회 메서드 ==========

    @Override
    public Optional<PlanningTechStackArchitectureDto> findTechStackWithArchitecture(
            Long techStackId) {
        return repository
                .findTechStackWithArchitecture(techStackId)
                .map(this::toPlanningTechStackArchitectureDto);
    }

    @Override
    public List<PlanningLayerModuleStructureDto> findLayerModuleStructures(
            Long architectureId, List<String> layerCodes) {
        List<LayerModuleStructureRow> rows =
                repository.findLayerModuleStructures(architectureId, layerCodes);

        return rows.stream().map(this::toPlanningLayerModuleStructureDto).toList();
    }

    private PlanningTechStackArchitectureDto toPlanningTechStackArchitectureDto(
            TechStackArchitectureRow row) {
        return new PlanningTechStackArchitectureDto(
                row.techStackId(),
                row.techStackName(),
                row.languageType(),
                row.languageVersion(),
                row.frameworkType(),
                row.frameworkVersion(),
                row.architectureId(),
                row.architectureName(),
                row.patternDescription());
    }

    private PlanningLayerModuleStructureDto toPlanningLayerModuleStructureDto(
            LayerModuleStructureRow row) {
        return new PlanningLayerModuleStructureDto(
                row.layerCode(),
                row.layerName(),
                row.layerDescription(),
                row.moduleId(),
                row.moduleName(),
                row.moduleDescription(),
                row.structureId(),
                row.pathPattern(),
                row.purposeDescription(),
                row.allowedClassTypes(),
                row.templateCount(),
                row.ruleCount());
    }

    // ========== Validation Context 조회 메서드 ==========

    @Override
    public List<ValidationZeroToleranceDto> findZeroToleranceRulesForValidation(
            Long architectureId, List<String> layerCodes, List<String> classTypes) {
        List<ValidationZeroToleranceRow> rows =
                repository.findZeroToleranceRulesForValidation(
                        architectureId, layerCodes, classTypes);

        return rows.stream().map(this::toValidationZeroToleranceDto).toList();
    }

    @Override
    public List<ValidationChecklistDto> findChecklistItemsForValidation(
            Long architectureId, List<String> layerCodes, List<String> classTypes) {
        List<ValidationChecklistRow> rows =
                repository.findChecklistItemsForValidation(architectureId, layerCodes, classTypes);

        return rows.stream().map(this::toValidationChecklistDto).toList();
    }

    private ValidationZeroToleranceDto toValidationZeroToleranceDto(
            ValidationZeroToleranceRow row) {
        List<String> appliesTo =
                row.appliesTo() != null && !row.appliesTo().isBlank()
                        ? List.of(row.appliesTo().split(","))
                        : List.of();

        return new ValidationZeroToleranceDto(
                row.layerCode(),
                row.ruleCode(),
                row.ruleName(),
                appliesTo,
                row.severity(),
                row.detectionPattern(),
                row.detectionType(),
                row.autoRejectPr());
    }

    private ValidationChecklistDto toValidationChecklistDto(ValidationChecklistRow row) {
        return new ValidationChecklistDto(
                row.layerCode(),
                row.ruleCode(),
                row.checkDescription(),
                row.severity(),
                row.automationTool() != null && !row.automationTool().isBlank());
    }
}
