package rs.vlt.league.repository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import rs.vlt.league.entity.Runner;
@ApplicationScoped public class RunnerRepository implements PanacheRepositoryBase<Runner, Integer> {}