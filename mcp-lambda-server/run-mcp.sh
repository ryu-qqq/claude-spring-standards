#!/bin/bash
cd /Users/sangwon-ryu/claude-spring-standards/mcp-lambda-server
export PYTHONWARNINGS=ignore
export API_BASE_URL="${API_BASE_URL:-https://api.set-of.com}"
exec /Library/Frameworks/Python.framework/Versions/3.12/bin/python3 -m src.server
