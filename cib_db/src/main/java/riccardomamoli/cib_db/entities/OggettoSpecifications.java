package riccardomamoli.cib_db.entities;
import org.springframework.data.jpa.domain.Specification;

public class OggettoSpecifications {

    public static Specification<Oggetto> hasUtenteId(Long utenteId) {
        return (root, query, cb) -> cb.equal(root.get("utente").get("id"), utenteId);
    }

    public static Specification<Oggetto> hasNomeOggetto(String nomeOggetto) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("nomeOggetto")), "%" + nomeOggetto.toLowerCase() + "%");
    }

    public static Specification<Oggetto> hasDisponibilita(boolean disponibilita) {
        return (root, query, cb) -> cb.equal(root.get("disponibilita"), disponibilita);
    }

    public static Specification<Oggetto> hasPrezzoOggetto(double prezzoOggetto) {
        return (root, query, cb) -> cb.equal(root.get("prezzoGiornaliero"), prezzoOggetto);
    }
}

