# Specialized Agent Usage Guide

This guide describes when to use each specialized sub-agent during development to ensure code quality, completeness, and adherence to project standards.

## Agent Overview

### task-completion-validator
**When to use:** After implementing a feature or task
**Purpose:** Verifies that claimed completions are truly functional and not superficial, mocked, or incomplete
**Example scenarios:**
- Developer claims authentication system is complete
- Feature implementation is marked as done
- Before moving to the next major task

### code-quality-pragmatist
**When to use:** Before committing code
**Purpose:** Reviews code for over-engineering, unnecessary complexity, and common anti-patterns
**Example scenarios:**
- After implementing a new feature
- Before creating a pull request
- After making architectural decisions

### jenny-spec-validator
**When to use:** When requirements seem met
**Purpose:** Verifies that implementation matches project specifications and requirements
**Example scenarios:**
- After completing a feature per specification
- When suspicious of gaps between requirements and implementation
- Before marking a milestone as complete

### ui-comprehensive-tester
**When to use:** For UI changes
**Purpose:** Performs thorough UI testing across different scenarios and edge cases
**Example scenarios:**
- After implementing new UI components
- After modifying user flows
- Before deploying UI changes to production

### simona
**When to use:** To assess project status
**Purpose:** Provides realistic assessment of completion status, cuts through incomplete implementations
**Example scenarios:**
- Multiple tasks marked complete but project seems broken
- Before deployment to verify readiness
- When needing a reality-check on claimed progress

### claude-md-compliance-checker
**When to use:** After making code changes
**Purpose:** Ensures recent changes adhere to project-specific instructions in CLAUDE.md files
**Example scenarios:**
- After implementing new features
- After adding documentation
- Before committing significant changes

### ultrathink-debugger
**When to use:** When encountering errors during building or running the project
**Purpose:** Deep investigation and root cause analysis for bugs, errors, and unexpected behavior
**Example scenarios:**
- Build failures
- Runtime errors in production
- Intermittent test failures
- Environment-specific issues

## Recommended Workflow

1. **Implementation Phase**
   - Write code
   - Use TodoWrite to track tasks

2. **Validation Phase**
   - Run `task-completion-validator` to verify completion
   - Run `claude-md-compliance-checker` to ensure guideline adherence

3. **Quality Check Phase**
   - Run `code-quality-pragmatist` to check for over-engineering
   - Run `jenny-spec-validator` to verify spec compliance
   - If UI changes: run `ui-comprehensive-tester`

4. **Pre-Commit Phase**
   - Final review with `code-quality-pragmatist`
   - Commit changes

5. **Debugging Phase** (when needed)
   - Use `ultrathink-debugger` for any build or runtime errors

6. **Project Assessment** (periodic)
   - Use `simona` for honest status assessment
   - Create realistic completion plans

## Notes

- These agents should be used proactively, not just when explicitly requested
- Multiple agents can be run in parallel when appropriate
- Agent feedback should be acted upon before proceeding to next steps

## Enforcement and Compliance

For a detailed checklist and workflow to ensure agents are consulted at the right times, see:

**[AGENT_CONSULTATION_CHECKLIST.md](./AGENT_CONSULTATION_CHECKLIST.md)**

This checklist provides:
- Mandatory consultation points
- Step-by-step workflows
- Self-check questions
- Integration with Git workflow
- Example usage and anti-patterns to avoid

**Key Rule**: Before claiming any task is "complete" or "done", you MUST run the relevant validation agents and address all findings.
