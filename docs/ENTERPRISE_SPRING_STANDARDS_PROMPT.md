# Spring Boot Standards Template - Brainstorming Session

## Core Objective
Create an enforceable standards template for Spring Boot 3.3.x projects using Java 21 that:
- Implements pure Hexagonal Architecture (Ports & Adapters)
- Enforces strict module boundaries with zero violations
- Prevents scope creep (ONLY requested code gets written)
- Maintains production-level quality through automated gates

## Technical Stack Requirements
- Java 21 with modern features (records, pattern matching, virtual threads)
- Spring Boot 3.3.x with reactive and traditional stacks
- JPA with QueryDSL for complex queries
- Multi-module Gradle/Maven structure
- Infrastructure as Code via Terraform

## Architecture Enforcement Needs

### Module Structure
Three core modules with STRICT dependency rules:
1. `domain` - Pure business logic, zero framework dependencies
2. `application` - Use cases/services, orchestrates domain
3. `adapter` - External interfaces (REST, DB, messaging)

### Critical Constraint
Claude Code doesn't support folder-specific hooks. I need a MASTER hook that:
- Analyzes file path/module context
- Routes to appropriate validation rules
- Enforces module-specific ArchUnit rules
- Detects and prevents dead code BEFORE commits
- Blocks cross-module violations in real-time

## Quality Gate Requirements

### ArchUnit Rules
Module-specific architectural tests:
- Domain: No Spring annotations, no external dependencies
- Application: Only depends on domain, no adapter references
- Adapter: Can depend on application/domain, handles all I/O

### Static Analysis
- SpotBugs: Bug pattern detection with custom rules
- Checkstyle: Enforce naming, formatting, complexity limits
- Dead Code Detection: CRITICAL - must catch unrequested code generation

### Documentation Standards
- Mandatory @author tags with ownership
- Public API Javadoc requirements
- Architecture Decision Records (ADRs) for key decisions
- Module-level README with dependency rules

## Key Problems to Solve

### 1. Scope Discipline
How do we ensure Claude ONLY writes explicitly requested code? Need concrete detection strategies.

### 2. Hook Routing Logic
Design intelligent master hook that:
- Parses file paths to determine module context
- Applies module-specific validation rules
- Provides clear error messages for violations
- Suggests corrections for common mistakes

### 3. Dead Code Prevention
Systematic approach to detect:
- Unused methods/classes
- Unrequested features
- Speculative implementations
- "Helper" code not explicitly asked for

### 4. Boundary Enforcement
Concrete strategies to prevent:
- Domain importing Spring classes
- Application directly accessing adapters
- Adapter logic leaking into domain

### 5. Terraform Patterns
Standardized modules for:
- RDS/Aurora setup
- ECS/Fargate deployment
- API Gateway configuration
- Security groups and networking

## Brainstorming Focus Areas

### 1. ArchUnit Rule Definitions
- Exact rules for each module type
- Custom rules for specific patterns
- Integration with CI/CD pipeline
- Failure handling and reporting

### 2. Master Hook Design
- Context detection algorithm
- Rule routing table structure
- Performance optimization for large projects
- Integration with IDE and CLI tools

### 3. Checkstyle Configuration
- Naming conventions (entities, DTOs, mappers)
- Method complexity limits
- Import order and grouping
- Custom checks for patterns

### 4. SpotBugs Customization
- Priority levels for different bug types
- Custom detectors for Spring antipatterns
- Exclusion filters for generated code
- Performance impact thresholds

### 5. Dead Code Detection Strategy
- AST analysis approaches
- Coverage-based detection
- Unused dependency scanning
- Automated removal workflows

### 6. Documentation Templates
- Javadoc templates for different component types
- ADR template with decision matrix
- Module README structure
- API documentation standards

### 7. Build Configuration
- Multi-module build optimization
- Dependency management strategy
- Plugin configuration inheritance
- Task orchestration for quality gates

### 8. Testing Standards
- Unit test patterns per module type
- Integration test boundaries
- Test data management
- Coverage requirements per module

## Expected Outcomes

This brainstorming session should produce:
1. Complete ArchUnit test suite for hexagonal architecture
2. Master hook implementation with intelligent routing
3. Checkstyle/SpotBugs configuration files
4. Dead code detection pipeline
5. Documentation templates and examples
6. Multi-module build setup
7. Terraform module library
8. Pre-commit hooks and CI/CD integration
9. Developer guide with examples
10. Violation resolution playbook

## Discovery Questions

Let's start by understanding:
1. What are your preferred build tool choices and why?
2. How strict should architectural boundaries be (fail fast vs warnings)?
3. What Spring modules do you commonly use across projects?
4. What's your team's current documentation maturity level?
5. Any existing patterns or conventions to preserve?
6. What's your deployment infrastructure (AWS, GCP, Azure, on-prem)?
7. What are the most common architectural violations you want to prevent?
8. How should the master hook handle violations (block, warn, log)?
9. What's your preferred dead code detection approach (static analysis, coverage)?
10. Any specific QueryDSL patterns or conventions you follow?
