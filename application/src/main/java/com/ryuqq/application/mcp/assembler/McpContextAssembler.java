package com.ryuqq.application.mcp.assembler;

import com.ryuqq.application.mcp.dto.context.CodingRuleWithDetailsDto;
import com.ryuqq.application.mcp.dto.context.ModuleWithLayerAndConventionDto;
import com.ryuqq.application.mcp.dto.context.PackageStructureWithPurposesDto;
import com.ryuqq.application.mcp.dto.context.PlanningLayerModuleStructureDto;
import com.ryuqq.application.mcp.dto.context.PlanningTechStackArchitectureDto;
import com.ryuqq.application.mcp.dto.context.TemplateAndTestDto;
import com.ryuqq.application.mcp.dto.response.ArchUnitTestDetailResult;
import com.ryuqq.application.mcp.dto.response.ArchitectureSummaryResult;
import com.ryuqq.application.mcp.dto.response.ChecklistItemDetailResult;
import com.ryuqq.application.mcp.dto.response.ClassTemplateDetailResult;
import com.ryuqq.application.mcp.dto.response.CodingRuleWithDetailsResult;
import com.ryuqq.application.mcp.dto.response.ConventionWithRulesResult;
import com.ryuqq.application.mcp.dto.response.ExecutionContextResult;
import com.ryuqq.application.mcp.dto.response.LayerSummaryResult;
import com.ryuqq.application.mcp.dto.response.LayerWithModulesResult;
import com.ryuqq.application.mcp.dto.response.ModuleContextResult;
import com.ryuqq.application.mcp.dto.response.ModuleContextSummaryResult;
import com.ryuqq.application.mcp.dto.response.ModuleSummaryResult;
import com.ryuqq.application.mcp.dto.response.ModuleWithPackagesResult;
import com.ryuqq.application.mcp.dto.response.PackagePurposeDetailResult;
import com.ryuqq.application.mcp.dto.response.PackageStructureWithDetailsResult;
import com.ryuqq.application.mcp.dto.response.PackageSummaryResult;
import com.ryuqq.application.mcp.dto.response.PlanningContextResult;
import com.ryuqq.application.mcp.dto.response.PlanningContextSummaryResult;
import com.ryuqq.application.mcp.dto.response.RuleContextResult;
import com.ryuqq.application.mcp.dto.response.RuleExampleDetailResult;
import com.ryuqq.application.mcp.dto.response.TechStackSummaryResult;
import com.ryuqq.application.mcp.dto.response.ZeroToleranceDetailResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * McpContextAssembler - MCP Context 조립 담당
 *
 * <p>조회된 DTO들을 응답 Result로 변환합니다.
 *
 * <p>ASM-001: Assembler 명명 규칙 준수.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
@SuppressWarnings("PMD.ExcessiveImports") // Assembler 특성상 다수 DTO 변환 필요
public class McpContextAssembler {

    /**
     * ModuleContextResult 조립
     *
     * @param moduleDto Module 기본 정보 DTO
     * @param executionContext 실행 컨텍스트
     * @param ruleContext 규칙 컨텍스트
     * @return ModuleContextResult
     */
    public ModuleContextResult assemble(
            ModuleWithLayerAndConventionDto moduleDto,
            ExecutionContextResult executionContext,
            RuleContextResult ruleContext) {
        ModuleSummaryResult moduleSummary = toModuleSummaryResult(moduleDto);
        ModuleContextSummaryResult summary = calculateSummary(executionContext, ruleContext);
        return new ModuleContextResult(moduleSummary, executionContext, ruleContext, summary);
    }

    /**
     * ModuleSummaryResult 변환
     *
     * @param dto Module 기본 정보 DTO
     * @return ModuleSummaryResult
     */
    public ModuleSummaryResult toModuleSummaryResult(ModuleWithLayerAndConventionDto dto) {
        LayerSummaryResult layerSummary = new LayerSummaryResult(dto.layerCode(), dto.layerName());
        return new ModuleSummaryResult(
                dto.moduleId(), dto.moduleName(), dto.moduleDescription(), layerSummary);
    }

