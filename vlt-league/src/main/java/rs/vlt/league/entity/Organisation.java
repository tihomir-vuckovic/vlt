package rs.vlt.league.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "organisation")
public class Organisation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "organisation_id") public Integer id;
    @Column(length = 200) public String name;
    @Column(length = 300) public String address;
}