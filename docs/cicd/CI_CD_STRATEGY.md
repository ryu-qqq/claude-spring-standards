# CI/CD 전략 - 멀티모듈 Spring 프로젝트

> **목표**: 변경된 부트스트랩만 선택적으로 빌드/배포하여 AWS ECS Blue/Green 배포 수행

---

## 📊 현재 상황 분석

### ✅ 이미 구현된 것들

1. **변경 감지 시스템** (`ci-module-validation.yml`)
   - `dorny/paths-filter` 사용
   - 모듈별 변경 감지 완벽 구현

2. **모듈별 선택적 검증**
   - Domain, Application, Adapter 모듈별 독립 검증
   - ArchUnit 자동 실행

3. **코드 품질 검증**
   - Checkstyle, SpotBugs, PMD
   - Lombok 금지 검증 (빌드 시)
   - JaCoCo 커버리지 (Domain 90%, Application 80%)

### ❌ 추가 필요한 것들

1. **Docker 빌드 및 ECR 배포**
2. **ECS Blue/Green 배포**
3. **Git Hooks 체계**
4. **Config 패키지 검증 자동화**

---

## 🎯 전체 CI/CD 파이프라인 설계

### 1단계: 로컬 개발 환경 (Git Hooks)

```
Developer
    ↓
[pre-commit]
    ├─ Spotless 자동 포맷팅 ✅
    ├─ Checkstyle 검증 ✅
    └─ Config 파일 검증 ✅
    ↓
[commit]
    ↓
[pre-push]
    ├─ 변경된 모듈만 빌드 ✅
    ├─ 변경된 모듈만 테스트 ✅
    └─ ArchUnit (Zero-Tolerance) ✅
    ↓
[push to remote]
```

**원칙**:
- ✅ **pre-commit**: 빠른 검증 (포맷, 정적 분석, 설정 검증)
- ✅ **pre-push**: 느린 검증 (빌드, 테스트)
- ❌ **pre-commit에 빌드 실행 금지** (너무 느림, 개발 경험 저하)

### 2단계: Pull Request (GitHub Actions)

```
PR 생성
    ↓
[ci-build-test.yml] (PR → main/develop)
    ├─ Code Quality (Checkstyle, SpotBugs, PMD)
    ├─ Build All Modules
    ├─ Unit Tests
    ├─ ArchUnit Tests (Zero-Tolerance)
    └─ Integration Tests (선택적)
    ↓
[ci-module-validation.yml] (변경 감지)
    ├─ Detect Changed Modules
    ├─ Validate Changed Modules (선택적)
    └─ Summary Report
    ↓
[Code Review]
    ├─ CodeRabbit AI Review
    └─ Human Review
    ↓
[Merge to main]
```

### 3단계: 배포 (main 브랜치 머지 후)

```
Merge to main
    ↓
[cd-deploy.yml] (NEW 필요)
    ├─ Detect Changed Bootstraps ⭐
    │  ├─ bootstrap-web-api 변경?
    │  └─ bootstrap-scheduler 변경?
    ├─ Build Docker Images (변경된 것만)
    │  ├─ Web API → spring-hexagonal-web-api:${version}
    │  └─ Scheduler → spring-hexagonal-scheduler:${version}
    ├─ Push to ECR
    │  ├─ ECR Repository: company/spring-hexagonal-web-api
    │  └─ ECR Repository: company/spring-hexagonal-scheduler
    ├─ Update ECS Task Definition
    │  ├─ Web API Task Definition
    │  └─ Scheduler Task Definition
    └─ Trigger ECS Blue/Green Deployment
       ├─ CodeDeploy Deployment Group
       ├─ Blue/Green 배포 실행
       └─ 트래픽 전환 (10% → 50% → 100%)
```

---

## 🔧 구현 세부 전략

### 1. Git Hooks 전략

**위치**: `hooks/` (프로젝트 루트)

