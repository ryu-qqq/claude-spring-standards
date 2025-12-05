# Terraform Infrastructure

Spring Hexagonal Template 프로젝트의 AWS 인프라를 **Wrapper Module 패턴**으로 관리합니다.

> **핵심 원칙**: Infrastructure 레포 모듈을 래핑하여 프로젝트 컨벤션을 강제 적용

## 아키텍처 개요

```
┌─────────────────────────────────────────────────────────────────┐
│                    이 프로젝트 (Wrapper Modules)                   │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐        │
│  │   ecr    │  │ecs-cluster│ │ecs-web-api│ │elasticache│ ...   │
│  │ (wrapper)│  │ (wrapper) │ │ (wrapper) │ │ (wrapper) │        │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘        │
│       │             │             │             │               │
│       │    🔒 컨벤션 강제: 네이밍, 태그, 보안 설정               │
│       │                                                         │
└───────┼─────────────┼─────────────┼─────────────┼───────────────┘
        │             │             │             │
        ▼             ▼             ▼             ▼
┌─────────────────────────────────────────────────────────────────┐
│              ryu-qqq/Infrastructure (외부 모듈)                  │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐        │
│  │   ecr    │  │ecs-cluster│ │ecs-service│ │elasticache│ ...   │
│  │ (module) │  │ (module)  │ │ (module)  │ │ (module)  │        │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘        │
└─────────────────────────────────────────────────────────────────┘
```

## 디렉토리 구조

```
terraform/
├── README.md                    # 이 파일
├── _shared/                     # 공유 구성
│   └── project-context.tf       # 프로젝트 공통 변수 정의
│
├── ecr/                         # ECR Wrapper Module
│   ├── main.tf                  # ECR 리포지토리 정의
│   ├── variables.tf             # 입력 변수
│   ├── outputs.tf               # 출력 값
│   └── provider.tf              # Provider 및 Backend 설정
│
├── ecs-cluster/                 # ECS Cluster Wrapper Module
│   ├── main.tf                  # ECS 클러스터 정의
│   ├── variables.tf
│   ├── outputs.tf
│   └── provider.tf
│
├── ecs-web-api/                 # ECS Web API Service Wrapper Module
│   ├── main.tf                  # ECS 서비스 정의 (ecs-service 래핑)
│   ├── variables.tf
│   ├── outputs.tf
│   └── provider.tf
│
├── elasticache/                 # ElastiCache Wrapper Module
│   ├── main.tf                  # Redis 클러스터 정의
│   ├── variables.tf
│   ├── outputs.tf
│   └── provider.tf
│
├── s3/                          # S3 Wrapper Module
│   ├── main.tf                  # S3 버킷 정의
│   ├── variables.tf
│   ├── outputs.tf
│   └── provider.tf
│
└── environments/                # 환경별 tfvars
    ├── dev.tfvars
    ├── staging.tfvars
    └── prod.tfvars
```

## Wrapper Module 패턴

### 컨벤션 강제 항목

| 항목 | 강제 내용 | 적용 방식 |
|------|----------|----------|
| **네이밍** | `{project}-{resource}-{env}` | `locals.naming.*` |
| **필수 태그** | 8개 태그 필수 | `variables.tf` validation |
| **보안** | 암호화, 프라이빗 서브넷 | 하드코딩 |
| **버전관리** | IMMUTABLE 태그, 버저닝 | 하드코딩 |
| **모니터링** | Container Insights, 알람 | 기본 활성화 |

### 필수 입력 변수 (Project Context)

모든 Wrapper Module에서 공통으로 사용하는 변수:

```hcl
variable "project_name" {
  description = "프로젝트 이름 (kebab-case)"
  type        = string
  # 예: "my-awesome-api"
}

variable "environment" {
  description = "배포 환경"
  type        = string
  # 허용값: dev, staging, prod
}

variable "team" {
  description = "담당 팀"
  type        = string
  # 예: "backend-team"
}

variable "owner" {
  description = "리소스 소유자 이메일"
  type        = string
  # 예: "team@company.com"
}

variable "cost_center" {
  description = "비용 센터"
  type        = string
  # 예: "engineering"
}
```

