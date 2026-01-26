"""
AWS Lambda Handler for Spring Standards MCP Server

MCP Streamable HTTP Transport 스펙 준수 (2025-03-26)
- POST: JSON-RPC 요청 처리
- GET: SSE 미지원 → 405 반환
- MCP-Protocol-Version 헤더 처리
- Mcp-Session-Id 세션 관리

References:
- https://modelcontextprotocol.io/specification/2025-06-18/basic/transports
"""

import asyncio
import json
import logging
import uuid
from typing import Any

# ruff: noqa: E402
# MCP 서버 임포트 (로깅 설정 후 import 필요)
from .server import mcp  # noqa: E402

# 로깅 설정
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# 지원 프로토콜 버전
SUPPORTED_PROTOCOL_VERSIONS = ["2025-03-26", "2024-11-05"]
DEFAULT_PROTOCOL_VERSION = "2025-03-26"


async def handle_test_db_connection() -> dict:
    """PostgreSQL 연결 테스트"""
    import os
    from urllib.parse import quote

    try:
        import asyncpg
    except ImportError:
        return {
            "status": "error",
            "message": "asyncpg not installed",
            "connection": None,
        }

    # 환경 변수 확인
    host = os.environ.get("PG_HOST")
    port = os.environ.get("PG_PORT", "5432")
    database = os.environ.get("PG_DATABASE", "shared_api")
    user = os.environ.get("PG_USER", "shared_api_user")
    password = os.environ.get("PG_PASSWORD", "")

    if not host:
        return {
            "status": "error",
            "message": "PG_HOST environment variable not set",
            "env_vars": {
                "PG_HOST": host,
                "PG_PORT": port,
                "PG_DATABASE": database,
                "PG_USER": user,
                "PG_PASSWORD": "***" if password else None,
            },
        }

    # 비밀번호 URL 인코딩 (특수문자 처리)
    encoded_password = quote(password, safe="")
    dsn = f"postgresql://{user}:{encoded_password}@{host}:{port}/{database}"
    logger.info(f"Testing PostgreSQL connection to {host}:{port}/{database}")

    try:
        # 연결 테스트
        conn = await asyncpg.connect(dsn, timeout=10)
        try:
            # SELECT 1 테스트
            result = await conn.fetchval("SELECT 1")
            # 버전 정보
            version = await conn.fetchval("SELECT version()")
            # 현재 시간
            db_time = await conn.fetchval("SELECT NOW()")

            return {
                "status": "success",
                "message": "PostgreSQL connection successful",
                "connection": {
                    "host": host,
                    "port": port,
                    "database": database,
                    "user": user,
                },
                "test_results": {
                    "select_1": result,
                    "db_time": str(db_time),
                    "version": version[:100] if version else None,
                },
            }
        finally:
            await conn.close()

    except asyncpg.InvalidCatalogNameError as e:
        return {
            "status": "error",
            "message": f"Database does not exist: {database}",
            "error_type": "InvalidCatalogNameError",
            "details": str(e),
        }
    except asyncpg.InvalidPasswordError as e:
        return {
            "status": "error",
            "message": "Invalid password",
            "error_type": "InvalidPasswordError",
            "details": str(e),
        }
    except OSError as e:
        return {
            "status": "error",
            "message": f"Connection failed (network/host): {str(e)}",
            "error_type": "OSError",
            "connection": {"host": host, "port": port, "database": database},
        }
    except Exception as e:
        return {
            "status": "error",
            "message": f"Connection failed: {str(e)}",
            "error_type": type(e).__name__,
            "details": str(e),
        }


def create_jsonrpc_response(id: Any, result: Any) -> dict:
    """JSON-RPC 2.0 성공 응답 생성"""
    return {"jsonrpc": "2.0", "id": id, "result": result}


def create_jsonrpc_error(id: Any, code: int, message: str, data: Any = None) -> dict:
    """JSON-RPC 2.0 에러 응답 생성"""
    error = {"code": code, "message": message}
    if data is not None:
        error["data"] = data
    return {"jsonrpc": "2.0", "id": id, "error": error}


async def handle_tools_list() -> dict:
    """tools/list 요청 처리"""
    tools = await mcp.get_tools()  # async 함수이므로 await 필요
    tool_list = []
    for tool in tools.values():
        tool_info = {
            "name": tool.name,
            "description": tool.description or "",
        }
        # inputSchema 추가
        if hasattr(tool, "parameters") and tool.parameters:
            tool_info["inputSchema"] = tool.parameters
        tool_list.append(tool_info)
    return {"tools": tool_list}