#### `hooks/pre-commit`
```bash
#!/bin/bash
set -e

echo "🔍 [pre-commit] Running code quality checks..."

# 1. Spotless 자동 포맷팅
echo "  ✅ Running Spotless..."
./gradlew spotlessApply

# 2. Checkstyle 검증
echo "  ✅ Running Checkstyle..."
./gradlew checkstyleMain checkstyleTest

# 3. Config 파일 검증
echo "  ✅ Validating config files..."
python3 .claude/hooks/scripts/validation-helper.py

echo "✅ [pre-commit] All checks passed!"
```

**특징**:
- ✅ 빠른 검증만 (< 30초)
- ✅ 자동 수정 가능한 것은 자동 수정 (Spotless)
- ✅ Config 파일 검증 (validation-helper.py 재사용)

#### `hooks/pre-push`
```bash
#!/bin/bash
set -e

echo "🚀 [pre-push] Running build and tests..."

# 1. 변경된 파일 감지
CHANGED_FILES=$(git diff --name-only @{upstream}..HEAD)
CHANGED_MODULES=""

# 2. 변경된 모듈 감지
if echo "$CHANGED_FILES" | grep -q "^domain/"; then
    CHANGED_MODULES="$CHANGED_MODULES :domain"
fi
if echo "$CHANGED_FILES" | grep -q "^application/"; then
    CHANGED_MODULES="$CHANGED_MODULES :application"
fi
if echo "$CHANGED_FILES" | grep -q "^adapter-in/"; then
    CHANGED_MODULES="$CHANGED_MODULES :adapter-in:rest-api"
fi
if echo "$CHANGED_FILES" | grep -q "^adapter-out/"; then
    CHANGED_MODULES="$CHANGED_MODULES :adapter-out:persistence-mysql"
fi
if echo "$CHANGED_FILES" | grep -q "^bootstrap/"; then
    CHANGED_MODULES="$CHANGED_MODULES :bootstrap:bootstrap-web-api :bootstrap:bootstrap-scheduler"
fi

# 3. 변경된 모듈만 빌드 및 테스트
if [ -z "$CHANGED_MODULES" ]; then
    echo "  ℹ️  No module changes detected, skipping build..."
else
    echo "  🔨 Building changed modules: $CHANGED_MODULES"
    for module in $CHANGED_MODULES; do
        ./gradlew ${module}:build
    done
fi

echo "✅ [pre-push] All checks passed!"
```

**특징**:
- ✅ 변경된 모듈만 빌드/테스트 (시간 절약)
- ✅ ArchUnit 자동 실행 (빌드에 포함)
- ✅ 실패 시 push 중단

#### Git Hooks 설치 자동화
```bash
# hooks/install-hooks.sh
#!/bin/bash

HOOKS_DIR=".git/hooks"
PROJECT_HOOKS_DIR="hooks"

echo "📦 Installing Git Hooks..."

# Copy hooks
cp "$PROJECT_HOOKS_DIR/pre-commit" "$HOOKS_DIR/pre-commit"
cp "$PROJECT_HOOKS_DIR/pre-push" "$HOOKS_DIR/pre-push"

# Make executable
chmod +x "$HOOKS_DIR/pre-commit"
chmod +x "$HOOKS_DIR/pre-push"

echo "✅ Git Hooks installed successfully!"
```

**gradle 통합**:
```gradle
// build.gradle 추가
tasks.register('installGitHooks') {
    group = 'build setup'
    description = 'Install Git Hooks'

    doLast {
        exec {
            commandLine 'bash', 'hooks/install-hooks.sh'
        }
    }
}

// 자동 실행
tasks.named('build') {
    dependsOn 'installGitHooks'
}
```

---

### 2. 변경된 Bootstrap만 Docker 빌드 전략

**핵심 아이디어**:
- ✅ `bootstrap/bootstrap-web-api` 변경 → Web API Docker 이미지만 빌드
- ✅ `bootstrap/bootstrap-scheduler` 변경 → Scheduler Docker 이미지만 빌드
- ✅ Domain/Application 변경 → 의존하는 모든 Bootstrap 빌드

