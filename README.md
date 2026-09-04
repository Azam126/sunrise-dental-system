# Sunrise Dental Clinic — Appointment and Patient Management System

CIS6003 Advanced Programming (WRIT1) coursework project.
A distributed, 3-tier application built for the Sunrise Dental Clinic scenario:

```
sunrise-client (JavaFX desktop app)
        │  HTTP / JSON (REST)
        ▼
sunrise-api (Spring Boot REST API)
        │  JDBC (Spring Data JPA)
        ▼
MySQL database
```

`sunrise-common` holds the DTOs and enums both `sunrise-api` and `sunrise-client`
depend on, so the two modules always agree on the JSON shape exchanged over
the network — this is what makes the client and API genuinely independent,
separately-deployable programs rather than one program pretending to be two.

This directly implements Task B(i) — "your program must be a distributed
application with web services" — and follows on from the UML design in
Task A (see the `CIS6003_WRIT1_TaskA.docx` produced earlier in this project).

## Module layout

| Module | Purpose |
|---|---|
| `sunrise-common` | Shared DTOs (`LoginRequest`, `AppointmentResponse`, …) and enums (`Role`, `AppointmentStatus`) |
| `sunrise-api` | Spring Boot REST API: entities, repositories, services, controllers, JWT security, design-pattern classes |
| `sunrise-client` | JavaFX desktop app: FXML screens + controllers, talks to `sunrise-api` only over HTTP |

## Design patterns implemented (Task B(ii))

Each pattern lives in its own clearly-named class with a doc comment
explaining **why** that pattern was the right fit here (not just that it
exists) — see the class Javadoc for each:

| Pattern | Class | Why |
|---|---|---|
| **Singleton** | `api/pattern/AppointmentNumberGenerator.java` | One authority for appointment numbers, so two receptionists working at once can never collide |
| **Singleton** (client-side) | `client/util/Session.java` | One signed-in user per running client instance |
| **Factory** | `api/pattern/UserFactory.java` | Decides Administrator vs Receptionist at runtime from the role an admin picks |
| **Builder** | `api/pattern/BillBuilder.java` | `Bill`'s constructor is package-private; only the builder can produce a valid, fully-calculated bill |
| **Strategy** | `api/pattern/FeeCalculationStrategy.java` + `StandardFeeStrategy.java` | How a bill total is calculated is swappable without touching `BillBuilder`, `Bill`, or any controller |
| **Repository** | `api/repository/*.java` (Spring Data JPA) | Persistence is abstracted behind an interface per aggregate |
| **DTO / Mapper** | `api/mapper/*.java` | Entities never leave the service layer; the REST contract stays stable independent of the entity model |

Double-booking protection is deliberately **defence in depth**: `AppointmentService.register()`
checks for a clash before saving (so the rule holds even if the optional SQL
script below was never applied), and `trg_prevent_double_booking` in
`db-extras.sql` enforces the same rule again at the database level.

Layered (controller → service → repository) architecture is used throughout
`sunrise-api`, matching the `AppointmentService ..> AppointmentRepository`
dependency shown in the Task A class diagram.

## Database (Task B(iii))

- `sunrise-api/src/main/resources/schema.sql` — table DDL, applied
  automatically by Spring Boot on startup.
- `sunrise-api/src/main/resources/data.sql` — seed data (one admin login,
  one receptionist login, two dentists, four treatment types), also applied
  automatically.
- `sunrise-api/src/main/resources/db-extras.sql` — **must be run manually**
  (see below) — contains two advanced features Spring Boot's automatic
  schema runner cannot apply (it splits statements on `;` and doesn't
  understand the MySQL-CLI-only `DELIMITER` directive a multi-statement
  trigger/function body needs):
  - `trg_prevent_double_booking` — a trigger blocking a dentist being
    double-booked for the same date/time, as a second line of defence
    behind `AppointmentService`'s own application-level check.
  - `fn_daily_revenue(date)` — a SQL function that totals the day's billed
    revenue, called directly by `BillRepository.getDailyRevenueViaFunction()`
    to power the Administrator "Daily Report" screen.

  ```bash
  mysql -u sunrise_app -p sunrise_dental < sunrise-api/src/main/resources/db-extras.sql
  ```

  Until this is applied, appointment registration and the trigger still work
  fine (the double-booking check also runs in the service layer), but the
  **Daily Report** screen will show a "could not be calculated" message —
  the **Revenue by Treatment** report does not depend on it and works
  either way, since it's a plain JPQL aggregate query.

