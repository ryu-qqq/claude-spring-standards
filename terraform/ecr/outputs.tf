# ============================================================================
# ECR Module Outputs
# ============================================================================

# ============================================================================
# web-api Repository
# ============================================================================

output "web_api_repository_url" {
  description = "web-api ECR 레포지토리 URL"
  value       = module.ecr_web_api.repository_url
}

output "web_api_repository_arn" {
  description = "web-api ECR 레포지토리 ARN"
  value       = module.ecr_web_api.repository_arn
}

output "web_api_repository_name" {
  description = "web-api ECR 레포지토리 이름"
  value       = module.ecr_web_api.repository_name
}

# ============================================================================
# scheduler Repository (Optional)
# ============================================================================

output "scheduler_repository_url" {
  description = "scheduler ECR 레포지토리 URL"
  value       = var.enable_scheduler ? module.ecr_scheduler[0].repository_url : null
}

output "scheduler_repository_arn" {
  description = "scheduler ECR 레포지토리 ARN"
  value       = var.enable_scheduler ? module.ecr_scheduler[0].repository_arn : null
}

output "scheduler_repository_name" {
  description = "scheduler ECR 레포지토리 이름"
  value       = var.enable_scheduler ? module.ecr_scheduler[0].repository_name : null
}
