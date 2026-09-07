# VLT database analysis

Source of truth: `DumpVLT_2_season_create_schema_create_dump_single_tran_all_obj.sql` (MySQL 8.0.34).

## Physical tables

| Table | Purpose | Primary key | Foreign keys |
|---|---|---|---|
| `organisation` | Club or organization of a runner | `organisation_id` | None |
| `season` | League season | `season_id` | None |
| `round_data` | One scheduled round within a season | `round_id` | `season_id -> season.season_id` |
| `race` | Gender and distance category held in a round | `race_id` | `round_id -> round_data.round_id` |
| `runner` | League competitor | `runner_id` | `organisation_id -> organisation.organisation_id`; `first_race_id -> race.race_id` |
| `participation` | A runner's result in a specific race | `participation_id` | `race_id -> race.race_id`; `runner_id -> runner.runner_id` |
| `race_import` | Temporary import/staging rows | `id` | None (the nullable `runner_id` is not constrained) |

`race_import` is operational staging data, not a core domain aggregate. It is retained because it is physically present in the dump.

## Constraints and indexes

| Table | Constraint or index | Definition |
|---|---|---|
| `organisation` | PK | `organisation_id` auto-increment |
| `season` | PK | `season_id` auto-increment |
| `round_data` | PK | `round_id` auto-increment |
| `round_data` | Unique | `uk_season_round (season_id, round_number)` |
| `round_data` | FK index | `fk_season_idx (season_id)` |
| `race` | PK | `race_id` auto-increment |
| `race` | FK index | `fk_round_idx (round_id)` |
| `race` | Check | `gender IN ('M', 'F')`; the column itself is nullable |
| `runner` | PK | `runner_id` auto-increment |
| `runner` | Unique | `name_UNIQUE (name)` and `start_number_UNIQUE (start_number)` |
| `runner` | FK indexes | `fk_organisation_idx (organisation_id)`, `fk_race_idf (first_race_id)` |
| `runner` | Checks | `gender IN ('M', 'F')` and `is_kid IN ('Y', 'N')` |
| `participation` | PK | `participation_id` auto-increment |
| `participation` | Unique | `uk_race_runner (race_id, runner_id)`; a runner can have one result per race |
| `participation` | FK indexes | `fk_race_idx (race_id)`, `fk_runner_idx (runner_id)` |
| `race_import` | PK | `id` auto-increment |

No additional indexes, unique constraints, or foreign keys exist in the dump.

## Hierarchy and relationships

```text
Season 1 --- * RoundData 1 --- * Race 1 --- * Participation * --- 1 Runner
                                                     |
Runner * --- 0..1 Organisation                         +-- race category: gender + length
Runner 0..1 --- 1 first Race
RaceImport: independent staging table used to create Runner and Participation records
```

The reporting hierarchy is `season -> round -> race -> participation`. A race is a category, not the calendar event itself: each round has one or more races for a combination of `gender` and `length`.

## Domain classification

| Concern | Authoritative objects |
|---|---|
| Competitors | `runner` |
| Clubs | `organisation` |
| Seasons | `season` |
| Scheduled league rounds | `round_data` |
| Race categories | `race` with `length` in km and `gender` (`M` or `F`) |
| Results | `participation` with elapsed `time`, `avg_pace`, and awarded `points` |
| Points | Stored per result in `participation.points`; no points-rule table exists |
| Rankings | Derived by `tabela_osvojenih_poena`; no physical rankings table exists |
| Import workflow | `race_import` plus the four stored procedures |

The dump contains no separate age-category, ranking, score-rule, calendar-event, or club-membership-history tables. Therefore none will be modeled as persistent entities in version 1.

## Views and procedures

| Object | Observed purpose |
|---|---|
| `all_race_info_2km` | Season 2023/2024 analytics for 2 km: previous result and faster/slower/same comparison |
| `all_race_info_4km` | Season 2023/2024 analytics: participation count, comparison, personal record marker, averages |
| `pivot_time_boys` | Wide, fixed 21-round time report for male runners in season 2023/2024 |
| `tabela_osvojenih_poena` | League table: points sum, appearances, best/average time and pace, ranked by points |
| `w_best_time_total` | Best time holder(s) per season, gender, and distance |
| `UpdateImportTabele` | Normalizes staging rows and resolves runner by exact name |
| `InsertNewRunnerFromImport` | Creates runners for staging rows without a resolved runner ID |
| `InsertParticipationFromImport` | Creates results for a supplied round only when that round has no existing participation |
| `InsertRoundAndDefaultRaces` | Creates a round and the default 2 km/4 km male/female races, optionally 8 km races |

## Verified business rules

1. A season has unique sequential round numbers.
2. A runner may record only one participation for the same race.
3. A race belongs to one round and has a required distance; its gender is restricted to `M` or `F` when provided.
4. A runner has a required name and gender; name and optional start number are globally unique.
5. Race result points are stored rather than derived by a persisted rule. Existing data demonstrates awards of 10, 8, 6, 4, and 2 for leading finishers in at least one race, but the dump does not establish that as a universal algorithm.
6. The league table includes only entries whose total points are greater than zero. It ranks by descending total points within gender and distance; output sorting adds best time as a display tie-breaker, but the `ROW_NUMBER()` rank itself has no deterministic tie-breaker.
7. Appearances are `COUNT(participation.time)`, so a participation with null time is not counted by the ranking view.

## Data snapshot

At dump time there are 2 seasons, 104 rounds (52 per season), 462 races, 414 runners, and 2,214 participation rows. Race categories are 2 km female/male (104 each), 4 km female/male (104 each), and 8 km female/male (23 each).

## Schema and data issues to resolve deliberately

1. `InsertRoundAndDefaultRaces` gets `MAX(round_number)` without filtering by `P_SeasonId`. This causes the first new round in a new season to continue the number of a prior season, instead of starting from that season's maximum.
2. `InsertParticipationFromImport` blocks the whole import when any result exists for the requested round. It cannot safely import a missing result or retry a partial batch.
3. `InsertNewRunnerFromImport` inserts all unresolved staging names directly into `runner`, but does not use `DISTINCT` or an existence check. Repeated names can violate `runner.name` uniqueness.
4. `race_import.runner_id` resembles a reference but lacks a foreign key; it can become invalid.
5. `race` has no unique key on `(round_id, length, gender)`, so duplicate race categories in the same round are possible.
6. `runner.name` is used as identity in both a unique key and views/procedures. This forbids same-name competitors and makes a later name correction a cross-reporting concern.
7. `tabela_osvojenih_poena` partitions rank by gender and distance but not season. It groups per season, yet its `ROW_NUMBER()` can rank rows from separate seasons together. The application must implement season-scoped ranking explicitly.
8. The views hard-code the season name `Sezona 2023/2024` in three reports; they are historical reports, not reusable API sources.
9. `season.end_date`, `round_data.start_date`, several runner attributes, and `participation.time/points/avg_pace` are nullable. The application must validate operationally required fields at API boundaries without claiming stronger historical database constraints.

The version-1 application will not mutate this schema to avoid diverging from the provided source of truth. It will address items 1-3 and 7 in Java service/query behavior when those workflows are exposed.