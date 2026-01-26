"""
MCP Server Configuration

Spring REST API 연결 설정 및 환경변수 관리
"""

import os
from dataclasses import dataclass
from typing import Optional


@dataclass(frozen=True)
class ApiConfig:
    """Spring REST API 연결 설정"""

    base_url: str
    timeout: float = 30.0

    @classmethod
    def from_env(cls) -> "ApiConfig":
        """환경변수에서 설정 로드"""
        return cls(
            base_url=os.getenv("API_BASE_URL", "http://localhost:8080"),
            timeout=float(os.getenv("API_TIMEOUT", "30.0")),
        )


@dataclass(frozen=True)
class ServerConfig:
    """MCP 서버 설정"""

    name: str = "conventionHub"
    version: str = "1.0.0"
    description: str = "Spring Boot 코딩 컨벤션 및 아키텍처 규칙 조회 서버"

    # 기본 Architecture ID (Hexagonal Architecture)
    default_architecture_id: Optional[int] = None

    @classmethod
    def from_env(cls) -> "ServerConfig":
        """환경변수에서 설정 로드"""
        arch_id = os.getenv("DEFAULT_ARCHITECTURE_ID")
        return cls(
            name=os.getenv("MCP_SERVER_NAME", "conventionHub"),
            version=os.getenv("MCP_SERVER_VERSION", "1.0.0"),
            default_architecture_id=int(arch_id) if arch_id else 1,
        )


# 싱글톤 설정 인스턴스
_api_config: Optional[ApiConfig] = None
_server_config: Optional[ServerConfig] = None


def get_api_config() -> ApiConfig:
    """API 설정 싱글톤 반환"""
    global _api_config
    if _api_config is None:
        _api_config = ApiConfig.from_env()
    return _api_config


def get_server_config() -> ServerConfig:
    """서버 설정 싱글톤 반환"""
    global _server_config
    if _server_config is None:
        _server_config = ServerConfig.from_env()
    return _server_config
