# ============================================================================
# S3 Bucket (Uploads)
# ============================================================================
# 파일 업로드용 S3 버킷 - Infrastructure 모듈 Wrapper
#
# 🎯 컨벤션 강제 항목:
#   - 네이밍: {project}-uploads-{env}
#   - 태그: 8개 필수 태그 자동 적용
#   - 보안: Public Access 완전 차단, 암호화 필수
#   - 버전관리: 기본 활성화
#   - Lifecycle: 90일 후 IA 이동, 180일 후 Glacier 이동
# ============================================================================

# ============================================================================
# S3 Bucket: uploads
# ============================================================================
module "s3_uploads" {
  source = "git::https://github.com/ryu-qqq/Infrastructure.git//terraform/modules/s3-bucket?ref=${var.infrastructure_module_ref}"

  # 🔒 네이밍 컨벤션 강제
  bucket_name = local.naming.bucket

  # 🔒 보안 설정 (컨벤션 강제 - Public Access 완전 차단)
  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true

  # KMS 암호화 (선택)
  kms_key_id = var.kms_key_id

  # 🔒 버전관리 (기본 활성화)
  versioning_enabled = true

  # 🔒 Lifecycle (컨벤션 기본값)
  lifecycle_rules = var.lifecycle_rules != null ? var.lifecycle_rules : [
    {
      id                         = "archive-old-uploads"
      enabled                    = true
      prefix                     = null
      transition_to_ia_days      = var.environment == "prod" ? 90 : 30
      transition_to_glacier_days = var.environment == "prod" ? 180 : null
      expiration_days            = var.environment == "prod" ? null : 365
      noncurrent_expiration_days = 30
      abort_incomplete_upload_days = 7
    }
  ]

  # CORS (필요 시 설정)
  cors_rules = var.cors_rules

  # 로깅 (선택)
  logging_enabled       = var.logging_enabled
  logging_target_bucket = var.logging_target_bucket
  logging_target_prefix = var.logging_target_prefix

  # 모니터링
  enable_cloudwatch_alarms     = var.enable_cloudwatch_alarms
  alarm_bucket_size_threshold  = var.alarm_bucket_size_threshold
  alarm_object_count_threshold = var.alarm_object_count_threshold
  alarm_actions                = var.alarm_actions

  # 🔒 force_destroy (dev만 허용)
  force_destroy = var.environment == "dev" ? var.force_destroy : false

  # 🔒 필수 태그 (project-context에서 주입)
  environment  = var.environment
  service_name = "${var.project_name}-uploads"
  team         = var.team
  owner        = var.owner
  cost_center  = var.cost_center
  project      = var.project_name
  data_class   = var.data_class
}

# ============================================================================
# SSM Parameters (Cross-Stack Reference)
# ============================================================================
resource "aws_ssm_parameter" "bucket_name" {
  name        = "/${var.project_name}/s3/uploads-bucket-name"
  type        = "String"
  value       = module.s3_uploads.bucket_id
  description = "S3 bucket name for ${var.project_name} uploads"

  tags = local.common_tags
}

resource "aws_ssm_parameter" "bucket_arn" {
  name        = "/${var.project_name}/s3/uploads-bucket-arn"
  type        = "String"
  value       = module.s3_uploads.bucket_arn
  description = "S3 bucket ARN for ${var.project_name} uploads"

  tags = local.common_tags
}

# ============================================================================
# Locals
# ============================================================================
locals {
  naming = {
    bucket = "${var.project_name}-uploads-${var.environment}"
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
