package riccardomamoli.cib_db.entities;

import jakarta.persistence.*;
import riccardomamoli.cib_db.enums.StatoPrenotazione;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "prenotazioni")
public class Prenotazione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.REMOVE)
    private Recensione recensione;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "utente_id")
    private Utente utente;

    @ManyToOne
    @JoinColumn(name = "oggetto_id")
    private Oggetto oggetto;

    private LocalDate dataPrenotazione;
    private LocalDate dataFinePrenotazione;
    private Integer durataPrenotazione;

    @Enumerated(EnumType.STRING)
    private StatoPrenotazione stato;

    private Double costoPrenotazione;

    public Prenotazione() {}

    public Prenotazione(Utente utente, Oggetto oggetto, LocalDate dataPrenotazione, LocalDate dataFinePrenotazione, double costoPrenotazione) {
        this.utente = utente;
        this.oggetto = oggetto;
        this.dataPrenotazione = dataPrenotazione;
        this.dataFinePrenotazione = dataFinePrenotazione;
        this.durataPrenotazione = calculateDurataPrenotazione();
        this.stato = StatoPrenotazione.IN_ATTESA;
        this.costoPrenotazione = costoPrenotazione;
    }

    public Double getCostoPrenotazione() {
        return costoPrenotazione;
    }

    public void setCostoPrenotazione(Double costoPrenotazione) {
        this.costoPrenotazione = costoPrenotazione;
    }

    public Long getId() {
        return id;
    }

    public Recensione getRecensione() {
        return recensione;
    }

    public void setRecensione(Recensione recensione) {
        this.recensione = recensione;
    }

    public Utente getUtente() {
        return utente;
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }

    public Oggetto getOggetto() {
        return oggetto;
    }

    public void setOggetto(Oggetto oggetto) {
        this.oggetto = oggetto;
    }

    public LocalDate getDataPrenotazione() {
        return dataPrenotazione;
    }

    public void setDataPrenotazione(LocalDate dataPrenotazione) {
        this.dataPrenotazione = dataPrenotazione;
        this.durataPrenotazione = calculateDurataPrenotazione();
    }

    public LocalDate getDataFinePrenotazione() {
        return dataFinePrenotazione;
    }

    public void setDataFinePrenotazione(LocalDate dataFinePrenotazione) {
        this.dataFinePrenotazione = dataFinePrenotazione;
        this.durataPrenotazione = calculateDurataPrenotazione();
    }

    public Integer getDurataPrenotazione() {
        return durataPrenotazione;
    }

    public StatoPrenotazione getStato() {
        return stato;
    }

    public void setStato(StatoPrenotazione stato) {
        this.stato = stato;
    }

    private Integer calculateDurataPrenotazione() {
        if (dataPrenotazione != null && dataFinePrenotazione != null) {
            return (int) ChronoUnit.DAYS.between(dataPrenotazione, dataFinePrenotazione);
        }
        return 0;
    }
}
