"""
get_layer_detail Tool

레이어 상세 조회 (순수 정보 브릿지)
"""

from typing import Any, Optional

from ..api_client import get_api_client


def get_layer_detail(
    layer_id: Optional[int] = None, layer_code: Optional[str] = None
) -> dict[str, Any]:
    """레이어 상세 조회. DOMAIN, APPLICATION, ADAPTER_OUT, ADAPTER_IN 등 레이어 정보 반환"""
    client = get_api_client()

    if layer_id:
        layer = client.get_layer_by_id(layer_id)
    elif layer_code:
        layer = client.get_layer_by_code(layer_code.upper())
    else:
        # 전체 목록 반환
        layers = client.get_layers()
        return {
            "layers": [
                {
                    "id": layer_item.id,
                    "architecture_id": layer_item.architecture_id,
                    "code": layer_item.code,
                    "name": layer_item.name,
                    "description": layer_item.description,
                    "order_index": layer_item.order_index,
                }
                for layer_item in layers
            ],
            "count": len(layers),
        }

    if not layer:
        return {"error": f"Layer not found: {layer_id or layer_code}"}

    return {
        "layer": {
            "id": layer.id,
            "architecture_id": layer.architecture_id,
            "code": layer.code,
            "name": layer.name,
            "description": layer.description,
            "order_index": layer.order_index,
        }
    }
