---
name: gemini-review
description: "Analyze Gemini code review comments and generate refactoring strategy"
category: utility
complexity: intermediate
mcp-servers: ["sequential-thinking"]
personas: ["analyzer", "architect"]
---

# /sc:gemini-review - Gemini Code Review Analysis

## Triggers
- PR review analysis requests from Gemini reviewer
- Need for systematic refactoring decision-making
- Code review feedback processing and action planning
- Quality improvement planning based on automated reviews

## Usage
```
/sc:gemini-review [pr-number] [--analyze-only] [--auto-refactor] [--priority high|medium|low]
```

## Behavioral Flow
1. **Fetch**: Retrieve PR details and Gemini review comments using GitHub CLI
2. **Analyze**: Parse and categorize review comments by type and severity
3. **Evaluate**: Assess each comment for refactoring necessity and impact
4. **Plan**: Generate refactoring strategy with priority and approach
5. **Report**: Present comprehensive analysis with actionable recommendations

Key behaviors:
- Intelligent comment categorization (critical, improvement, suggestion, style)
- Impact assessment for each review item
- Refactoring priority matrix (must-fix, should-fix, nice-to-have)
- Code location mapping and dependency analysis
- Implementation strategy with effort estimation

## Tool Coordination
- **Bash**: GitHub CLI operations for PR and review data fetching
- **Sequential Thinking**: Multi-step reasoning for complex refactoring decisions
- **Grep**: Code pattern analysis and issue location identification
- **Read**: Source code inspection for context understanding
- **Write**: Refactoring plan documentation and decision records

## Key Patterns
- **Review Parsing**: Gemini comments → structured analysis data
- **Severity Classification**: Comment type → priority level assignment
- **Impact Analysis**: Code changes → ripple effect assessment
- **Strategy Generation**: Analysis results → refactoring roadmap
- **Decision Documentation**: Strategy → actionable implementation plan

## Examples

### Analyze Current PR's Gemini Review
```
/sc:gemini-review
# Analyzes Gemini review comments on current branch's PR
# Generates comprehensive refactoring strategy
```

### Analyze Specific PR
```
/sc:gemini-review 42
# Analyzes Gemini review comments on PR #42
# Provides detailed breakdown and recommendations
```

### Analysis-Only Mode
```
/sc:gemini-review --analyze-only
# Analyzes review comments without generating refactoring plan
# Useful for understanding review feedback first
```

### Auto-Refactor High Priority Items
```
/sc:gemini-review --auto-refactor --priority high
# Analyzes reviews and automatically implements critical fixes
# Only applies changes for high-priority items
```

## Analysis Output Structure

### 1. Review Summary
- Total comments count
- Severity distribution (critical/improvement/suggestion/style)
- Common themes and patterns
- Overall review sentiment

### 2. Categorized Analysis
For each review comment:
- **Category**: Critical | Improvement | Suggestion | Style
- **Location**: File path and line numbers
- **Issue**: Description of the problem
- **Impact**: Potential consequences if unaddressed
- **Refactor Decision**: Must-fix | Should-fix | Nice-to-have | Skip
- **Reasoning**: Why this decision was made
- **Effort**: Estimated implementation time

### 3. Refactoring Strategy
- **Immediate Actions**: Must-fix items requiring urgent attention
- **Short-term Improvements**: Should-fix items for next iteration
- **Long-term Enhancements**: Nice-to-have items for backlog
- **Skipped Items**: Reasoning for decisions to skip certain suggestions

### 4. Implementation Plan
- **Phase 1**: Critical fixes (estimated time)
- **Phase 2**: Important improvements (estimated time)
- **Phase 3**: Optional enhancements (estimated time)
- **Dependencies**: Order of implementation based on code dependencies
- **Testing Strategy**: Required test updates for each phase

### 5. Decision Record
- **Accepted Changes**: What will be implemented and why
- **Deferred Changes**: What will be addressed later and when
- **Rejected Changes**: What won't be implemented and reasoning
- **Trade-offs**: Analyzed costs vs. benefits for each decision

## Boundaries

**Will:**
- Fetch and analyze Gemini review comments from GitHub PRs
- Categorize and prioritize review feedback systematically
- Generate comprehensive refactoring strategies with effort estimates
- Provide decision reasoning and trade-off analysis
- Map review comments to specific code locations

**Will Not:**
- Automatically implement changes without user review (unless --auto-refactor)
- Dismiss Gemini suggestions without analysis
- Make architectural decisions without considering project context
- Modify code outside the scope of review comments

## Decision Criteria

### Must-Fix (Critical)
- Security vulnerabilities
- Data integrity issues
- Breaking changes or runtime errors
- Critical performance problems
- Violations of core architecture principles

### Should-Fix (Improvement)
- Code maintainability issues
- Moderate performance improvements
- Important best practice violations
- Significant technical debt
- Readability and documentation gaps

### Nice-to-Have (Suggestion)
- Code style improvements
- Minor optimizations
- Optional refactoring opportunities
- Enhanced error messages
- Additional code comments

### Skip (Not Applicable)
- Conflicts with project standards
- Out of scope for current iteration
- Low ROI improvements
- Overly opinionated suggestions
- Already addressed by other means

## Integration with Git Workflow

### Recommended Flow
1. Create PR → Gemini reviews automatically
2. Run `/sc:gemini-review` to analyze feedback
3. Review generated refactoring strategy
4. Implement approved changes systematically
5. Update PR with refactored code
6. Re-run analysis if Gemini provides additional feedback

### Commit Strategy
- Group related refactoring changes by category
- Use conventional commit messages referencing review items
- Create separate commits for critical vs. improvement changes
- Document decision rationale in commit messages

## Advanced Usage

### Custom Priority Filtering
```
/sc:gemini-review --filter "security,performance"
# Only analyze security and performance-related comments
```

### Diff-Based Analysis
```
/sc:gemini-review --diff-only
# Focus analysis on code that changed in the PR
```

### Export Analysis
```
/sc:gemini-review --export refactoring-plan.md
# Export comprehensive analysis to markdown file
```

### Interactive Mode
```
/sc:gemini-review --interactive
# Step through each review comment with decision prompts
```

## Tool Requirements
- GitHub CLI (`gh`) installed and authenticated
- Repository must have Gemini configured as PR reviewer
- Current branch must have associated PR or PR number provided
