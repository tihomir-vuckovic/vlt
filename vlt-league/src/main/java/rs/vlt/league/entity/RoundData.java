package rs.vlt.league.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "round_data", uniqueConstraints = @UniqueConstraint(name = "uk_season_round", columnNames = {"season_id", "round_number"}))
public class RoundData {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "round_id") public Integer id;
    @ManyToOne(optional = false) @JoinColumn(name = "season_id") public Season season;
    @Column(name = "round_number", nullable = false) public Integer roundNumber;
    @Column(name = "start_date") public LocalDate startDate;
    @Column(length = 100) public String location;
    @Column(length = 300) public String description;
}