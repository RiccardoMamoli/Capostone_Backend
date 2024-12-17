package riccardomamoli.cib_db.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ruoli")

public class Ruolo {
    @Id
    @GeneratedValue
    private Long id;
    private String tipo;

    @OneToMany(mappedBy = "ruolo")
    @JsonIgnore
    private List<UtenteRuolo> utenteRuolo = new ArrayList<>();

    public Ruolo(){};

    public Ruolo(String tipo) {
        this.tipo = tipo;
    }

    public Long getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public List<UtenteRuolo> getUtenteRuolo() {
        return utenteRuolo;
    }

    public void setUtenteRuolo(List<UtenteRuolo> utenteRuolo) {
        this.utenteRuolo = utenteRuolo;
    }
}
