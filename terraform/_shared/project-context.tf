# ============================================================================
# Project Context - 프로젝트 공통 설정
# ============================================================================
# 이 파일은 템플릿 프로젝트의 핵심 컨벤션을 정의합니다.
# 모든 Terraform 모듈은 이 컨텍스트를 참조해야 합니다.
#
# 사용법:
#   다른 모듈에서 symlink 또는 terraform_remote_state로 참조
# ============================================================================

# ============================================================================
# 🔴 필수 수정 항목 - 프로젝트 시작 시 반드시 변경하세요!
# ============================================================================

variable "project_name" {
  description = "프로젝트 이름 (kebab-case). 모든 리소스 네이밍에 사용됩니다."
  type        = string
  default     = "my-project"  # TODO: 프로젝트 이름으로 변경

  validation {
    condition     = can(regex("^[a-z][a-z0-9-]*[a-z0-9]$", var.project_name))
    error_message = "Project name must use kebab-case (e.g., 'my-project', 'user-service')."
  }
}

variable "team" {
  description = "담당 팀 이름 (kebab-case)"
  type        = string
  default     = "platform-team"  # TODO: 팀 이름으로 변경

  validation {
    condition     = can(regex("^[a-z][a-z0-9-]*[a-z0-9]$", var.team))
    error_message = "Team must use kebab-case (e.g., 'platform-team', 'backend-team')."
  }
}

variable "owner" {
  description = "리소스 소유자 이메일"
  type        = string
  default     = "platform@example.com"  # TODO: 실제 이메일로 변경

  validation {
    condition     = can(regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", var.owner))
    error_message = "Owner must be a valid email address."
  }
}

variable "cost_center" {
  description = "비용 센터 (kebab-case)"
  type        = string
  default     = "engineering"  # TODO: 비용 센터로 변경

  validation {
    condition     = can(regex("^[a-z][a-z0-9-]*[a-z0-9]$", var.cost_center))
    error_message = "Cost center must use kebab-case."
  }
}

# ============================================================================
# 환경 설정 (dev, staging, prod)
# ============================================================================

variable "environment" {
  description = "배포 환경"
  type        = string
  default     = "prod"

  validation {
    condition     = contains(["dev", "staging", "prod"], var.environment)
    error_message = "Environment must be one of: dev, staging, prod."
  }
}

variable "aws_region" {
  description = "AWS 리전"
  type        = string
  default     = "ap-northeast-2"
}

# ============================================================================
# Infrastructure 모듈 버전
# ============================================================================

variable "infrastructure_module_ref" {
  description = "Infrastructure 레포지토리 Git ref (tag 또는 branch)"
  type        = string
  default     = "main"  # 운영에서는 특정 tag 사용 권장 (e.g., "v1.0.0")
}

# ============================================================================
# Computed Locals - 자동 생성되는 값들
# ============================================================================

locals {
  # Infrastructure 모듈 소스 베이스 URL
  module_source_base = "git::https://github.com/ryu-qqq/Infrastructure.git//terraform/modules"

  # 공통 태그 (모든 리소스에 적용)
  common_tags = {
    environment  = var.environment
    team         = var.team
    owner        = var.owner
    cost_center  = var.cost_center
    project      = var.project_name
  }

  # 리소스 네이밍 패턴
  # Format: {project_name}-{resource_type}-{environment}
  naming = {
    ecr_web_api     = "${var.project_name}-web-api-${var.environment}"
    ecr_scheduler   = "${var.project_name}-scheduler-${var.environment}"
    ecs_cluster     = "${var.project_name}-cluster-${var.environment}"
    ecs_web_api     = "${var.project_name}-web-api-${var.environment}"
    ecs_scheduler   = "${var.project_name}-scheduler-${var.environment}"
    elasticache     = "${var.project_name}-redis-${var.environment}"
    s3_uploads      = "${var.project_name}-uploads-${var.environment}"
    sqs_queue       = "${var.project_name}-queue-${var.environment}"
  }

  # SSM Parameter 경로 패턴
  ssm_prefix = "/${var.project_name}"
}

# ============================================================================
# Outputs - 다른 모듈에서 참조
# ============================================================================

output "project_name" {
  value = var.project_name
}

output "environment" {
  value = var.environment
}

output "aws_region" {
  value = var.aws_region
}

output "common_tags" {
  value = local.common_tags
}

output "naming" {
  value = local.naming
}

output "module_source_base" {
  value = local.module_source_base
}

output "infrastructure_module_ref" {
  value = var.infrastructure_module_ref
}

output "ssm_prefix" {
  value = local.ssm_prefix
}
