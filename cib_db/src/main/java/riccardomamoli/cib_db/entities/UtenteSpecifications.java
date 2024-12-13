package riccardomamoli.cib_db.entities;

import org.springframework.data.jpa.domain.Specification;


public class UtenteSpecifications {

    public static Specification<Utente> hasUsername(String username) {
        return (root, query, builder) -> username == null ? null : builder.equal(root.get("usernameUtente"), username);
    }

    public static Specification<Utente> hasIndirizzo(String indirizzo) {
        return (root, query, builder) -> indirizzo == null ? null : builder.like(root.get("indirizzoUtente"), "%" + indirizzo + "%");
    }

    public static Specification<Utente> hasCitta(String citta) {
        return (root, query, builder) -> citta == null ? null : builder.equal(root.get("cittaUtente"), citta);
    }

    public static Specification<Utente> hasStato(String stato) {
        return (root, query, builder) -> stato == null ? null : builder.equal(root.get("statoUtente"), stato);
    }

    public static Specification<Utente> hasCodicePostale(String codicePostale) {
        return (root, query, builder) -> codicePostale == null ? null : builder.equal(root.get("codicePostaleUtente"), codicePostale);
    }

}

