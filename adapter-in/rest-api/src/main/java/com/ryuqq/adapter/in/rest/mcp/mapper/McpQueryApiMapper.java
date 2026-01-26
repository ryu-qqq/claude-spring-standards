package com.ryuqq.adapter.in.rest.mcp.mapper;

import com.ryuqq.adapter.in.rest.mcp.dto.request.GetConfigFilesApiRequest;
import com.ryuqq.adapter.in.rest.mcp.dto.request.GetOnboardingApiRequest;
import com.ryuqq.adapter.in.rest.mcp.dto.request.ModuleContextApiRequest;
import com.ryuqq.adapter.in.rest.mcp.dto.request.PlanningContextApiRequest;
import com.ryuqq.adapter.in.rest.mcp.dto.request.ValidationContextApiRequest;
import com.ryuqq.adapter.in.rest.mcp.dto.response.ArchUnitTestDetailApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.ArchitectureSummaryApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.ChecklistItemApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.ChecklistItemDetailApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.ClassTemplateDetailApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.CodingRuleWithDetailsApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.ConfigFileApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.ConfigFilesApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.ConventionWithRulesApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.ExecutionContextApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.LayerSummaryApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.LayerValidationStatsApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.LayerWithModulesApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.ModuleContextApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.ModuleContextSummaryApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.ModuleSummaryApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.ModuleWithPackagesApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.OnboardingApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.OnboardingContextsApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.PackagePurposeDetailApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.PackageStructureWithDetailsApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.PackageSummaryApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.PlanningContextApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.PlanningContextSummaryApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.RuleContextApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.RuleExampleDetailApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.TechStackSummaryApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.ValidationContextApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.ValidationContextSummaryApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.ZeroToleranceDetailApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.ZeroToleranceRuleApiResponse;
import com.ryuqq.application.mcp.dto.query.GetConfigFilesQuery;
import com.ryuqq.application.mcp.dto.query.GetOnboardingQuery;
import com.ryuqq.application.mcp.dto.query.ModuleContextQuery;
import com.ryuqq.application.mcp.dto.query.PlanningContextQuery;
import com.ryuqq.application.mcp.dto.query.ValidationContextQuery;
import com.ryuqq.application.mcp.dto.response.ArchUnitTestDetailResult;
import com.ryuqq.application.mcp.dto.response.ArchitectureSummaryResult;
import com.ryuqq.application.mcp.dto.response.ChecklistItemDetailResult;
import com.ryuqq.application.mcp.dto.response.ChecklistItemResult;
import com.ryuqq.application.mcp.dto.response.ClassTemplateDetailResult;
import com.ryuqq.application.mcp.dto.response.CodingRuleWithDetailsResult;
import com.ryuqq.application.mcp.dto.response.ConfigFileResult;
import com.ryuqq.application.mcp.dto.response.ConfigFilesResult;
import com.ryuqq.application.mcp.dto.response.ConventionWithRulesResult;
import com.ryuqq.application.mcp.dto.response.ExecutionContextResult;
import com.ryuqq.application.mcp.dto.response.LayerWithModulesResult;
import com.ryuqq.application.mcp.dto.response.ModuleContextResult;
import com.ryuqq.application.mcp.dto.response.ModuleContextSummaryResult;
import com.ryuqq.application.mcp.dto.response.ModuleSummaryResult;
import com.ryuqq.application.mcp.dto.response.ModuleWithPackagesResult;
import com.ryuqq.application.mcp.dto.response.OnboardingContextsResult;
import com.ryuqq.application.mcp.dto.response.OnboardingResult;
import com.ryuqq.application.mcp.dto.response.PackagePurposeDetailResult;
import com.ryuqq.application.mcp.dto.response.PackageStructureWithDetailsResult;
import com.ryuqq.application.mcp.dto.response.PackageSummaryResult;
import com.ryuqq.application.mcp.dto.response.PlanningContextResult;
import com.ryuqq.application.mcp.dto.response.PlanningContextSummaryResult;
import com.ryuqq.application.mcp.dto.response.RuleContextResult;
import com.ryuqq.application.mcp.dto.response.RuleExampleDetailResult;
import com.ryuqq.application.mcp.dto.response.TechStackSummaryResult;
import com.ryuqq.application.mcp.dto.response.ValidationContextResult;
import com.ryuqq.application.mcp.dto.response.ValidationContextSummaryResult;
import com.ryuqq.application.mcp.dto.response.ZeroToleranceDetailResult;
import com.ryuqq.application.mcp.dto.response.ZeroToleranceRuleResult;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * McpQueryApiMapper - MCP Query API 변환 매퍼
 *
 * <p>API Request/Response와 Application Query/Result 간 변환을 담당합니다.
 *
 * <p>MAP-001: Mapper는 @Component 필수.
 *
 * <p>MAP-002: Static 메서드 금지.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
