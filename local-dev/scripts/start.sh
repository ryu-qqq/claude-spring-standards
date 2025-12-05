#!/bin/bash
# ========================================
# Spring Standards Template - 로컬 환경 시작
# ========================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LOCAL_DEV_DIR="$(dirname "$SCRIPT_DIR")"

echo "🚀 로컬 개발 환경을 시작합니다..."

cd "$LOCAL_DEV_DIR"

# Docker Compose 실행
docker-compose -f docker-compose.local.yml up -d --build

echo ""
echo "✅ 로컬 환경이 시작되었습니다!"
echo ""
echo "📍 서비스 URL:"
echo "   - Web API:          http://localhost:8080"
echo "   - Swagger UI:       http://localhost:8080/swagger-ui.html"
echo ""
echo "🖥️  Admin Tools:"
echo "   - phpMyAdmin:       http://localhost:18080  (MySQL 관리)"
echo "   - Redis Commander:  http://localhost:18081  (admin/admin)"
echo ""
echo "📦 인프라:"
echo "   - MySQL:            localhost:13306"
echo "   - Redis:            localhost:16379"
echo ""
echo "📝 로그 확인: docker-compose -f docker-compose.local.yml logs -f"
echo "🛑 종료: ./scripts/stop.sh"
