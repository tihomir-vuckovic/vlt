package rs.vlt.league.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "race")
public class Race {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "race_id") public Integer id;
    @ManyToOne(optional = false) @JoinColumn(name = "round_id") public RoundData round;
    @Column(nullable = false) public Integer length;
    @Column(length = 1) public String gender;
    @Column(length = 300) public String description;
}