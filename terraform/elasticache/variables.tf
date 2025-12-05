# ============================================================================
# ElastiCache Module Variables
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
# Redis Configuration
# ============================================================================

variable "redis_version" {
  description = "Redis 버전 (예: 7.0, 6.2)"
  type        = string
  default     = "7.0"

  validation {
    condition     = can(regex("^[0-9]+\\.[0-9]+$", var.redis_version))
    error_message = "Redis version must be in format X.Y (e.g., 7.0)."
  }
}

variable "node_type" {
  description = "노드 인스턴스 타입"
  type        = string
  default     = "cache.t3.micro"

  validation {
    condition     = can(regex("^cache\\.[a-z0-9]+\\.[a-z0-9]+$", var.node_type))
    error_message = "Node type must be a valid ElastiCache instance type."
  }
}

# ============================================================================
# Security
# ============================================================================

variable "security_group_ids" {
  description = "보안 그룹 ID 목록"
  type        = list(string)
}

variable "auth_token" {
  description = "Redis AUTH 토큰 (비밀번호, 16-128자)"
  type        = string
  default     = null
  sensitive   = true

  validation {
    condition     = var.auth_token == null ? true : (length(var.auth_token) >= 16 && length(var.auth_token) <= 128)
    error_message = "Auth token must be between 16 and 128 characters."
  }
}

variable "kms_key_id" {
  description = "KMS 키 ARN (null이면 AWS managed key 사용)"
  type        = string
  default     = null
}

# ============================================================================
# Replication (Optional)
# ============================================================================

variable "enable_replication" {
  description = "복제 그룹 활성화 여부"
  type        = bool
  default     = false
}

variable "num_shards" {
  description = "샤드(노드 그룹) 개수"
  type        = number
  default     = 1

  validation {
    condition     = var.num_shards >= 1 && var.num_shards <= 500
    error_message = "Number of shards must be between 1 and 500."
  }
}

variable "replicas_per_shard" {
  description = "샤드당 복제본 개수"
  type        = number
  default     = 1

  validation {
    condition     = var.replicas_per_shard >= 0 && var.replicas_per_shard <= 5
    error_message = "Replicas per shard must be between 0 and 5."
  }
}

# ============================================================================
# Parameters (Optional)
# ============================================================================

variable "redis_parameters" {
  description = "Redis 파라미터 목록"
  type = list(object({
    name  = string
    value = string
  }))
  default = []
}

# ============================================================================
# Monitoring
# ============================================================================

variable "alarm_connection_threshold" {
  description = "연결 수 알람 임계값"
  type        = number
  default     = 1000

  validation {
    condition     = var.alarm_connection_threshold > 0
    error_message = "Connection threshold must be greater than 0."
  }
}

variable "alarm_actions" {
  description = "알람 발생 시 알림 받을 SNS ARN 목록"
  type        = list(string)
  default     = []
}
