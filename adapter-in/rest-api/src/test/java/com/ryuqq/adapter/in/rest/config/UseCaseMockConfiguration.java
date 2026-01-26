package com.ryuqq.adapter.in.rest.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ryuqq.application.architecture.dto.response.ArchitectureSliceResult;
import com.ryuqq.application.architecture.port.in.CreateArchitectureUseCase;
import com.ryuqq.application.architecture.port.in.SearchArchitecturesByCursorUseCase;
import com.ryuqq.application.architecture.port.in.UpdateArchitectureUseCase;
import com.ryuqq.application.archunittest.dto.response.ArchUnitTestSliceResult;
import com.ryuqq.application.archunittest.port.in.CreateArchUnitTestUseCase;
import com.ryuqq.application.archunittest.port.in.SearchArchUnitTestsByCursorUseCase;
import com.ryuqq.application.archunittest.port.in.UpdateArchUnitTestUseCase;
import com.ryuqq.application.checklistitem.dto.response.ChecklistItemSliceResult;
import com.ryuqq.application.checklistitem.port.in.CreateChecklistItemUseCase;
import com.ryuqq.application.checklistitem.port.in.SearchChecklistItemsByCursorUseCase;
import com.ryuqq.application.checklistitem.port.in.UpdateChecklistItemUseCase;
import com.ryuqq.application.classtemplate.dto.response.ClassTemplateSliceResult;
import com.ryuqq.application.classtemplate.port.in.CreateClassTemplateUseCase;
import com.ryuqq.application.classtemplate.port.in.SearchClassTemplatesByCursorUseCase;
import com.ryuqq.application.classtemplate.port.in.UpdateClassTemplateUseCase;
import com.ryuqq.application.classtype.dto.response.ClassTypeSliceResult;
import com.ryuqq.application.classtype.port.in.CreateClassTypeUseCase;
import com.ryuqq.application.classtype.port.in.SearchClassTypesByCursorUseCase;
import com.ryuqq.application.classtype.port.in.UpdateClassTypeUseCase;
import com.ryuqq.application.classtypecategory.dto.response.ClassTypeCategorySliceResult;
import com.ryuqq.application.classtypecategory.port.in.CreateClassTypeCategoryUseCase;
import com.ryuqq.application.classtypecategory.port.in.SearchClassTypeCategoriesByCursorUseCase;
import com.ryuqq.application.classtypecategory.port.in.UpdateClassTypeCategoryUseCase;
import com.ryuqq.application.codingrule.dto.response.CodingRuleSliceResult;
import com.ryuqq.application.codingrule.port.in.CreateCodingRuleUseCase;
import com.ryuqq.application.codingrule.port.in.ListCodingRuleIndexUseCase;
import com.ryuqq.application.codingrule.port.in.SearchCodingRulesByCursorUseCase;
import com.ryuqq.application.codingrule.port.in.UpdateCodingRuleUseCase;
import com.ryuqq.application.convention.dto.response.ConventionSliceResult;
import com.ryuqq.application.convention.port.in.CreateConventionUseCase;
import com.ryuqq.application.convention.port.in.SearchConventionsByCursorUseCase;
import com.ryuqq.application.convention.port.in.UpdateConventionUseCase;
import com.ryuqq.application.feedbackqueue.dto.response.FeedbackQueueResult;
import com.ryuqq.application.feedbackqueue.dto.response.FeedbackQueueSliceResult;
import com.ryuqq.application.feedbackqueue.port.in.CreateFeedbackUseCase;
import com.ryuqq.application.feedbackqueue.port.in.GetAwaitingHumanReviewUseCase;
import com.ryuqq.application.feedbackqueue.port.in.GetPendingFeedbacksUseCase;
import com.ryuqq.application.feedbackqueue.port.in.MergeFeedbackUseCase;
import com.ryuqq.application.feedbackqueue.port.in.ProcessFeedbackUseCase;
import com.ryuqq.application.feedbackqueue.port.in.SearchFeedbacksByCursorUseCase;
import com.ryuqq.application.layer.dto.response.LayerSliceResult;
import com.ryuqq.application.layer.port.in.CreateLayerUseCase;
import com.ryuqq.application.layer.port.in.SearchLayersByCursorUseCase;
import com.ryuqq.application.layer.port.in.UpdateLayerUseCase;
import com.ryuqq.application.layerdependency.port.in.CreateLayerDependencyRuleUseCase;
import com.ryuqq.application.layerdependency.port.in.SearchLayerDependencyRulesByCursorUseCase;
import com.ryuqq.application.layerdependency.port.in.UpdateLayerDependencyRuleUseCase;
import com.ryuqq.application.mcp.port.in.GetModuleContextUseCase;
import com.ryuqq.application.mcp.port.in.GetPlanningContextUseCase;
import com.ryuqq.application.mcp.port.in.GetValidationContextUseCase;
import com.ryuqq.application.module.dto.response.ModuleSliceResult;
import com.ryuqq.application.module.port.in.CreateModuleUseCase;
import com.ryuqq.application.module.port.in.GetModuleTreeUseCase;
import com.ryuqq.application.module.port.in.SearchModulesByCursorUseCase;
import com.ryuqq.application.module.port.in.UpdateModuleUseCase;
import com.ryuqq.application.packagepurpose.dto.response.PackagePurposeSliceResult;
import com.ryuqq.application.packagepurpose.port.in.CreatePackagePurposeUseCase;
import com.ryuqq.application.packagepurpose.port.in.SearchPackagePurposesByCursorUseCase;
import com.ryuqq.application.packagepurpose.port.in.UpdatePackagePurposeUseCase;
import com.ryuqq.application.packagestructure.dto.response.PackageStructureSliceResult;
import com.ryuqq.application.packagestructure.port.in.CreatePackageStructureUseCase;
import com.ryuqq.application.packagestructure.port.in.SearchPackageStructuresByCursorUseCase;
import com.ryuqq.application.packagestructure.port.in.UpdatePackageStructureUseCase;
import com.ryuqq.application.resourcetemplate.dto.response.ResourceTemplateSliceResult;
import com.ryuqq.application.resourcetemplate.port.in.CreateResourceTemplateUseCase;
import com.ryuqq.application.resourcetemplate.port.in.SearchResourceTemplatesByCursorUseCase;
import com.ryuqq.application.resourcetemplate.port.in.UpdateResourceTemplateUseCase;
import com.ryuqq.application.ruleexample.dto.response.RuleExampleSliceResult;
import com.ryuqq.application.ruleexample.port.in.CreateRuleExampleUseCase;
import com.ryuqq.application.ruleexample.port.in.SearchRuleExamplesByCursorUseCase;
import com.ryuqq.application.ruleexample.port.in.UpdateRuleExampleUseCase;
import com.ryuqq.application.techstack.dto.response.TechStackSliceResult;
import com.ryuqq.application.techstack.port.in.CreateTechStackUseCase;
import com.ryuqq.application.techstack.port.in.SearchTechStacksByCursorUseCase;
import com.ryuqq.application.techstack.port.in.UpdateTechStackUseCase;
import com.ryuqq.application.zerotolerance.dto.response.ZeroToleranceRuleSliceResult;
import com.ryuqq.application.zerotolerance.port.in.CreateZeroToleranceRuleUseCase;
import com.ryuqq.application.zerotolerance.port.in.SearchZeroToleranceRulesByCursorUseCase;
import com.ryuqq.application.zerotolerance.port.in.UpdateZeroToleranceRuleUseCase;
import java.time.Instant;
import java.util.List;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * REST API 테스트를 위한 UseCase Mock Configuration
 *
 * <p>모든 UseCase 인터페이스를 Mock으로 제공합니다. Controller 테스트에서 실제 비즈니스 로직이 아닌 HTTP 요청/응답 매핑만 테스트할 수 있도록 합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@TestConfiguration
