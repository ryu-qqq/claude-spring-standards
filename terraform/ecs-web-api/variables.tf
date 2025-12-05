# ============================================================================
# ECS Web API Module Variables
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
# Container Configuration
# ============================================================================

variable "image_tag" {
  description = "컨테이너 이미지 태그 (CI/CD에서 주입)"
  type        = string
  default     = "latest"
}

variable "container_port" {
  description = "컨테이너 포트 (Spring Boot 기본값)"
  type        = number
  default     = 8080

  validation {
    condition     = var.container_port > 0 && var.container_port < 65536
    error_message = "Container port must be between 1 and 65535."
  }
}

variable "cpu" {
  description = "CPU 유닛 (256, 512, 1024, 2048, 4096)"
  type        = number
  default     = 512

  validation {
    condition     = contains([256, 512, 1024, 2048, 4096, 8192, 16384], var.cpu)
    error_message = "CPU must be one of: 256, 512, 1024, 2048, 4096, 8192, 16384."
  }
}

variable "memory" {
  description = "메모리 (MiB)"
  type        = number
  default     = 1024

  validation {
    condition     = var.memory >= 512 && var.memory <= 30720
    error_message = "Memory must be between 512 and 30720 MiB."
  }
}

variable "desired_count" {
  description = "실행할 태스크 개수"
  type        = number
  default     = 1

  validation {
    condition     = var.desired_count >= 0
    error_message = "Desired count must be >= 0."
  }
}

# ============================================================================
# Network & Security
# ============================================================================

variable "security_group_ids" {
  description = "보안 그룹 ID 목록"
  type        = list(string)
}

variable "execution_role_arn" {
  description = "ECS Task Execution Role ARN"
  type        = string
}

variable "task_role_arn" {
  description = "ECS Task Role ARN"
  type        = string
}

# ============================================================================
# Load Balancer (Optional)
# ============================================================================

variable "target_group_arn" {
  description = "ALB Target Group ARN (null이면 LB 없음)"
  type        = string
  default     = null
}

# ============================================================================
# Health Check
# ============================================================================

variable "health_check_path" {
  description = "Health Check 경로 (예: /actuator/health)"
  type        = string
  default     = "/actuator/health"
}

variable "health_check_grace_period" {
  description = "Health Check 시작 전 대기 시간 (초)"
  type        = number
  default     = 60

  validation {
    condition     = var.health_check_grace_period >= 0 && var.health_check_grace_period <= 300
    error_message = "Health check grace period must be between 0 and 300 seconds."
  }
}

# ============================================================================
# Environment Variables & Secrets
# ============================================================================

variable "container_environment" {
  description = "추가 환경 변수 목록"
  type = list(object({
    name  = string
    value = string
  }))
  default = []
}

variable "container_secrets" {
  description = "Secrets Manager/Parameter Store에서 주입할 시크릿"
  type = list(object({
    name      = string
    valueFrom = string
  }))
  default = []
}

# ============================================================================
# ECS Exec (Development Only)
# ============================================================================

variable "enable_execute_command" {
  description = "ECS Exec 활성화 (dev 환경에서만 작동)"
  type        = bool
  default     = false
}

# ============================================================================
# Auto Scaling (Optional)
# ============================================================================

variable "enable_autoscaling" {
  description = "Auto Scaling 활성화 여부"
  type        = bool
  default     = false
}

variable "autoscaling_min_capacity" {
  description = "최소 태스크 개수"
  type        = number
  default     = 1

  validation {
    condition     = var.autoscaling_min_capacity >= 0
    error_message = "Min capacity must be >= 0."
  }
}

variable "autoscaling_max_capacity" {
  description = "최대 태스크 개수"
  type        = number
  default     = 4

  validation {
    condition     = var.autoscaling_max_capacity >= 1
    error_message = "Max capacity must be >= 1."
  }
}

variable "autoscaling_target_cpu" {
  description = "CPU 목표 사용률 (%)"
  type        = number
  default     = 70

  validation {
    condition     = var.autoscaling_target_cpu >= 1 && var.autoscaling_target_cpu <= 100
    error_message = "Target CPU must be between 1 and 100."
  }
}

variable "autoscaling_target_memory" {
  description = "메모리 목표 사용률 (%)"
  type        = number
  default     = 80

  validation {
    condition     = var.autoscaling_target_memory >= 1 && var.autoscaling_target_memory <= 100
    error_message = "Target memory must be between 1 and 100."
  }
}

# ============================================================================
# Sidecars (Optional)
# ============================================================================

variable "sidecars" {
  description = "Sidecar 컨테이너 목록 (예: OTEL Collector, Datadog Agent)"
  type = list(object({
    name      = string
    image     = string
    cpu       = optional(number, 256)
    memory    = optional(number, 512)
    essential = optional(bool, false)
    command   = optional(list(string), [])
    portMappings = optional(list(object({
      containerPort = number
      protocol      = optional(string, "tcp")
      hostPort      = optional(number)
    })), [])
    environment = optional(list(object({
      name  = string
      value = string
    })), [])
    secrets = optional(list(object({
      name      = string
      valueFrom = string
    })), [])
    logConfiguration = optional(object({
      logDriver = string
      options   = map(string)
    }))
    healthCheck = optional(object({
      command     = list(string)
      interval    = optional(number, 30)
      timeout     = optional(number, 5)
      retries     = optional(number, 3)
      startPeriod = optional(number, 60)
    }))
    dependsOn = optional(list(object({
      containerName = string
      condition     = string
    })), [])
  }))
  default = []
}
