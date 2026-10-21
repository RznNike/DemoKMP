<!-- Context: project-intelligence/notes | Priority: medium | Version: 1.2 | Updated: 2026-10-21 -->

# Living Notes

> Active state for DemoKMP: tech debt, open questions, gotchas, and patterns worth preserving.

## Quick Reference

- **Purpose**: current state and open items
- **Update**: when status changes

---

## Technical Debt

| Item                            | Impact                                                                                  | Priority | Mitigation                                                   |
|---------------------------------|-----------------------------------------------------------------------------------------|----------|--------------------------------------------------------------|
| No Android unit test source set | Android platform code (camera, MLKit, permissions) is untested; regressions caught late | Medium   | Add `androidUnitTest` with `kotlin.test`                     |
| No instrumented / UI tests      | UI regressions caught only by manual QA                                                 | Low      | Add `androidTest` with Compose UI tests, or accept manual QA |

## Open Questions

| Question                | Stakeholders | Status | Next Action                     |
|-------------------------|--------------|--------|---------------------------------|
| Add Android unit tests? | Maintainer   | Open   | Decide scope for a demo project |

## Known Issues

| Issue                               | Severity | Workaround                             | Status                       |
|-------------------------------------|----------|----------------------------------------|------------------------------|
| Desktop builds are OS-specific      | Low      | Build on target OS / VM                | Known (by design)            |
| Fakes drift from gateway interfaces | Low      | Update fakes when an interface changes | Known (no mock regeneration) |

## Insights & Lessons Learned

### What Works Well
- Clean Architecture + Koin keeps features isolated and copyable
- `expect/actual` cleanly separates platform code
- `UseCaseResult` keeps error handling explicit
- Splitting tests into `commonTest` + `desktopTest` keeps 82 of 86 tests target-independent
- Handwritten fakes make test state inspectable and avoid a mocking-framework dependency

### Gotchas for Maintainers
- `expect`/`actual` pairs must be updated together across source sets
- KSP codegen (Room/Ktorfit) must run before compile; clean build if types are stale
- `BaseUiViewModel.setProgress` debounces via a `Timer` (250ms default)
- Fakes must be updated by hand whenever a gateway interface changes — nothing regenerates them
- `commonTest` cannot touch JVM/desktop-only APIs; such tests belong in `desktopTest`

## Patterns & Conventions

### Code Patterns Worth Preserving
- Gateway interface (domain) + `Impl` (data) separation
- One Koin module per layer/concern
- Immutable `@Immutable` UiState data classes
- Room entity ↔ domain model extension mappers
- Test class named `<ClassUnderTest>Test`, in the same package as the production class

## Onboarding Checklist

- [ ] Review tech debt and its impact
- [ ] Know open questions
- [ ] Be aware of gotchas
- [ ] Know where tests live and how fakes are written

## 📂 Codebase References

| Item    | Code                                                                    |
|---------|-------------------------------------------------------------------------|
| Signing | `composeApp/build.gradle.kts` (`signingConfigs`), `demokmp.jks`         |
| Version | `composeApp/build.gradle.kts` (`globalVersionName`/`globalVersionCode`) |
| DI      | `app/di/*.kt`                                                           |
| UI base | `app/common/viewmodel/BaseUiViewModel.kt`                               |
| Errors  | `domain/common/interactor/UseCaseResult.kt`                             |
| Tests   | `composeApp/src/commonTest/`, `composeApp/src/desktopTest/`             |
| Fakes   | `composeApp/src/commonTest/.../domain/gateway/Fake*.kt`                 |

## Related Files

- `decisions-log.md` · `technical-domain.md` · `testing-strategy.md` · `business-domain.md`