    /**
     * ExecutionContextResult 조립
     *
     * @param structures PackageStructure + Purpose 목록
     * @param templatesAndTests Template + ArchUnitTest 목록
     * @return ExecutionContextResult
     */
    public ExecutionContextResult toExecutionContextResult(
            List<PackageStructureWithPurposesDto> structures,
            List<TemplateAndTestDto> templatesAndTests) {
        if (structures.isEmpty()) {
            return new ExecutionContextResult(List.of());
        }

        Map<Long, TemplateAndTestDto> templateTestMap =
                templatesAndTests.stream()
                        .collect(
                                Collectors.toMap(
                                        TemplateAndTestDto::structureId,
                                        Function.identity(),
                                        (a, b) -> a));

        List<PackageStructureWithDetailsResult> packageStructureResults = new ArrayList<>();
        for (PackageStructureWithPurposesDto structure : structures) {
            List<PackagePurposeDetailResult> purposeResults =
                    structure.purposes().stream()
                            .map(
                                    p ->
                                            new PackagePurposeDetailResult(
                                                    p.code(), p.description(), "제약사항 정보"))
                            .toList();

            TemplateAndTestDto templateAndTest = templateTestMap.get(structure.structureId());

            List<ClassTemplateDetailResult> templateResults =
                    templateAndTest != null
                            ? templateAndTest.templates().stream()
                                    .map(
                                            t ->
                                                    new ClassTemplateDetailResult(
                                                            t.templateId(),
                                                            t.classTypeId(),
                                                            t.templateCode(),
                                                            t.description(),
                                                            t.templateCode()))
                                    .toList()
                            : List.of();

            List<ArchUnitTestDetailResult> archTestResults =
                    templateAndTest != null
                            ? templateAndTest.archUnitTests().stream()
                                    .map(
                                            a ->
                                                    new ArchUnitTestDetailResult(
                                                            a.testId(),
                                                            a.name(),
                                                            a.description(),
                                                            a.testCode()))
                                    .toList()
                            : List.of();

            packageStructureResults.add(
                    new PackageStructureWithDetailsResult(
                            structure.structureId(),
                            structure.pathPattern(),
                            structure.description(),
                            purposeResults,
                            templateResults,
                            archTestResults));
        }

        return new ExecutionContextResult(packageStructureResults);
    }

    /**
     * RuleContextResult 조립
     *
     * @param moduleDto Module 기본 정보 DTO (Convention 정보 포함)
     * @param codingRules CodingRule + 상세 정보 목록
     * @return RuleContextResult
     */
    public RuleContextResult toRuleContextResult(
            ModuleWithLayerAndConventionDto moduleDto, List<CodingRuleWithDetailsDto> codingRules) {
        if (moduleDto.conventionId() == null) {
            return new RuleContextResult(List.of());
        }

        List<CodingRuleWithDetailsResult> codingRuleResults =
                codingRules.stream().map(this::toCodingRuleWithDetailsResult).toList();

        ConventionWithRulesResult conventionResult =
                new ConventionWithRulesResult(
                        moduleDto.conventionId(),
                        moduleDto.conventionVersion(),
                        moduleDto.conventionDescription(),
                        codingRuleResults);

        return new RuleContextResult(List.of(conventionResult));
    }

    /**
     * ModuleContextSummaryResult 계산
     *
     * @param executionContext 실행 컨텍스트
     * @param ruleContext 규칙 컨텍스트
     * @return ModuleContextSummaryResult
     */
    public ModuleContextSummaryResult calculateSummary(
            ExecutionContextResult executionContext, RuleContextResult ruleContext) {
        int totalPackageStructures = executionContext.packageStructures().size();
        int totalTemplates =
                executionContext.packageStructures().stream()
                        .mapToInt(ps -> ps.templates().size())
                        .sum();
        int totalArchTests =
                executionContext.packageStructures().stream()
                        .mapToInt(ps -> ps.archUnitTests().size())
                        .sum();

        int totalRules =
                ruleContext.conventions().stream().mapToInt(c -> c.codingRules().size()).sum();
        int totalZeroTolerance =
                ruleContext.conventions().stream()
                        .flatMap(c -> c.codingRules().stream())
                        .filter(r -> r.zeroTolerance() != null)
                        .mapToInt(r -> 1)
                        .sum();

        return new ModuleContextSummaryResult(
                totalPackageStructures,
                totalTemplates,
                totalRules,
                totalZeroTolerance,
                totalArchTests);
    }

