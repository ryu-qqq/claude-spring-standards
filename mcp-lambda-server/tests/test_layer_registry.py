"""
LayerRegistry 단위 테스트

DB 기반 동적 레이어 코드 관리 및 검증 로직 테스트
"""

from unittest.mock import MagicMock, patch

import pytest

from src.layer_registry import LayerRegistry, get_layer_registry
from src.models import Layer


class TestLayerRegistry:
    """LayerRegistry 기본 동작 테스트"""

    def setup_method(self):
        """각 테스트마다 새로운 인스턴스 생성"""
        self.registry = LayerRegistry()

    @patch("src.layer_registry.get_api_client")
    def test_get_db_layer_codes_success(self, mock_get_client):
        """DB에서 레이어 코드 조회 성공"""
        mock_client = MagicMock()
        mock_layers = []
        for code in ["DOMAIN", "APPLICATION", "ADAPTER_OUT", "ADAPTER_IN", "BOOTSTRAP"]:
            layer = MagicMock()
            layer.code = code
            layer.id = 1
            layer.architecture_id = 1
            layer.name = code
            layer.description = f"{code} layer"
            layer.order_index = 0
            mock_layers.append(layer)
        mock_client.get_layers.return_value = mock_layers
        mock_get_client.return_value = mock_client

        codes = self.registry.get_db_layer_codes()

        assert len(codes) == 5
        assert "DOMAIN" in codes
        assert "ADAPTER_OUT" in codes
        assert "ADAPTER_IN" in codes

    @patch("src.layer_registry.get_api_client")
    def test_get_db_layer_codes_cached(self, mock_get_client):
        """두 번째 호출 시 캐시 사용"""
        mock_client = MagicMock()
        layer = MagicMock()
        layer.code = "DOMAIN"
        layer.id = 1
        layer.architecture_id = 1
        layer.name = "Domain"
        layer.description = "Domain layer"
        layer.order_index = 0
        mock_client.get_layers.return_value = [layer]
        mock_get_client.return_value = mock_client

        self.registry.get_db_layer_codes()
        self.registry.get_db_layer_codes()

        # API는 한 번만 호출
        mock_client.get_layers.assert_called_once()

    @patch("src.layer_registry.get_api_client")
    def test_get_db_layer_codes_fallback_on_error(self, mock_get_client):
        """DB 접속 실패 시 폴백 코드 반환"""
        mock_client = MagicMock()
        mock_client.get_layers.side_effect = Exception("Connection refused")
        mock_get_client.return_value = mock_client

        codes = self.registry.get_db_layer_codes()

        # 폴백: CORE_ENUM_CODES 사용
        assert set(codes) == LayerRegistry.CORE_ENUM_CODES

    @patch("src.layer_registry.get_api_client")
    def test_get_db_layers_detail(self, mock_get_client):
        """DB 레이어 상세 정보 조회"""
        mock_client = MagicMock()
        layer = MagicMock()
        layer.id = 1
        layer.architecture_id = 1
        layer.code = "DOMAIN"
        layer.name = "Domain Layer"
        layer.description = "도메인 레이어"
        layer.order_index = 1
        mock_client.get_layers.return_value = [layer]
        mock_get_client.return_value = mock_client

        layers = self.registry.get_db_layers()

        assert len(layers) == 1
        assert layers[0]["code"] == "DOMAIN"
        assert layers[0]["name"] == "Domain Layer"
        assert layers[0]["order_index"] == 1

    @patch("src.layer_registry.get_api_client")
    def test_is_valid_layer_code(self, mock_get_client):
        """레이어 코드 유효성 검사"""
        mock_client = MagicMock()
        for code in ["DOMAIN", "APPLICATION"]:
            layer = MagicMock()
            layer.code = code
            layer.id = 1
            layer.architecture_id = 1
            layer.name = code
            layer.description = ""
            layer.order_index = 0
        mock_client.get_layers.return_value = [
            m for m in [MagicMock(code="DOMAIN", id=1, architecture_id=1, name="", description="", order_index=0),
                        MagicMock(code="APPLICATION", id=2, architecture_id=1, name="", description="", order_index=1)]
        ]
        mock_get_client.return_value = mock_client

        assert self.registry.is_valid_layer_code("DOMAIN") is True
        assert self.registry.is_valid_layer_code("domain") is True  # 대소문자 무시
        assert self.registry.is_valid_layer_code("NONEXISTENT") is False

    @patch("src.layer_registry.get_api_client")
    def test_invalidate_cache(self, mock_get_client):
        """캐시 무효화 후 재조회"""
        mock_client = MagicMock()
        layer1 = MagicMock(code="DOMAIN", id=1, architecture_id=1, name="", description="", order_index=0)
        layer2 = MagicMock(code="APPLICATION", id=2, architecture_id=1, name="", description="", order_index=1)
        mock_client.get_layers.return_value = [layer1]
        mock_get_client.return_value = mock_client

        # 첫 번째 조회
        codes1 = self.registry.get_db_layer_codes()
        assert codes1 == ["DOMAIN"]

        # 캐시 무효화
        mock_client.get_layers.return_value = [layer1, layer2]
        self.registry.invalidate_cache()

        # 두 번째 조회 (재로드)
        codes2 = self.registry.get_db_layer_codes()
        assert codes2 == ["DOMAIN", "APPLICATION"]
        assert mock_client.get_layers.call_count == 2


