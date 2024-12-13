package riccardomamoli.cib_db.entities;


import jakarta.persistence.*;

@Entity
@Table(name = "recensioni")

public class Recensione {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Prenotazione prenotazione;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "utente_id")
    private Utente utente;

    private String testoRecensione;
    private Integer valutazioneRecensione;

    public Recensione(){};

    public Recensione(Prenotazione prenotazione, Utente utente, String testoRecensione, Integer valutazioneRecensione) {
        this.prenotazione = prenotazione;
        this.utente = utente;
        this.testoRecensione = testoRecensione;
        this.valutazioneRecensione = valutazioneRecensione;
    }

    public Long getId() {
        return id;
    }


    public Prenotazione getPrenotazione() {
        return prenotazione;
    }

    public void setPrenotazione(Prenotazione prenotazione) {
        this.prenotazione = prenotazione;
    }

    public Utente getUtente() {
        return utente;
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }

    public String getTestoRecensione() {
        return testoRecensione;
    }

    public void setTestoRecensione(String testoRecensione) {
        this.testoRecensione = testoRecensione;
    }

    public Integer getValutazioneRecensione() {
        return valutazioneRecensione;
    }

    public void setValutazioneRecensione(Integer valutazioneRecensione) {
        this.valutazioneRecensione = valutazioneRecensione;
    }
}
