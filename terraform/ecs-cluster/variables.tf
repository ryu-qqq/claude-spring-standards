# ============================================================================
# ECS Cluster Module Variables
# ============================================================================

# ============================================================================
# Project Context (필수 - project-context.tf와 동일하게 유지)
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

  validation {
    condition     = can(regex("^[a-z][a-z0-9-]*[a-z0-9]$", var.team))
    error_message = "Team must use kebab-case."
  }
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

  validation {
    condition     = can(regex("^[a-z][a-z0-9-]*[a-z0-9]$", var.cost_center))
    error_message = "Cost center must use kebab-case."
  }
}

variable "aws_region" {
  description = "AWS 리전"
  type        = string
  default     = "ap-northeast-2"
}

# ============================================================================
# ECS Cluster Configuration
# ============================================================================

variable "enable_container_insights" {
  description = "Container Insights 활성화 여부"
  type        = bool
  default     = true
}

variable "enable_fargate_spot" {
  description = "Fargate Spot 활성화 여부 (비용 최적화)"
  type        = bool
  default     = true
}

variable "fargate_base_count" {
  description = "Fargate 기본 실행 개수 (Spot보다 우선 배치)"
  type        = number
  default     = 0

  validation {
    condition     = var.fargate_base_count >= 0
    error_message = "Fargate base count must be >= 0."
  }
}
