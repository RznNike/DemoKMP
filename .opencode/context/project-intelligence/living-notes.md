<!-- Context: project-intelligence/notes | Priority: medium | Version: 1.1 | Updated: 2026-10-15 -->

# Living Notes

> Active state for DemoKMP: tech debt, open questions, gotchas, and patterns worth preserving.

## Quick Reference

- **Purpose**: current state and open items
- **Update**: when status changes

## Technical Debt

| Item | Impact | Priority | Mitigation |
|------|--------|----------|------------|
| No shared test source set | Logic unverified; no regression safety | Medium | Add `commonTest` with `kotlin.test` |

## Open Questions

| Question | Stakeholders | Status | Next Action |
|----------|--------------|--------|-------------|
| Add a test suite? | Maintainer | Open | Decide scope for a demo project |
| Split reusable code into libraries? | Maintainer | Open | Decide if artifacts should publish |

## Known Issues

| Issue | Severity | Workaround | Status |
|-------|----------|------------|--------|
| Desktop builds are OS-specific | Low | Build on target OS / VM | Known (by design) |

## Insights & Lessons Learned

### What Works Well
- Clean Architecture + Koin keeps features isolated and copyable
- `expect/actual` cleanly separates platform code
- `UseCaseResult` keeps error handling explicit

### Gotchas for Maintainers
- `expect`/`actual` pairs must be updated together across source sets
- KSP codegen (Room/Ktorfit) must run before compile; clean build if types are stale
- `BaseUiViewModel.setProgress` debounces via a `Timer` (250ms default)

## Patterns & Conventions

### Code Patterns Worth Preserving
- Gateway interface (domain) + `Impl` (data) separation
- One Koin module per layer/concern
- Immutable `@Immutable` UiState data classes
- Room entity ↔ domain model extension mappers

## Archive (Resolved Items)

(none yet)

## Onboarding Checklist

- [ ] Review tech debt and its impact
- [ ] Know open questions
- [ ] Be aware of gotchas

## 📂 Codebase References

| Item | Code |
|------|------|
| Signing | `composeApp/build.gradle.kts` (`signingConfigs`), `demokmp.jks` |
| Version | `composeApp/build.gradle.kts` (`globalVersionName`/`globalVersionCode`) |
| DI | `app/di/*.kt` |
| UI base | `app/common/viewmodel/BaseUiViewModel.kt` |
| Errors | `domain/common/interactor/UseCaseResult.kt` |

## Related Files

- `decisions-log.md` · `technical-domain.md` · `business-domain.md`
