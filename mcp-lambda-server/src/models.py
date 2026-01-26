"""
MCP Server Response Models

Pydantic 모델을 사용한 Spring API 응답 매핑
"""

from enum import Enum
from typing import Generic, Optional, TypeVar

from pydantic import BaseModel, ConfigDict, Field


# ============================================
# Enums
# ============================================


class Layer(str, Enum):
    """레이어 타입"""

    DOMAIN = "DOMAIN"
    APPLICATION = "APPLICATION"
    PERSISTENCE = "PERSISTENCE"
    REST_API = "REST_API"
    SCHEDULER = "SCHEDULER"
    COMMON = "COMMON"
    TESTING = "TESTING"


class RuleSeverity(str, Enum):
    """규칙 심각도"""

    BLOCKER = "BLOCKER"
    CRITICAL = "CRITICAL"
    MAJOR = "MAJOR"
    MINOR = "MINOR"
    INFO = "INFO"


class RuleCategory(str, Enum):
    """규칙 카테고리"""

    NAMING = "NAMING"
    STRUCTURE = "STRUCTURE"
    DEPENDENCY = "DEPENDENCY"
    BEHAVIOR = "BEHAVIOR"
    ANNOTATION = "ANNOTATION"
    SECURITY = "SECURITY"
    PERFORMANCE = "PERFORMANCE"


class ClassType(str, Enum):
    """클래스 타입"""

    AGGREGATE = "AGGREGATE"
    VALUE_OBJECT = "VALUE_OBJECT"
    DOMAIN_EVENT = "DOMAIN_EVENT"
    DOMAIN_EXCEPTION = "DOMAIN_EXCEPTION"
    USE_CASE = "USE_CASE"
    COMMAND_SERVICE = "COMMAND_SERVICE"
    QUERY_SERVICE = "QUERY_SERVICE"
    PORT_IN = "PORT_IN"
    PORT_OUT = "PORT_OUT"
    ENTITY = "ENTITY"
    JPA_REPOSITORY = "JPA_REPOSITORY"
    ADAPTER = "ADAPTER"
    CONTROLLER = "CONTROLLER"
    REQUEST_DTO = "REQUEST_DTO"
    RESPONSE_DTO = "RESPONSE_DTO"
    MAPPER = "MAPPER"
    ASSEMBLER = "ASSEMBLER"
    FACTORY = "FACTORY"


class IntentType(str, Enum):
    """사용자 요청 의도 타입 (AI 코드 생성 가드레일용)"""

    # Domain Layer Intents
    CREATE_AGGREGATE = "CREATE_AGGREGATE"
    CREATE_VALUE_OBJECT = "CREATE_VALUE_OBJECT"
    CREATE_DOMAIN_EVENT = "CREATE_DOMAIN_EVENT"
    CREATE_DOMAIN_EXCEPTION = "CREATE_DOMAIN_EXCEPTION"

    # Application Layer Intents
    CREATE_USE_CASE = "CREATE_USE_CASE"
    CREATE_COMMAND_SERVICE = "CREATE_COMMAND_SERVICE"
    CREATE_QUERY_SERVICE = "CREATE_QUERY_SERVICE"
    CREATE_PORT = "CREATE_PORT"

    # Persistence Layer Intents
    CREATE_ENTITY = "CREATE_ENTITY"
    CREATE_REPOSITORY = "CREATE_REPOSITORY"
    CREATE_PERSISTENCE_ADAPTER = "CREATE_PERSISTENCE_ADAPTER"

    # REST API Layer Intents
    CREATE_CONTROLLER = "CREATE_CONTROLLER"
    CREATE_REQUEST_DTO = "CREATE_REQUEST_DTO"
    CREATE_RESPONSE_DTO = "CREATE_RESPONSE_DTO"

    # Modification Intents
    ADD_METHOD = "ADD_METHOD"
    MODIFY_LOGIC = "MODIFY_LOGIC"
    REFACTOR_CODE = "REFACTOR_CODE"

    # Other Intents
    ANALYZE_CODE = "ANALYZE_CODE"
    EXPLAIN_CODE = "EXPLAIN_CODE"
    UNKNOWN = "UNKNOWN"


# ============================================
# Generic API Response Wrapper
# ============================================

T = TypeVar("T")


