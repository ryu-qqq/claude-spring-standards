"""
Context Engine Module

AI 코드 생성 가드레일의 Context Engine 구현
- 의도 분류 (Intent Classification)
- 레이어/클래스 타입 감지
- 최소 컨텍스트 수집
"""

from .context_collector import MinimalContext, get_minimal_context
from .intent_classifier import classify_intent
from .layer_detector import DetectionResult, detect_layer_and_class_type

__all__ = [
    "classify_intent",
    "detect_layer_and_class_type",
    "DetectionResult",
    "get_minimal_context",
    "MinimalContext",
]