public class UseCaseMockConfiguration {

    // ========================================
    // Architecture UseCases
    // ========================================
    @Bean
    @Primary
    public CreateArchitectureUseCase createArchitectureUseCase() {
        CreateArchitectureUseCase mock = mock(CreateArchitectureUseCase.class);
        when(mock.execute(any())).thenReturn(1L);
        return mock;
    }

    @Bean
    @Primary
    public UpdateArchitectureUseCase updateArchitectureUseCase() {
        return mock(UpdateArchitectureUseCase.class);
    }

    @Bean
    @Primary
    public SearchArchitecturesByCursorUseCase searchArchitecturesByCursorUseCase() {
        SearchArchitecturesByCursorUseCase mock = mock(SearchArchitecturesByCursorUseCase.class);
        when(mock.execute(any())).thenReturn(ArchitectureSliceResult.empty(0));
        return mock;
    }

    // ========================================
    // ArchUnitTest UseCases
    // ========================================
    @Bean
    @Primary
    public CreateArchUnitTestUseCase createArchUnitTestUseCase() {
        CreateArchUnitTestUseCase mock = mock(CreateArchUnitTestUseCase.class);
        when(mock.execute(any())).thenReturn(1L);
        return mock;
    }

    @Bean
    @Primary
    public UpdateArchUnitTestUseCase updateArchUnitTestUseCase() {
        return mock(UpdateArchUnitTestUseCase.class);
    }

