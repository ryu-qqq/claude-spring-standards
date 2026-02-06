"""
suggest_convention Tool

코드/appliesTo 기반으로 적절한 convention을 자동 추천합니다.
3가지 전략 (code_prefix, applies_to_layer, description_keywords) + 투표 방식.
"""

import logging
from typing import Any, Optional

from ..api_client import get_api_client
from ..template.engine import CLASS_TYPE_TO_LAYER

logger = logging.getLogger(__name__)

# 코드 prefix → 레이어 매핑 (정적 도메인 지식)
_CODE_PREFIX_LAYER_MAP = {
    "DOM": "DOMAIN",
    "AGG": "DOMAIN",
    "VO": "DOMAIN",
    "EVT": "DOMAIN",
    "APP": "APPLICATION",
    "SVC": "APPLICATION",
    "UC": "APPLICATION",
    "PER": "ADAPTER_OUT",
    "ENT": "ADAPTER_OUT",
    "JPA": "ADAPTER_OUT",
    "API": "ADAPTER_IN",
    "CTR": "ADAPTER_IN",
    "BOOT": "BOOTSTRAP",
    "CFG": "BOOTSTRAP",
}

# description 키워드 → 레이어 매핑
_KEYWORD_LAYER_MAP = {
    "DOMAIN": ["domain", "aggregate", "value object", "도메인", "집합체", "값 객체", "도메인 이벤트"],
    "APPLICATION": ["application", "service", "use case", "port", "애플리케이션", "서비스", "유스케이스"],
    "ADAPTER_OUT": ["persistence", "repository", "entity", "jpa", "adapter out", "영속성", "리포지토리"],
    "ADAPTER_IN": ["api", "controller", "request", "response", "rest", "dto", "컨트롤러"],
    "BOOTSTRAP": ["bootstrap", "config", "configuration", "부트스트랩", "설정"],
}


def _strategy_code_prefix(
    code: str, conventions: list[dict[str, Any]]
) -> Optional[dict[str, Any]]:
    """전략 1: 코드 prefix 기반 추천 (신뢰도 0.9)"""
    code_upper = code.upper()
    for prefix, layer_code in _CODE_PREFIX_LAYER_MAP.items():
        if code_upper.startswith(prefix):
            for conv in conventions:
                if conv.get("layer_code") == layer_code and conv.get("active", True):
                    return {
                        "convention": conv,
                        "confidence": 0.9,
                        "reason": f"코드 prefix '{prefix}' → {layer_code} 매핑",
                        "strategy": "code_prefix",
                    }
            break
    return None


def _strategy_applies_to_layer(
    applies_to: list[str], conventions: list[dict[str, Any]]
) -> Optional[dict[str, Any]]:
    """전략 2: appliesTo class_type → 레이어 역추적 (신뢰도 0.85)"""
    layer_votes: dict[str, int] = {}
    for class_type in applies_to:
        layer = CLASS_TYPE_TO_LAYER.get(class_type)
        if layer:
            layer_votes[layer] = layer_votes.get(layer, 0) + 1

    if not layer_votes:
        return None

    best_layer = max(layer_votes, key=layer_votes.get)
    for conv in conventions:
        if conv.get("layer_code") == best_layer and conv.get("active", True):
            matched_types = [ct for ct in applies_to if CLASS_TYPE_TO_LAYER.get(ct) == best_layer]
            return {
                "convention": conv,
                "confidence": 0.85,
                "reason": f"appliesTo {matched_types} → {best_layer} 레이어 매핑",
                "strategy": "applies_to_layer",
            }
    return None


def _strategy_description_keywords(
    description: str, conventions: list[dict[str, Any]]
) -> Optional[dict[str, Any]]:
    """전략 3: description 키워드 매칭 (신뢰도 0.5~0.8)"""
    desc_lower = description.lower()
    layer_scores: dict[str, float] = {}

    for layer_code, keywords in _KEYWORD_LAYER_MAP.items():
        score = 0.0
        matched_keywords = []
        for keyword in keywords:
            if keyword in desc_lower:
                score += 1.0
                matched_keywords.append(keyword)
        if score > 0:
            layer_scores[layer_code] = score
            # 매칭 키워드 수에 따라 신뢰도 조정 (1개=0.5, 2개=0.65, 3개+=0.8)
            layer_scores[f"{layer_code}_keywords"] = matched_keywords

    if not layer_scores:
        return None

    # 점수 기반 키만 필터
    score_layers = {k: v for k, v in layer_scores.items() if not k.endswith("_keywords")}
    best_layer = max(score_layers, key=score_layers.get)
    best_score = score_layers[best_layer]
    matched_keywords = layer_scores.get(f"{best_layer}_keywords", [])

    confidence = min(0.5 + (best_score - 1) * 0.15, 0.8)

    for conv in conventions:
        if conv.get("layer_code") == best_layer and conv.get("active", True):
            return {
                "convention": conv,
                "confidence": confidence,
                "reason": f"키워드 {matched_keywords} → {best_layer} 매핑",
                "strategy": "description_keywords",
            }
    return None


