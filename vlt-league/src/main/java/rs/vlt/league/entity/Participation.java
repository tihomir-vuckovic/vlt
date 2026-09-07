package rs.vlt.league.entity;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "participation", uniqueConstraints = @UniqueConstraint(name = "uk_race_runner", columnNames = {"race_id", "runner_id"}))
public class Participation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participation_id") public Integer id;
    @ManyToOne(optional = false) @JoinColumn(name = "race_id") public Race race;
    @ManyToOne(optional = false) @JoinColumn(name = "runner_id") public Runner runner;
    public LocalTime time;
    public Integer points;
    @Column(name = "avg_pace") public LocalTime avgPace;
}