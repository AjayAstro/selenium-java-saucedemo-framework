# Changelog

All notable changes to this project are documented here.
The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.1.0] - 2026-06-27

### Added
- **Live Allure reporting on GitHub Pages** — every `main` run publishes the
  latest report with execution history and trends to a single shareable link.
- **Allure environment & categories** — `environment.properties` (browser, OS,
  Java, base URL) and `categories.json` (defect classification) are generated
  automatically and power the report's Environment and Categories widgets.
- **ServiceLoader listener registration** so logging and screenshot-on-failure
  apply to every run, including single-test runs.
- Polished documentation: architecture and flow diagrams, badges, troubleshooting
  guide, roadmap, `CONTRIBUTING.md`, `CHANGELOG.md`, `LICENSE`, and `.editorconfig`.
- POM project metadata (license, SCM, developer, URL).

### Changed
- CI split into a `test` job and a `publish-report` job.

## [1.0.0] - 2026-06-27

### Added
- Initial Selenium + Java + TestNG framework for SauceDemo with a Cucumber BDD
  layer, Page Object Model, thread-safe driver management, layered configuration,
  Allure reporting with screenshots on failure, SLF4J + Log4j2 logging, and a
  GitHub Actions CI pipeline running headless Chrome.
- Test coverage across login, inventory, cart, checkout, navigation, and
  negative/edge scenarios.

### Fixed
- Reliable interaction with the SauceDemo React app under headless Chrome
  (scripted clicks, add/remove confirmation waits, React-aware form input).