    @Bean
    @Primary
    public SearchArchUnitTestsByCursorUseCase searchArchUnitTestsByCursorUseCase() {
        SearchArchUnitTestsByCursorUseCase mock = mock(SearchArchUnitTestsByCursorUseCase.class);
        when(mock.execute(any())).thenReturn(ArchUnitTestSliceResult.empty(0));
        return mock;
    }

    // ========================================
    // ChecklistItem UseCases
    // ========================================
    @Bean
    @Primary
    public CreateChecklistItemUseCase createChecklistItemUseCase() {
        CreateChecklistItemUseCase mock = mock(CreateChecklistItemUseCase.class);
        when(mock.execute(any())).thenReturn(1L);
        return mock;
    }

    @Bean
    @Primary
    public UpdateChecklistItemUseCase updateChecklistItemUseCase() {
        return mock(UpdateChecklistItemUseCase.class);
    }

    @Bean
    @Primary
    public SearchChecklistItemsByCursorUseCase searchChecklistItemsByCursorUseCase() {
        SearchChecklistItemsByCursorUseCase mock = mock(SearchChecklistItemsByCursorUseCase.class);
        when(mock.execute(any())).thenReturn(ChecklistItemSliceResult.empty());
        return mock;
    }

    // ========================================
    // ClassTemplate UseCases
    // ========================================
    @Bean
    @Primary
    public CreateClassTemplateUseCase createClassTemplateUseCase() {
        CreateClassTemplateUseCase mock = mock(CreateClassTemplateUseCase.class);
        when(mock.execute(any())).thenReturn(1L);
        return mock;
    }

    @Bean
    @Primary
    public UpdateClassTemplateUseCase updateClassTemplateUseCase() {
        return mock(UpdateClassTemplateUseCase.class);
    }

    @Bean
    @Primary
    public SearchClassTemplatesByCursorUseCase searchClassTemplatesByCursorUseCase() {
        SearchClassTemplatesByCursorUseCase mock = mock(SearchClassTemplatesByCursorUseCase.class);
        when(mock.execute(any())).thenReturn(ClassTemplateSliceResult.empty());
        return mock;
    }

    // ========================================
    // ClassType UseCases
    // ========================================
    @Bean
    @Primary
    public CreateClassTypeUseCase createClassTypeUseCase() {
        CreateClassTypeUseCase mock = mock(CreateClassTypeUseCase.class);
        when(mock.execute(any())).thenReturn(1L);
        return mock;
    }