## Reports (Task B(ii): "a suitable set of reports... which add more value")

Administrator-only, matching the "View Clinic Reports" use case in the Task A
Use Case diagram (UC13):

- **Daily Report** (`GET /api/admin/reports/daily?date=...`) — how many
  appointments were scheduled/completed/cancelled on a given day, and total
  revenue billed that day (via `fn_daily_revenue`).
- **Revenue by Treatment** (`GET /api/admin/reports/revenue?from=...&to=...`)
  — revenue and appointment count broken down by treatment type over a date
  range, so an administrator can see which treatments are actually driving
  income. Answered with a JPQL `GROUP BY` aggregate query.

Both are reachable from the JavaFX client via **Main Menu → View Clinic
Reports (Admin)**, hidden entirely for a Receptionist login.

## A naming note

The client-side bill screen's controller is named `BillScreenController`
(not `BillController`) specifically so it never gets confused with
`lk.zaa.sunrise.api.controller.BillController`, the REST endpoint it calls —
the two live in different modules and would compile fine either way, but
distinct names make the client/API boundary easier to follow when reading
the code side by side.

## Security

Stateless JWT bearer-token authentication (`api/security/`). `/api/auth/login`
is open; every other endpoint requires a valid token; `/api/admin/**` additionally
requires the `ADMINISTRATOR` role. Passwords are BCrypt-hashed
(`spring-security-crypto`), never stored in plain text.

## Seed logins (from `data.sql`)

| Username | Password | Role |
|---|---|---|
| `admin` | `Admin@123` | Administrator |
| `reception` | `Front@123` | Receptionist |

**Change or remove these before any real deployment.**

## Building and running

> **A note on this environment:** this project was scaffolded inside a
> sandboxed container with no access to Maven Central, so `mvn compile`
> could not be run here to verify it builds. Every file was written
> carefully and cross-checked (brace balance, class/file name matching,
> FXML well-formedness, `fx:id` ↔ `@FXML` field matching — all pass), but
> you should still run a full build on your own machine before treating it
> as final, and fix up anything a real compiler catches that this review
> couldn't.

1. **Create the database** (or let `createDatabaseIfNotExist=true` in
   `application.yml` do it) and a MySQL user matching
   `sunrise-api/src/main/resources/application.yml`
   (`DB_USERNAME`/`DB_PASSWORD` env vars override the defaults).

2. **Build everything** from the project root:
   ```bash
   mvn clean install
   ```

3. **Run the API:**
   ```bash
   cd sunrise-api
   mvn spring-boot:run
   ```
   Then apply `db-extras.sql` as shown above (only needed once, and only
   required for the Daily Report screen — everything else works without it).

4. **Run the client** (in a second terminal):
   ```bash
   cd sunrise-client
   mvn javafx:run
   ```

## What's next (Task B remainder)

- Flesh out validation edge cases and error states further if you want extra
  polish beyond what's already handled (blank fields, bad time format,
  duplicate usernames, not-found appointments/bills all currently show a
  message rather than crashing).
- `sunrise-client/src/main/java/.../ApiClient.java` hard-codes
  `http://localhost:8080/api` — move this to a config file if you'll run the
  client against a non-local API.

## Testing (Task C)

47 automated tests across three levels — unit (Mockito/AssertJ), repository
integration (`@DataJpaTest` + H2), and full-stack integration (`@SpringBootTest`
+ MockMvc + real JWTs). Run them with:

```bash
mvn test
```

See `docs/CIS6003_WRIT1_TaskC.docx` for the full test plan, the test-driven-development
narrative (including the genuine `Clock`-injection refactor visible in the Git
history below), and the traceability matrix linking tests back to the brief's
requirements.

## Version Control and Git Workflow (Task D)

This repository's history is real, not reconstructed after the fact — commit
dates reflect when each piece was actually written across three work
sessions (29 Aug, 30 Aug, and 4 Sep 2026), and two commits are genuine
before/after pairs: the double-booking check going from absent to present in
`fix(api): add application-level double-booking check`, and the `Clock`
refactor in `refactor(api): inject Clock into AppointmentNumberGenerator and
BillBuilder`, which the Task C report describes in the same terms.

