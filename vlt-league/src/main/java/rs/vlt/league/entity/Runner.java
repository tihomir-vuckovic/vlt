package rs.vlt.league.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "runner")
public class Runner {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "runner_id") public Integer id;
    @Column(name = "start_number", unique = true) public Integer startNumber;
    @Column(nullable = false, unique = true, length = 100) public String name;
    @Column(length = 45) public String nickname;
    @Column(nullable = false, length = 1) public String gender;
    public LocalDate birthday;
    @Column(name = "is_kid", length = 1) public String isKid;
    @Column(name = "home_address", length = 100) public String homeAddress;
    @Column(length = 100) public String email;
    @ManyToOne @JoinColumn(name = "organisation_id") public Organisation organisation;
    @ManyToOne @JoinColumn(name = "first_race_id") public Race firstRace;
}