    @Bean
    @Primary
    public UpdateClassTypeUseCase updateClassTypeUseCase() {
        return mock(UpdateClassTypeUseCase.class);
    }

    @Bean
    @Primary
    public SearchClassTypesByCursorUseCase searchClassTypesByCursorUseCase() {
        SearchClassTypesByCursorUseCase mock = mock(SearchClassTypesByCursorUseCase.class);
        when(mock.execute(any())).thenReturn(ClassTypeSliceResult.empty(20));
        return mock;
    }

    // ========================================
    // ClassTypeCategory UseCases
    // ========================================
    @Bean
    @Primary
    public CreateClassTypeCategoryUseCase createClassTypeCategoryUseCase() {
        CreateClassTypeCategoryUseCase mock = mock(CreateClassTypeCategoryUseCase.class);
        when(mock.execute(any())).thenReturn(1L);
        return mock;
    }

    @Bean
    @Primary
    public UpdateClassTypeCategoryUseCase updateClassTypeCategoryUseCase() {
        return mock(UpdateClassTypeCategoryUseCase.class);
    }

    @Bean
    @Primary
    public SearchClassTypeCategoriesByCursorUseCase searchClassTypeCategoriesByCursorUseCase() {
        SearchClassTypeCategoriesByCursorUseCase mock =
                mock(SearchClassTypeCategoriesByCursorUseCase.class);
        when(mock.execute(any())).thenReturn(ClassTypeCategorySliceResult.empty(20));
        return mock;
    }

    // ========================================
    // CodingRule UseCases
    // ========================================
    @Bean
    @Primary
    public CreateCodingRuleUseCase createCodingRuleUseCase() {
        CreateCodingRuleUseCase mock = mock(CreateCodingRuleUseCase.class);
        when(mock.execute(any())).thenReturn(1L);
        return mock;
    }

    @Bean
    @Primary
    public UpdateCodingRuleUseCase updateCodingRuleUseCase() {
        return mock(UpdateCodingRuleUseCase.class);
    }

    @Bean
    @Primary
    public SearchCodingRulesByCursorUseCase searchCodingRulesByCursorUseCase() {
        SearchCodingRulesByCursorUseCase mock = mock(SearchCodingRulesByCursorUseCase.class);
        when(mock.execute(any())).thenReturn(CodingRuleSliceResult.empty());
        return mock;
    }

    @Bean
    @Primary
    public ListCodingRuleIndexUseCase listCodingRuleIndexUseCase() {
        ListCodingRuleIndexUseCase mock = mock(ListCodingRuleIndexUseCase.class);
        when(mock.execute(any())).thenReturn(List.of());
        return mock;
    }

    // ========================================
    // Convention UseCases
    // ========================================
    @Bean
    @Primary
    public CreateConventionUseCase createConventionUseCase() {
        CreateConventionUseCase mock = mock(CreateConventionUseCase.class);
        when(mock.execute(any())).thenReturn(1L);
        return mock;
    }

    @Bean
    @Primary
    public UpdateConventionUseCase updateConventionUseCase() {
        return mock(UpdateConventionUseCase.class);
    }

    @Bean
    @Primary
    public SearchConventionsByCursorUseCase searchConventionsByCursorUseCase() {
        SearchConventionsByCursorUseCase mock = mock(SearchConventionsByCursorUseCase.class);
        when(mock.execute(any())).thenReturn(ConventionSliceResult.empty(0));
        return mock;
    }

    // ========================================
    // Layer UseCases
    // ========================================
    @Bean
    @Primary
    public CreateLayerUseCase createLayerUseCase() {
        CreateLayerUseCase mock = mock(CreateLayerUseCase.class);
        when(mock.execute(any())).thenReturn(1L);
        return mock;
    }

