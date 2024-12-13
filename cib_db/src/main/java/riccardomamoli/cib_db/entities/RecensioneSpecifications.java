package riccardomamoli.cib_db.entities;

import org.springframework.data.jpa.domain.Specification;

public class RecensioneSpecifications {

    public static Specification<Recensione> hasUtenteId(Long utenteId) {
        return (root, query, cb) -> cb.equal(root.get("utente").get("id"), utenteId);
    }

    public static Specification<Recensione> hasPrenotazioneId(Long prenotazioneId) {
        return (root, query, cb) -> cb.equal(root.get("prenotazione").get("id"), prenotazioneId);
    }

    public static Specification<Recensione> hasValutazioneRecensione(Integer valutazioneRecensione) {
        return (root, query, cb) -> cb.equal(root.get("valutazioneRecensione"), valutazioneRecensione);
    }

    public static Specification<Recensione> hasTestoRecensione(String testoRecensione) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("testoRecensione")), "%" + testoRecensione.toLowerCase() + "%");
    }
}