@SuppressWarnings({"PMD.ExcessiveImports", "PMD.TooManyMethods"})
// MCP Context 변환을 위해 다수 DTO import 및 변환 메서드 필요
public class McpQueryApiMapper {

    /**
     * PlanningContextApiRequest -> PlanningContextQuery 변환
     *
     * @param request Planning Context 조회 요청 DTO
     * @return Planning Context 조회 쿼리
     */
    public PlanningContextQuery toQuery(PlanningContextApiRequest request) {
        return new PlanningContextQuery(request.layers(), request.techStackId());
    }

    /**
     * PlanningContextResult -> PlanningContextApiResponse 변환
     *
     * @param result Planning Context 조회 결과
     * @return Planning Context API 응답
     */
    public PlanningContextApiResponse toResponse(PlanningContextResult result) {
        return new PlanningContextApiResponse(
                toTechStackResponse(result.techStack()),
                toArchitectureResponse(result.architecture()),
                toLayerResponses(result.layers()),
                toSummaryResponse(result.summary()));
    }

    private TechStackSummaryApiResponse toTechStackResponse(TechStackSummaryResult result) {
        return new TechStackSummaryApiResponse(result.id(), result.name(), result.description());
    }

    private ArchitectureSummaryApiResponse toArchitectureResponse(
            ArchitectureSummaryResult result) {
        return new ArchitectureSummaryApiResponse(result.id(), result.name(), result.description());
    }

    private List<LayerWithModulesApiResponse> toLayerResponses(
            List<LayerWithModulesResult> results) {
        return results.stream().map(this::toLayerResponse).toList();
    }

    private LayerWithModulesApiResponse toLayerResponse(LayerWithModulesResult result) {
        return new LayerWithModulesApiResponse(
                result.code(),
                result.name(),
                result.description(),
                toModuleResponses(result.modules()));
    }

    private List<ModuleWithPackagesApiResponse> toModuleResponses(
            List<ModuleWithPackagesResult> results) {
        return results.stream().map(this::toModuleResponse).toList();
    }

    private ModuleWithPackagesApiResponse toModuleResponse(ModuleWithPackagesResult result) {
        return new ModuleWithPackagesApiResponse(
                result.id(),
                result.name(),
                result.description(),
                toPackageResponses(result.packages()));
    }

    private List<PackageSummaryApiResponse> toPackageResponses(List<PackageSummaryResult> results) {
        return results.stream().map(this::toPackageResponse).toList();
    }

    private PackageSummaryApiResponse toPackageResponse(PackageSummaryResult result) {
        return new PackageSummaryApiResponse(
                result.id(),
                result.pathPattern(),
                result.purposeSummary(),
                result.allowedClassTypes(),
                result.templateCount(),
                result.ruleCount());
    }

    private PlanningContextSummaryApiResponse toSummaryResponse(
            PlanningContextSummaryResult result) {
        return new PlanningContextSummaryApiResponse(
                result.totalModules(),
                result.totalPackages(),
                result.totalTemplates(),
                result.totalRules());
    }

    /**
     * ModuleContextApiRequest -> ModuleContextQuery 변환
     *
     * @param request Module Context 조회 요청 DTO
     * @return Module Context 조회 쿼리
     */
    public ModuleContextQuery toQuery(ModuleContextApiRequest request, Long moduleId) {
        return new ModuleContextQuery(moduleId, request.classTypeId());
    }

    /**
     * ModuleContextResult -> ModuleContextApiResponse 변환
     *
     * @param result Module Context 조회 결과
     * @return Module Context API 응답
     */
    public ModuleContextApiResponse toResponse(ModuleContextResult result) {
        return new ModuleContextApiResponse(
                toModuleSummaryResponse(result.module()),
                toExecutionContextResponse(result.executionContext()),
                toRuleContextResponse(result.ruleContext()),
                toModuleContextSummaryResponse(result.summary()));
    }