## 사용 방법

### 1. Backend 설정 변경

각 모듈의 `provider.tf`에서 TODO 항목 수정:

```hcl
backend "s3" {
  bucket         = "my-project-terraform-state"    # TODO: 변경
  key            = "ecr/terraform.tfstate"
  region         = "ap-northeast-2"
  dynamodb_table = "my-project-terraform-lock"     # TODO: 변경
  encrypt        = true
}
```

### 2. 환경별 tfvars 생성

```bash
# environments/dev.tfvars
project_name = "my-project"
environment  = "dev"
team         = "backend-team"
owner        = "dev@company.com"
cost_center  = "engineering"
aws_region   = "ap-northeast-2"
```

### 3. 모듈 배포

```bash
# ECR 배포
cd terraform/ecr
terraform init
terraform plan -var-file="../environments/dev.tfvars"
terraform apply -var-file="../environments/dev.tfvars"

# ECS Cluster 배포
cd ../ecs-cluster
terraform init
terraform plan -var-file="../environments/dev.tfvars"
terraform apply -var-file="../environments/dev.tfvars"

# ECS Web API 서비스 배포
cd ../ecs-web-api
terraform init
terraform plan -var-file="../environments/dev.tfvars"
terraform apply -var-file="../environments/dev.tfvars"
```

## SSM Parameter 참조

Wrapper Module들은 SSM Parameter Store를 통해 값을 공유합니다:

| Parameter Path | 설명 | 생성 모듈 |
|----------------|------|----------|
| `/{project}/ecr/web-api-repository-url` | ECR 레포 URL | ecr |
| `/{project}/ecs/cluster-arn` | ECS 클러스터 ARN | ecs-cluster |
| `/{project}/ecs/cluster-name` | ECS 클러스터 이름 | ecs-cluster |
| `/{project}/elasticache/redis-endpoint` | Redis 엔드포인트 | elasticache |
| `/{project}/s3/uploads-bucket-name` | S3 버킷 이름 | s3 |

### SSM 값 참조 예시

```hcl
# 다른 모듈에서 ECR URL 참조
data "aws_ssm_parameter" "ecr_url" {
  name = "/${var.project_name}/ecr/web-api-repository-url"
}

# ECS 서비스에서 사용
container_image = data.aws_ssm_parameter.ecr_url.value
```

## 배포 순서

```
1. ecr           → ECR 레포지토리 생성
2. ecs-cluster   → ECS 클러스터 생성
3. elasticache   → Redis 클러스터 생성 (선택)
4. s3            → S3 버킷 생성 (선택)
5. ecs-web-api   → ECS 서비스 배포
```

## 주의사항

### 🔒 보안 강제 설정 (변경 불가)

- **ECR**: `image_tag_mutability = "IMMUTABLE"`, `scan_on_push = true`
- **ECS**: `assign_public_ip = false` (Private Subnet 강제)
- **ElastiCache**: `at_rest_encryption_enabled = true`, `transit_encryption_enabled = true`
- **S3**: Public Access 완전 차단, 버전관리 기본 활성화

### 🏷️ 필수 태그 (8개)

모든 리소스에 자동 적용:
- `Environment`, `Project`, `Team`, `Owner`
- `CostCenter`, `ManagedBy`, `ServiceName`, `DataClass`

### ⚠️ 환경별 제약

- `force_destroy`: dev 환경에서만 허용
- `enable_execute_command`: dev 환경에서만 허용
- `snapshot_retention`: prod는 14일, 나머지 7일

---

## Infrastructure 레포 사용 가능한 모듈