    @Bean
    @Primary
    public UpdateLayerUseCase updateLayerUseCase() {
        return mock(UpdateLayerUseCase.class);
    }

    @Bean
    @Primary
    public SearchLayersByCursorUseCase searchLayersByCursorUseCase() {
        SearchLayersByCursorUseCase mock = mock(SearchLayersByCursorUseCase.class);
        when(mock.execute(any())).thenReturn(LayerSliceResult.empty(20));
        return mock;
    }

    // ========================================
    // LayerDependency UseCases
    // ========================================
    @Bean
    @Primary
    public CreateLayerDependencyRuleUseCase createLayerDependencyRuleUseCase() {
        CreateLayerDependencyRuleUseCase mock = mock(CreateLayerDependencyRuleUseCase.class);
        when(mock.execute(any())).thenReturn(1L);
        return mock;
    }

    @Bean
    @Primary
    public UpdateLayerDependencyRuleUseCase updateLayerDependencyRuleUseCase() {
        return mock(UpdateLayerDependencyRuleUseCase.class);
    }

    @Bean
    @Primary
    public SearchLayerDependencyRulesByCursorUseCase searchLayerDependencyRulesByCursorUseCase() {
        SearchLayerDependencyRulesByCursorUseCase mock =
                mock(SearchLayerDependencyRulesByCursorUseCase.class);
        when(mock.execute(any()))
                .thenReturn(
                        com.ryuqq.application.layerdependency.dto.response
                                .LayerDependencyRuleSliceResult.empty(20));
        return mock;
    }

    // ========================================
    // MCP UseCases
    // ========================================
    @Bean
    @Primary
    public GetPlanningContextUseCase getPlanningContextUseCase() {
        GetPlanningContextUseCase mock = mock(GetPlanningContextUseCase.class);
        when(mock.execute(any())).thenReturn(null);
        return mock;
    }

    @Bean
    @Primary
    public GetValidationContextUseCase getValidationContextUseCase() {
        GetValidationContextUseCase mock = mock(GetValidationContextUseCase.class);
        when(mock.execute(any())).thenReturn(null);
        return mock;
    }

    @Bean
    @Primary
    public GetModuleContextUseCase getModuleContextUseCase() {
        GetModuleContextUseCase mock = mock(GetModuleContextUseCase.class);
        when(mock.execute(any())).thenReturn(null);
        return mock;
    }

    // ========================================
    // Module UseCases
    // ========================================
    @Bean
    @Primary
    public CreateModuleUseCase createModuleUseCase() {
        CreateModuleUseCase mock = mock(CreateModuleUseCase.class);
        when(mock.execute(any())).thenReturn(1L);
        return mock;
    }

    @Bean
    @Primary
    public UpdateModuleUseCase updateModuleUseCase() {
        return mock(UpdateModuleUseCase.class);
    }

    @Bean
    @Primary
    public SearchModulesByCursorUseCase searchModulesByCursorUseCase() {
        SearchModulesByCursorUseCase mock = mock(SearchModulesByCursorUseCase.class);
        when(mock.execute(any())).thenReturn(ModuleSliceResult.empty());
        return mock;
    }

    @Bean
    @Primary
    public GetModuleTreeUseCase getModuleTreeUseCase() {
        GetModuleTreeUseCase mock = mock(GetModuleTreeUseCase.class);
        when(mock.execute(anyLong())).thenReturn(List.of());
        return mock;
    }

    // ========================================
    // PackagePurpose UseCases
    // ========================================
    @Bean
    @Primary
    public CreatePackagePurposeUseCase createPackagePurposeUseCase() {
        CreatePackagePurposeUseCase mock = mock(CreatePackagePurposeUseCase.class);
        when(mock.execute(any())).thenReturn(1L);
        return mock;
    }

    @Bean
    @Primary
    public UpdatePackagePurposeUseCase updatePackagePurposeUseCase() {
        return mock(UpdatePackagePurposeUseCase.class);
    }

