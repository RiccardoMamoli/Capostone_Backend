package riccardomamoli.cib_db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import riccardomamoli.cib_db.entities.Oggetto;


public interface OggettoRepository extends JpaRepository<Oggetto, Long>, JpaSpecificationExecutor<Oggetto> {
    boolean existsByNomeOggetto(String nomeOggetto);

}