> **GitHub**: [ryu-qqq/Infrastructure](https://github.com/ryu-qqq/Infrastructure)
>
> **모듈 위치**: `terraform/modules/`

### 현재 구현된 Wrapper (5개)

| Wrapper | Infrastructure 모듈 | 설명 |
|---------|---------------------|------|
| `terraform/ecr/` | `ecr` | Docker 이미지 저장소 |
| `terraform/ecs-cluster/` | - (직접 구현) | ECS 클러스터 |
| `terraform/ecs-web-api/` | `ecs-service` | Spring Boot API 서비스 |
| `terraform/elasticache/` | `elasticache` | Redis 캐시 클러스터 |
| `terraform/s3/` | `s3-bucket` | 파일 업로드 버킷 |

### 추가 가능한 모듈 (17개)

필요 시 아래 모듈들의 Wrapper를 생성할 수 있습니다:

| Infrastructure 모듈 | 용도 | 우선순위 |
|---------------------|------|----------|
| `alb` | Application Load Balancer | 높음 |
| `rds` | RDS 데이터베이스 (MySQL/PostgreSQL) | 높음 |
| `security-group` | 보안 그룹 | 높음 |
| `cloudfront` | CDN 배포 | 중간 |
| `route53-record` | DNS 레코드 | 중간 |
| `sns` | SNS 토픽 (알림) | 중간 |
| `sqs` | SQS 큐 (메시징) | 중간 |
| `lambda` | Lambda 함수 | 중간 |
| `eventbridge` | EventBridge 규칙 | 중간 |
| `waf` | Web Application Firewall | 중간 |
| `cloudwatch-log-group` | CloudWatch 로그 그룹 | 낮음 |
| `iam-role-policy` | IAM 역할/정책 | 낮음 |
| `messaging-pattern` | SNS+SQS 메시징 패턴 | 낮음 |
| `bastion-ssm` | SSM 기반 Bastion 호스트 | 낮음 |
| `adot-sidecar` | AWS Distro for OpenTelemetry | 낮음 |
| `ecs-task-role-observability` | ECS Task 관측성 역할 | 낮음 |
| `common-tags` | 공통 태그 (내부 사용) | - |

---

## 새 Wrapper Module 생성 가이드

### 1. 디렉토리 구조 생성

```bash
mkdir -p terraform/{module-name}
touch terraform/{module-name}/{main.tf,variables.tf,outputs.tf,provider.tf}
```

### 2. 파일별 작성 패턴

#### `provider.tf` - Provider 및 Backend 설정

```hcl
terraform {
  required_version = ">= 1.5.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }

  backend "s3" {
    bucket         = "my-project-terraform-state"    # TODO: 변경
    key            = "{module-name}/terraform.tfstate"
    region         = "ap-northeast-2"
    dynamodb_table = "my-project-terraform-lock"     # TODO: 변경
    encrypt        = true
  }
}

provider "aws" {
  region = var.aws_region

  default_tags {
    tags = {
      ManagedBy   = "terraform"
      Project     = var.project_name
      Environment = var.environment
      Team        = var.team
      Owner       = var.owner
      CostCenter  = var.cost_center
    }
  }
}
```

#### `variables.tf` - 입력 변수 (Project Context 필수)

```hcl
# ============================================================================
# Project Context (필수 - 모든 Wrapper에서 동일)
# ============================================================================

variable "project_name" {
  description = "프로젝트 이름 (kebab-case)"
  type        = string

  validation {
    condition     = can(regex("^[a-z][a-z0-9-]*[a-z0-9]$", var.project_name))
    error_message = "Project name must use kebab-case."
  }
}

variable "environment" {
  description = "배포 환경 (dev, staging, prod)"
  type        = string

  validation {
    condition     = contains(["dev", "staging", "prod"], var.environment)
    error_message = "Environment must be one of: dev, staging, prod."
  }
}

variable "team" {
  description = "담당 팀 (kebab-case)"
  type        = string
}

variable "owner" {
  description = "리소스 소유자 이메일"
  type        = string

  validation {
    condition     = can(regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", var.owner))
    error_message = "Owner must be a valid email address."
  }
}

variable "cost_center" {
  description = "비용 센터 (kebab-case)"
  type        = string
}

variable "aws_region" {
  description = "AWS 리전"
  type        = string
  default     = "ap-northeast-2"
}

variable "infrastructure_module_ref" {
  description = "Infrastructure 모듈 Git ref"
  type        = string
  default     = "main"
}

# ============================================================================
# Module-Specific Variables (모듈별 추가)
# ============================================================================

# 여기에 모듈별 변수 추가
```

#### `main.tf` - 모듈 래핑 (핵심)

```hcl
# ============================================================================
# {Module Name}
# ============================================================================
# 🎯 컨벤션 강제 항목:
#   - 네이밍: {project}-{resource}-{env}
#   - 태그: 필수 태그 자동 적용
#   - 보안: [보안 설정 목록]
# ============================================================================

module "{module_name}" {
  source = "git::https://github.com/ryu-qqq/Infrastructure.git//terraform/modules/{module-name}?ref=${var.infrastructure_module_ref}"

  # 🔒 네이밍 컨벤션 강제
  name = local.naming.resource

  # 🔒 보안 설정 (컨벤션 강제 - 하드코딩)
  # encryption_enabled = true
  # public_access     = false

  # 🔒 필수 태그 (project-context에서 주입)
  environment  = var.environment
  service_name = "${var.project_name}-{resource}"
  team         = var.team
  owner        = var.owner
  cost_center  = var.cost_center
  project      = var.project_name
}

# ============================================================================
# SSM Parameters (Cross-Stack Reference)
# ============================================================================
resource "aws_ssm_parameter" "{resource}_arn" {
  name        = "/${var.project_name}/{resource}/{resource}-arn"
  type        = "String"
  value       = module.{module_name}.arn
  description = "{Resource} ARN for ${var.project_name}"

  tags = local.common_tags
}

# ============================================================================
# Locals
# ============================================================================
locals {
  naming = {
    resource = "${var.project_name}-{resource}-${var.environment}"
  }

  common_tags = {
    Environment = var.environment
    Team        = var.team
    Owner       = var.owner
    CostCenter  = var.cost_center
    Project     = var.project_name
    ManagedBy   = "terraform"
  }
}
```

#### `outputs.tf` - 출력 값

```hcl
# ============================================================================
# Module Outputs
# ============================================================================

output "{resource}_id" {
  description = "{Resource} ID"
  value       = module.{module_name}.id
}

output "{resource}_arn" {
  description = "{Resource} ARN"
  value       = module.{module_name}.arn
}

# ============================================================================
# SSM Parameter Paths
# ============================================================================

output "ssm_{resource}_arn_path" {
  description = "{Resource} ARN SSM Parameter 경로"
  value       = aws_ssm_parameter.{resource}_arn.name
}
```

### 3. Wrapper 생성 시 체크리스트

- [ ] Project Context 변수 복사 (필수 5개 + aws_region + infrastructure_module_ref)
- [ ] 네이밍 컨벤션 `locals.naming.*` 적용
- [ ] 보안 설정 하드코딩 (암호화, 접근 제어 등)
- [ ] 환경별 분기 로직 추가 (dev vs prod)
- [ ] SSM Parameter 생성 (Cross-Stack 참조용)
- [ ] Backend key 변경 (`{module-name}/terraform.tfstate`)

### 4. Infrastructure 모듈 변수 확인 방법

```bash
# 모듈 변수 확인
cat /path/to/infrastructure/terraform/modules/{module-name}/variables.tf

# 또는 GitHub에서 직접 확인
# https://github.com/ryu-qqq/Infrastructure/tree/main/terraform/modules/{module-name}
```

---

자세한 가이드는 [terraform-guide.md](../docs/coding_convention/00-project-setup/terraform-guide.md)를 참조하세요.