    @Bean
    @Primary
    public SearchPackagePurposesByCursorUseCase searchPackagePurposesByCursorUseCase() {
        SearchPackagePurposesByCursorUseCase mock =
                mock(SearchPackagePurposesByCursorUseCase.class);
        when(mock.execute(any())).thenReturn(PackagePurposeSliceResult.empty());
        return mock;
    }

    // ========================================
    // PackageStructure UseCases
    // ========================================
    @Bean
    @Primary
    public CreatePackageStructureUseCase createPackageStructureUseCase() {
        CreatePackageStructureUseCase mock = mock(CreatePackageStructureUseCase.class);
        when(mock.execute(any())).thenReturn(1L);
        return mock;
    }

    @Bean
    @Primary
    public UpdatePackageStructureUseCase updatePackageStructureUseCase() {
        return mock(UpdatePackageStructureUseCase.class);
    }

    @Bean
    @Primary
    public SearchPackageStructuresByCursorUseCase searchPackageStructuresByCursorUseCase() {
        SearchPackageStructuresByCursorUseCase mock =
                mock(SearchPackageStructuresByCursorUseCase.class);
        when(mock.execute(any())).thenReturn(PackageStructureSliceResult.empty());
        return mock;
    }

    // ========================================
    // ResourceTemplate UseCases
    // ========================================
    @Bean
    @Primary
    public CreateResourceTemplateUseCase createResourceTemplateUseCase() {
        CreateResourceTemplateUseCase mock = mock(CreateResourceTemplateUseCase.class);
        when(mock.execute(any())).thenReturn(1L);
        return mock;
    }

    @Bean
    @Primary
    public UpdateResourceTemplateUseCase updateResourceTemplateUseCase() {
        return mock(UpdateResourceTemplateUseCase.class);
    }

    @Bean
    @Primary
    public SearchResourceTemplatesByCursorUseCase searchResourceTemplatesByCursorUseCase() {
        SearchResourceTemplatesByCursorUseCase mock =
                mock(SearchResourceTemplatesByCursorUseCase.class);
        when(mock.execute(any())).thenReturn(ResourceTemplateSliceResult.empty());
        return mock;
    }

    // ========================================
    // RuleExample UseCases
    // ========================================
    @Bean
    @Primary
    public CreateRuleExampleUseCase createRuleExampleUseCase() {
        CreateRuleExampleUseCase mock = mock(CreateRuleExampleUseCase.class);
        when(mock.execute(any())).thenReturn(1L);
        return mock;
    }

    @Bean
    @Primary
    public UpdateRuleExampleUseCase updateRuleExampleUseCase() {
        return mock(UpdateRuleExampleUseCase.class);
    }

    @Bean
    @Primary
    public SearchRuleExamplesByCursorUseCase searchRuleExamplesByCursorUseCase() {
        SearchRuleExamplesByCursorUseCase mock = mock(SearchRuleExamplesByCursorUseCase.class);
        when(mock.execute(any())).thenReturn(RuleExampleSliceResult.empty());
        return mock;
    }

    // ========================================
    // TechStack UseCases
    // ========================================
    @Bean
    @Primary
    public CreateTechStackUseCase createTechStackUseCase() {
        CreateTechStackUseCase mock = mock(CreateTechStackUseCase.class);
        when(mock.execute(any())).thenReturn(1L);
        return mock;
    }

    @Bean
    @Primary
    public UpdateTechStackUseCase updateTechStackUseCase() {
        return mock(UpdateTechStackUseCase.class);
    }

    @Bean
    @Primary
    public SearchTechStacksByCursorUseCase searchTechStacksByCursorUseCase() {
        SearchTechStacksByCursorUseCase mock = mock(SearchTechStacksByCursorUseCase.class);
        when(mock.execute(any())).thenReturn(TechStackSliceResult.empty(0));
        return mock;
    }