async def handle_tools_call(params: dict) -> dict:
    """tools/call 요청 처리"""
    tool_name = params.get("name")
    arguments = params.get("arguments", {})

    if not tool_name:
        raise ValueError("Tool name is required")

    # FastMCP의 tool 가져오기 (async 함수이므로 await 필요)
    tool = await mcp.get_tool(tool_name)
    if not tool:
        raise ValueError(f"Tool not found: {tool_name}")

    # Tool 실행
    try:
        result = await tool.run(arguments)

        # ToolResult 객체인 경우 content 추출
        if hasattr(result, "to_mcp_result"):
            mcp_result = result.to_mcp_result()
            # CallToolResult 또는 list[ContentBlock] 형태
            if hasattr(mcp_result, "content"):
                # CallToolResult 객체
                content_blocks = mcp_result.content
            elif isinstance(mcp_result, tuple):
                # (list[ContentBlock], meta) 튜플
                content_blocks = mcp_result[0]
            else:
                # list[ContentBlock]
                content_blocks = mcp_result

            # ContentBlock을 dict로 변환 (None 값 제외하여 MCP 스펙 준수)
            content = []
            for block in content_blocks:
                if hasattr(block, "model_dump"):
                    # exclude_none=True로 annotations: null 등 제거
                    content.append(block.model_dump(exclude_none=True))
                elif hasattr(block, "__dict__"):
                    content.append({"type": block.type, "text": block.text})
                else:
                    content.append({"type": "text", "text": str(block)})

            return {"content": content}
        else:
            # 일반 결과값
            return {
                "content": [
                    {
                        "type": "text",
                        "text": json.dumps(result, ensure_ascii=False, default=str)
                        if not isinstance(result, str)
                        else result,
                    }
                ]
            }
    except Exception as e:
        logger.exception(f"Tool execution error: {tool_name}")
        return {
            "content": [{"type": "text", "text": f"Error: {str(e)}"}],
            "isError": True,
        }


async def handle_initialize(params: dict, client_protocol_version: str = None) -> dict:
    """initialize 요청 처리 - MCP Streamable HTTP 스펙 준수"""
    # 클라이언트 요청 프로토콜 버전 확인
    client_version = params.get("protocolVersion", client_protocol_version)

    # 협상된 프로토콜 버전 결정
    negotiated_version = DEFAULT_PROTOCOL_VERSION
    if client_version in SUPPORTED_PROTOCOL_VERSIONS:
        negotiated_version = client_version

    return {
        "protocolVersion": negotiated_version,
        "capabilities": {
            "tools": {"listChanged": False},
        },
        "serverInfo": {
            "name": mcp.name,
            "version": mcp.version or "1.1.0",
        },
    }


async def handle_request(method: str, params: dict | None) -> Any:
    """MCP 메서드 라우팅"""
    handlers = {
        "initialize": handle_initialize,
        "tools/list": handle_tools_list,
        "tools/call": handle_tools_call,
        "test/db_connection": handle_test_db_connection,
    }

    handler = handlers.get(method)
    if not handler:
        raise ValueError(f"Unknown method: {method}")

    # 파라미터가 필요 없는 메서드들
    no_params_methods = {"tools/list", "test/db_connection"}
    if method in no_params_methods:
        return await handler()
    else:
        return await handler(params or {})


def get_cors_headers() -> dict:
    """CORS 헤더 반환 - 원격 접근 허용"""
    return {
        "Access-Control-Allow-Origin": "*",
        "Access-Control-Allow-Methods": "GET, POST, DELETE, OPTIONS",
        "Access-Control-Allow-Headers": (
            "Content-Type, Accept, MCP-Protocol-Version, Mcp-Session-Id"
        ),
        "Access-Control-Expose-Headers": "Mcp-Session-Id, MCP-Protocol-Version",
    }


