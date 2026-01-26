#!/bin/bash
# Lambda 배포 패키지 빌드 스크립트
# tree-sitter C 확장 호환성을 위해 Amazon Linux 2023 Docker 이미지 사용

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BUILD_DIR="${SCRIPT_DIR}/build"
OUTPUT_FILE="${SCRIPT_DIR}/lambda-package.zip"

echo "🔧 Lambda 배포 패키지 빌드 시작..."

# 이전 빌드 정리
rm -rf "${BUILD_DIR}"
rm -f "${OUTPUT_FILE}"
mkdir -p "${BUILD_DIR}"

# Docker로 의존성 빌드 (Amazon Linux 2023 + Python 3.12)
echo "📦 Docker로 의존성 설치 중..."

# GitHub Actions에서 실행 중인지 확인하여 권한 처리
HOST_UID=$(id -u)
HOST_GID=$(id -g)

docker run --rm \
    --platform linux/amd64 \
    --entrypoint /bin/bash \
    -v "${SCRIPT_DIR}:/app" \
    -w /app \
    -e HOST_UID="${HOST_UID}" \
    -e HOST_GID="${HOST_GID}" \
    public.ecr.aws/lambda/python:3.12 \
    -c "
        pip install -r requirements.txt -t /app/build --no-cache-dir
        # 불필요한 파일 정리
        find /app/build -type d -name '__pycache__' -exec rm -rf {} + 2>/dev/null || true
        find /app/build -type d -name '*.dist-info' -exec rm -rf {} + 2>/dev/null || true
        find /app/build -type d -name 'tests' -exec rm -rf {} + 2>/dev/null || true
        find /app/build -type f -name '*.pyc' -delete 2>/dev/null || true
        # 호스트 사용자에게 권한 부여 (CI 환경에서 cleanup 가능하도록)
        chown -R \${HOST_UID}:\${HOST_GID} /app/build 2>/dev/null || true
    "

# 소스 코드 복사
echo "📁 소스 코드 복사 중..."
cp -r "${SCRIPT_DIR}/src" "${BUILD_DIR}/"

# ZIP 패키지 생성
echo "📦 ZIP 패키지 생성 중..."
cd "${BUILD_DIR}"
zip -r9 "${OUTPUT_FILE}" . -x "*.pyc" -x "__pycache__/*"

# 패키지 크기 확인
PACKAGE_SIZE=$(du -h "${OUTPUT_FILE}" | cut -f1)
echo "✅ Lambda 패키지 빌드 완료: ${OUTPUT_FILE} (${PACKAGE_SIZE})"

# 정리
rm -rf "${BUILD_DIR}"
echo "🧹 빌드 디렉토리 정리 완료"
