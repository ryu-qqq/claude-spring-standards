rootProject.name = "spring-hexagonal-template"

// ========================================
// Core Modules (Hexagonal Architecture)
// ========================================
include("domain")
include("application")

// ========================================
// Adapter Modules (Ports & Adapters)
// ========================================
// Inbound Adapters (Driving) - {type}-in-{subject}-{middleware}
include("adapter-in:rest-api")

// Outbound Adapters (Driven) - {purpose}-{technology}
include("adapter-out:persistence-mysql")
include("adapter-out:client-aws-s3")
include("adapter-out:client-aws-sqs")

// ========================================
// Bootstrap Modules (Runnable Applications)
// ========================================
include("bootstrap:bootstrap-web-api")

// ========================================
// Project Structure
// ========================================
project(":domain").projectDir = file("domain")
project(":application").projectDir = file("application")

project(":adapter-in:rest-api").projectDir = file("adapter-in/rest-api")
project(":adapter-out:persistence-mysql").projectDir = file("adapter-out/persistence-mysql")
project(":adapter-out:client-aws-s3").projectDir = file("adapter-out/client-aws-s3")
project(":adapter-out:client-aws-sqs").projectDir = file("adapter-out/client-aws-sqs")

project(":bootstrap:bootstrap-web-api").projectDir = file("bootstrap/bootstrap-web-api")
