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
include("adapter:in:rest-in-admin-servlet")

// Outbound Adapters (Driven) - {purpose}-{technology}
include("adapter:out:persistence-jpa")
include("adapter:out:client-aws-s3")
include("adapter:out:client-aws-sqs")

// ========================================
// Bootstrap Modules (Runnable Applications)
// ========================================
include("bootstrap:bootstrap-web-api")

// ========================================
// Project Structure
// ========================================
project(":domain").projectDir = file("domain")
project(":application").projectDir = file("application")

project(":adapter:in:rest-in-admin-servlet").projectDir = file("adapter/in/rest-in-admin-servlet")
project(":adapter:out:persistence-jpa").projectDir = file("adapter/out/persistence-jpa")
project(":adapter:out:client-aws-s3").projectDir = file("adapter/out/client-aws-s3")
project(":adapter:out:client-aws-sqs").projectDir = file("adapter/out/client-aws-sqs")

project(":bootstrap:bootstrap-web-api").projectDir = file("bootstrap/bootstrap-web-api")