#### Dockerfile 구조

**`bootstrap/bootstrap-web-api/Dockerfile`**:
```dockerfile
# Stage 1: Build
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app

# Copy Gradle wrapper and build files
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle gradle.properties ./

# Copy all modules (for dependency resolution)
COPY domain domain
COPY application application
COPY adapter-in adapter-in
COPY adapter-out adapter-out
COPY bootstrap/bootstrap-web-api bootstrap/bootstrap-web-api

# Build
RUN ./gradlew :bootstrap:bootstrap-web-api:build -x test

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy JAR
COPY --from=builder /app/bootstrap/bootstrap-web-api/build/libs/*.jar app.jar

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**`bootstrap/bootstrap-scheduler/Dockerfile`**:
```dockerfile
# 동일 구조, 포트만 다름 (8081)
```

#### GitHub Actions: CD 워크플로우

**`.github/workflows/cd-deploy.yml`** (NEW):
```yaml
name: CD - Deploy to AWS ECS

on:
  push:
    branches:
      - main
    paths:
      - 'domain/**'
      - 'application/**'
      - 'adapter-**/**'
      - 'bootstrap/**'

env:
  AWS_REGION: ap-northeast-2
  ECR_REGISTRY: 123456789012.dkr.ecr.ap-northeast-2.amazonaws.com

