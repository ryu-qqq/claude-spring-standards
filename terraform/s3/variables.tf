# ============================================================================
# S3 Module Variables
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
# S3 Configuration
# ============================================================================

variable "kms_key_id" {
  description = "KMS 키 ARN (null이면 AES256 암호화)"
  type        = string
  default     = null
}

variable "data_class" {
  description = "데이터 분류 (confidential, internal, public)"
  type        = string
  default     = "confidential"

  validation {
    condition     = contains(["confidential", "internal", "public"], var.data_class)
    error_message = "Data class must be one of: confidential, internal, public."
  }
}

# ============================================================================
# Lifecycle Rules (Optional)
# ============================================================================

variable "lifecycle_rules" {
  description = "Lifecycle 규칙 (null이면 기본 규칙 적용)"
  type = list(object({
    id                           = string
    enabled                      = bool
    prefix                       = optional(string)
    expiration_days              = optional(number)
    transition_to_ia_days        = optional(number)
    transition_to_glacier_days   = optional(number)
    noncurrent_expiration_days   = optional(number)
    abort_incomplete_upload_days = optional(number)
  }))
  default = null
}

# ============================================================================
# CORS (Optional)
# ============================================================================

variable "cors_rules" {
  description = "CORS 규칙 목록"
  type = list(object({
    allowed_headers = list(string)
    allowed_methods = list(string)
    allowed_origins = list(string)
    expose_headers  = optional(list(string))
    max_age_seconds = optional(number)
  }))
  default = []
}

# ============================================================================
# Logging (Optional)
# ============================================================================

variable "logging_enabled" {
  description = "액세스 로깅 활성화"
  type        = bool
  default     = false
}

variable "logging_target_bucket" {
  description = "로그 저장 버킷"
  type        = string
  default     = null
}

variable "logging_target_prefix" {
  description = "로그 저장 경로 Prefix"
  type        = string
  default     = "logs/"
}

# ============================================================================
# Monitoring (Optional)
# ============================================================================

variable "enable_cloudwatch_alarms" {
  description = "CloudWatch 알람 활성화"
  type        = bool
  default     = false
}

variable "alarm_bucket_size_threshold" {
  description = "버킷 크기 알람 임계값 (bytes, 기본: 100GB)"
  type        = number
  default     = 107374182400

  validation {
    condition     = var.alarm_bucket_size_threshold > 0
    error_message = "Bucket size threshold must be greater than 0."
  }
}

variable "alarm_object_count_threshold" {
  description = "객체 수 알람 임계값 (기본: 1,000,000)"
  type        = number
  default     = 1000000

  validation {
    condition     = var.alarm_object_count_threshold > 0
    error_message = "Object count threshold must be greater than 0."
  }
}

variable "alarm_actions" {
  description = "알람 발생 시 알림 받을 SNS ARN 목록"
  type        = list(string)
  default     = []
}

# ============================================================================
# Danger Zone
# ============================================================================

variable "force_destroy" {
  description = "비어있지 않은 버킷 삭제 허용 (dev에서만 동작)"
  type        = bool
  default     = false
}
