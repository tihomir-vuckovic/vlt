package rs.vlt.league.repository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import rs.vlt.league.entity.Race;
@ApplicationScoped public class RaceRepository implements PanacheRepositoryBase<Race, Integer> {}