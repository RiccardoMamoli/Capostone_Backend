package riccardomamoli.cib_db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import riccardomamoli.cib_db.entities.Utente;

import java.util.Optional;

@Repository
public interface UtenteRepository extends JpaRepository<Utente, Long>, JpaSpecificationExecutor<Utente> {
    Optional<Utente> findByEmailUtente(String emailUtente);
    Optional<Utente> findByUsernameUtente(String usernameUtente);
    boolean existsByEmailUtente(String emailUtente);
    boolean existsByUsernameUtente (String usernameUtente);
}
