# ============================================================================
# ECR Module Variables
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

variable "infrastructure_module_ref" {
  description = "Infrastructure 모듈 Git ref"
  type        = string
  default     = "main"
}

# ============================================================================
# ECR Configuration
# ============================================================================

variable "kms_key_arn" {
  description = "KMS 키 ARN (null이면 AES256 암호화)"
  type        = string
  default     = null
}

variable "max_image_count" {
  description = "보관할 최대 이미지 수"
  type        = number
  default     = 30

  validation {
    condition     = var.max_image_count >= 10 && var.max_image_count <= 100
    error_message = "Max image count must be between 10 and 100."
  }
}

variable "enable_scheduler" {
  description = "Scheduler ECR 레포지토리 생성 여부"
  type        = bool
  default     = false
}
