# ============================================================================
# ECR Repositories
# ============================================================================
# 컨테이너 이미지 저장소 - Infrastructure 모듈 Wrapper
#
# 🎯 컨벤션 강제 항목:
#   - 네이밍: {project}-{service}-{env}
#   - 태그: 8개 필수 태그 자동 적용
#   - 보안: IMMUTABLE 태그, scan_on_push 활성화
#   - 관리: Lifecycle Policy 자동 적용
# ============================================================================

# ============================================================================
# ECR Repository: web-api
# ============================================================================
module "ecr_web_api" {
  source = "git::https://github.com/ryu-qqq/Infrastructure.git//terraform/modules/ecr?ref=${var.infrastructure_module_ref}"

  # 🔒 네이밍 컨벤션 강제 (project-context에서 정의)
  name = local.naming.ecr_web_api

  # 🔒 보안 설정 (컨벤션 강제 - 변경 금지)
  image_tag_mutability = "IMMUTABLE"
  scan_on_push         = true

  # KMS 암호화 (선택 - null이면 AES256)
  kms_key_arn = var.kms_key_arn

  # Lifecycle Policy (컨벤션 기본값)
  enable_lifecycle_policy    = true
  max_image_count            = var.max_image_count
  lifecycle_tag_prefixes     = ["v", "prod", "latest"]
  untagged_image_expiry_days = 7

  # SSM Parameter 생성 (Cross-Stack 참조용)
  create_ssm_parameter = true

  # 🔒 필수 태그 (project-context에서 주입)
  environment  = var.environment
  service_name = "${var.project_name}-web-api"
  team         = var.team
  owner        = var.owner
  cost_center  = var.cost_center
  project      = var.project_name
  data_class   = "confidential"
}

# ============================================================================
# ECR Repository: scheduler (선택)
# ============================================================================
module "ecr_scheduler" {
  count  = var.enable_scheduler ? 1 : 0
  source = "git::https://github.com/ryu-qqq/Infrastructure.git//terraform/modules/ecr?ref=${var.infrastructure_module_ref}"

  name = local.naming.ecr_scheduler

  image_tag_mutability = "IMMUTABLE"
  scan_on_push         = true
  kms_key_arn          = var.kms_key_arn

  enable_lifecycle_policy    = true
  max_image_count            = var.max_image_count
  lifecycle_tag_prefixes     = ["v", "prod", "latest"]
  untagged_image_expiry_days = 7

  create_ssm_parameter = true

  environment  = var.environment
  service_name = "${var.project_name}-scheduler"
  team         = var.team
  owner        = var.owner
  cost_center  = var.cost_center
  project      = var.project_name
  data_class   = "confidential"
}

# ============================================================================
# Locals - 네이밍 컨벤션
# ============================================================================
locals {
  naming = {
    ecr_web_api   = "${var.project_name}-web-api-${var.environment}"
    ecr_scheduler = "${var.project_name}-scheduler-${var.environment}"
  }
}