class ApiResponse(BaseModel, Generic[T]):
    """Spring API 공통 응답 래퍼"""

    model_config = ConfigDict(populate_by_name=True)

    data: T
    timestamp: str
    request_id: Optional[str] = Field(None, alias="requestId")


class SliceResponse(BaseModel, Generic[T]):
    """Spring API 페이징 응답"""

    model_config = ConfigDict(populate_by_name=True)

    content: list[T]
    size: int
    has_next: bool = Field(alias="hasNext")
    next_cursor: Optional[str] = Field(None, alias="nextCursor")


# ============================================
# Convention Models (Spring API 응답)
# ============================================


class ConventionApiResponse(BaseModel):
    """Convention API 응답 모델

    백엔드 ConventionApiResponse.java와 동일한 필드:
    - id, moduleId, version, description, active, createdAt, updatedAt
    """

    model_config = ConfigDict(populate_by_name=True)

    id: int
    module_id: int = Field(alias="moduleId")
    version: str
    description: str
    active: bool
    created_at: Optional[str] = Field(None, alias="createdAt")
    updated_at: Optional[str] = Field(None, alias="updatedAt")


# ============================================
# CodingRule Models (Spring API 응답)
# ============================================


class CodingRuleApiResponse(BaseModel):
    """CodingRule API 응답 모델"""

    model_config = ConfigDict(populate_by_name=True)

    coding_rule_id: int = Field(alias="codingRuleId")
    convention_id: int = Field(alias="conventionId")
    structure_id: Optional[int] = Field(None, alias="structureId")
    code: str
    name: str
    severity: str
    category: str
    description: str
    rationale: str
    auto_fixable: bool = Field(alias="autoFixable")
    applies_to: Optional[list[str]] = Field(None, alias="appliesTo")
    sdk_constraint: Optional[str] = Field(None, alias="sdkConstraint")
    created_at: Optional[str] = Field(None, alias="createdAt")
    updated_at: Optional[str] = Field(None, alias="updatedAt")


class RuleExampleApiResponse(BaseModel):
    """RuleExample API 응답 모델"""

    model_config = ConfigDict(populate_by_name=True)

    id: int
    coding_rule_id: int = Field(alias="codingRuleId")
    example_type: str = Field(alias="exampleType")
    title: Optional[str] = None
    code: str
    language: str
    explanation: str
    highlight_lines: Optional[str] = Field(None, alias="highlightLines")


class CodingRuleWithExamplesApiResponse(BaseModel):
    """CodingRule + Examples API 응답 모델"""

    model_config = ConfigDict(populate_by_name=True)

    coding_rule_id: int = Field(alias="codingRuleId")
    convention_id: int = Field(alias="conventionId")
    code: str
    name: str
    severity: str
    category: str
    description: str
    rationale: str
    auto_fixable: bool = Field(alias="autoFixable")
    applies_to: Optional[list[str]] = Field(None, alias="appliesTo")
    examples: list[RuleExampleApiResponse] = Field(default_factory=list)


# ============================================
# ZeroToleranceRule Models (Spring API 응답)
# ============================================


class ZeroToleranceRuleDetailApiResponse(BaseModel):
    """ZeroToleranceRule 상세 API 응답 모델"""

    model_config = ConfigDict(populate_by_name=True)

    id: int
    code: str
    name: str
    severity: str
    category: str
    description: str
    rationale: str
    auto_fixable: bool = Field(alias="autoFixable")
    applies_to: Optional[list[str]] = Field(None, alias="appliesTo")
    examples: list[RuleExampleApiResponse] = Field(default_factory=list)
    checklist_items: list = Field(default_factory=list, alias="checklistItems")
    created_at: Optional[str] = Field(None, alias="createdAt")
    updated_at: Optional[str] = Field(None, alias="updatedAt")


class ZeroToleranceRuleSliceApiResponse(BaseModel):
    """ZeroToleranceRule 슬라이스 API 응답 모델"""

    model_config = ConfigDict(populate_by_name=True)

    rules: list[ZeroToleranceRuleDetailApiResponse]
    has_next: bool = Field(alias="hasNext")
    next_cursor_id: Optional[int] = Field(None, alias="nextCursorId")


# ============================================
# ClassTemplate Models (Spring API 응답)
# ============================================