jobs:
  # ========================================
  # Step 1: Detect Changed Bootstraps
  # ========================================
  detect-changes:
    name: Detect Changed Bootstraps
    runs-on: ubuntu-latest
    outputs:
      web-api: ${{ steps.filter.outputs.web-api }}
      scheduler: ${{ steps.filter.outputs.scheduler }}

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Detect bootstrap changes
        uses: dorny/paths-filter@v3
        id: filter
        with:
          filters: |
            web-api:
              - 'domain/**'
              - 'application/**'
              - 'adapter-in/**'
              - 'adapter-out/**'
              - 'bootstrap/bootstrap-web-api/**'
            scheduler:
              - 'domain/**'
              - 'application/**'
              - 'adapter-out/**'
              - 'bootstrap/bootstrap-scheduler/**'

  # ========================================
  # Step 2: Build & Push Web API
  # ========================================
  deploy-web-api:
    name: Deploy Web API to ECS
    runs-on: ubuntu-latest
    needs: detect-changes
    if: needs.detect-changes.outputs.web-api == 'true'

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Configure AWS credentials
        uses: aws-actions/configure-aws-credentials@v4
        with:
          role-to-assume: ${{ secrets.AWS_ROLE_ARN }}
          aws-region: ${{ env.AWS_REGION }}

      - name: Login to Amazon ECR
        id: login-ecr
        uses: aws-actions/amazon-ecr-login@v2

      - name: Build Docker image
        run: |
          docker build \
            -f bootstrap/bootstrap-web-api/Dockerfile \
            -t spring-hexagonal-web-api:${{ github.sha }} \
            .

      - name: Tag and push to ECR
        run: |
          docker tag spring-hexagonal-web-api:${{ github.sha }} \
            ${{ env.ECR_REGISTRY }}/spring-hexagonal-web-api:${{ github.sha }}
          docker tag spring-hexagonal-web-api:${{ github.sha }} \
            ${{ env.ECR_REGISTRY }}/spring-hexagonal-web-api:latest
          docker push ${{ env.ECR_REGISTRY }}/spring-hexagonal-web-api:${{ github.sha }}
          docker push ${{ env.ECR_REGISTRY }}/spring-hexagonal-web-api:latest

      - name: Update ECS task definition
        id: task-def
        uses: aws-actions/amazon-ecs-render-task-definition@v1
        with:
          task-definition: .aws/task-definition-web-api.json
          container-name: spring-hexagonal-web-api
          image: ${{ env.ECR_REGISTRY }}/spring-hexagonal-web-api:${{ github.sha }}

      - name: Deploy to ECS (Blue/Green)
        uses: aws-actions/amazon-ecs-deploy-task-definition@v2
        with:
          task-definition: ${{ steps.task-def.outputs.task-definition }}
          service: spring-hexagonal-web-api-service
          cluster: spring-hexagonal-cluster
          wait-for-service-stability: true
          codedeploy-appspec: .aws/appspec-web-api.yaml
          codedeploy-application: spring-hexagonal-web-api
          codedeploy-deployment-group: spring-hexagonal-web-api-dg

  # ========================================
  # Step 3: Build & Push Scheduler
  # ========================================
  deploy-scheduler:
    name: Deploy Scheduler to ECS
    runs-on: ubuntu-latest
    needs: detect-changes
    if: needs.detect-changes.outputs.scheduler == 'true'

    steps:
      # Web API와 동일, 경로만 다름
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Configure AWS credentials
        uses: aws-actions/configure-aws-credentials@v4
        with:
          role-to-assume: ${{ secrets.AWS_ROLE_ARN }}
          aws-region: ${{ env.AWS_REGION }}

      - name: Login to Amazon ECR
        id: login-ecr
        uses: aws-actions/amazon-ecr-login@v2

      - name: Build Docker image
        run: |
          docker build \
            -f bootstrap/bootstrap-scheduler/Dockerfile \
            -t spring-hexagonal-scheduler:${{ github.sha }} \
            .

      - name: Tag and push to ECR
        run: |
          docker tag spring-hexagonal-scheduler:${{ github.sha }} \
            ${{ env.ECR_REGISTRY }}/spring-hexagonal-scheduler:${{ github.sha }}
          docker tag spring-hexagonal-scheduler:${{ github.sha }} \
            ${{ env.ECR_REGISTRY }}/spring-hexagonal-scheduler:latest
          docker push ${{ env.ECR_REGISTRY }}/spring-hexagonal-scheduler:${{ github.sha }}
          docker push ${{ env.ECR_REGISTRY }}/spring-hexagonal-scheduler:latest

      - name: Update ECS task definition
        id: task-def
        uses: aws-actions/amazon-ecs-render-task-definition@v1
        with:
          task-definition: .aws/task-definition-scheduler.json
          container-name: spring-hexagonal-scheduler
          image: ${{ env.ECR_REGISTRY }}/spring-hexagonal-scheduler:${{ github.sha }}

      - name: Deploy to ECS (Rolling Update)
        uses: aws-actions/amazon-ecs-deploy-task-definition@v2
        with:
          task-definition: ${{ steps.task-def.outputs.task-definition }}
          service: spring-hexagonal-scheduler-service
          cluster: spring-hexagonal-cluster
          wait-for-service-stability: true

  # ========================================
  # Step 4: Deployment Summary
  # ========================================
  deployment-summary:
    name: Deployment Summary
    runs-on: ubuntu-latest
    needs: [detect-changes, deploy-web-api, deploy-scheduler]
    if: always()

    steps:
      - name: Summary
        run: |
          echo "========================================="
          echo "Deployment Summary"
          echo "========================================="
          echo "Web API: ${{ needs.deploy-web-api.result || 'skipped' }}"
          echo "Scheduler: ${{ needs.deploy-scheduler.result || 'skipped' }}"
          echo "========================================="

          if [[ "${{ needs.deploy-web-api.result }}" == "failure" ]] || \
             [[ "${{ needs.deploy-scheduler.result }}" == "failure" ]]; then
            echo "❌ Deployment FAILED"
            exit 1
          else
            echo "✅ Deployment SUCCESSFUL"
          fi
