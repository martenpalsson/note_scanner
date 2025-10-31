# Agent Consultation Checklist

This checklist ensures that specialized sub-agents are consulted at appropriate times during development to maintain code quality, verify completeness, and ensure compliance with project standards.

## When to Consult Agents

### MANDATORY Agent Consultation Points

You **MUST** consult the relevant agents in these scenarios:

#### 1. After Implementing a Feature
- [ ] **task-completion-validator**: Verify the feature is truly complete and functional
- [ ] **code-quality-pragmatist**: Check for over-engineering and unnecessary complexity
- [ ] **jenny-spec-validator** (if applicable): Ensure implementation matches specifications

**Trigger**: When you claim a feature or task is "complete" or "done"

#### 2. Before Committing Code
- [ ] **code-quality-pragmatist**: Review for pragmatic design and anti-patterns
- [ ] **claude-md-compliance-checker**: Ensure changes follow project guidelines

**Trigger**: Before running `git add` or `git commit`

#### 3. After UI Changes
- [ ] **ui-comprehensive-tester**: Perform thorough UI validation
- [ ] **task-completion-validator**: Verify UI works as intended

**Trigger**: After modifying any UI components, screens, or user flows

#### 4. When Build/Runtime Errors Occur
- [ ] **ultrathink-debugger**: Deep investigation and root cause analysis

**Trigger**: Build failures, runtime errors, or unexpected behavior

#### 5. Project Status Assessment (Periodic)
- [ ] **simona**: Realistic assessment of what's actually complete vs. claimed

**Trigger**: Before milestones, deployments, or when suspicious of completion status

---

## Agent Consultation Workflow

### Standard Feature Implementation Flow

```
1. Plan Feature
   ↓
2. Implement Code
   ↓
3. ✓ Run task-completion-validator
   ↓
4. ✓ Run code-quality-pragmatist
   ↓
5. Fix Issues Found
   ↓
6. ✓ Run claude-md-compliance-checker
   ↓
7. If UI changes: ✓ Run ui-comprehensive-tester
   ↓
8. If specs exist: ✓ Run jenny-spec-validator
   ↓
9. Build & Test
   ↓
10. Commit (only after all agents pass)
```

### Debugging Workflow

```
1. Encounter Error
   ↓
2. ✓ Run ultrathink-debugger
   ↓
3. Implement Fix
   ↓
4. ✓ Run task-completion-validator (verify fix works)
   ↓
5. ✓ Run code-quality-pragmatist (ensure fix isn't over-engineered)
   ↓
6. Build & Test
   ↓
7. Commit
```

---

## Quick Reference: Which Agent When?

| Scenario | Required Agents | Priority |
|----------|----------------|----------|
| Feature complete | task-completion-validator, code-quality-pragmatist | CRITICAL |
| Before commit | code-quality-pragmatist, claude-md-compliance-checker | CRITICAL |
| UI changes | ui-comprehensive-tester, task-completion-validator | HIGH |
| Build/runtime errors | ultrathink-debugger | CRITICAL |
| Spec compliance needed | jenny-spec-validator | HIGH |
| Project assessment | simona | CRITICAL |

---

## Enforcement Mechanism

### For Claude Code Assistant

**Before claiming task completion:**
1. Explicitly state: "Running validation agents..."
2. Use Task tool to launch required agents in parallel when possible
3. Wait for agent reports
4. Address all issues found
5. Only then claim completion

**Self-Check Questions:**
- Did I just implement a feature? → Run validators
- Am I about to commit? → Run quality/compliance checks
- Did I change UI? → Run UI tester
- Did I encounter an error? → Run debugger
- Am I claiming something is done? → Run completion validator 
- Does the completion validator say it is done?  → Run Simona

### For Developers

Add this to your Git pre-commit hook:

```bash
#!/bin/bash
echo "⚠️  REMINDER: Have you run the required agents?"
echo "  - task-completion-validator (if feature complete)"
echo "  - code-quality-pragmatist (before commit)"
echo "  - claude-md-compliance-checker (before commit)"
echo ""
read -p "Continue with commit? (y/n) " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]
then
    exit 1
fi
```

---

## Example: Complete Feature Implementation

```markdown
## Task: Add swipeable image viewer

### Implementation Steps:
1. ✅ Implemented HorizontalPager with text/image pages
2. ✅ Built successfully and deployed to device

### Agent Validation:

#### task-completion-validator:
- ✅ Feature is genuinely complete
- ✅ All claimed functionality present
- ⚠️  95% confidence (5% for lack of automated tests)

#### code-quality-pragmatist:
- ❌ Broken text formatting toolbar (doesn't work as expected)
- ❌ Redundant navigation icons (duplicates swipe)
- ❌ Auto-save thrashing (saves every keystroke)
- ❌ Export message ghost feature (no UI display)

#### Actions Taken:
1. Removed formatting toolbar
2. Removed navigation icons
3. Added 500ms debounce to auto-save
4. Removed export message code
5. Rebuilt and redeployed

#### Final Validation:
- ✅ task-completion-validator: PASS
- ✅ code-quality-pragmatist: PASS (pragmatic and clean)
- ✅ claude-md-compliance-checker: COMPLIANT

### Outcome: Feature complete and validated
```

---

## Benefits of This Process

1. **Catches Issues Early**: Validators find problems before they become technical debt
2. **Maintains Code Quality**: Pragmatist agent prevents over-engineering
3. **Ensures Completeness**: Completion validator verifies claims aren't superficial
4. **Enforces Standards**: Compliance checker maintains project consistency
5. **Improves Reliability**: UI tester validates user-facing functionality
6. **Accelerates Debugging**: Debugger agent provides systematic investigation

---

## Integration with Development Workflow

### Before First Use
1. Read this checklist
2. Understand which agents to use when
3. Review the workflow diagrams

### During Development
1. Keep checklist visible
2. Mark off agents as you consult them
3. Document agent findings
4. Address all issues before proceeding

### After Each Session
1. Review which agents were used
2. Note any patterns in findings
3. Adjust development approach accordingly

---

## Anti-Patterns to Avoid

❌ **Don't:**
- Skip agent consultation because "it's a small change"
- Claim completion without validation
- Ignore agent findings
- Run agents after committing
- Batch agent runs for multiple features

✅ **Do:**
- Run agents proactively
- Address findings immediately
- Use agents in parallel when possible
- Document agent results
- Treat agent feedback as mandatory

---

## Continuous Improvement

### Track Agent Effectiveness

Keep a log of agent findings:
```
Date: 2025-10-31
Feature: Swipeable image viewer
Agents Run: task-completion-validator, code-quality-pragmatist, claude-md-compliance-checker
Issues Found: 4 (formatting toolbar, navigation icons, auto-save, export message)
Time Saved: ~2 hours of debugging later
```

### Update This Checklist

As you discover new patterns:
1. Add new mandatory consultation points
2. Refine workflow diagrams
3. Document common findings
4. Share learnings with team

---

## Quick Start Command

When in doubt, run this sequence:

```bash
# After implementing a feature:
1. task-completion-validator → verify it's done
2. code-quality-pragmatist → check for over-engineering
3. claude-md-compliance-checker → ensure compliance
4. [If UI] ui-comprehensive-tester → validate UI
5. [If spec exists] jenny-spec-validator → verify spec match

# Then and only then:
git add . && git commit
```

---

## Version History

- v1.0 (2025-10-31): Initial checklist created based on swipeable viewer implementation learnings