class ClassTemplateApiResponse(BaseModel):
    """ClassTemplate API 응답 모델"""

    model_config = ConfigDict(populate_by_name=True)

    id: int = Field(alias="classTemplateId")
    structure_id: int = Field(alias="structureId")
    class_type: str = Field(alias="classType")
    template_code: str = Field(alias="templateCode")
    naming_pattern: Optional[str] = Field(None, alias="namingPattern")
    required_annotations: Optional[list[str]] = Field(None, alias="requiredAnnotations")
    forbidden_annotations: Optional[list[str]] = Field(
        None, alias="forbiddenAnnotations"
    )
    required_interfaces: Optional[list[str]] = Field(None, alias="requiredInterfaces")
    forbidden_inheritance: Optional[list[str]] = Field(
        None, alias="forbiddenInheritance"
    )
    required_methods: Optional[list[str]] = Field(None, alias="requiredMethods")
    description: Optional[str] = None
    created_at: Optional[str] = Field(None, alias="createdAt")
    updated_at: Optional[str] = Field(None, alias="updatedAt")


# ============================================
# LayerDependencyRule Models (Spring API 응답)
# ============================================


class LayerDependencyRuleApiResponse(BaseModel):
    """LayerDependencyRule API 응답 모델"""

    model_config = ConfigDict(populate_by_name=True)

    layer_dependency_rule_id: int = Field(alias="layerDependencyRuleId")
    architecture_id: int = Field(alias="architectureId")
    source_layer: str = Field(alias="fromLayer")
    target_layer: str = Field(alias="toLayer")
    dependency_type: str = Field(alias="dependencyType")
    rationale: Optional[str] = Field(None, alias="conditionDescription")
    created_at: Optional[str] = Field(None, alias="createdAt")
    updated_at: Optional[str] = Field(None, alias="updatedAt")

    @property
    def allowed(self) -> bool:
        """ALLOWED이면 True, 그 외(FORBIDDEN, CONDITIONAL)는 False"""
        return self.dependency_type == "ALLOWED"

    @property
    def direction(self) -> str:
        """의존성 방향"""
        return f"{self.source_layer} → {self.target_layer}"


# ============================================
# MCP Aggregated Response Models (FastMCP 최적화)
# ============================================


class ConventionTreeCodingRule(BaseModel):
    """Convention Tree 내 간소화된 CodingRule

    백엔드 CodingRuleSummaryApiResponse.java와 동일한 필드:
    - id, code, title, severity, zeroTolerance
    """

    model_config = ConfigDict(populate_by_name=True)

    id: int
    code: str
    title: Optional[str] = None
    severity: Optional[str] = None
    zero_tolerance: bool = Field(False, alias="zeroTolerance")


class ConventionTreeClassTemplate(BaseModel):
    """Convention Tree 내 간소화된 ClassTemplate

    백엔드 ClassTemplateSummaryApiResponse.java와 동일한 필드:
    - id, type, name, description
    """

    model_config = ConfigDict(populate_by_name=True)

    id: int
    type: Optional[str] = None
    name: Optional[str] = None
    description: Optional[str] = None


class ConventionTreeApiResponse(BaseModel):
    """Convention Tree API 응답 모델 (최적화된 전체 트리)"""

    model_config = ConfigDict(populate_by_name=True)

    id: int
    name: str  # version
    layer: str
    description: Optional[str] = None
    coding_rules: list[ConventionTreeCodingRule] = Field(
        default_factory=list, alias="codingRules"
    )
    class_templates: list[ConventionTreeClassTemplate] = Field(
        default_factory=list, alias="classTemplates"
    )


class SearchResultCodingRule(BaseModel):
    """Search 결과 내 CodingRule"""

    model_config = ConfigDict(populate_by_name=True)

    id: int
    code: str
    title: Optional[str] = None
    matched_field: Optional[str] = Field(None, alias="matchedField")


class SearchResultClassTemplate(BaseModel):
    """Search 결과 내 ClassTemplate"""

    model_config = ConfigDict(populate_by_name=True)

    id: int
    type: Optional[str] = None
    name: Optional[str] = None
    matched_field: Optional[str] = Field(None, alias="matchedField")


class SearchResultModule(BaseModel):
    """Search 결과 내 Module"""

    model_config = ConfigDict(populate_by_name=True)

    id: int
    name: Optional[str] = None
    matched_field: Optional[str] = Field(None, alias="matchedField")


