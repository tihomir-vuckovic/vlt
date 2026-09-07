# Domain, REST API, and architecture

## Domain model

### Aggregates

| Aggregate | Root | Includes | Responsibility |
|---|---|---|---|
| Season calendar | `Season` | `RoundData`, `Race` | Organize rounds and the gender/distance categories in each round |
| Competitor | `Runner` | Optional `Organisation` reference and first-race reference | Maintain competitor profile and club affiliation |
| Result | `Participation` | References one `Runner` and one `Race` | Record official time, pace, and awarded points |
| Import batch | `RaceImport` | Staging rows | Preserve imported raw and normalized values |

`LeagueStanding` is a read model, not an entity. It is calculated from `Season`, `RoundData`, `Race`, `Participation`, and `Runner`.

### Primary use cases

1. Browse dashboard counts, last completed round, and next scheduled round.
2. Search, paginate, sort, and inspect runners, including club and result history.
3. Browse seasons, rounds, and category races; inspect race results.
4. Display a season league table filtered by distance and gender.
5. Register/update a runner and record a result, with uniqueness and reference validation.
6. Import result staging rows later, retaining the dump's import table as an explicit admin workflow.

### Points and ranking

The schema has no points configuration or scoring-rule table. `Participation.points` is authoritative and must be entered or imported. The default sample values show 10/8/6/4/2 but this is not elevated to a global business rule.

For a requested season, gender, and distance, the API ranking query will calculate:

- `totalPoints = SUM(points)`
- `appearanceCount = COUNT(time)`
- `bestTime = MIN(time)`
- `bestPace = MIN(avg_pace)`
- average time/pace from the same values
- sequential rank ordered by `totalPoints DESC`, then `bestTime ASC`, then runner ID for deterministic results

This corrects the historical view's missing season partition while preserving its visible scoring semantics.

### DTO candidates

`DashboardDto`, `RunnerSummaryDto`, `RunnerDetailDto`, `OrganisationDto`, `SeasonDto`, `RoundDto`, `RaceDto`, `RaceResultDto`, `ParticipationCreateDto`, `LeagueStandingDto`, `PageResponseDto`.

## REST API proposal

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/dashboard` | Counts, last round, and upcoming round |
| GET | `/api/runners?page=&size=&q=&sort=` | Searchable/sortable runner list |
| GET | `/api/runners/{id}` | Runner profile and result history |
| POST | `/api/runners` | Create runner |
| PUT | `/api/runners/{id}` | Update runner |
| GET | `/api/seasons` | List seasons |
| GET | `/api/seasons/{id}/rounds` | Rounds for one season |
| GET | `/api/races?page=&size=&seasonId=` | Paginated race categories |
| GET | `/api/races/{id}` | Race category details |
| GET | `/api/races/{id}/results` | Ordered results for a race |
| POST | `/api/races/{id}/results` | Record one participation |
| GET | `/api/standings?seasonId=&length=&gender=` | Season-scoped league table |

The API will use bean validation, `404` for missing references, `409` for unique-result/runner conflicts, and RFC 7807-style JSON error responses.

## Quarkus application architecture

Use a single deployable modular monolith on Java 21, Quarkus 3.x, Gradle, MySQL 8, Hibernate ORM with Panache, Flyway, JAX-RS, Jackson, Hibernate Validator, SmallRye OpenAPI, and static frontend assets served by Quarkus.

```text
HTTP / static frontend
        |
    resource (REST boundary, request validation)
        |
    service (use cases, transactions, domain validation)
        |
repository (Panache persistence and projection queries)
        |
entity (direct mapping of dump tables)
        |
      MySQL / Flyway baseline
```

Proposed project structure:

```text
vlt-league/
  build.gradle
  settings.gradle
  Dockerfile
  compose.yaml
  src/main/java/rs/vlt/league/
    config/
    entity/
    repository/
    service/
    resource/
    dto/
    mapper/
    exception/
  src/main/resources/
    application.properties
    db/migration/
    META-INF/resources/
      index.html
      css/app.css
      js/app.js
  docs/
```

Flyway will baseline the existing dump as `V1__baseline_vlt_schema.sql`; this preserves the schema, seed data, views, and procedures exactly as supplied. The baseline needs a MySQL 8 server because the dump uses window functions and MySQL-version-specific dump directives.