"""
Context Service

컨벤션 컨텍스트 조회 및 관리 서비스 (v2.0 API 기반)
"""

from typing import Any, Optional

from ..api_client import get_api_client


class ContextService:
    """컨벤션 컨텍스트 서비스 (v2.0 API 기반)"""

    # 유효한 레이어 목록
    VALID_LAYERS = {"DOMAIN", "APPLICATION", "PERSISTENCE", "REST_API"}

    # 레이어별 안티패턴 매핑
    ANTI_PATTERNS = {
        "DOMAIN": [
            {"code": "AP-001", "name": "Anemic Domain Model", "description": "도메인 객체에 로직 없이 getter/setter만 존재"},
            {"code": "AP-002", "name": "Premature Abstraction", "description": "불필요한 추상화로 복잡도 증가"},
        ],
        "APPLICATION": [
            {"code": "AP-003", "name": "God Service", "description": "하나의 서비스가 너무 많은 책임"},
            {"code": "AP-004", "name": "Transaction Script", "description": "절차적 코드로 도메인 로직 분산"},
        ],
        "PERSISTENCE": [
            {"code": "AP-005", "name": "N+1 Query", "description": "반복적인 쿼리로 성능 저하"},
            {"code": "AP-006", "name": "Lazy Loading Trap", "description": "지연 로딩으로 인한 예외"},
        ],
        "REST_API": [
            {"code": "AP-007", "name": "Chatty API", "description": "과도하게 세분화된 API"},
            {"code": "AP-008", "name": "Missing Validation", "description": "입력값 검증 누락"},
        ],
    }

    # 기본값
    DEFAULT_TECH_STACK_ID = 1
    DEFAULT_ARCHITECTURE_ID = 1

    def __init__(self) -> None:
        self._client = get_api_client()

    def get_context(
        self,
        layer: Optional[str] = None,
        class_type: Optional[str] = None,
        tech_stack_id: Optional[int] = None,
        architecture_id: Optional[int] = None,
    ) -> dict[str, Any]:
        """컨벤션 컨텍스트 조회 (v2.0 API 기반)

        v2.0 validation_context API를 사용하여 layer 기반 조회 지원.
        기존의 broken convention_id 체인을 제거하고 직접 layers 파라미터 사용.

        Args:
            layer: 레이어 코드 (DOMAIN|APPLICATION|PERSISTENCE|REST_API)
            class_type: 클래스 타입 (AGGREGATE|USE_CASE|ENTITY|CONTROLLER 등)
            tech_stack_id: 기술 스택 ID (기본값: 1)
            architecture_id: 아키텍처 ID (기본값: 1)

        Returns:
            컨벤션 컨텍스트 (zero_tolerance_rules, checklist, anti_patterns 등)
        """
        # 기본값 설정
        effective_tech_stack_id = tech_stack_id or self.DEFAULT_TECH_STACK_ID
        effective_architecture_id = architecture_id or self.DEFAULT_ARCHITECTURE_ID

        # 레이어 정규화
        normalized_layer = layer.upper() if layer else None
        if normalized_layer and normalized_layer not in self.VALID_LAYERS:
            return {
                "error": f"Invalid layer: {normalized_layer}. Valid: {sorted(self.VALID_LAYERS)}"
            }

        # 조회할 레이어 목록 결정
        layers_to_query = [normalized_layer] if normalized_layer else list(self.VALID_LAYERS)

        # class_types 정규화
        class_types = [class_type.upper()] if class_type else None

        result: dict[str, Any] = {
            "tech_stack_id": effective_tech_stack_id,
            "architecture_id": effective_architecture_id,
            "layer": normalized_layer,
            "class_type": class_type.upper() if class_type else None,
        }

        try:
            # v2.0 validation_context API 호출
            validation_result = self._client.get_validation_context(
                layers=layers_to_query,
                tech_stack_id=effective_tech_stack_id,
                architecture_id=effective_architecture_id,
                class_types=class_types,
            )

            # Zero-Tolerance 규칙 추출
            zt_rules = validation_result.get("zeroToleranceRules", [])
            result["zero_tolerance_rules"] = [
                {
                    "code": r.get("code", ""),
                    "name": r.get("name", ""),
                    "severity": r.get("severity", ""),
                    "description": r.get("description", ""),
                    "detection_type": r.get("detectionType", ""),
                    "detection_pattern": r.get("detectionPattern", ""),
                    "auto_reject_pr": r.get("autoRejectPr", False),
                }
                for r in zt_rules
            ]

            # 체크리스트 추출
            checklists = validation_result.get("checklists", [])
            result["checklist_items"] = [
                {
                    "id": c.get("id", 0),
                    "content": c.get("content", ""),
                    "priority": c.get("priority", ""),
                    "auto_checkable": c.get("autoCheckable", False),
                }
                for c in checklists
            ]

            # 레이어별 통계
            layer_stats = validation_result.get("layerStats", [])
            result["layer_stats"] = [
                {
                    "layer": s.get("layerCode", ""),
                    "zero_tolerance_count": s.get("zeroToleranceCount", 0),
                    "checklist_count": s.get("checklistCount", 0),
                }
                for s in layer_stats
            ]

        except Exception as e:
            # API 호출 실패 시 빈 결과 반환
            result["zero_tolerance_rules"] = []
            result["checklist_items"] = []
            result["layer_stats"] = []
            result["api_error"] = str(e)

        # 레이어 의존성 규칙 조회
        try:
            deps = self._client.get_layer_dependencies(
                architecture_id=effective_architecture_id
            )
            result["layer_dependencies"] = [
                {
                    "from": d.source_layer,
                    "to": d.target_layer,
                    "allowed": d.allowed,
                    "dependency_type": d.dependency_type,
                    "rationale": d.rationale,
                }
                for d in deps
                if normalized_layer is None or d.source_layer == normalized_layer
            ]
        except Exception:
            result["layer_dependencies"] = []

        # 안티패턴
        result["anti_patterns"] = self._get_anti_patterns(normalized_layer)

        return result

    def _get_anti_patterns(self, layer: Optional[str]) -> list[dict]:
        """레이어별 안티패턴 조회"""
        if layer and layer in self.ANTI_PATTERNS:
            return self.ANTI_PATTERNS[layer]
        elif layer is None:
            # 모든 레이어의 안티패턴 반환
            all_patterns = []
            for patterns in self.ANTI_PATTERNS.values():
                all_patterns.extend(patterns)
            return all_patterns
        return []

    def search(
        self,
        query: str,
        scope: str = "all",
        convention_id: Optional[int] = None,
    ) -> dict[str, Any]:
        """통합 검색"""
        search_result = self._client.search(query, convention_id)

        result: dict[str, Any] = {
            "query": query,
            "scope": scope,
            "total_count": search_result.total_count,
        }

        # scope에 따른 필터링
        if scope in ("all", "rules"):
            result["rules"] = [
                {
                    "id": r.id,
                    "code": r.code,
                    "name": r.title,
                    "matched_field": r.matched_field,
                }
                for r in search_result.results.coding_rules
            ]
        else:
            result["rules"] = []

        if scope in ("all", "templates"):
            result["templates"] = [
                {
                    "id": t.id,
                    "type": t.type,
                    "name": t.name,
                    "matched_field": t.matched_field,
                }
                for t in search_result.results.class_templates
            ]
        else:
            result["templates"] = []

        result["modules"] = [
            {
                "id": m.id,
                "name": m.name,
                "matched_field": m.matched_field,
            }
            for m in search_result.results.modules
        ]

        return result

    def list_rules(
        self,
        convention_id: Optional[int] = None,
        severities: Optional[list[str]] = None,
        categories: Optional[list[str]] = None,
    ) -> dict[str, Any]:
        """규칙 인덱스 조회 (code, name, severity, category만)

        경량 인덱스로 캐싱 효율성 극대화.
        상세 정보는 get_rule_detail(code)로 개별 조회.

        Args:
            convention_id: 컨벤션 ID (null이면 전체)
            severities: 심각도 필터 목록
            categories: 카테고리 필터 목록

        Returns:
            규칙 인덱스 목록 + 메타정보
        """
        rules = self._client.get_coding_rule_index(
            convention_id=convention_id,
            severities=severities,
            categories=categories,
        )

        return {
            "total_count": len(rules),
            "rules": rules,
            "usage_hint": "상세 정보는 get_rule(code)로 개별 조회",
        }

    def get_rule_detail(self, code: str) -> Optional[dict[str, Any]]:
        """규칙 상세 + 예시 조회"""
        # 먼저 규칙 코드로 검색
        rule = self._client.get_coding_rule_by_code(code)
        if not rule:
            return None

        # 상세 정보 + 예시 조회
        detail = self._client.get_coding_rule_with_examples(rule.coding_rule_id)
        if not detail:
            return {
                "code": rule.code,
                "name": rule.name,
                "severity": rule.severity,
                "category": rule.category,
                "description": rule.description,
                "rationale": rule.rationale,
                "examples": {"good": [], "bad": []},
            }

        # 예시 분류
        good_examples = []
        bad_examples = []
        for ex in detail.examples:
            example_data = {
                "code": ex.code,
                "language": ex.language,
                "explanation": ex.explanation,
            }
            if ex.example_type == "GOOD":
                good_examples.append(example_data)
            elif ex.example_type == "BAD":
                bad_examples.append(example_data)

        return {
            "code": detail.code,
            "name": detail.name,
            "severity": detail.severity,
            "category": detail.category,
            "description": detail.description,
            "rationale": detail.rationale,
            "applies_to": detail.applies_to or [],
            "auto_fixable": detail.auto_fixable,
            "examples": {
                "good": good_examples,
                "bad": bad_examples,
            },
        }


# 싱글톤 인스턴스
_service: Optional[ContextService] = None


def get_context_service() -> ContextService:
    """ContextService 싱글톤 반환"""
    global _service
    if _service is None:
        _service = ContextService()
    return _service
