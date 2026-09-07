package rs.vlt.league.service;

import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.NotFoundException;
import rs.vlt.league.dto.ApiDtos.*;
import rs.vlt.league.entity.*;
import rs.vlt.league.repository.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@ApplicationScoped
public class LeagueService {
    @Inject RunnerRepository runners;
    @Inject RaceRepository races;
    @Inject SeasonRepository seasons;
    @Inject RoundRepository rounds;
    @Inject ParticipationRepository participations;
    @Inject EntityManager entityManager;

    public Dashboard dashboard() {
        RoundData last = rounds.find("startDate <= ?1 order by startDate desc", LocalDate.now()).firstResult();
        RoundData next = rounds.find("startDate > ?1 order by startDate", LocalDate.now()).firstResult();
        return new Dashboard(runners.count(), races.count(), seasons.count(), toRound(last), toRound(next));
    }

    public PageResponse<RunnerSummary> runnerPage(int page, int size, String query, String sort) {
        String order = switch (sort == null ? "name" : sort) { case "startNumber" -> "startNumber"; case "gender" -> "gender, name"; default -> "name"; };
        var result = (query == null || query.isBlank()) ? runners.find("order by " + order) : runners.find("lower(name) like ?1 order by " + order, "%" + query.toLowerCase() + "%");
        long total = result.count();
        List<RunnerSummary> items = result.page(Page.of(page, size)).list().stream().map(this::toRunner).toList();
        return new PageResponse<>(items, total, page, size);
    }

    public RunnerDetail runner(Integer id) {
        Runner runner = runners.findByIdOptional(id).orElseThrow(() -> new NotFoundException("Runner not found: " + id));
        return new RunnerDetail(runner.id, runner.startNumber, runner.name, runner.nickname, runner.gender, runner.birthday, runner.isKid, runner.email, runner.homeAddress, runner.organisation == null ? null : runner.organisation.name, runner.firstRace == null ? null : runner.firstRace.id);
    }

    public List<Season> seasonList() { return seasons.list("startDate"); }

    public PageResponse<RaceSummary> racePage(int page, int size, Integer seasonId) {
        var result = seasonId == null ? races.find("order by round.startDate desc, length, gender") : races.find("round.season.id = ?1 order by round.startDate desc, length, gender", seasonId);
        long total = result.count();
        return new PageResponse<>(result.page(Page.of(page, size)).list().stream().map(this::toRace).toList(), total, page, size);
    }

    public RaceSummary race(Integer id) { return toRace(races.findByIdOptional(id).orElseThrow(() -> new NotFoundException("Race not found: " + id))); }

    public List<RaceResult> results(Integer raceId) {
        if (!races.isPersistent(races.findById(raceId))) throw new NotFoundException("Race not found: " + raceId);
        return participations.find("race.id = ?1 order by time, runner.name", raceId).list().stream().map(p -> new RaceResult(p.id, p.runner.id, p.runner.name, p.runner.startNumber, p.time, p.avgPace, p.points)).toList();
    }

    public List<Standing> standings(Integer seasonId, Integer length, String gender) {
        if (!seasons.isPersistent(seasons.findById(seasonId))) throw new NotFoundException("Season not found: " + seasonId);
        List<Object[]> rows = entityManager.createQuery("select p.runner.id, p.runner.name, p.runner.gender, p.race.length, sum(coalesce(p.points, 0)), count(p.time), min(p.time), min(p.avgPace) from Participation p where p.race.round.season.id = :season and (:length is null or p.race.length = :length) and (:gender is null or p.runner.gender = :gender) group by p.runner.id, p.runner.name, p.runner.gender, p.race.length having sum(coalesce(p.points, 0)) > 0 order by sum(coalesce(p.points, 0)) desc, min(p.time), p.runner.id", Object[].class).setParameter("season", seasonId).setParameter("length", length).setParameter("gender", gender).getResultList();
        List<Standing> standings = new ArrayList<>();
        int rank = 1;
        for (Object[] row : rows) standings.add(new Standing(rank++, (Integer) row[0], (String) row[1], (String) row[2], (Integer) row[3], ((Number) row[4]).longValue(), ((Number) row[5]).longValue(), (LocalTime) row[6], (LocalTime) row[7]));
        return standings;
    }

    private RoundSummary toRound(RoundData round) { return round == null ? null : new RoundSummary(round.id, round.roundNumber, round.startDate, round.location, round.season.name); }
    private RunnerSummary toRunner(Runner runner) { return new RunnerSummary(runner.id, runner.startNumber, runner.name, runner.nickname, runner.gender, runner.organisation == null ? null : runner.organisation.name); }
    private RaceSummary toRace(Race race) { return new RaceSummary(race.id, race.round.roundNumber, race.round.startDate, race.round.season.name, race.length, race.gender, race.description); }
}