```

---

### 3. ECS Blue/Green 배포 설정

#### Task Definition

**`.aws/task-definition-web-api.json`**:
```json
{
  "family": "spring-hexagonal-web-api",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "512",
  "memory": "1024",
  "containerDefinitions": [
    {
      "name": "spring-hexagonal-web-api",
      "image": "123456789012.dkr.ecr.ap-northeast-2.amazonaws.com/spring-hexagonal-web-api:latest",
      "portMappings": [
        {
          "containerPort": 8080,
          "protocol": "tcp"
        }
      ],
      "essential": true,
      "environment": [
        {
          "name": "SPRING_PROFILES_ACTIVE",
          "value": "prod"
        }
      ],
      "secrets": [
        {
          "name": "DB_PASSWORD",
          "valueFrom": "arn:aws:secretsmanager:ap-northeast-2:123456789012:secret:db-password"
        }
      ],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "/ecs/spring-hexagonal-web-api",
          "awslogs-region": "ap-northeast-2",
          "awslogs-stream-prefix": "ecs"
        }
      },
      "healthCheck": {
        "command": [
          "CMD-SHELL",
          "curl -f http://localhost:8080/actuator/health || exit 1"
        ],
        "interval": 30,
        "timeout": 5,
        "retries": 3,
        "startPeriod": 60
      }
    }
  ]
}
```

#### AppSpec (Blue/Green)

**`.aws/appspec-web-api.yaml`**:
```yaml
version: 0.0
Resources:
  - TargetService:
      Type: AWS::ECS::Service
      Properties:
        TaskDefinition: "arn:aws:ecs:ap-northeast-2:123456789012:task-definition/spring-hexagonal-web-api"
        LoadBalancerInfo:
          ContainerName: "spring-hexagonal-web-api"
          ContainerPort: 8080
        PlatformVersion: "LATEST"

Hooks:
  - BeforeInstall: "LambdaFunctionToValidateBeforeInstall"
  - AfterInstall: "LambdaFunctionToValidateAfterInstall"
  - AfterAllowTestTraffic: "LambdaFunctionToValidateAfterTestTrafficStarts"
  - BeforeAllowTraffic: "LambdaFunctionToValidateBeforeAllowingProductionTraffic"
  - AfterAllowTraffic: "LambdaFunctionToValidateAfterAllowingProductionTraffic"
```

---

### 4. Config 패키지 검증 전략

**현재**: `config/` 디렉터리에 Checkstyle, SpotBugs, PMD 설정 존재

**전략**:
1. ✅ **빌드 시 자동 검증** (이미 구현됨)
2. ✅ **pre-commit에서 검증** (validation-helper.py 활용)
3. ❌ **별도 워크플로우 불필요** (빌드에 통합되어 있음)

**validation-helper.py 확장**:
```python
# .claude/hooks/scripts/validation-helper.py 확장
def validate_config_files():
    """
    Config 파일 검증
    - checkstyle.xml
    - spotbugs-exclude.xml
    - pmd-ruleset.xml
    """
    config_files = [
        'config/checkstyle/checkstyle.xml',
        'config/spotbugs/spotbugs-exclude.xml',
        'config/pmd/pmd-ruleset.xml'
    ]

    for file in config_files:
        if not os.path.exists(file):
            print(f"❌ Config file missing: {file}")
            sys.exit(1)

        # XML 유효성 검사
        try:
            ET.parse(file)
        except ET.ParseError as e:
            print(f"❌ Invalid XML in {file}: {e}")
            sys.exit(1)

    print("✅ All config files validated")
```

---

## 📋 최종 CI/CD 파이프라인 요약

### Git Hooks (로컬)
```
✅ pre-commit: Spotless + Checkstyle + Config 검증 (< 30초)
✅ pre-push: 변경된 모듈만 빌드/테스트 (< 3분)
```

### GitHub Actions (PR)
```
✅ ci-build-test.yml: 전체 빌드 및 테스트 (기존)
✅ ci-module-validation.yml: 변경된 모듈만 검증 (기존)
```

### GitHub Actions (CD, main 브랜치)
```
⭐ cd-deploy.yml:
   1. 변경된 Bootstrap 감지
   2. Docker 빌드 (변경된 것만)
   3. ECR 푸시
   4. ECS Blue/Green 배포
