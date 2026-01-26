#!/usr/bin/env python3
"""
MCP Lambda Bridge

stdio ↔ HTTP Lambda 브릿지
Claude Code (stdio) → Lambda Function URL (HTTP)
"""

import json
import sys
import urllib.request
import urllib.error

LAMBDA_URL = "https://wkig37k7dk62o2dqepkz5eyulu0iviuo.lambda-url.ap-northeast-2.on.aws/"


def send_to_lambda(message: dict) -> dict:
    """Lambda에 요청 전송"""
    data = json.dumps(message).encode("utf-8")
    req = urllib.request.Request(
        LAMBDA_URL,
        data=data,
        headers={"Content-Type": "application/json"},
        method="POST",
    )
    try:
        with urllib.request.urlopen(req, timeout=30) as response:
            return json.loads(response.read().decode("utf-8"))
    except urllib.error.HTTPError as e:
        return {"error": {"code": e.code, "message": str(e)}}
    except Exception as e:
        return {"error": {"code": -1, "message": str(e)}}


def main():
    """메인 루프: stdin에서 읽고 Lambda로 전송, 결과를 stdout으로"""
    for line in sys.stdin:
        line = line.strip()
        if not line:
            continue
        try:
            message = json.loads(line)
            response = send_to_lambda(message)
            print(json.dumps(response), flush=True)
        except json.JSONDecodeError:
            error_response = {
                "jsonrpc": "2.0",
                "error": {"code": -32700, "message": "Parse error"},
                "id": None,
            }
            print(json.dumps(error_response), flush=True)


if __name__ == "__main__":
    main()
