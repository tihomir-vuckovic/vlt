package rs.vlt.league.repository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import rs.vlt.league.entity.Participation;
@ApplicationScoped public class ParticipationRepository implements PanacheRepositoryBase<Participation, Integer> {}