    private CodingRuleWithDetailsResult toCodingRuleWithDetailsResult(
            CodingRuleWithDetailsDto dto) {
        List<RuleExampleDetailResult> exampleResults =
                dto.examples().stream()
                        .map(
                                e ->
                                        new RuleExampleDetailResult(
                                                e.exampleType(), e.code(), e.explanation()))
                        .toList();

        ZeroToleranceDetailResult zeroToleranceResult =
                dto.zeroTolerance() != null
                        ? new ZeroToleranceDetailResult(
                                dto.zeroTolerance().detectionPattern(),
                                dto.zeroTolerance().detectionType(),
                                dto.zeroTolerance().autoRejectPr())
                        : null;

        ChecklistItemDetailResult checklistItemResult =
                dto.checklistItem() != null
                        ? new ChecklistItemDetailResult(
                                dto.checklistItem().checkDescription(),
                                dto.checklistItem().hasAutomation())
                        : null;

        return new CodingRuleWithDetailsResult(
                dto.ruleId(),
                dto.ruleCode(),
                dto.ruleName(),
                dto.ruleDescription(),
                dto.severity(),
                dto.appliesTo(),
                exampleResults,
                zeroToleranceResult,
                checklistItemResult);
    }

    // ========== Planning Context 조립 메서드 ==========

    /**
     * PlanningContextResult 조립
     *
     * @param techStackDto TechStack + Architecture DTO
     * @param layerStructures Flat 구조의 Layer + Module + Structure 목록
     * @return PlanningContextResult
     */
    public PlanningContextResult assemblePlanningContext(
            PlanningTechStackArchitectureDto techStackDto,
            List<PlanningLayerModuleStructureDto> layerStructures) {
        TechStackSummaryResult techStackSummary = toTechStackSummaryResult(techStackDto);
        ArchitectureSummaryResult architectureSummary = toArchitectureSummaryResult(techStackDto);
        List<LayerWithModulesResult> layerResults = toLayerWithModulesResults(layerStructures);
        PlanningContextSummaryResult summary = calculatePlanningSummary(layerStructures);

        return new PlanningContextResult(
                techStackSummary, architectureSummary, layerResults, summary);
    }

    /**
     * TechStackSummaryResult 변환
     *
     * @param dto TechStack + Architecture DTO
     * @return TechStackSummaryResult
     */
    public TechStackSummaryResult toTechStackSummaryResult(PlanningTechStackArchitectureDto dto) {
        String description =
                String.format(
                        "%s %s + %s %s",
                        dto.languageType(),
                        dto.languageVersion(),
                        dto.frameworkType(),
                        dto.frameworkVersion());
        return new TechStackSummaryResult(dto.techStackId(), dto.techStackName(), description);
    }

    /**
     * ArchitectureSummaryResult 변환
     *
     * @param dto TechStack + Architecture DTO
     * @return ArchitectureSummaryResult
     */
    public ArchitectureSummaryResult toArchitectureSummaryResult(
            PlanningTechStackArchitectureDto dto) {
        return new ArchitectureSummaryResult(
                dto.architectureId(), dto.architectureName(), dto.patternDescription());
    }

