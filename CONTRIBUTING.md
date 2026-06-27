# Contributing

Thanks for taking a look! This is primarily a portfolio project, but the
guidelines below keep it tidy and make contributions easy to review.

## Getting set up

1. Install **JDK 21+** and **Maven 3.9+** (Chrome is resolved automatically by
   WebDriverManager).
2. Clone the repo and run the smoke suite to confirm your setup:
   ```bash
   mvn clean test -Dsuite=smoke.xml
   ```

## Branching & commits

- Branch off `main` using a descriptive name, e.g. `feat/checkout-coupons`
  or `fix/cart-badge-wait`.
- Keep commits focused and write imperative, descriptive messages
  (e.g. _"Add wait for cart badge to settle"_).
- Open a pull request into `main`; CI must be green before merge.

## Coding conventions

- **Page Objects** live in `src/main/java/.../pages` and expose intent-revealing
  methods (`addProductToCart`), never raw locators, to callers.
- **No `Thread.sleep`.** Synchronise with the helpers in `BasePage`
  (explicit waits only).
- Keep locators in one place per page as `private static final By` fields;
  prefer SauceDemo's `data-test` attributes where available.
- Add an `@Step` annotation to user-facing page actions so they appear in Allure.
- Match the existing formatting (see `.editorconfig`). Run `mvn -q test-compile`
  before pushing.

## Adding tests

- Put TestNG tests under `src/test/java/.../tests` and register new classes in
  `testng.xml`.
- Annotate tests with `@Epic` / `@Feature` / `@Story` / `@Severity` and a clear
  `description` so the Allure report stays readable.
- For BDD, add scenarios under `src/test/resources/features` and step
  definitions under `src/test/java/.../bdd`.

## Before you open a PR

```bash
mvn clean test          # full regression suite
```

All tests should pass and the build should be clean.
