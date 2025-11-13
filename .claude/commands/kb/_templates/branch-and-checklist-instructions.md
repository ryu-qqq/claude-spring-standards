# Common TDD Instructions - Branch & Checklist Management

## MUST DO BEFORE STARTING

### 1. Git Branch Check ⚠️
**CRITICAL**: Never work on main/master branch!

**Steps**:
1. Check current branch: `git branch`
2. If on main/master:
   - Extract Issue Key from plan file (e.g., MEMBER-001)
   - Layer suffix: `-domain`, `-application`, `-persistence`, `-rest-api`, or `-integration`
   - Create branch: `git checkout -b feature/{ISSUE-KEY}-{layer}`
   - Example: `feature/MEMBER-001-application`
3. If branch doesn't exist: create it
4. If branch exists: checkout to it

**Why This Matters**:
- Prevents polluting main branch with WIP commits
- Enables clean PR workflow
- Protects production-ready main branch

---

### 2. Plan File Checklist Update 📝
**After completing each TDD phase**, update the plan file checklist items to `[x]`.

**Checklist Update Pattern**:
```markdown
# BEFORE (unmarked)
- [ ] `LoginTypeTest.java` 작성
- [ ] KAKAO, PHONE 타입 존재 확인 테스트

# AFTER (marked complete)
- [x] `LoginTypeTest.java` 작성
- [x] KAKAO, PHONE 타입 존재 확인 테스트
```

**When to Update**:
- **RED Phase**: After writing tests → Mark Red checklist as `[x]` → Commit
- **GREEN Phase**: After implementation → Mark Green checklist as `[x]` → Commit
- **REFACTOR Phase**: After refactoring → Mark Refactor checklist as `[x]` → Commit

**Why This Matters**:
- Tracks progress accurately in plan file
- Enables resuming from any point
- Documents what was completed

---

### 3. Test Status Markers in Plan File

**Status Convention**:
- `### Test N: ...` (unmarked) → Not started
- `### Test N: ... 🔄 IN PROGRESS` → Currently working
- `### Test N: ... ✅ COMPLETED` → Finished

**Update Rules**:
- Mark as `🔄 IN PROGRESS` when starting
- Mark as `✅ COMPLETED` when all phases (Red/Green/Refactor) are done

---

## Instructions Template (Copy to each go.md)

```markdown
## Instructions

1. **Check Git Branch** (see MUST DO section above):
   - Ensure NOT on main/master branch
   - Extract Issue Key from plan file (e.g., MEMBER-001)
   - If feature/{ISSUE-KEY}-{layer} branch doesn't exist, create it
   - Checkout to feature branch before starting TDD

2. **Read plan file** from `docs/prd/plans/{ISSUE-KEY}-{layer}-plan.md`

3. **Find the next unmarked test** in the {Layer} section

4. **Mark the test as in-progress** by adding "🔄 IN PROGRESS" marker

5. **Execute the TDD Cycle**:
   - **RED**: Write the simplest failing test first → **Update Red checklist items to [x]** → Commit
   - **GREEN**: Implement minimum code to make the test pass → **Update Green checklist items to [x]** → Commit
   - **REFACTOR**: Improve structure only after tests pass → **Update Refactor checklist items to [x]** → Commit
   - **TIDY**: Clean up tests using TestFixture pattern

6. **Run all tests** (excluding long-running tests)

7. **Verify** all tests pass before proceeding

8. **Mark test complete** in plan file (change "🔄 IN PROGRESS" to "✅ COMPLETED")
```