class SearchResults(BaseModel):
    """Search API의 results 객체"""

    model_config = ConfigDict(populate_by_name=True)

    coding_rules: list[SearchResultCodingRule] = Field(
        default_factory=list, alias="codingRules"
    )
    class_templates: list[SearchResultClassTemplate] = Field(
        default_factory=list, alias="classTemplates"
    )
    modules: list[SearchResultModule] = Field(default_factory=list)


class McpSearchResultApiResponse(BaseModel):
    """MCP 통합 검색 결과 응답 모델"""

    model_config = ConfigDict(populate_by_name=True)

    query: str
    convention_id: Optional[int] = Field(None, alias="conventionId")
    results: SearchResults
    total_count: int = Field(0, alias="totalCount")


# ============================================
# MCP Tool Output Models (간소화된 출력)
# ============================================


class SimpleCodingRule(BaseModel):
    """MCP Tool 출력용 간소화된 코딩 규칙"""

    code: str
    name: str
    severity: str
    category: str
    description: str
    rationale: str


class SimpleClassTemplate(BaseModel):
    """MCP Tool 출력용 간소화된 클래스 템플릿"""

    class_type: str
    template_code: str
    naming_pattern: Optional[str] = None
    description: Optional[str] = None


class SimpleLayerDependency(BaseModel):
    """MCP Tool 출력용 간소화된 레이어 의존성"""

    source_layer: str
    target_layer: str
    allowed: bool
    direction: str
    rationale: Optional[str] = None


# ============================================
# RuleExample CRUD Models (Spring API 응답)
# ============================================


class RuleExampleCrudApiResponse(BaseModel):
    """RuleExample CRUD API 응답 모델 (독립 API용)"""

    model_config = ConfigDict(populate_by_name=True)

    rule_example_id: int = Field(alias="ruleExampleId")
    rule_id: int = Field(alias="ruleId")
    example_type: str = Field(alias="exampleType")
    code: str
    language: str
    explanation: Optional[str] = None
    highlight_lines: list[int] = Field(default_factory=list, alias="highlightLines")
    source: str
    feedback_id: Optional[int] = Field(None, alias="feedbackId")
    created_at: Optional[str] = Field(None, alias="createdAt")
    updated_at: Optional[str] = Field(None, alias="updatedAt")


class RuleExampleIdApiResponse(BaseModel):
    """RuleExample ID 응답 모델"""

    model_config = ConfigDict(populate_by_name=True)

    rule_example_id: int = Field(alias="ruleExampleId")


# ============================================
# PackageStructure Models (Spring API 응답)
# ============================================


class PackageStructureApiResponse(BaseModel):
    """PackageStructure API 응답 모델"""

    model_config = ConfigDict(populate_by_name=True)

    package_structure_id: int = Field(alias="packageStructureId")
    module_id: int = Field(alias="moduleId")
    purpose_id: Optional[int] = Field(None, alias="purposeId")
    path_pattern: str = Field(alias="pathPattern")
    allowed_class_types: list[str] = Field(
        default_factory=list, alias="allowedClassTypes"
    )
    naming_pattern: Optional[str] = Field(None, alias="namingPattern")
    naming_suffix: Optional[str] = Field(None, alias="namingSuffix")
    description: Optional[str] = None
    created_at: Optional[str] = Field(None, alias="createdAt")
    updated_at: Optional[str] = Field(None, alias="updatedAt")


# ============================================
# Context Engine Models (AI Code Generation Guardrails)
# ============================================


class IntentResult(BaseModel):
    """의도 분류 결과 모델"""

    intent_type: str = Field(description="분류된 의도 타입")
    target_layer: Optional[str] = Field(
        None, description="대상 레이어 (DOMAIN, APPLICATION, PERSISTENCE, REST_API)"
    )
    class_type: Optional[str] = Field(None, description="생성할 클래스 타입")
    confidence: float = Field(description="분류 신뢰도 (0.0 ~ 1.0)")
    keywords_matched: list[str] = Field(
        default_factory=list, description="매칭된 키워드 목록"
    )
    reasoning: str = Field(description="분류 근거 설명")


# ============================================
# TechStack Models (Spring API 응답)
# ============================================


