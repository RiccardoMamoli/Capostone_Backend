package riccardomamoli.cib_db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import riccardomamoli.cib_db.entities.Recensione;

public interface RecensioneRepository extends JpaRepository<Recensione, Long>, JpaSpecificationExecutor<Recensione> {
}