**Branching model:** short-lived feature branches off `main`, merged back with
`--no-ff` so the graph keeps each unit of work visible instead of flattening
it into `main`'s straight-line history:

| Branch | Contains |
|---|---|
| `feature/api-core` | sunrise-api: entities, design patterns, repositories, security, services, controllers |
| `feature/javafx-client` | sunrise-client: all six screens from the Task A Use Case diagram |
| `feature/hardening-and-reports` | Application-level double-booking check; administrator reports feature |
| `feature/testing-suite` | The `Clock` refactor and all 47 tests (Task C) |
| `feature/ci-and-docs` | GitHub Actions workflow; this docs folder (Task D) |

**Tags** mark each integrated milestone: `v0.1.0-api-core` → `v0.1.0` (client
complete) → `v0.2.0` (reports + hardening) → `v0.3.0` (testing complete) →
`v1.0.0` (CI + docs). `git log --oneline --graph --all` shows the full graph;
`git tag -l -n1` lists each tag with its message.

**Commit convention:** Conventional-Commits-style prefixes throughout
(`feat`, `fix`, `refactor`, `test`, `docs`, `chore`, `ci`) so the history
itself documents what kind of change each commit was, without needing to
open every diff.

**Workflow (CI/CD):** `.github/workflows/ci.yml` runs `mvn clean test` on
every push and pull request against `main`, uploading the Surefire reports
as a build artifact — this is what actually proves the 47 tests pass, rather
than relying solely on a screenshot taken once locally.

### Pushing this repository to GitHub

This project was built and version-controlled in a sandboxed environment
with no GitHub account or credentials available to it, so the repository
below was prepared locally and needs to be pushed by you:

1. On GitHub, create a **new, empty, public** repository — do **not** tick
   "Add a README", "Add .gitignore", or "Choose a license" (an empty repo
   avoids a merge conflict with the history already prepared here).
2. From this project's root:
   ```bash
   git remote add origin https://github.com/<your-username>/<repo-name>.git
   git push -u origin main
   git push origin --all      # pushes every feature branch too
   git push origin --tags     # pushes v0.1.0-api-core, v0.1.0, v0.2.0, v0.3.0, v1.0.0
   ```
3. **Before pushing**, if you want commits attributed to your own GitHub
   account (so they show your avatar and count toward your contribution
   graph), update the author identity and rewrite history to match:
   ```bash
   git config user.name "Your Name"
   git config user.email "your-github-account-email@example.com"
   git filter-branch -f --env-filter '
     export GIT_AUTHOR_NAME="Your Name"
     export GIT_AUTHOR_EMAIL="your-github-account-email@example.com"
     export GIT_COMMITTER_NAME="Your Name"
     export GIT_COMMITTER_EMAIL="your-github-account-email@example.com"
   ' --tag-name-filter cat -- --all
   ```
   (Skip this step if the current placeholder identity is fine for
   submission — the commit *dates* are unaffected either way.)
4. Once pushed, go to **Settings → General** and confirm the repository
   visibility is **Public** (required by the brief) — take a screenshot of
   this for your documentation.
5. Open the **Actions** tab, confirm the CI workflow ran and passed on the
   push, and screenshot that too — this is the "workflow deployed with the
   Git repository" evidence the brief asks for.
6. Optionally, open a real Pull Request for one of the feature branches
   (e.g. `feature/ci-and-docs` → `main`) to additionally demonstrate GitHub's
   PR review workflow, then merge it there.

## Documentation

**`docs/CIS6003_WRIT1_Combined.docx` is the complete, single-file submission
document** — Parts A, B, C and D together with one title page, one table of
contents, and continuous page numbering, matching the brief's requirement to
submit one PDF. Convert it to PDF and rename it to your student ID before
uploading to Turnitin (e.g. `st12345678_CIS6003_WRIT1.pdf`), per the
assessment brief's file-naming convention.

The four individual reports remain in `docs/` as well, for reference:

- `docs/CIS6003_WRIT1_TaskA.docx` — Task A: system design and UML diagrams.
- `docs/CIS6003_WRIT1_TaskC.docx` — Task C: test plan, TDD rationale, and
  traceability matrix.
- `docs/CIS6003_WRIT1_TaskD.docx` — Task D: version control documentation.