class TechStackApiResponse(BaseModel):
    """TechStack API 응답 모델"""

    model_config = ConfigDict(populate_by_name=True)

    id: int
    name: str
    status: str
    language_type: str = Field(alias="languageType")
    language_version: str = Field(alias="languageVersion")
    language_features: list[str] = Field(default_factory=list, alias="languageFeatures")
    framework_type: str = Field(alias="frameworkType")
    framework_version: str = Field(alias="frameworkVersion")
    framework_modules: list[str] = Field(default_factory=list, alias="frameworkModules")
    platform_type: str = Field(alias="platformType")
    runtime_environment: str = Field(alias="runtimeEnvironment")
    build_tool_type: str = Field(alias="buildToolType")
    build_config_file: str = Field(alias="buildConfigFile")
    created_at: Optional[str] = Field(None, alias="createdAt")
    updated_at: Optional[str] = Field(None, alias="updatedAt")


# ============================================
# Architecture Models (Spring API 응답)
# ============================================


class ArchitectureApiResponse(BaseModel):
    """Architecture API 응답 모델"""

    model_config = ConfigDict(populate_by_name=True)

    id: int
    tech_stack_id: int = Field(alias="techStackId")
    name: str
    pattern_type: str = Field(alias="patternType")
    pattern_description: Optional[str] = Field(None, alias="patternDescription")
    pattern_principles: list[str] = Field(default_factory=list, alias="patternPrinciples")
    created_at: Optional[str] = Field(None, alias="createdAt")
    updated_at: Optional[str] = Field(None, alias="updatedAt")


# ============================================
# Layer Models (Spring API 응답)
# ============================================


class LayerApiResponse(BaseModel):
    """Layer API 응답 모델"""

    model_config = ConfigDict(populate_by_name=True)

    id: int
    architecture_id: int = Field(alias="architectureId")
    code: str
    name: str
    description: Optional[str] = None
    order_index: int = Field(alias="orderIndex")
    created_at: Optional[str] = Field(None, alias="createdAt")
    updated_at: Optional[str] = Field(None, alias="updatedAt")


# ============================================
# Module Models (Spring API 응답)
# ============================================


class ModuleApiResponse(BaseModel):
    """Module API 응답 모델"""

    model_config = ConfigDict(populate_by_name=True)

    module_id: int = Field(alias="moduleId")
    layer_id: int = Field(alias="layerId")
    parent_module_id: Optional[int] = Field(None, alias="parentModuleId")
    name: str
    description: Optional[str] = None
    module_path: str = Field(alias="modulePath")
    build_identifier: str = Field(alias="buildIdentifier")
    created_at: Optional[str] = Field(None, alias="createdAt")
    updated_at: Optional[str] = Field(None, alias="updatedAt")


# ============================================
# PackagePurpose Models (Spring API 응답)
# ============================================


class PackagePurposeApiResponse(BaseModel):
    """PackagePurpose API 응답 모델"""

    model_config = ConfigDict(populate_by_name=True)

    package_purpose_id: int = Field(alias="packagePurposeId")
    layer_id: Optional[int] = Field(None, alias="layerId")
    code: str
    name: str
    description: Optional[str] = None
    default_allowed_class_types: list[str] = Field(
        default_factory=list, alias="defaultAllowedClassTypes"
    )
    default_naming_pattern: Optional[str] = Field(None, alias="defaultNamingPattern")
    default_naming_suffix: Optional[str] = Field(None, alias="defaultNamingSuffix")
    created_at: Optional[str] = Field(None, alias="createdAt")
    updated_at: Optional[str] = Field(None, alias="updatedAt")


# ============================================
# ArchUnitTest Models (Spring API 응답)
# ============================================


class ArchUnitTestApiResponse(BaseModel):
    """ArchUnitTest API 응답 모델"""

    model_config = ConfigDict(populate_by_name=True)

    arch_unit_test_id: int = Field(alias="archUnitTestId")
    structure_id: int = Field(alias="structureId")
    code: str
    name: str
    description: Optional[str] = None
    test_class_name: Optional[str] = Field(None, alias="testClassName")
    test_method_name: Optional[str] = Field(None, alias="testMethodName")
    test_code: str = Field(alias="testCode")
    severity: Optional[str] = None
    created_at: Optional[str] = Field(None, alias="createdAt")
    updated_at: Optional[str] = Field(None, alias="updatedAt")


# ============================================
# ResourceTemplate Models (Spring API 응답)
# ============================================


