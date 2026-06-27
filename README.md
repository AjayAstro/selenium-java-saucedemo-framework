# SauceDemo Selenium Java Automation Framework

A production-quality UI test automation framework for the
[SauceDemo](https://www.saucedemo.com/) web application, built with **Java 21**,
**Selenium WebDriver**, **TestNG** (primary) and a small **Cucumber BDD** layer.
It demonstrates the patterns expected of a real-world QA automation suite:
the Page Object Model, thread-safe parallel execution, externalised
configuration, explicit waits (no flaky sleeps), rich Allure reporting with
screenshots on failure, and a ready-to-run GitHub Actions pipeline.

[![CI](https://github.com/AjayAstro/selenium-java-saucedemo-framework/actions/workflows/ci.yml/badge.svg)](https://github.com/AjayAstro/selenium-java-saucedemo-framework/actions/workflows/ci.yml)

---

## Table of contents

- [Tech stack](#tech-stack)
- [Framework structure](#framework-structure)
- [Test coverage](#test-coverage)
- [Configuration](#configuration)
- [Running locally](#running-locally)
- [Running specific suites / tests](#running-specific-suites--tests)
- [Allure reporting](#allure-reporting)
- [Continuous integration](#continuous-integration)
- [Design highlights](#design-highlights)
- [Why this demonstrates real QA skills](#why-this-demonstrates-real-qa-skills)

---

## Tech stack

| Concern              | Choice                                            |
|----------------------|---------------------------------------------------|
| Language             | Java 21                                            |
| Browser automation   | Selenium WebDriver 4                               |
| Build / dependencies | Maven                                              |
| Test runner          | TestNG (primary)                                   |
| BDD layer            | Cucumber 7 (runs through TestNG)                   |
| Driver provisioning  | WebDriverManager                                   |
| Reporting            | Allure                                             |
| Logging              | SLF4J + Log4j2                                     |
| CI/CD                | GitHub Actions (headless Chrome)                   |
| Design pattern       | Page Object Model + reusable component objects     |

---

## Framework structure

```
selenium-java-saucedemo-framework
├── pom.xml                        # Build, dependencies, Surefire + Allure plugins
├── testng.xml                     # Full regression suite (UI + BDD)
├── smoke.xml                      # Fast smoke suite
├── .github/workflows/ci.yml       # GitHub Actions pipeline
│
├── src/main/java/com/saucedemo
│   ├── config/      ConfigManager.java          # Layered, overridable configuration
│   ├── driver/      DriverFactory, DriverManager, BrowserType
│   ├── pages/       BasePage + one Page Object per page + HeaderComponent
│   ├── listeners/   TestListener.java           # Logging + screenshot-on-failure
│   ├── testdata/    Products.java, Messages.java # Shared constants
│   └── utils/       ScreenshotUtils.java
│
└── src/test
    ├── java/com/saucedemo
    │   ├── base/    BaseTest.java                # Per-test driver lifecycle
    │   ├── tests/   LoginTest, InventoryTest, CartTest,
    │   │            CheckoutTest, NavigationTest, NegativeAccessTest
    │   └── bdd/     Hooks, steps/, runner/       # Cucumber layer
    └── resources
        ├── config/config.properties             # Default configuration
        ├── features/login.feature               # Gherkin scenarios
        ├── log4j2.xml                            # Logging configuration
        └── allure.properties
```

The Page Objects live in `src/main` so they form a reusable library; the tests
that consume them live in `src/test`. Every page extends `BasePage`, which owns
the driver, the explicit `WebDriverWait`, and the synchronised interaction
helpers — so no concrete page ever duplicates wait logic or calls `Thread.sleep`.

---

## Test coverage

**40+ checks across 6 test classes plus a Cucumber feature.**

| Area        | Scenarios |
|-------------|-----------|
| **Login**       | standard user success · locked-out user · invalid credentials · missing username · missing password |
| **Inventory**   | page loads · product list visible · sort A→Z · sort Z→A · price low→high · price high→low |
| **Cart**        | add one item · add multiple items · remove item · badge updates · cart persists across navigation |
| **Checkout**    | full happy path · order-complete message · missing first name · missing last name · missing postal code · overview totals (subtotal = Σ items, total = subtotal + tax) |
| **Navigation**  | logout · reset app state · menu opens/closes |
| **Negative/edge** | protected page redirects unauthenticated users · locked user cannot reach inventory · checkout blocks on missing required fields |
| **BDD (Cucumber)** | login happy path · locked-out user · invalid-credentials scenario outline |

---

## Configuration

All settings live in `src/test/resources/config/config.properties` and **any
value can be overridden at runtime** with a JVM system property or environment
variable of the same name. Resolution order: **system property → environment
variable → properties file**.

| Property                 | Default                     | Description                                  |
|--------------------------|-----------------------------|----------------------------------------------|
| `baseUrl`                | `https://www.saucedemo.com` | Application under test                        |
| `browser`                | `chrome`                    | `chrome` \| `firefox` \| `edge`              |
| `headless`               | `true`                      | Run without a visible UI                     |
| `maximize`               | `true`                      | Maximise window (ignored when headless)      |
| `explicitWaitSeconds`    | `15`                        | Timeout for all explicit waits               |
| `pageLoadTimeoutSeconds` | `30`                        | Page-load timeout                            |
| `standardUser` / `password` | SauceDemo demo creds     | Test data                                    |
| `browserBinary`          | *(auto)*                    | Optional explicit browser binary path        |
| `browserVersion`         | *(auto)*                    | Optionally pin the driver version            |

Example: `mvn clean test -Dbrowser=chrome -Dheadless=false`

---

## Running locally

### Prerequisites
- JDK 21+
- Maven 3.9+
- Google Chrome (the matching driver is downloaded automatically by WebDriverManager)

### Run the full regression suite
```bash
mvn clean test
```

### Run headed (watch the browser)
```bash
mvn clean test -Dheadless=false
```

---

## Running specific suites / tests

```bash
# Fast smoke suite (login + add-to-cart + checkout happy paths)
mvn clean test -Dsuite=smoke.xml

# A single test class
mvn clean test -Dtest=LoginTest

# A single test method
mvn clean test -Dtest=CheckoutTest#successfulCheckout

# Only the Cucumber BDD layer
mvn clean test -Dtest=CucumberRunnerTest
```

---

## Allure reporting

The build writes Allure results to `target/allure-results`. Generate and open
the HTML report:

```bash
# Generate a static report into target/site/allure-maven-plugin
mvn allure:report

# Or build and open it in a browser in one step
mvn allure:serve
```

The report includes meaningful test names and descriptions, **severity labels**
(`@Severity`), an Epic → Feature → Story hierarchy, step-by-step breakdowns
(`@Step`), browser/session parameters, and **screenshots attached automatically
on any failure**.

> _Add a screenshot of your generated Allure dashboard here, e.g._
> `docs/allure-report.png`

---

## Continuous integration

`.github/workflows/ci.yml` runs on every push and pull request:

1. Checks out the repository and sets up **JDK 21** with a **cached Maven** repo.
2. Installs stable **Google Chrome**.
3. Runs the full suite headless: `mvn -B clean test -Dheadless=true`.
4. Generates the Allure report.
5. Uploads **Allure results**, the **Allure HTML report**, and
   **Surefire reports + logs** as build artifacts (downloadable from the run).
6. **Fails the pipeline if any test fails.**

---

## Design highlights

- **Page Object Model** with a shared `BasePage` and a reusable `HeaderComponent`
  for the cart/menu that appear on every authenticated page.
- **Thread-safe parallel execution** — the driver is held in a `ThreadLocal` via
  `DriverManager`, and `testng.xml` runs classes in parallel.
- **Explicit waits only** — every interaction synchronises on element
  state; there are no arbitrary `Thread.sleep` calls.
- **Centralised, overridable configuration** — no magic strings scattered
  through the tests.
- **Screenshots on failure** captured by a TestNG listener and attached to
  Allure (and a Cucumber hook does the same for BDD scenarios).
- **Clean, intention-revealing assertions** backed by shared `Messages` and
  `Products` constants.

---

## Why this demonstrates real QA skills

This is deliberately more than a beginner demo. It shows the things that
matter on a real automation team: a maintainable architecture that separates
*what* a test does from *how* the UI works, stable synchronisation that avoids
flakiness, configuration that lets the same suite run locally and in CI across
browsers, professional reporting that a non-engineer can read, and a green CI
pipeline that gates every change. The Cucumber layer shows BDD fluency while
TestNG remains the primary, scalable engine — exactly the kind of pragmatic,
production-minded setup employers and clients look for.
