# ============================================================================
# ECS Web API Module Outputs
# ============================================================================

output "service_id" {
  description = "ECS Service ID"
  value       = module.ecs_web_api.service_id
}

output "service_name" {
  description = "ECS Service 이름"
  value       = module.ecs_web_api.service_name
}

output "task_definition_arn" {
  description = "Task Definition ARN"
  value       = module.ecs_web_api.task_definition_arn
}

output "task_definition_family" {
  description = "Task Definition Family"
  value       = module.ecs_web_api.task_definition_family
}

# ============================================================================
# SSM Parameter Paths
# ============================================================================

output "ssm_service_name_path" {
  description = "Service Name SSM Parameter 경로"
  value       = aws_ssm_parameter.service_name.name
}
