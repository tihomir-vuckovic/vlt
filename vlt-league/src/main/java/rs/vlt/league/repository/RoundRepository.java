package rs.vlt.league.repository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import rs.vlt.league.entity.RoundData;
@ApplicationScoped public class RoundRepository implements PanacheRepositoryBase<RoundData, Integer> {}