def _find_similar_rules(code: str) -> list[dict[str, str]]:
    """코드 prefix 기반 유사 규칙 조회"""
    if not code:
        return []

    # code에서 prefix 추출 (예: "API-DTO-SEARCH-002" → "API-DTO")
    parts = code.split("-")
    if len(parts) < 2:
        search_prefix = parts[0]
    else:
        search_prefix = f"{parts[0]}-{parts[1]}"

    try:
        client = get_api_client()
        rules = client.get_coding_rules(
            search_field="CODE", search_word=search_prefix, limit=10
        )
        return [
            {"code": r.code, "name": r.name}
            for r in rules
        ]
    except Exception as e:
        logger.warning(f"유사 규칙 조회 실패: {e}")
        return []


def suggest_convention(
    code: str = "",
    applies_to: Optional[list[str]] = None,
    description: str = "",
) -> dict[str, Any]:
    """convention 자동 추천

    3가지 전략으로 적절한 convention을 추천합니다:
    1. code_prefix: 규칙 코드의 prefix로 레이어 매핑 (신뢰도 0.9)
    2. applies_to_layer: appliesTo class_type으로 레이어 역추적 (신뢰도 0.85)
    3. description_keywords: 설명 내 키워드로 레이어 매핑 (신뢰도 0.5~0.8)

    여러 전략이 동일 convention을 추천하면 confidence 보너스를 부여합니다.

    Args:
        code: 규칙 코드 (예: API-DTO-SEARCH-002)
        applies_to: 적용 대상 class_type 코드 목록 (예: ["REQUEST_DTO"])
        description: 규칙 설명

    Returns:
        추천 결과 (suggested_convention, existing_similar_rules, all_conventions)
    """
    if not code and not applies_to and not description:
        return {
            "success": False,
            "error": "code, applies_to, description 중 하나 이상 입력해주세요.",
        }

    try:
        client = get_api_client()
        conventions = client.get_all_conventions_with_modules()
    except Exception as e:
        return {
            "success": False,
            "error": f"Convention 목록 조회 실패: {e}",
        }

    if not conventions:
        return {
            "success": False,
            "error": "등록된 convention이 없습니다.",
        }

    # 3가지 전략 실행
    suggestions: list[dict[str, Any]] = []

    if code:
        result = _strategy_code_prefix(code, conventions)
        if result:
            suggestions.append(result)

    if applies_to:
        result = _strategy_applies_to_layer(applies_to, conventions)
        if result:
            suggestions.append(result)

    if description:
        result = _strategy_description_keywords(description, conventions)
        if result:
            suggestions.append(result)

    if not suggestions:
        return {
            "success": True,
            "suggested_convention": None,
            "message": "적합한 convention을 자동 추천할 수 없습니다. all_conventions에서 직접 선택하세요.",
            "all_conventions": [
                {"id": c["id"], "module_name": c["module_name"], "layer_code": c["layer_code"]}
                for c in conventions
                if c.get("active", True)
            ],
        }

    # 투표: 동일 convention 추천 시 confidence 보너스
    vote_map: dict[int, dict[str, Any]] = {}
    for s in suggestions:
        conv_id = s["convention"]["id"]
        if conv_id in vote_map:
            existing = vote_map[conv_id]
            existing["confidence"] = min(existing["confidence"] + 0.05, 1.0)
            existing["reasons"].append(s["reason"])
            existing["strategies"].append(s["strategy"])
        else:
            vote_map[conv_id] = {
                "id": conv_id,
                "module_name": s["convention"]["module_name"],
                "layer_code": s["convention"]["layer_code"],
                "confidence": s["confidence"],
                "reasons": [s["reason"]],
                "strategies": [s["strategy"]],
            }

    # 최고 confidence 선택
    best = max(vote_map.values(), key=lambda x: x["confidence"])
    best["reason"] = " + ".join(best.pop("reasons"))
    best.pop("strategies")

    # 유사 규칙 조회
    similar_rules = _find_similar_rules(code) if code else []

    return {
        "success": True,
        "suggested_convention": best,
        "existing_similar_rules": similar_rules,
        "all_conventions": [
            {"id": c["id"], "module_name": c["module_name"], "layer_code": c["layer_code"]}
            for c in conventions
            if c.get("active", True)
        ],
    }