class TestLayerRegistryValidation:
    """Layer enum과 DB 코드 비교 검증 테스트"""

    def setup_method(self):
        self.registry = LayerRegistry()

    @patch("src.layer_registry.get_api_client")
    def test_validate_all_match(self, mock_get_client):
        """모든 핵심 코드 일치 시 경고 없음"""
        mock_client = MagicMock()
        db_codes = ["DOMAIN", "APPLICATION", "ADAPTER_OUT", "ADAPTER_IN", "BOOTSTRAP"]
        mock_client.get_layers.return_value = [
            MagicMock(code=c, id=i, architecture_id=1, name=c, description="", order_index=i)
            for i, c in enumerate(db_codes)
        ]
        mock_get_client.return_value = mock_client

        warnings = self.registry.validate()

        assert len(warnings) == 0

    @patch("src.layer_registry.get_api_client")
    def test_validate_db_has_new_code(self, mock_get_client):
        """DB에 새 레이어가 추가된 경우"""
        mock_client = MagicMock()
        db_codes = ["DOMAIN", "APPLICATION", "ADAPTER_OUT", "ADAPTER_IN", "BOOTSTRAP", "INFRASTRUCTURE"]
        mock_client.get_layers.return_value = [
            MagicMock(code=c, id=i, architecture_id=1, name=c, description="", order_index=i)
            for i, c in enumerate(db_codes)
        ]
        mock_get_client.return_value = mock_client

        warnings = self.registry.validate()

        assert len(warnings) == 1
        assert "INFRASTRUCTURE" in warnings[0]
        assert "Layer enum에 추가" in warnings[0]

    @patch("src.layer_registry.get_api_client")
    def test_validate_core_code_missing_from_db(self, mock_get_client):
        """핵심 코드가 DB에서 사라진 경우"""
        mock_client = MagicMock()
        # ADAPTER_OUT이 DB에서 제거됨
        db_codes = ["DOMAIN", "APPLICATION", "ADAPTER_IN", "BOOTSTRAP"]
        mock_client.get_layers.return_value = [
            MagicMock(code=c, id=i, architecture_id=1, name=c, description="", order_index=i)
            for i, c in enumerate(db_codes)
        ]
        mock_get_client.return_value = mock_client

        warnings = self.registry.validate()

        assert len(warnings) == 1
        assert "ADAPTER_OUT" in warnings[0]
        assert "DB에 없습니다" in warnings[0]

    @patch("src.layer_registry.get_api_client")
    def test_validate_db_code_renamed(self, mock_get_client):
        """DB에서 레이어 코드 이름이 변경된 경우 (추가 + 삭제)"""
        mock_client = MagicMock()
        # ADAPTER_OUT → PERSISTENCE_ADAPTER로 변경됨
        db_codes = ["DOMAIN", "APPLICATION", "PERSISTENCE_ADAPTER", "ADAPTER_IN", "BOOTSTRAP"]
        mock_client.get_layers.return_value = [
            MagicMock(code=c, id=i, architecture_id=1, name=c, description="", order_index=i)
            for i, c in enumerate(db_codes)
        ]
        mock_get_client.return_value = mock_client

        warnings = self.registry.validate()

        # 2개 경고: 새 코드 추가 필요 + 기존 코드 DB에 없음
        assert len(warnings) == 2
        warning_text = " ".join(warnings)
        assert "PERSISTENCE_ADAPTER" in warning_text
        assert "ADAPTER_OUT" in warning_text

    @patch("src.layer_registry.get_api_client")
    def test_validate_cached_after_first_call(self, mock_get_client):
        """검증은 한 번만 실행됨 (캐싱)"""
        mock_client = MagicMock()
        db_codes = ["DOMAIN", "APPLICATION", "ADAPTER_OUT", "ADAPTER_IN", "BOOTSTRAP"]
        mock_client.get_layers.return_value = [
            MagicMock(code=c, id=i, architecture_id=1, name=c, description="", order_index=i)
            for i, c in enumerate(db_codes)
        ]
        mock_get_client.return_value = mock_client

        warnings1 = self.registry.validate()
        warnings2 = self.registry.validate()

        assert warnings1 == []
        assert warnings2 == []
        # DB는 한 번만 조회
        mock_client.get_layers.assert_called_once()

    @patch("src.layer_registry.get_api_client")
    def test_validate_and_log_success(self, mock_get_client):
        """validate_and_log - 성공 시 True 반환"""
        mock_client = MagicMock()
        db_codes = ["DOMAIN", "APPLICATION", "ADAPTER_OUT", "ADAPTER_IN", "BOOTSTRAP"]
        mock_client.get_layers.return_value = [
            MagicMock(code=c, id=i, architecture_id=1, name=c, description="", order_index=i)
            for i, c in enumerate(db_codes)
        ]
        mock_get_client.return_value = mock_client

        result = self.registry.validate_and_log()

        assert result is True

    @patch("src.layer_registry.get_api_client")
    def test_validate_and_log_with_warnings(self, mock_get_client):
        """validate_and_log - 경고 시 False 반환"""
        mock_client = MagicMock()
        # 새 레이어 추가
        db_codes = ["DOMAIN", "APPLICATION", "ADAPTER_OUT", "ADAPTER_IN", "BOOTSTRAP", "NEW_LAYER"]
        mock_client.get_layers.return_value = [
            MagicMock(code=c, id=i, architecture_id=1, name=c, description="", order_index=i)
            for i, c in enumerate(db_codes)
        ]
        mock_get_client.return_value = mock_client

        result = self.registry.validate_and_log()

        assert result is False


class TestLayerRegistrySingleton:
    """싱글톤 패턴 테스트"""

    def test_get_layer_registry_returns_same_instance(self):
        """싱글톤 인스턴스 반환 확인"""
        # 모듈 수준 싱글톤 초기화
        import src.layer_registry as module

        module._registry = None  # 초기화

        r1 = get_layer_registry()
        r2 = get_layer_registry()

        assert r1 is r2
