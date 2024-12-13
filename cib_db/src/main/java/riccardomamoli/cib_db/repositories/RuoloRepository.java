package riccardomamoli.cib_db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import riccardomamoli.cib_db.entities.Ruolo;
import riccardomamoli.cib_db.entities.Utente;

import java.util.List;
import java.util.Optional;

@Repository
public interface RuoloRepository extends JpaRepository<Ruolo, Long> {
    Optional<Ruolo> findByTipo(String tipo);
    List<Utente> findUtentiByTipo(String tipo);
}
