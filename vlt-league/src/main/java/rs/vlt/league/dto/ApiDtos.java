package rs.vlt.league.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public final class ApiDtos {
    private ApiDtos() {}
    public record PageResponse<T>(List<T> items, long total, int page, int size) {}
    public record Dashboard(long runners, long races, long seasons, RoundSummary lastRound, RoundSummary nextRound) {}
    public record SeasonSummary(Integer id, String name) {}
    public record RoundSummary(Integer id, Integer number, LocalDate date, String location, String seasonName) {}
    public record RunnerSummary(Integer id, Integer startNumber, String name, String nickname, String gender, String organisation) {}
    public record RunnerDetail(Integer id, Integer startNumber, String name, String nickname, String gender, LocalDate birthday, String isKid, String email, String address, String organisation, Integer firstRaceId) {}
    public record RaceSummary(Integer id, Integer roundNumber, LocalDate date, String seasonName, Integer length, String gender, String description) {}
    public record RaceResult(Integer participationId, Integer runnerId, String runnerName, Integer startNumber, LocalTime time, LocalTime avgPace, Integer points) {}
    public record Standing(int rank, Integer runnerId, String runnerName, String gender, Integer length, long totalPoints, long appearances, LocalTime bestTime, LocalTime bestPace) {}
}