    /**
     * Flat 구조를 계층적 LayerWithModulesResult 목록으로 변환
     *
     * <p>LinkedHashMap을 사용하여 순서를 보장합니다.
     *
     * @param flatStructures Flat 구조의 조회 결과
     * @return 계층적으로 그룹핑된 결과
     */
    public List<LayerWithModulesResult> toLayerWithModulesResults(
            List<PlanningLayerModuleStructureDto> flatStructures) {
        if (flatStructures.isEmpty()) {
            return List.of();
        }

        // Layer → Module → Package 순서로 그룹핑 (순서 보장을 위해 LinkedHashMap 사용)
        Map<String, Map<Long, List<PlanningLayerModuleStructureDto>>> grouped =
                flatStructures.stream()
                        .collect(
                                Collectors.groupingBy(
                                        PlanningLayerModuleStructureDto::layerCode,
                                        LinkedHashMap::new,
                                        Collectors.groupingBy(
                                                PlanningLayerModuleStructureDto::moduleId,
                                                LinkedHashMap::new,
                                                Collectors.toList())));

        List<LayerWithModulesResult> layerResults = new ArrayList<>();

        for (Map.Entry<String, Map<Long, List<PlanningLayerModuleStructureDto>>> layerEntry :
                grouped.entrySet()) {
            String layerCode = layerEntry.getKey();
            Map<Long, List<PlanningLayerModuleStructureDto>> moduleMap = layerEntry.getValue();

            // 첫 번째 항목에서 Layer 정보 추출
            PlanningLayerModuleStructureDto firstItem = moduleMap.values().iterator().next().get(0);
            String layerName = firstItem.layerName();
            String layerDescription = firstItem.layerDescription();

            List<ModuleWithPackagesResult> moduleResults = new ArrayList<>();

            for (Map.Entry<Long, List<PlanningLayerModuleStructureDto>> moduleEntry :
                    moduleMap.entrySet()) {
                List<PlanningLayerModuleStructureDto> packages = moduleEntry.getValue();
                PlanningLayerModuleStructureDto firstPackage = packages.get(0);

                List<PackageSummaryResult> packageResults =
                        packages.stream()
                                .filter(p -> p.structureId() != null)
                                .map(this::toPackageSummaryResult)
                                .toList();

                ModuleWithPackagesResult moduleResult =
                        new ModuleWithPackagesResult(
                                firstPackage.moduleId(),
                                firstPackage.moduleName(),
                                firstPackage.moduleDescription(),
                                packageResults);

                moduleResults.add(moduleResult);
            }

            LayerWithModulesResult layerResult =
                    new LayerWithModulesResult(
                            layerCode, layerName, layerDescription, moduleResults);

            layerResults.add(layerResult);
        }

        return layerResults;
    }

    /**
     * PlanningContextSummaryResult 계산
     *
     * @param flatStructures Flat 구조의 조회 결과
     * @return PlanningContextSummaryResult
     */
    public PlanningContextSummaryResult calculatePlanningSummary(
            List<PlanningLayerModuleStructureDto> flatStructures) {
        int totalModules =
                (int)
                        flatStructures.stream()
                                .map(PlanningLayerModuleStructureDto::moduleId)
                                .distinct()
                                .count();

        int totalPackages =
                (int)
                        flatStructures.stream()
                                .filter(s -> s.structureId() != null)
                                .map(PlanningLayerModuleStructureDto::structureId)
                                .distinct()
                                .count();

        int totalTemplates = flatStructures.stream().mapToInt(s -> s.templateCount()).sum();

        int totalRules = flatStructures.stream().mapToInt(s -> s.ruleCount()).sum();

        return new PlanningContextSummaryResult(
                totalModules, totalPackages, totalTemplates, totalRules);
    }

    private PackageSummaryResult toPackageSummaryResult(PlanningLayerModuleStructureDto dto) {
        List<String> allowedClassTypes =
                dto.allowedClassTypes() != null && !dto.allowedClassTypes().isEmpty()
                        ? Arrays.asList(dto.allowedClassTypes().split(","))
                        : List.of();

        return new PackageSummaryResult(
                dto.structureId(),
                dto.pathPattern(),
                dto.purposeDescription(),
                allowedClassTypes,
                dto.templateCount(),
                dto.ruleCount());
    }
}
