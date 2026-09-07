package rs.vlt.league.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "season")
public class Season {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "season_id") public Integer id;
    @Column(nullable = false, length = 100) public String name;
    @Column(name = "start_date") public LocalDate startDate;
    @Column(name = "end_date") public LocalDate endDate;
    @Column(length = 300) public String description;
}