```

### 예시 시나리오

#### 시나리오 1: Domain 변경
```
Domain 변경
  ↓
pre-commit: Checkstyle ✅
  ↓
pre-push: Domain 빌드/테스트 ✅
  ↓
PR: 전체 빌드 + Domain 검증 ✅
  ↓
Merge to main
  ↓
CD: Web API + Scheduler 모두 배포 (Domain은 모든 Bootstrap에 영향)
```

#### 시나리오 2: Web API만 변경
```
bootstrap-web-api 변경
  ↓
pre-commit: Checkstyle ✅
  ↓
pre-push: Web API 빌드/테스트 ✅
  ↓
PR: 전체 빌드 + Web API 검증 ✅
  ↓
Merge to main
  ↓
CD: Web API만 배포 (Scheduler 스킵) ⭐
```

#### 시나리오 3: Config 파일 변경
```
config/checkstyle/checkstyle.xml 변경
  ↓
pre-commit: Config 검증 ✅
  ↓
pre-push: 전체 빌드 (Config 변경은 모든 모듈에 영향)
  ↓
PR: 전체 빌드 ✅
  ↓
Merge to main
  ↓
CD: 배포 스킵 (Config 변경은 배포 불필요)
```

---

## 🚀 구현 우선순위

### Phase 1: Git Hooks (1-2일)
1. `hooks/pre-commit` 구현
2. `hooks/pre-push` 구현
3. `hooks/install-hooks.sh` 구현
4. Gradle 통합 (`installGitHooks` task)

### Phase 2: Docker & ECR (2-3일)
1. `bootstrap/bootstrap-web-api/Dockerfile` 작성
2. `bootstrap/bootstrap-scheduler/Dockerfile` 작성
3. ECR Repository 생성 (Terraform/CloudFormation)
4. 로컬 Docker 빌드 테스트

### Phase 3: ECS 설정 (3-4일)
1. Task Definition 작성 (`.aws/task-definition-*.json`)
2. Service 생성 (Blue/Green 설정)
3. CodeDeploy Application/DeploymentGroup 생성
4. AppSpec 작성 (`.aws/appspec-*.yaml`)

### Phase 4: CD 워크플로우 (2-3일)
1. `.github/workflows/cd-deploy.yml` 작성
2. AWS IAM Role 설정 (OIDC)
3. GitHub Secrets 설정
4. 통합 테스트

---

## 💡 권장 사항

### ✅ Do
1. **Git Hooks를 가볍게 유지**: pre-commit은 30초 이내, pre-push는 3분 이내
2. **변경 감지를 적극 활용**: 불필요한 빌드/배포 방지
3. **Blue/Green 배포**: Web API는 Blue/Green, Scheduler는 Rolling Update
4. **Health Check 필수**: ECS Task Definition에 Health Check 포함
5. **Secrets Manager 사용**: 민감 정보는 AWS Secrets Manager 활용

### ❌ Don't
1. **pre-commit에 빌드 넣지 마세요**: 개발 경험 저하
2. **모든 Bootstrap을 항상 배포하지 마세요**: 시간 낭비
3. **Config 변경 시 배포하지 마세요**: 불필요한 배포
4. **테스트 없이 배포하지 마세요**: CI 통과 후에만 CD 실행

---

## 📚 참고 자료

- [dorny/paths-filter](https://github.com/dorny/paths-filter)
- [AWS ECS Blue/Green Deployment](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/deployment-type-bluegreen.html)
- [GitHub Actions - AWS ECS Deploy](https://github.com/aws-actions/amazon-ecs-deploy-task-definition)
- [Spring Boot Docker Best Practices](https://spring.io/guides/topicals/spring-boot-docker)

---

**✅ 이 전략을 따르면 변경된 Bootstrap만 선택적으로 빌드/배포하여 효율적인 CI/CD 파이프라인을 구축할 수 있습니다!**
