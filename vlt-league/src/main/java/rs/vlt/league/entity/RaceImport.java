package rs.vlt.league.entity;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "race_import")
public class RaceImport {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) public Integer id;
    @Column(name = "runner_name", length = 200) public String runnerName;
    public Integer length;
    @Column(name = "time_var", length = 20) public String timeVar;
    public Integer points;
    @Column(name = "avg_pace_var", length = 20) public String avgPaceVar;
    public LocalTime time;
    @Column(name = "avg_pace") public LocalTime avgPace;
    @Column(name = "runner_id") public Integer runnerId;
    @Column(length = 1) public String gender;
}