    // ========================================
    // ZeroTolerance UseCases
    // ========================================
    @Bean
    @Primary
    public CreateZeroToleranceRuleUseCase createZeroToleranceRuleUseCase() {
        CreateZeroToleranceRuleUseCase mock = mock(CreateZeroToleranceRuleUseCase.class);
        when(mock.execute(any())).thenReturn(1L);
        return mock;
    }

    @Bean
    @Primary
    public UpdateZeroToleranceRuleUseCase updateZeroToleranceRuleUseCase() {
        return mock(UpdateZeroToleranceRuleUseCase.class);
    }

    @Bean
    @Primary
    public SearchZeroToleranceRulesByCursorUseCase searchZeroToleranceRulesByCursorUseCase() {
        SearchZeroToleranceRulesByCursorUseCase mock =
                mock(SearchZeroToleranceRulesByCursorUseCase.class);
        when(mock.execute(any())).thenReturn(ZeroToleranceRuleSliceResult.empty());
        return mock;
    }

    // ========================================
    // FeedbackQueue UseCases
    // ========================================
    @Bean
    @Primary
    public CreateFeedbackUseCase createFeedbackUseCase() {
        CreateFeedbackUseCase mock = mock(CreateFeedbackUseCase.class);
        when(mock.execute(any())).thenReturn(1L);
        return mock;
    }

    @Bean
    @Primary
    public SearchFeedbacksByCursorUseCase searchFeedbacksByCursorUseCase() {
        SearchFeedbacksByCursorUseCase mock = mock(SearchFeedbacksByCursorUseCase.class);
        when(mock.execute(any())).thenReturn(FeedbackQueueSliceResult.empty());
        return mock;
    }

    @Bean
    @Primary
    public GetPendingFeedbacksUseCase getPendingFeedbacksUseCase() {
        GetPendingFeedbacksUseCase mock = mock(GetPendingFeedbacksUseCase.class);
        when(mock.execute(any())).thenReturn(FeedbackQueueSliceResult.empty());
        return mock;
    }

    @Bean
    @Primary
    public GetAwaitingHumanReviewUseCase getAwaitingHumanReviewUseCase() {
        GetAwaitingHumanReviewUseCase mock = mock(GetAwaitingHumanReviewUseCase.class);
        when(mock.execute(any())).thenReturn(FeedbackQueueSliceResult.empty());
        return mock;
    }

    @Bean
    @Primary
    public ProcessFeedbackUseCase processFeedbackUseCase() {
        ProcessFeedbackUseCase mock = mock(ProcessFeedbackUseCase.class);
        when(mock.execute(any()))
                .thenReturn(
                        new FeedbackQueueResult(
                                1L,
                                "CODING_RULE",
                                1L,
                                "CREATE",
                                "LOW",
                                "{\"code\":\"AGG-001\"}",
                                "LLM_APPROVED",
                                null,
                                Instant.now(),
                                Instant.now()));
        return mock;
    }

    @Bean
    @Primary
    public MergeFeedbackUseCase mergeFeedbackUseCase() {
        MergeFeedbackUseCase mock = mock(MergeFeedbackUseCase.class);
        when(mock.execute(any()))
                .thenReturn(
                        new FeedbackQueueResult(
                                1L,
                                "CODING_RULE",
                                1L,
                                "CREATE",
                                "LOW",
                                "{\"code\":\"AGG-001\"}",
                                "MERGED",
                                null,
                                Instant.now(),
                                Instant.now()));
        return mock;
    }

    // ========================================
    // ConfigFileTemplate UseCases
    // ========================================
    @Bean
    @Primary
    public com.ryuqq.application.configfiletemplate.port.in.CreateConfigFileTemplateUseCase
            createConfigFileTemplateUseCase() {
        com.ryuqq.application.configfiletemplate.port.in.CreateConfigFileTemplateUseCase mock =
                mock(
                        com.ryuqq.application.configfiletemplate.port.in
                                .CreateConfigFileTemplateUseCase.class);
        when(mock.execute(any())).thenReturn(1L);
        return mock;
    }