    private ModuleSummaryApiResponse toModuleSummaryResponse(ModuleSummaryResult result) {
        return new ModuleSummaryApiResponse(
                result.id(),
                result.name(),
                result.description(),
                new LayerSummaryApiResponse(result.layer().code(), result.layer().name()));
    }

    private ExecutionContextApiResponse toExecutionContextResponse(ExecutionContextResult result) {
        return new ExecutionContextApiResponse(
                result.packageStructures().stream()
                        .map(this::toPackageStructureWithDetailsResponse)
                        .toList());
    }

    private PackageStructureWithDetailsApiResponse toPackageStructureWithDetailsResponse(
            PackageStructureWithDetailsResult result) {
        return new PackageStructureWithDetailsApiResponse(
                result.id(),
                result.pathPattern(),
                result.description(),
                result.purposes().stream().map(this::toPackagePurposeDetailResponse).toList(),
                result.templates().stream().map(this::toClassTemplateDetailResponse).toList(),
                result.archUnitTests().stream().map(this::toArchUnitTestDetailResponse).toList());
    }

    private PackagePurposeDetailApiResponse toPackagePurposeDetailResponse(
            PackagePurposeDetailResult result) {
        return new PackagePurposeDetailApiResponse(
                result.classType(), result.description(), result.constraints());
    }

    private ClassTemplateDetailApiResponse toClassTemplateDetailResponse(
            ClassTemplateDetailResult result) {
        return new ClassTemplateDetailApiResponse(
                result.id(),
                result.classTypeId(),
                result.name(),
                result.description(),
                result.body());
    }

    private ArchUnitTestDetailApiResponse toArchUnitTestDetailResponse(
            ArchUnitTestDetailResult result) {
        return new ArchUnitTestDetailApiResponse(
                result.id(), result.name(), result.description(), result.testCode());
    }

    private RuleContextApiResponse toRuleContextResponse(RuleContextResult result) {
        return new RuleContextApiResponse(
                result.conventions().stream().map(this::toConventionWithRulesResponse).toList());
    }

    private ConventionWithRulesApiResponse toConventionWithRulesResponse(
            ConventionWithRulesResult result) {
        return new ConventionWithRulesApiResponse(
                result.id(),
                result.name(),
                result.description(),
                result.codingRules().stream().map(this::toCodingRuleWithDetailsResponse).toList());
    }

    private CodingRuleWithDetailsApiResponse toCodingRuleWithDetailsResponse(
            CodingRuleWithDetailsResult result) {
        return new CodingRuleWithDetailsApiResponse(
                result.id(),
                result.code(),
                result.title(),
                result.description(),
                result.severity(),
                result.classType(),
                result.examples().stream().map(this::toRuleExampleDetailResponse).toList(),
                result.zeroTolerance() != null
                        ? toZeroToleranceDetailResponse(result.zeroTolerance())
                        : null,
                result.checklistItem() != null
                        ? toChecklistItemDetailResponse(result.checklistItem())
                        : null);
    }

    private RuleExampleDetailApiResponse toRuleExampleDetailResponse(
            RuleExampleDetailResult result) {
        return new RuleExampleDetailApiResponse(result.type(), result.code(), result.explanation());
    }

    private ZeroToleranceDetailApiResponse toZeroToleranceDetailResponse(
            ZeroToleranceDetailResult result) {
        return new ZeroToleranceDetailApiResponse(
                result.detectionPattern(), result.detectionType(), result.autoRejectPr());
    }

    private ChecklistItemDetailApiResponse toChecklistItemDetailResponse(
            ChecklistItemDetailResult result) {
        return new ChecklistItemDetailApiResponse(
                result.checkDescription(), result.autoCheckable());
    }

    private ModuleContextSummaryApiResponse toModuleContextSummaryResponse(
            ModuleContextSummaryResult result) {
        return new ModuleContextSummaryApiResponse(
                result.packageCount(),
                result.templateCount(),
                result.ruleCount(),
                result.zeroToleranceCount(),
                result.archTestCount());
    }

    /**
     * ValidationContextApiRequest -> ValidationContextQuery 변환
     *
     * @param request Validation Context 조회 요청 DTO
     * @return Validation Context 조회 쿼리
     */
    public ValidationContextQuery toQuery(ValidationContextApiRequest request) {
        return new ValidationContextQuery(
                request.techStackId(),
                request.architectureId(),
                request.layers(),
                request.classTypes());
    }

