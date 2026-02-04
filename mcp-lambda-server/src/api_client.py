"""
Spring REST API Client

httpx를 사용한 Spring REST API 호출 클라이언트
"""

from typing import Any, Optional

import httpx

from .config import get_api_config
from .models import (
    ArchitectureApiResponse,
    ArchUnitTestApiResponse,
    ChecklistItemApiResponse,
    ClassTemplateApiResponse,
    CodingRuleApiResponse,
    CodingRuleWithExamplesApiResponse,
    ConventionApiResponse,
    ConventionTreeApiResponse,
    FeedbackQueueApiResponse,
    FeedbackQueueIdApiResponse,
    LayerApiResponse,
    LayerDependencyRuleApiResponse,
    McpSearchResultApiResponse,
    ModuleApiResponse,
    PackagePurposeApiResponse,
    PackageStructureApiResponse,
    ResourceTemplateApiResponse,
    RuleExampleCrudApiResponse,
    RuleExampleIdApiResponse,
    TechStackApiResponse,
    ZeroToleranceRuleDetailApiResponse,
    ZeroToleranceRuleSliceApiResponse,
)


class ConventionApiClient:
    """Spring REST API 클라이언트"""

    def __init__(self) -> None:
        self._config = get_api_config()
        self._client: Optional[httpx.Client] = None

    def _get_client(self) -> httpx.Client:
        """HTTP 클라이언트 반환 (lazy initialization)"""
        if self._client is None:
            self._client = httpx.Client(
                base_url=self._config.base_url,
                timeout=self._config.timeout,
                verify=False,  # VPC 내부 통신이므로 SSL 검증 비활성화
            )
        return self._client

    def close(self) -> None:
        """클라이언트 연결 종료"""
        if self._client is not None:
            self._client.close()
            self._client = None

    def _get(self, path: str, params: Optional[dict] = None) -> dict[str, Any]:
        """GET 요청"""
        response = self._get_client().get(path, params=params)
        response.raise_for_status()
        return response.json()

    def _post(self, path: str, json_data: dict[str, Any]) -> dict[str, Any]:
        """POST 요청"""
        response = self._get_client().post(path, json=json_data)
        response.raise_for_status()
        return response.json()

    def _put(self, path: str, json_data: dict[str, Any]) -> dict[str, Any]:
        """PUT 요청"""
        response = self._get_client().put(path, json=json_data)
        response.raise_for_status()
        return response.json()

    def _patch(self, path: str, json_data: Optional[dict] = None) -> dict[str, Any]:
        """PATCH 요청"""
        response = self._get_client().patch(path, json=json_data or {})
        response.raise_for_status()
        return response.json()

    def _extract_data(self, response: dict[str, Any]) -> Any:
        """API 응답에서 data 필드 추출"""
        return response.get("data", response)

    # ============================================
    # Convention API
    # ============================================

    def get_conventions(
        self,
        module_ids: Optional[list[int]] = None,
        limit: int = 100,
        cursor: Optional[str] = None,
    ) -> list[ConventionApiResponse]:
        """컨벤션 목록 조회

        Args:
            module_ids: 모듈 ID 필터 (백엔드는 moduleIds만 지원, layer 파라미터 지원 안함)
            limit: 슬라이스 크기 (최대 100)
            cursor: 커서 값 (마지막 항목의 ID)

        Note:
            - 레이어별 조회가 필요하면 get_convention_by_layer()를 사용하세요.
            - 백엔드 API는 moduleIds 파라미터만 지원합니다.
        """
        params: dict[str, Any] = {"size": limit}
        if module_ids:
            params["moduleIds"] = ",".join(str(mid) for mid in module_ids)
        if cursor:
            params["cursor"] = cursor

        response = self._get("/api/v1/templates/conventions", params)
        data = self._extract_data(response)

        # SliceResponse 형태
        content = data.get("content", [])
        return [ConventionApiResponse(**item) for item in content]

    def get_convention_by_layer(self, layer: str) -> Optional[ConventionApiResponse]:
        """레이어별 컨벤션 조회 (첫 번째 결과)

        Args:
            layer: 레이어 코드 (DOMAIN, APPLICATION, ADAPTER_OUT, ADAPTER_IN)

        Returns:
            해당 레이어의 첫 번째 컨벤션 (없으면 None)

        Note:
            layer_code → layer_id → modules → module_ids → conventions 순서로 조회
        """
        # 1. 레이어 코드로 레이어 조회
        layer_info = self.get_layer_by_code(layer.upper())
        if not layer_info:
            return None

        # 2. 해당 레이어의 모듈 조회
        modules = self.get_modules(layer_id=layer_info.id, limit=100)
        if not modules:
            return None

        # 3. 모듈 ID로 컨벤션 조회
        module_ids = [m.id for m in modules]
        conventions = self.get_conventions(module_ids=module_ids, limit=1)
        return conventions[0] if conventions else None

    # ============================================
    # CodingRule API
    # ============================================

    def get_coding_rules(
        self,
        severities: Optional[list[str]] = None,
        categories: Optional[list[str]] = None,
        search_field: Optional[str] = None,
        search_word: Optional[str] = None,
        limit: int = 100,
        cursor: Optional[str] = None,
    ) -> list[CodingRuleApiResponse]:
        """코딩 규칙 목록 조회

        Args:
            severities: 심각도 필터 목록 (BLOCKER, CRITICAL, MAJOR, MINOR, INFO)
            categories: 카테고리 필터 목록
            search_field: 검색 필드 (CODE, NAME, DESCRIPTION)
            search_word: 검색어 (부분 일치)
            limit: 슬라이스 크기 (최대 100)
            cursor: 커서 값 (마지막 항목의 ID)
        """
        params: dict[str, Any] = {"size": limit}
        if severities:
            params["severities"] = severities
        if categories:
            params["categories"] = categories
        if search_field:
            params["searchField"] = search_field
        if search_word:
            params["searchWord"] = search_word
        if cursor:
            params["cursor"] = cursor

        response = self._get("/api/v1/templates/coding-rules", params)
        data = self._extract_data(response)

        content = data.get("content", [])
        return [CodingRuleApiResponse(**item) for item in content]

    def get_coding_rules_by_layer(self, layer: str) -> list[CodingRuleApiResponse]:
        """레이어별 코딩 규칙 조회 (convention-tree 사용)

        Args:
            layer: 레이어 코드 (DOMAIN, APPLICATION, ADAPTER_OUT, ADAPTER_IN)

        Returns:
            해당 레이어의 코딩 규칙 목록

        Note:
            get_layer_convention_complete()를 사용하여 convention-tree API를 호출합니다.
        """
        tree = self.get_layer_convention_complete(layer)
        if not tree:
            return []
        return tree.codingRules

    def get_convention_tree_by_layer(
        self, layer: str
    ) -> Optional[ConventionTreeApiResponse]:
        """레이어별 컨벤션 트리 조회 (get_layer_convention_complete의 별칭)

        Args:
            layer: 레이어 코드 (DOMAIN, APPLICATION, ADAPTER_OUT, ADAPTER_IN)

        Returns:
            컨벤션 트리 (코딩 규칙, 템플릿, 체크리스트 포함)
        """
        return self.get_layer_convention_complete(layer)

    def get_zero_tolerance_rules(
        self, layer: Optional[str] = None, size: int = 100
    ) -> list[ZeroToleranceRuleDetailApiResponse]:
        """Zero-Tolerance 규칙 조회 (전용 엔드포인트 사용)

        Args:
            layer: 레이어 코드 (DOMAIN, APPLICATION, ADAPTER_OUT, ADAPTER_IN)
            size: 슬라이스 크기 (최대 100)

        Returns:
            Zero-Tolerance 규칙 목록

        Note:
            layer가 지정되면 해당 레이어의 컨벤션 ID를 조회하여 필터링합니다.
            layer_code → layer_id → modules → module_ids → convention → conventionIds
        """
        params: dict[str, Any] = {"size": size}

        # layer 필터링이 필요한 경우 conventionIds 파라미터 추가
        if layer:
            convention = self.get_convention_by_layer(layer)
            if not convention:
                return []
            # Spring은 List<Long>을 콤마로 구분된 문자열로 받음
            params["conventionIds"] = str(convention.id)

        response = self._get("/api/v1/standards/zero-tolerance-rules", params=params)
        data = self._extract_data(response)

        # 슬라이스 응답 파싱
        slice_response = ZeroToleranceRuleSliceApiResponse(**data)
        return slice_response.rules

    def get_coding_rule_by_code(self, code: str) -> Optional[CodingRuleApiResponse]:
        """규칙 코드로 코딩 규칙 조회"""
        rules = self.get_coding_rules(search_field="CODE", search_word=code, limit=10)
        # 정확히 일치하는 규칙 찾기
        for rule in rules:
            if rule.code == code:
                return rule
        return None

    def get_coding_rule_with_examples(
        self, coding_rule_id: int
    ) -> Optional[CodingRuleWithExamplesApiResponse]:
        """코딩 규칙 + 예시 조회"""
        try:
            response = self._get(f"/api/v1/templates/coding-rules/{coding_rule_id}")
            data = self._extract_data(response)
            return CodingRuleWithExamplesApiResponse(**data)
        except httpx.HTTPStatusError:
            return None

    def search_coding_rules(
        self, keyword: str, convention_id: Optional[int] = None
    ) -> McpSearchResultApiResponse:
        """키워드로 코딩 규칙 검색 (Search API 사용)"""
        return self.search(query=keyword, convention_id=convention_id)

    # ============================================
    # ClassTemplate API
    # ============================================

    def get_class_templates(
        self,
        structure_ids: Optional[list[int]] = None,
        class_types: Optional[list[str]] = None,
        limit: int = 100,
        cursor: Optional[str] = None,
    ) -> list[ClassTemplateApiResponse]:
        """클래스 템플릿 목록 조회

        Args:
            structure_ids: 패키지 구조 ID 필터 목록
            class_types: 클래스 타입 필터 목록 (AGGREGATE, VALUE_OBJECT 등)
            limit: 슬라이스 크기 (최대 100)
            cursor: 커서 값 (마지막 항목의 ID)
        """
        params: dict[str, Any] = {"size": limit}
        if structure_ids:
            params["structureIds"] = structure_ids
        if class_types:
            params["classTypes"] = class_types
        if cursor:
            params["cursor"] = cursor

        response = self._get("/api/v1/templates/class-templates", params)
        data = self._extract_data(response)

        content = data.get("content", [])
        return [ClassTemplateApiResponse(**item) for item in content]

    def get_class_template_by_type(
        self, class_type: str
    ) -> Optional[ClassTemplateApiResponse]:
        """클래스 타입으로 템플릿 조회"""
        templates = self.get_class_templates(class_types=[class_type], limit=1)
        return templates[0] if templates else None

    def get_class_templates_by_layer(
        self, layer: str
    ) -> list[ClassTemplateApiResponse]:
        """레이어별 클래스 템플릿 조회 (convention-tree 사용)"""
        tree = self.get_convention_tree_by_layer(layer)
        if not tree:
            return []
        return tree.classTemplates

    # ============================================
    # LayerDependencyRule API
    # ============================================

    def get_layer_dependencies(
        self, architecture_id: int = 1
    ) -> list[LayerDependencyRuleApiResponse]:
        """레이어 의존성 규칙 조회"""
        response = self._get(
            f"/api/v1/templates/architectures/{architecture_id}/layer-dependency-rules"
        )
        data = self._extract_data(response)

        # data가 직접 리스트인 경우
        if isinstance(data, list):
            return [LayerDependencyRuleApiResponse(**item) for item in data]
        # data가 딕셔너리인 경우 (content 필드 포함)
        content = data.get("content", [])
        return [LayerDependencyRuleApiResponse(**item) for item in content]

    # ============================================
    # MCP Aggregated API (최적화)
    # ============================================

    def get_convention_tree(
        self, convention_id: int
    ) -> Optional[ConventionTreeApiResponse]:
        """컨벤션 전체 트리 조회 (최적화 API)"""
        try:
            response = self._get(f"/api/v1/templates/convention-tree/{convention_id}")
            data = self._extract_data(response)
            return ConventionTreeApiResponse(**data)
        except httpx.HTTPStatusError:
            return None

    def get_layer_convention_complete(
        self, layer: str
    ) -> Optional[ConventionTreeApiResponse]:
        """레이어별 전체 컨벤션 정보 조회"""
        convention = self.get_convention_by_layer(layer)
        if not convention:
            return None
        return self.get_convention_tree(convention.id)

    def search(
        self,
        query: str,
        convention_id: Optional[int] = None,
    ) -> McpSearchResultApiResponse:
        """MCP 통합 검색"""
        params: dict[str, Any] = {"q": query}
        if convention_id:
            params["conventionId"] = convention_id

        response = self._get("/api/v1/templates/search", params)
        data = self._extract_data(response)
        return McpSearchResultApiResponse(**data)

    # ============================================
    # PackageStructure API
    # ============================================

    def get_package_structures(
        self,
        module_id: Optional[int] = None,
        purpose_id: Optional[int] = None,
        cursor: Optional[int] = None,
        size: int = 100,
    ) -> list[PackageStructureApiResponse]:
        """패키지 구조 목록 조회"""
        params: dict[str, Any] = {"size": size}
        if module_id:
            params["moduleId"] = module_id
        if purpose_id:
            params["purposeId"] = purpose_id
        if cursor:
            params["cursor"] = cursor

        response = self._get("/api/v1/templates/package-structures", params)
        data = self._extract_data(response)

        content = data.get("content", [])
        return [PackageStructureApiResponse(**item) for item in content]

    def get_package_structure_by_id(
        self, package_structure_id: int
    ) -> Optional[PackageStructureApiResponse]:
        """패키지 구조 단건 조회"""
        try:
            response = self._get(
                f"/api/v1/templates/package-structures/{package_structure_id}"
            )
            data = self._extract_data(response)
            return PackageStructureApiResponse(**data)
        except httpx.HTTPStatusError:
            return None

    # ============================================
    # RuleExample CRUD API
    # ============================================

    def get_rule_examples(
        self,
        rule_id: Optional[int] = None,
        example_type: Optional[str] = None,
        language: Optional[str] = None,
        cursor: Optional[int] = None,
        size: int = 20,
    ) -> list[RuleExampleCrudApiResponse]:
        """RuleExample 목록 조회"""
        params: dict[str, Any] = {"size": size}
        if rule_id:
            params["ruleId"] = rule_id
        if example_type:
            params["exampleType"] = example_type
        if language:
            params["language"] = language
        if cursor:
            params["cursor"] = cursor

        response = self._get("/api/v1/templates/rule-examples", params)
        data = self._extract_data(response)

        content = data.get("content", [])
        return [RuleExampleCrudApiResponse(**item) for item in content]

    def get_rule_example_by_id(
        self, rule_example_id: int
    ) -> Optional[RuleExampleCrudApiResponse]:
        """RuleExample 단건 조회"""
        try:
            response = self._get(f"/api/v1/templates/rule-examples/{rule_example_id}")
            data = self._extract_data(response)
            return RuleExampleCrudApiResponse(**data)
        except httpx.HTTPStatusError:
            return None

    def create_rule_example(
        self,
        rule_id: int,
        example_type: str,
        code: str,
        language: str,
        explanation: Optional[str] = None,
        highlight_lines: Optional[list[int]] = None,
    ) -> RuleExampleIdApiResponse:
        """RuleExample 생성"""
        json_data: dict[str, Any] = {
            "ruleId": rule_id,
            "exampleType": example_type,
            "code": code,
            "language": language,
        }
        if explanation:
            json_data["explanation"] = explanation
        if highlight_lines:
            json_data["highlightLines"] = highlight_lines

        response = self._post("/api/v1/templates/rule-examples", json_data)
        data = self._extract_data(response)
        return RuleExampleIdApiResponse(**data)

    def update_rule_example(
        self,
        rule_example_id: int,
        rule_id: int,
        example_type: str,
        code: str,
        language: str,
        explanation: Optional[str] = None,
        highlight_lines: Optional[list[int]] = None,
    ) -> RuleExampleIdApiResponse:
        """RuleExample 수정"""
        json_data: dict[str, Any] = {
            "ruleId": rule_id,
            "exampleType": example_type,
            "code": code,
            "language": language,
        }
        if explanation:
            json_data["explanation"] = explanation
        if highlight_lines:
            json_data["highlightLines"] = highlight_lines

        response = self._put(
            f"/api/v1/templates/rule-examples/{rule_example_id}", json_data
        )
        data = self._extract_data(response)
        return RuleExampleIdApiResponse(**data)

    def delete_rule_example(self, rule_example_id: int) -> RuleExampleIdApiResponse:
        """RuleExample 삭제 (soft delete)"""
        response = self._patch(
            f"/api/v1/templates/rule-examples/{rule_example_id}/delete"
        )
        data = self._extract_data(response)
        return RuleExampleIdApiResponse(**data)

    # ============================================
    # TechStack API
    # ============================================

    def get_tech_stacks(
        self,
        limit: int = 100,
        cursor: Optional[str] = None,
    ) -> list[TechStackApiResponse]:
        """기술 스택 목록 조회"""
        params: dict[str, Any] = {"size": limit}
        if cursor:
            params["cursor"] = cursor

        response = self._get("/api/v1/templates/tech-stacks", params)
        data = self._extract_data(response)

        content = data.get("content", [])
        return [TechStackApiResponse(**item) for item in content]

    def get_tech_stack_by_id(
        self, tech_stack_id: int
    ) -> Optional[TechStackApiResponse]:
        """기술 스택 단건 조회"""
        try:
            response = self._get(f"/api/v1/templates/tech-stacks/{tech_stack_id}")
            data = self._extract_data(response)
            return TechStackApiResponse(**data)
        except httpx.HTTPStatusError:
            return None

    # ============================================
    # Architecture API
    # ============================================

    def get_architectures(
        self,
        tech_stack_id: Optional[int] = None,
        limit: int = 100,
        cursor: Optional[str] = None,
    ) -> list[ArchitectureApiResponse]:
        """아키텍처 목록 조회"""
        params: dict[str, Any] = {"size": limit}
        if tech_stack_id:
            params["techStackId"] = tech_stack_id
        if cursor:
            params["cursor"] = cursor

        response = self._get("/api/v1/templates/architectures", params)
        data = self._extract_data(response)

        content = data.get("content", [])
        return [ArchitectureApiResponse(**item) for item in content]

    def get_architecture_by_id(
        self, architecture_id: int
    ) -> Optional[ArchitectureApiResponse]:
        """아키텍처 단건 조회"""
        try:
            response = self._get(f"/api/v1/templates/architectures/{architecture_id}")
            data = self._extract_data(response)
            return ArchitectureApiResponse(**data)
        except httpx.HTTPStatusError:
            return None

    # ============================================
    # Layer API
    # ============================================

    def get_layers(
        self,
        architecture_id: Optional[int] = None,
        limit: int = 100,
        cursor: Optional[str] = None,
    ) -> list[LayerApiResponse]:
        """레이어 목록 조회"""
        params: dict[str, Any] = {"size": limit}
        if architecture_id:
            params["architectureId"] = architecture_id
        if cursor:
            params["cursor"] = cursor

        response = self._get("/api/v1/templates/layers", params)
        data = self._extract_data(response)

        content = data.get("content", [])
        return [LayerApiResponse(**item) for item in content]

    def get_layer_by_id(self, layer_id: int) -> Optional[LayerApiResponse]:
        """레이어 단건 조회"""
        try:
            response = self._get(f"/api/v1/templates/layers/{layer_id}")
            data = self._extract_data(response)
            return LayerApiResponse(**data)
        except httpx.HTTPStatusError:
            return None

    def get_layer_by_code(self, code: str) -> Optional[LayerApiResponse]:
        """레이어 코드로 조회"""
        layers = self.get_layers(limit=100)
        for layer in layers:
            if layer.code == code:
                return layer
        return None

    # ============================================
    # Module API
    # ============================================

    def get_modules(
        self,
        layer_id: Optional[int] = None,
        parent_module_id: Optional[int] = None,
        limit: int = 100,
        cursor: Optional[str] = None,
    ) -> list[ModuleApiResponse]:
        """모듈 목록 조회"""
        params: dict[str, Any] = {"size": limit}
        if layer_id:
            params["layerId"] = layer_id
        if parent_module_id:
            params["parentModuleId"] = parent_module_id
        if cursor:
            params["cursor"] = cursor

        response = self._get("/api/v1/templates/modules", params)
        data = self._extract_data(response)

        content = data.get("content", [])
        return [ModuleApiResponse(**item) for item in content]

    def get_module_by_id(self, module_id: int) -> Optional[ModuleApiResponse]:
        """모듈 단건 조회"""
        try:
            response = self._get(f"/api/v1/templates/modules/{module_id}")
            data = self._extract_data(response)
            return ModuleApiResponse(**data)
        except httpx.HTTPStatusError:
            return None

    # ============================================
    # PackagePurpose API
    # ============================================

    def get_package_purposes(
        self,
        layer_id: Optional[int] = None,
        limit: int = 100,
        cursor: Optional[str] = None,
    ) -> list[PackagePurposeApiResponse]:
        """패키지 목적 목록 조회"""
        params: dict[str, Any] = {"size": limit}
        if layer_id:
            params["layerId"] = layer_id
        if cursor:
            params["cursor"] = cursor

        response = self._get("/api/v1/templates/ref/package-purposes", params)
        data = self._extract_data(response)

        content = data.get("content", [])
        return [PackagePurposeApiResponse(**item) for item in content]

    def get_package_purpose_by_id(
        self, package_purpose_id: int
    ) -> Optional[PackagePurposeApiResponse]:
        """패키지 목적 단건 조회"""
        try:
            response = self._get(
                f"/api/v1/templates/ref/package-purposes/{package_purpose_id}"
            )
            data = self._extract_data(response)
            return PackagePurposeApiResponse(**data)
        except httpx.HTTPStatusError:
            return None

    # ============================================
    # ArchUnitTest API
    # ============================================

    def get_arch_unit_tests(
        self,
        structure_id: Optional[int] = None,
        limit: int = 100,
        cursor: Optional[str] = None,
    ) -> list[ArchUnitTestApiResponse]:
        """ArchUnit 테스트 목록 조회"""
        params: dict[str, Any] = {"size": limit}
        if structure_id:
            params["structureId"] = structure_id
        if cursor:
            params["cursor"] = cursor

        response = self._get("/api/v1/templates/arch-unit-tests", params)
        data = self._extract_data(response)

        content = data.get("content", [])
        return [ArchUnitTestApiResponse(**item) for item in content]

    def get_arch_unit_test_by_id(
        self, arch_unit_test_id: int
    ) -> Optional[ArchUnitTestApiResponse]:
        """ArchUnit 테스트 단건 조회"""
        try:
            response = self._get(
                f"/api/v1/templates/arch-unit-tests/{arch_unit_test_id}"
            )
            data = self._extract_data(response)
            return ArchUnitTestApiResponse(**data)
        except httpx.HTTPStatusError:
            return None

    # ============================================
    # ResourceTemplate API
    # ============================================

    def get_resource_templates(
        self,
        module_id: Optional[int] = None,
        category: Optional[str] = None,
        limit: int = 100,
        cursor: Optional[str] = None,
    ) -> list[ResourceTemplateApiResponse]:
        """리소스 템플릿 목록 조회"""
        params: dict[str, Any] = {"size": limit}
        if module_id:
            params["moduleId"] = module_id
        if category:
            params["category"] = category
        if cursor:
            params["cursor"] = cursor

        response = self._get("/api/v1/templates/resource-templates", params)
        data = self._extract_data(response)

        content = data.get("content", [])
        return [ResourceTemplateApiResponse(**item) for item in content]

    def get_resource_template_by_id(
        self, resource_template_id: int
    ) -> Optional[ResourceTemplateApiResponse]:
        """리소스 템플릿 단건 조회"""
        try:
            response = self._get(
                f"/api/v1/templates/resource-templates/{resource_template_id}"
            )
            data = self._extract_data(response)
            return ResourceTemplateApiResponse(**data)
        except httpx.HTTPStatusError:
            return None

    # ============================================
    # ChecklistItem API
    # ============================================

    def get_checklist_items(
        self,
        rule_id: Optional[int] = None,
        limit: int = 100,
        cursor: Optional[str] = None,
    ) -> list[ChecklistItemApiResponse]:
        """체크리스트 항목 목록 조회"""
        params: dict[str, Any] = {"size": limit}
        if rule_id:
            params["ruleId"] = rule_id
        if cursor:
            params["cursor"] = cursor

        response = self._get("/api/v1/templates/checklist-items", params)
        data = self._extract_data(response)

        content = data.get("content", [])
        return [ChecklistItemApiResponse(**item) for item in content]

    def get_checklist_item_by_id(
        self, checklist_item_id: int
    ) -> Optional[ChecklistItemApiResponse]:
        """체크리스트 항목 단건 조회"""
        try:
            response = self._get(
                f"/api/v1/templates/checklist-items/{checklist_item_id}"
            )
            data = self._extract_data(response)
            return ChecklistItemApiResponse(**data)
        except httpx.HTTPStatusError:
            return None

    # ============================================
    # FeedbackQueue API
    # ============================================

    def create_feedback(
        self,
        target_type: str,
        feedback_type: str,
        payload: str,
        target_id: Optional[int] = None,
    ) -> FeedbackQueueIdApiResponse:
        """피드백 생성 (FeedbackQueue에 PENDING 상태로 저장)

        Args:
            target_type: 대상 타입 (RULE_EXAMPLE, CLASS_TEMPLATE, CODING_RULE 등)
            feedback_type: 피드백 유형 (ADD, MODIFY, DELETE)
            payload: JSON 문자열 형태의 피드백 데이터
            target_id: 대상 ID (ADD 시 0, MODIFY/DELETE 시 기존 ID)
        """
        json_data: dict[str, Any] = {
            "targetType": target_type,
            "feedbackType": feedback_type,
            "payload": payload,
            "targetId": target_id if target_id is not None else 0,
        }

        response = self._post("/api/v1/templates/feedback-queue", json_data)
        data = self._extract_data(response)
        return FeedbackQueueIdApiResponse(**data)

    def get_feedback(self, feedback_queue_id: int) -> Optional[FeedbackQueueApiResponse]:
        """피드백 단건 조회"""
        try:
            response = self._get(f"/api/v1/templates/feedback-queue/{feedback_queue_id}")
            data = self._extract_data(response)
            return FeedbackQueueApiResponse(**data)
        except httpx.HTTPStatusError:
            return None

    def get_feedbacks(
        self,
        status: Optional[str] = None,
        risk_level: Optional[str] = None,
        target_type: Optional[str] = None,
        limit: int = 20,
        cursor: Optional[str] = None,
    ) -> list[FeedbackQueueApiResponse]:
        """피드백 목록 조회

        Args:
            status: 상태 필터 (PENDING, LLM_APPROVED 등)
            risk_level: 위험 수준 필터 (SAFE, MEDIUM)
            target_type: 대상 타입 필터 (RULE_EXAMPLE, CODING_RULE 등)
            limit: 조회 개수
            cursor: 페이징 커서
        """
        params: dict[str, Any] = {"size": limit}
        if status:
            params["status"] = status
        if risk_level:
            params["riskLevel"] = risk_level
        if target_type:
            params["targetType"] = target_type
        if cursor:
            params["cursor"] = cursor

        response = self._get("/api/v1/templates/feedback-queue", params)
        data = self._extract_data(response)

        content = data.get("content", [])
        return [FeedbackQueueApiResponse(**item) for item in content]

    def llm_approve_feedback(
        self, feedback_queue_id: int
    ) -> FeedbackQueueApiResponse:
        """LLM 1차 승인 (PENDING → LLM_APPROVED)"""
        response = self._patch(
            f"/api/v1/templates/feedback-queue/{feedback_queue_id}/llm-approve"
        )
        data = self._extract_data(response)
        return FeedbackQueueApiResponse(**data)

    def llm_reject_feedback(
        self,
        feedback_queue_id: int,
        review_notes: Optional[str] = None,
    ) -> FeedbackQueueApiResponse:
        """LLM 1차 거절 (PENDING → LLM_REJECTED)"""
        json_data: dict[str, Any] = {}
        if review_notes:
            json_data["reviewNotes"] = review_notes

        response = self._patch(
            f"/api/v1/templates/feedback-queue/{feedback_queue_id}/llm-reject",
            json_data if json_data else None,
        )
        data = self._extract_data(response)
        return FeedbackQueueApiResponse(**data)

    def human_approve_feedback(
        self, feedback_queue_id: int
    ) -> FeedbackQueueApiResponse:
        """Human 2차 승인 (LLM_APPROVED → HUMAN_APPROVED, Medium 레벨 전용)"""
        response = self._patch(
            f"/api/v1/templates/feedback-queue/{feedback_queue_id}/human-approve"
        )
        data = self._extract_data(response)
        return FeedbackQueueApiResponse(**data)

    def human_reject_feedback(
        self,
        feedback_queue_id: int,
        review_notes: Optional[str] = None,
    ) -> FeedbackQueueApiResponse:
        """Human 2차 거절 (LLM_APPROVED → HUMAN_REJECTED, Medium 레벨 전용)"""
        json_data: dict[str, Any] = {}
        if review_notes:
            json_data["reviewNotes"] = review_notes

        response = self._patch(
            f"/api/v1/templates/feedback-queue/{feedback_queue_id}/human-reject",
            json_data if json_data else None,
        )
        data = self._extract_data(response)
        return FeedbackQueueApiResponse(**data)

    def merge_feedback(self, feedback_queue_id: int) -> FeedbackQueueApiResponse:
        """피드백 머지 (승인된 피드백을 실제 테이블에 반영)"""
        response = self._post(
            f"/api/v1/templates/feedback-queue/{feedback_queue_id}/merge", {}
        )
        data = self._extract_data(response)
        return FeedbackQueueApiResponse(**data)


    # ============================================
    # MCP Workflow API (v2.0 - Module-Centric)
    # ============================================

    def get_planning_context(
        self,
        layers: list[str],
        tech_stack_id: Optional[int] = None,
    ) -> dict[str, Any]:
        """Planning Phase 컨텍스트 조회

        개발 계획 수립에 필요한 컨텍스트:
        - 기술 스택/아키텍처 정보
        - 레이어별 모듈 목록
        - 패키지 구조 요약
        """
        params: dict[str, Any] = {"layers": ",".join(layers)}
        if tech_stack_id:
            params["techStackId"] = tech_stack_id

        response = self._get("/api/v1/templates/mcp/planning-context", params)
        return self._extract_data(response)

    def get_module_context(
        self,
        module_id: int,
        class_type_id: int,
    ) -> dict[str, Any]:
        """Execution Phase 컨텍스트 조회

        코드 생성에 필요한 Module 전체 컨텍스트:
        - execution_context: PackageStructure, Template, ArchUnitTest
        - rule_context: Convention, CodingRule, RuleExample
        """
        params: dict[str, Any] = {"classTypeId": class_type_id}

        response = self._get(f"/api/v1/templates/mcp/module/{module_id}/context", params)
        return self._extract_data(response)

    def get_validation_context(
        self,
        layers: list[str],
        tech_stack_id: int,
        architecture_id: int,
        class_types: Optional[list[str]] = None,
    ) -> dict[str, Any]:
        """Validation Phase 컨텍스트 조회

        코드 검증에 필요한 컨텍스트:
        - zero_tolerance_rules: 자동 거부 규칙 (REGEX 패턴 포함)
        - checklist: 체크리스트 항목
        """
        params: dict[str, Any] = {
            "layers": ",".join(layers),
            "techStackId": tech_stack_id,
            "architectureId": architecture_id,
        }
        if class_types:
            params["classTypes"] = ",".join(class_types)

        response = self._get("/api/v1/templates/mcp/validation-context", params)
        return self._extract_data(response)

    def get_mcp_skeleton(self) -> dict[str, Any]:
        """전체 구조 스켈레톤 조회

        캐싱/초기화용 경량 데이터:
        - 기술 스택/아키텍처 기본 정보
        - 레이어/모듈 ID + 이름만
        """
        response = self._get("/api/v1/templates/mcp/skeleton")
        return self._extract_data(response)

    def get_mcp_version(self) -> dict[str, Any]:
        """MCP API 버전 정보 조회"""
        response = self._get("/api/v1/mcp/version")
        return self._extract_data(response)

    def get_coding_rule_index(
        self,
        convention_id: Optional[int] = None,
        severities: Optional[list[str]] = None,
        categories: Optional[list[str]] = None,
    ) -> list[dict[str, Any]]:
        """코딩 규칙 인덱스 조회 (code, name, severity, category만)

        경량 인덱스로 캐싱 효율성 극대화. 상세는 get_rule(code)로 개별 조회.

        Args:
            convention_id: 컨벤션 ID (null이면 전체)
            severities: 심각도 필터 목록 (BLOCKER, CRITICAL, MAJOR, MINOR)
            categories: 카테고리 필터 목록

        Returns:
            규칙 인덱스 목록 [{code, name, severity, category}, ...]
        """
        params: dict[str, Any] = {}
        if convention_id:
            params["conventionId"] = convention_id
        if severities:
            params["severities"] = severities
        if categories:
            params["categories"] = categories

        response = self._get("/api/v1/templates/coding-rules/index", params)
        data = self._extract_data(response)

        # 응답이 리스트인 경우 직접 반환
        if isinstance(data, list):
            return data
        # content 필드가 있는 경우
        return data.get("content", data) if isinstance(data, dict) else []

    # ============================================
    # Convention Hub API (Phase 2)
    # ============================================

    def get_config_files_for_mcp(
        self,
        tech_stack_id: int,
        architecture_id: Optional[int] = None,
        tool_types: Optional[list[str]] = None,
    ) -> dict[str, Any]:
        """MCP용 설정 파일 템플릿 조회 (init_project Tool용)

        Args:
            tech_stack_id: 기술 스택 ID (필수)
            architecture_id: 아키텍처 ID (선택)
            tool_types: 도구 타입 목록 (선택, 예: CLAUDE, CURSOR)

        Returns:
            config_files: 설정 파일 템플릿 목록
            total_count: 총 개수
        """
        params: dict[str, Any] = {"techStackId": tech_stack_id}
        if architecture_id:
            params["architectureId"] = architecture_id
        if tool_types:
            params["toolTypes"] = ",".join(tool_types)

        response = self._get("/api/v1/templates/mcp/config-files", params)
        return self._extract_data(response)

    def get_onboarding_for_mcp(
        self,
        tech_stack_id: int,
        architecture_id: Optional[int] = None,
        context_types: Optional[list[str]] = None,
    ) -> dict[str, Any]:
        """MCP용 온보딩 컨텍스트 조회 (get_onboarding_context Tool용)

        Args:
            tech_stack_id: 기술 스택 ID (필수)
            architecture_id: 아키텍처 ID (선택)
            context_types: 컨텍스트 타입 목록 (선택, 예: SUMMARY, ZERO_TOLERANCE)

        Returns:
            onboarding_contexts: 온보딩 컨텍스트 목록
            total_count: 총 개수
        """
        params: dict[str, Any] = {"techStackId": tech_stack_id}
        if architecture_id:
            params["architectureId"] = architecture_id
        if context_types:
            params["contextTypes"] = ",".join(context_types)

        response = self._get("/api/v1/templates/mcp/onboarding", params)
        return self._extract_data(response)


# 싱글톤 인스턴스
_client: Optional[ConventionApiClient] = None


def get_api_client() -> ConventionApiClient:
    """API 클라이언트 싱글톤 반환"""
    global _client
    if _client is None:
        _client = ConventionApiClient()
    return _client
