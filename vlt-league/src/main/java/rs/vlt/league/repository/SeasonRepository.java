package rs.vlt.league.repository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import rs.vlt.league.entity.Season;
@ApplicationScoped public class SeasonRepository implements PanacheRepositoryBase<Season, Integer> {}