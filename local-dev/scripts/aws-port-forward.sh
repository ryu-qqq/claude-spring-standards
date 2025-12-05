#!/bin/bash
# ========================================
# AWS SSM 포트 포워딩 스크립트
# ========================================
# Bastion Host를 통해 AWS RDS, ElastiCache에 로컬에서 접근
#
# 사전 조건:
#   1. AWS CLI 설치: brew install awscli
#   2. Session Manager Plugin 설치:
#      https://docs.aws.amazon.com/systems-manager/latest/userguide/session-manager-working-with-install-plugin.html
#   3. AWS 자격 증명 설정: aws configure 또는 AWS SSO
#   4. .env.aws 파일에 설정값 입력
#
# 사용법:
#   ./scripts/aws-port-forward.sh
#   (별도 터미널에서 실행 - 포그라운드로 계속 실행됨)
# ========================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LOCAL_DEV_DIR="$(dirname "$SCRIPT_DIR")"

# .env.aws 파일 로드
if [ -f "$LOCAL_DEV_DIR/.env.aws" ]; then
    export $(grep -v '^#' "$LOCAL_DEV_DIR/.env.aws" | xargs)
else
    echo "❌ .env.aws 파일이 없습니다."
    echo "   cp .env.aws.example .env.aws 로 생성 후 값을 설정하세요."
    exit 1
fi

# 필수 환경 변수 확인
check_required_vars() {
    local missing=()

    [ -z "$AWS_BASTION_INSTANCE_ID" ] && missing+=("AWS_BASTION_INSTANCE_ID")
    [ -z "$AWS_RDS_ENDPOINT" ] && missing+=("AWS_RDS_ENDPOINT")
    [ -z "$AWS_RDS_PORT" ] && missing+=("AWS_RDS_PORT")
    [ -z "$AWS_RDS_LOCAL_PORT" ] && missing+=("AWS_RDS_LOCAL_PORT")
    [ -z "$AWS_REDIS_ENDPOINT" ] && missing+=("AWS_REDIS_ENDPOINT")
    [ -z "$AWS_REDIS_PORT" ] && missing+=("AWS_REDIS_PORT")
    [ -z "$AWS_REDIS_LOCAL_PORT" ] && missing+=("AWS_REDIS_LOCAL_PORT")

    if [ ${#missing[@]} -ne 0 ]; then
        echo "❌ 필수 환경 변수가 설정되지 않았습니다:"
        printf '   - %s\n' "${missing[@]}"
        exit 1
    fi
}

# AWS CLI 및 Session Manager Plugin 확인
check_prerequisites() {
    if ! command -v aws &> /dev/null; then
        echo "❌ AWS CLI가 설치되지 않았습니다."
        echo "   설치: brew install awscli"
        exit 1
    fi

    if ! aws ssm start-session --help &> /dev/null; then
        echo "❌ Session Manager Plugin이 설치되지 않았습니다."
        echo "   설치 가이드: https://docs.aws.amazon.com/systems-manager/latest/userguide/session-manager-working-with-install-plugin.html"
        exit 1
    fi

    # AWS 자격 증명 확인
    if ! aws sts get-caller-identity &> /dev/null; then
        echo "❌ AWS 자격 증명이 유효하지 않습니다."
        echo "   aws configure 또는 aws sso login 을 실행하세요."
        exit 1
    fi
}

# 포트 포워딩 시작
start_port_forwarding() {
    echo "🚀 AWS SSM 포트 포워딩을 시작합니다..."
    echo ""
    echo "📍 Bastion Host: $AWS_BASTION_INSTANCE_ID"
    echo ""
    echo "📍 포트 매핑:"
    echo "   - MySQL (RDS):   localhost:$AWS_RDS_LOCAL_PORT → $AWS_RDS_ENDPOINT:$AWS_RDS_PORT"
    echo "   - Redis (Cache): localhost:$AWS_REDIS_LOCAL_PORT → $AWS_REDIS_ENDPOINT:$AWS_REDIS_PORT"
    echo ""
    echo "⏳ 포트 포워딩 세션을 시작합니다... (Ctrl+C로 종료)"
    echo ""

    # 병렬로 두 개의 포트 포워딩 실행
    # 백그라운드로 실행하고 trap으로 종료 처리

    cleanup() {
        echo ""
        echo "🛑 포트 포워딩을 종료합니다..."
        kill $(jobs -p) 2>/dev/null
        exit 0
    }

    trap cleanup SIGINT SIGTERM

    # RDS 포트 포워딩
    aws ssm start-session \
        --target "$AWS_BASTION_INSTANCE_ID" \
        --document-name AWS-StartPortForwardingSessionToRemoteHost \
        --parameters "{\"host\":[\"$AWS_RDS_ENDPOINT\"],\"portNumber\":[\"$AWS_RDS_PORT\"],\"localPortNumber\":[\"$AWS_RDS_LOCAL_PORT\"]}" \
        --region "${AWS_REGION:-ap-northeast-2}" &

    sleep 2

    # Redis 포트 포워딩
    aws ssm start-session \
        --target "$AWS_BASTION_INSTANCE_ID" \
        --document-name AWS-StartPortForwardingSessionToRemoteHost \
        --parameters "{\"host\":[\"$AWS_REDIS_ENDPOINT\"],\"portNumber\":[\"$AWS_REDIS_PORT\"],\"localPortNumber\":[\"$AWS_REDIS_LOCAL_PORT\"]}" \
        --region "${AWS_REGION:-ap-northeast-2}" &

    echo "✅ 포트 포워딩이 시작되었습니다!"
    echo ""
    echo "📝 연결 테스트:"
    echo "   - MySQL: mysql -h localhost -P $AWS_RDS_LOCAL_PORT -u admin -p"
    echo "   - Redis: redis-cli -h localhost -p $AWS_REDIS_LOCAL_PORT"
    echo ""

    # 백그라운드 프로세스 대기
    wait
}

# 메인 실행
echo "=========================================="
echo " AWS SSM Port Forwarding"
echo "=========================================="
echo ""

check_required_vars
check_prerequisites
start_port_forwarding