class ResourceTemplateApiResponse(BaseModel):
    """ResourceTemplate API 응답 모델"""

    model_config = ConfigDict(populate_by_name=True)

    resource_template_id: int = Field(alias="resourceTemplateId")
    module_id: int = Field(alias="moduleId")
    category: str
    file_path: str = Field(alias="filePath")
    file_type: str = Field(alias="fileType")
    description: Optional[str] = None
    template_content: Optional[str] = Field(None, alias="templateContent")
    required: bool = False
    created_at: Optional[str] = Field(None, alias="createdAt")
    updated_at: Optional[str] = Field(None, alias="updatedAt")


# ============================================
# ChecklistItem Models (Spring API 응답)
# ============================================


class ChecklistItemApiResponse(BaseModel):
    """ChecklistItem API 응답 모델"""

    model_config = ConfigDict(populate_by_name=True)

    id: int
    rule_id: int = Field(alias="ruleId")
    sequence_order: int = Field(alias="sequenceOrder")
    check_description: str = Field(alias="checkDescription")
    check_type: str = Field(alias="checkType")
    automation_tool: Optional[str] = Field(None, alias="automationTool")
    automation_rule_id: Optional[str] = Field(None, alias="automationRuleId")
    critical: bool = False
    source: str
    feedback_id: Optional[int] = Field(None, alias="feedbackId")
    created_at: Optional[str] = Field(None, alias="createdAt")
    updated_at: Optional[str] = Field(None, alias="updatedAt")


# ============================================
# FeedbackQueue Models (Spring API 응답)
# ============================================


class FeedbackTargetType(str, Enum):
    """피드백 대상 타입"""

    RULE_EXAMPLE = "RULE_EXAMPLE"
    CLASS_TEMPLATE = "CLASS_TEMPLATE"
    CODING_RULE = "CODING_RULE"
    CHECKLIST_ITEM = "CHECKLIST_ITEM"
    ARCH_UNIT_TEST = "ARCH_UNIT_TEST"


class FeedbackType(str, Enum):
    """피드백 유형 (백엔드 API 스펙에 맞춤)"""

    ADD = "ADD"  # 새로운 항목 추가
    MODIFY = "MODIFY"  # 기존 항목 수정
    DELETE = "DELETE"  # 기존 항목 삭제

    # MCP 도구에서 사용하는 별칭 (하위 호환)
    @classmethod
    def from_alias(cls, value: str) -> "FeedbackType":
        """CREATE/UPDATE 별칭을 ADD/MODIFY로 변환"""
        alias_map = {
            "CREATE": cls.ADD,
            "UPDATE": cls.MODIFY,
        }
        if value in alias_map:
            return alias_map[value]
        return cls(value)


class FeedbackRiskLevel(str, Enum):
    """피드백 위험 수준"""

    SAFE = "SAFE"  # 자동 머지 가능
    MEDIUM = "MEDIUM"  # Human 승인 필요


class FeedbackStatus(str, Enum):
    """피드백 상태"""

    PENDING = "PENDING"  # 대기 중
    LLM_APPROVED = "LLM_APPROVED"  # LLM 1차 승인
    LLM_REJECTED = "LLM_REJECTED"  # LLM 1차 거절
    HUMAN_APPROVED = "HUMAN_APPROVED"  # Human 2차 승인
    HUMAN_REJECTED = "HUMAN_REJECTED"  # Human 2차 거절
    MERGED = "MERGED"  # 실제 테이블에 반영 완료


class FeedbackQueueApiResponse(BaseModel):
    """FeedbackQueue API 응답 모델"""

    model_config = ConfigDict(populate_by_name=True)

    feedback_queue_id: int = Field(alias="feedbackQueueId")
    target_type: str = Field(alias="targetType")
    target_id: Optional[int] = Field(None, alias="targetId")
    feedback_type: str = Field(alias="feedbackType")
    risk_level: str = Field(alias="riskLevel")
    payload: str
    status: str
    review_notes: Optional[str] = Field(None, alias="reviewNotes")
    created_at: Optional[str] = Field(None, alias="createdAt")
    updated_at: Optional[str] = Field(None, alias="updatedAt")


class FeedbackQueueIdApiResponse(BaseModel):
    """FeedbackQueue ID 응답 모델"""

    model_config = ConfigDict(populate_by_name=True)

    feedback_queue_id: int = Field(alias="feedbackQueueId")