    @Bean
    @Primary
    public com.ryuqq.application.configfiletemplate.port.in.UpdateConfigFileTemplateUseCase
            updateConfigFileTemplateUseCase() {
        return mock(
                com.ryuqq.application.configfiletemplate.port.in.UpdateConfigFileTemplateUseCase
                        .class);
    }

    @Bean
    @Primary
    public com.ryuqq.application.configfiletemplate.port.in.SearchConfigFileTemplatesByCursorUseCase
            searchConfigFileTemplatesByCursorUseCase() {
        com.ryuqq.application.configfiletemplate.port.in.SearchConfigFileTemplatesByCursorUseCase
                mock =
                        mock(
                                com.ryuqq.application.configfiletemplate.port.in
                                        .SearchConfigFileTemplatesByCursorUseCase.class);
        when(mock.execute(any()))
                .thenReturn(
                        com.ryuqq.application.configfiletemplate.dto.response
                                .ConfigFileTemplateSliceResult.empty(20));
        return mock;
    }

    // ========================================
    // MCP ConfigFiles & Onboarding UseCases
    // ========================================
    @Bean
    @Primary
    public com.ryuqq.application.mcp.port.in.GetConfigFilesForMcpUseCase
            getConfigFilesForMcpUseCase() {
        com.ryuqq.application.mcp.port.in.GetConfigFilesForMcpUseCase mock =
                mock(com.ryuqq.application.mcp.port.in.GetConfigFilesForMcpUseCase.class);
        when(mock.execute(any()))
                .thenReturn(
                        new com.ryuqq.application.mcp.dto.response.ConfigFilesResult(List.of(), 0));
        return mock;
    }

    @Bean
    @Primary
    public com.ryuqq.application.mcp.port.in.GetOnboardingForMcpUseCase
            getOnboardingForMcpUseCase() {
        com.ryuqq.application.mcp.port.in.GetOnboardingForMcpUseCase mock =
                mock(com.ryuqq.application.mcp.port.in.GetOnboardingForMcpUseCase.class);
        when(mock.execute(any()))
                .thenReturn(
                        new com.ryuqq.application.mcp.dto.response.OnboardingContextsResult(
                                List.of(), 0));
        return mock;
    }

    // ========================================
    // OnboardingContext UseCases
    // ========================================
    @Bean
    @Primary
    public com.ryuqq.application.onboardingcontext.port.in.CreateOnboardingContextUseCase
            createOnboardingContextUseCase() {
        com.ryuqq.application.onboardingcontext.port.in.CreateOnboardingContextUseCase mock =
                mock(
                        com.ryuqq.application.onboardingcontext.port.in
                                .CreateOnboardingContextUseCase.class);
        when(mock.execute(any())).thenReturn(1L);
        return mock;
    }

    @Bean
    @Primary
    public com.ryuqq.application.onboardingcontext.port.in.UpdateOnboardingContextUseCase
            updateOnboardingContextUseCase() {
        return mock(
                com.ryuqq.application.onboardingcontext.port.in.UpdateOnboardingContextUseCase
                        .class);
    }

    @Bean
    @Primary
    public com.ryuqq.application.onboardingcontext.port.in.SearchOnboardingContextsByCursorUseCase
            searchOnboardingContextsByCursorUseCase() {
        com.ryuqq.application.onboardingcontext.port.in.SearchOnboardingContextsByCursorUseCase
                mock =
                        mock(
                                com.ryuqq.application.onboardingcontext.port.in
                                        .SearchOnboardingContextsByCursorUseCase.class);
        when(mock.execute(any()))
                .thenReturn(
                        com.ryuqq.application.onboardingcontext.dto.response
                                .OnboardingContextSliceResult.empty(20));
        return mock;
    }
}
