package riccardomamoli.cib_db.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import riccardomamoli.cib_db.entities.Prenotazione;

import java.time.LocalDate;
import java.util.List;

public interface PrenotazioneRepository extends JpaRepository<Prenotazione, Long> , JpaSpecificationExecutor<Prenotazione>{
    List<Prenotazione> findByOggettoIdAndDataPrenotazioneBetween(Long oggettoId, LocalDate dataPrenotazione, LocalDate dataFinePrenotazione);

    @Query("SELECT p FROM Prenotazione p WHERE p.oggetto.utente.id = :proprietarioId")
    Page<Prenotazione> findByOggettoProprietarioId(@Param("proprietarioId") Long proprietarioId, Pageable pageable);

    List<Prenotazione> findByOggettoId(Long oggettoId);


}
