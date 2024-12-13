package riccardomamoli.cib_db.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "utente_ruolo")
public class UtenteRuolo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "ruolo_id")
    private Ruolo ruolo;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "utente_id")
    @JsonBackReference
    @JsonIgnore
    private Utente utente;

    public UtenteRuolo(){};

    public UtenteRuolo(Utente utente, Ruolo ruolo) {
        this.utente = utente;
        this.ruolo = ruolo;
    }

    public Long getId() {
        return id;
    }

    public Ruolo getRuolo() {
        return ruolo;
    }

    public void setRuolo(Ruolo ruolo) {
        this.ruolo = ruolo;
    }

    public Utente getUtente() {
        return utente;
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }
}