    /**
     * ValidationContextResult -> ValidationContextApiResponse 변환
     *
     * @param result Validation Context 조회 결과
     * @return Validation Context API 응답
     */
    public ValidationContextApiResponse toResponse(ValidationContextResult result) {
        return new ValidationContextApiResponse(
                result.zeroToleranceRules().stream()
                        .map(this::toZeroToleranceRuleResponse)
                        .toList(),
                result.checklist().stream().map(this::toChecklistItemResponse).toList(),
                toValidationContextSummaryResponse(result.summary()));
    }

    private ZeroToleranceRuleApiResponse toZeroToleranceRuleResponse(
            ZeroToleranceRuleResult result) {
        return new ZeroToleranceRuleApiResponse(
                result.ruleCode(),
                result.ruleTitle(),
                result.layer(),
                result.classTypes(),
                result.detectionPattern(),
                result.detectionType(),
                result.autoRejectPr(),
                result.message());
    }

    private ChecklistItemApiResponse toChecklistItemResponse(ChecklistItemResult result) {
        return new ChecklistItemApiResponse(
                result.ruleCode(),
                result.checkDescription(),
                result.severity(),
                result.autoCheckable());
    }

    private ValidationContextSummaryApiResponse toValidationContextSummaryResponse(
            ValidationContextSummaryResult result) {
        Map<String, LayerValidationStatsApiResponse> byLayerMap =
                result.byLayer().entrySet().stream()
                        .collect(
                                Collectors.toMap(
                                        Map.Entry::getKey,
                                        entry ->
                                                new LayerValidationStatsApiResponse(
                                                        entry.getValue().zeroTolerance(),
                                                        entry.getValue().checklist())));
        return new ValidationContextSummaryApiResponse(
                result.totalZeroTolerance(),
                result.totalChecklist(),
                result.autoCheckableCount(),
                byLayerMap);
    }

    // ========================================
    // Convention Hub Methods (Phase 2)
    // ========================================

    /**
     * GetConfigFilesApiRequest -> GetConfigFilesQuery 변환
     *
     * @param request Config Files 조회 요청 DTO
     * @return Config Files 조회 쿼리
     */
    public GetConfigFilesQuery toQuery(GetConfigFilesApiRequest request) {
        return new GetConfigFilesQuery(
                request.toolTypes(), request.techStackId(), request.architectureId());
    }

    /**
     * ConfigFilesResult -> ConfigFilesApiResponse 변환
     *
     * @param result Config Files 조회 결과
     * @return Config Files API 응답
     */
    public ConfigFilesApiResponse toResponse(ConfigFilesResult result) {
        List<ConfigFileApiResponse> configFiles =
                result.configFiles().stream().map(this::toConfigFileResponse).toList();
        return new ConfigFilesApiResponse(configFiles, result.totalCount());
    }

    private ConfigFileApiResponse toConfigFileResponse(ConfigFileResult result) {
        return new ConfigFileApiResponse(
                result.id(),
                result.toolType(),
                result.filePath(),
                result.fileName(),
                result.description(),
                result.templateContent(),
                result.priority());
    }

    /**
     * GetOnboardingApiRequest -> GetOnboardingQuery 변환
     *
     * @param request Onboarding Context 조회 요청 DTO
     * @return Onboarding Context 조회 쿼리
     */
    public GetOnboardingQuery toQuery(GetOnboardingApiRequest request) {
        return new GetOnboardingQuery(
                request.techStackId(), request.architectureId(), request.contextTypes());
    }

    /**
     * OnboardingContextsResult -> OnboardingContextsApiResponse 변환
     *
     * @param result Onboarding Context 조회 결과
     * @return Onboarding Context API 응답
     */
    public OnboardingContextsApiResponse toResponse(OnboardingContextsResult result) {
        List<OnboardingApiResponse> contexts =
                result.contexts().stream().map(this::toOnboardingResponse).toList();
        return new OnboardingContextsApiResponse(contexts, result.totalCount());
    }

    private OnboardingApiResponse toOnboardingResponse(OnboardingResult result) {
        return new OnboardingApiResponse(
                result.id(),
                result.contextType(),
                result.title(),
                result.content(),
                result.priority());
    }
}
