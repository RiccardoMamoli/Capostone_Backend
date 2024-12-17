package riccardomamoli.cib_db.entities;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class PrenotazioneSpecifications {

    public static Specification<Prenotazione> hasUtenteId(Long utenteId) {
        return (root, query, cb) -> cb.equal(root.get("utente").get("id"), utenteId);
    }

    public static Specification<Prenotazione> hasOggettoId(Long oggettoId) {
        return (root, query, cb) -> cb.equal(root.get("oggetto").get("id"), oggettoId);
    }

    public static Specification<Prenotazione> hasNomeOggetto(String nomeOggetto) {
        return (root, query, cb) -> cb.equal(root.get("oggetto").get("nome"), nomeOggetto);
    }

    public static Specification<Prenotazione> hasDataPrenotazione(LocalDate dataPrenotazione) {
        return (root, query, cb) -> cb.equal(root.get("dataPrenotazione"), dataPrenotazione);
    }

    public static Specification<Prenotazione> hasDataFinePrenotazione(LocalDate dataFinePrenotazione) {
        return (root, query, cb) -> cb.equal(root.get("dataFinePrenotazione"), dataFinePrenotazione);
    }

    public static Specification<Prenotazione> isPrenotazioneInRange(LocalDate dataInizio, LocalDate dataFine) {
        return (root, query, cb) -> {
            Predicate dataInizioPredicate = cb.lessThanOrEqualTo(root.get("dataPrenotazione"), dataFine);
            Predicate dataFinePredicate = cb.greaterThanOrEqualTo(root.get("dataFinePrenotazione"), dataInizio);
            return cb.and(dataInizioPredicate, dataFinePredicate);
        };
    }

    public static Specification<Prenotazione> hasDataFineBefore(LocalDate data) {
        return (root, query, cb) -> cb.lessThan(root.get("dataFinePrenotazione"), data);
    }

    public static Specification<Prenotazione> hasDataPrenotazioneAfter(LocalDate data) {
        return (root, query, cb) -> cb.greaterThan(root.get("dataPrenotazione"), data);
    }
}


