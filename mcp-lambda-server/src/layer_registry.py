"""
Layer Registry

DB 기반 동적 레이어 코드 관리 및 검증 모듈.
Layer enum의 하드코딩 문제를 해결하기 위해:
1. DB에서 레이어 코드를 동적으로 로드/캐싱
2. 서버 시작 시 Layer enum과 DB 코드를 비교 검증
3. 불일치 발견 시 명확한 경고 메시지 제공
"""

import logging
from typing import Optional

from .api_client import get_api_client
from .models import Layer

logger = logging.getLogger(__name__)


class LayerRegistry:
    """레이어 코드 레지스트리 - DB 기반 동적 관리 및 검증"""

    # Layer enum에서 핵심 레이어 코드 (DB와 반드시 일치해야 하는 코드)
    # SCHEDULER, COMMON, TESTING 등은 확장용이므로 DB에 없을 수 있음
    CORE_ENUM_CODES = {"DOMAIN", "APPLICATION", "ADAPTER_OUT", "ADAPTER_IN", "BOOTSTRAP"}

    def __init__(self) -> None:
        self._db_codes: Optional[list[str]] = None
        self._db_layers: Optional[list[dict]] = None
        self._validated: bool = False

    def get_db_layer_codes(self) -> list[str]:
        """DB에서 레이어 코드 목록을 동적으로 조회 (캐싱)

        Returns:
            DB에 등록된 레이어 코드 목록.
            DB 접속 실패 시 Layer enum 값을 폴백으로 반환.
        """
        if self._db_codes is None:
            self._load_from_db()
        return self._db_codes

    def get_db_layers(self) -> list[dict]:
        """DB에서 레이어 상세 정보를 동적으로 조회 (캐싱)

        Returns:
            레이어 상세 정보 목록 (id, code, name, description, order_index)
        """
        if self._db_layers is None:
            self._load_from_db()
        return self._db_layers

    def is_valid_layer_code(self, code: str) -> bool:
        """주어진 코드가 DB에 등록된 유효한 레이어 코드인지 확인"""
        return code.upper() in set(self.get_db_layer_codes())

    def invalidate_cache(self) -> None:
        """캐시 무효화 (DB 변경 후 재조회 필요 시)"""
        self._db_codes = None
        self._db_layers = None
        self._validated = False

    def validate(self) -> list[str]:
        """Layer enum과 DB 레이어 코드를 비교 검증

        Returns:
            경고 메시지 목록. 빈 리스트면 검증 통과.
        """
        if self._validated:
            return []

        db_codes = set(self.get_db_layer_codes())
        enum_codes = {layer.value for layer in Layer}
        warnings = []

        # 1. DB에만 있는 코드 → Layer enum에 추가 필요
        db_only = db_codes - enum_codes
        for code in sorted(db_only):
            warnings.append(
                f"DB 레이어 '{code}'가 Layer enum에 없습니다. "
                f"models.py Layer enum에 추가하세요."
            )

        # 2. 핵심 enum 코드가 DB에 없음 → DB 변경됨
        missing_core = (self.CORE_ENUM_CODES & enum_codes) - db_codes
        for code in sorted(missing_core):
            warnings.append(
                f"Layer enum의 핵심 코드 '{code}'가 DB에 없습니다. "
                f"DB 레이어 코드가 변경되었을 수 있습니다. "
                f"내부 모듈(layer_detector, intent_classifier, context_collector, "
                f"validation/rules, template/engine)의 매핑도 함께 업데이트하세요."
            )

        self._validated = True
        return warnings

    def validate_and_log(self) -> bool:
        """검증 실행 후 결과를 로그에 출력

        Returns:
            True: 검증 통과 (경고 없음)
            False: 경고 발견
        """
        warnings = self.validate()

        if not warnings:
            db_codes = self.get_db_layer_codes()
            logger.info(
                "Layer 코드 검증 통과. DB 레이어: %s", db_codes
            )
            return True

        for warning in warnings:
            logger.warning("Layer 코드 불일치: %s", warning)

        logger.warning(
            "Layer enum과 DB 레이어 코드가 일치하지 않습니다. "
            "내부 모듈의 레이어 매핑이 정확하지 않을 수 있습니다. "
            "claudedocs/layer-code-refactoring-plan.md를 참고하세요."
        )
        return False

    def _load_from_db(self) -> None:
        """DB에서 레이어 정보 로드"""
        try:
            client = get_api_client()
            layers = client.get_layers()
            self._db_codes = [layer.code for layer in layers]
            self._db_layers = [
                {
                    "id": layer.id,
                    "architecture_id": layer.architecture_id,
                    "code": layer.code,
                    "name": layer.name,
                    "description": layer.description,
                    "order_index": layer.order_index,
                }
                for layer in layers
            ]
        except Exception as e:
            logger.warning(
                "DB에서 레이어 코드를 조회할 수 없습니다. "
                "Layer enum 값을 폴백으로 사용합니다: %s",
                e,
            )
            # 폴백: Layer enum의 핵심 코드 사용
            self._db_codes = sorted(self.CORE_ENUM_CODES)
            self._db_layers = []


# 싱글톤 인스턴스
_registry: Optional[LayerRegistry] = None


def get_layer_registry() -> LayerRegistry:
    """LayerRegistry 싱글톤 반환"""
    global _registry
    if _registry is None:
        _registry = LayerRegistry()
    return _registry