def handler(event: dict, context: Any) -> dict:
    """Lambda 핸들러 엔트리포인트

    MCP Streamable HTTP Transport 스펙 준수:
    - POST /mcp: JSON-RPC 요청 처리
    - GET /mcp: SSE 미지원 → 405
    - DELETE /mcp: 세션 종료 → 204
    - OPTIONS: CORS preflight
    """
    logger.info(f"Received event: {json.dumps(event)[:500]}")

    # 기본 응답 헤더 (CORS 포함)
    base_headers = {
        "Content-Type": "application/json",
        **get_cors_headers(),
    }

    try:
        # HTTP 메서드 확인 (Lambda Function URL 또는 API Gateway)
        http_method = event.get("requestContext", {}).get("http", {}).get("method")
        if not http_method:
            http_method = event.get("httpMethod", "POST")

        # 요청 헤더 추출 (대소문자 무관하게 처리)
        raw_headers = event.get("headers", {}) or {}
        headers = {k.lower(): v for k, v in raw_headers.items()}

        # MCP 프로토콜 버전 헤더 확인
        mcp_protocol_version = headers.get("mcp-protocol-version", DEFAULT_PROTOCOL_VERSION)

        # 세션 ID 확인/생성
        session_id = headers.get("mcp-session-id")

        # OPTIONS: CORS preflight
        if http_method == "OPTIONS":
            return {
                "statusCode": 204,
                "headers": base_headers,
                "body": "",
            }

        # GET: SSE 미지원 → 405
        if http_method == "GET":
            return {
                "statusCode": 405,
                "headers": {
                    **base_headers,
                    "Allow": "POST, DELETE, OPTIONS",
                },
                "body": json.dumps({
                    "error": "SSE streaming not supported. Use POST for JSON-RPC requests."
                }),
            }

        # DELETE: 세션 종료
        if http_method == "DELETE":
            logger.info(f"Session terminated: {session_id}")
            return {
                "statusCode": 204,
                "headers": base_headers,
                "body": "",
            }

        # POST: JSON-RPC 요청 처리
        if http_method != "POST":
            return {
                "statusCode": 405,
                "headers": {
                    **base_headers,
                    "Allow": "POST, DELETE, OPTIONS",
                },
                "body": json.dumps({"error": f"Method {http_method} not allowed"}),
            }

        # Body 파싱
        body = event.get("body", "{}")
        if event.get("isBase64Encoded", False):
            import base64
            body = base64.b64decode(body).decode("utf-8")
        request = json.loads(body) if isinstance(body, str) else body

        logger.info(f"Parsed request: {json.dumps(request)[:500]}")

        # JSON-RPC 요청 검증
        jsonrpc_version = request.get("jsonrpc")
        request_id = request.get("id")
        method = request.get("method")
        params = request.get("params")

        # 응답 헤더 (프로토콜 버전 포함)
        response_headers = {
            **base_headers,
            "MCP-Protocol-Version": mcp_protocol_version,
        }

        if jsonrpc_version != "2.0":
            response = create_jsonrpc_error(
                request_id, -32600, "Invalid Request: jsonrpc must be '2.0'"
            )
        elif not method:
            response = create_jsonrpc_error(
                request_id, -32600, "Invalid Request: method is required"
            )
        else:
            # 비동기 핸들러 실행
            try:
                result = asyncio.run(handle_request(method, params))
                response = create_jsonrpc_response(request_id, result)

                # initialize 응답에 세션 ID 추가
                if method == "initialize":
                    new_session_id = str(uuid.uuid4())
                    response_headers["Mcp-Session-Id"] = new_session_id
                    logger.info(f"New session created: {new_session_id}")

            except ValueError as e:
                response = create_jsonrpc_error(request_id, -32601, str(e))
            except Exception as e:
                logger.exception("Request handling error")
                response = create_jsonrpc_error(
                    request_id, -32603, f"Internal error: {str(e)}"
                )

        logger.info(f"Response: {json.dumps(response)[:500]}")

        return {
            "statusCode": 200,
            "headers": response_headers,
            "body": json.dumps(response, ensure_ascii=False),
            "isBase64Encoded": False,
        }

    except json.JSONDecodeError as e:
        logger.error(f"JSON decode error: {e}")
        error_response = create_jsonrpc_error(None, -32700, f"Parse error: {str(e)}")
        return {
            "statusCode": 200,
            "headers": base_headers,
            "body": json.dumps(error_response),
            "isBase64Encoded": False,
        }
    except Exception as e:
        logger.exception("Unexpected error")
        error_response = create_jsonrpc_error(None, -32603, f"Internal error: {str(e)}")
        return {
            "statusCode": 500,
            "headers": base_headers,
            "body": json.dumps(error_response),
            "isBase64Encoded": False,
        }
