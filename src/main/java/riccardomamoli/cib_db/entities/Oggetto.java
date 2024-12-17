package riccardomamoli.cib_db.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "oggetti")
public class Oggetto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "utente_id")
    private Utente utente;

    private String nomeOggetto;
    private String descrizioneOggetto;
    private Boolean disponibilita;
    private double prezzoGiornaliero;


    @ElementCollection
    @CollectionTable(name = "oggetto_foto", joinColumns = @JoinColumn(name = "oggetto_id"))
    @Column(name = "foto_url")
    private List<String> fotoUrls = new ArrayList<>();


    @OneToMany(mappedBy = "oggetto", cascade = CascadeType.PERSIST)
    @JsonManagedReference
    private List<CategoriaOggetto> oggettoCategorie = new ArrayList<>();

    public Oggetto(){};

    public Oggetto(Utente utente, String nomeOggetto, String descrizioneOggetto, Boolean disponibilita, double prezzoGiornaliero, List<Categoria> categorie) {
        this.utente = utente;
        this.nomeOggetto = nomeOggetto;
        this.descrizioneOggetto = descrizioneOggetto;
        this.disponibilita = disponibilita;
        this.prezzoGiornaliero = prezzoGiornaliero;
        for (Categoria categoria : categorie) {
            CategoriaOggetto categoriaOggetto = new CategoriaOggetto(categoria, this);
            this.oggettoCategorie.add(categoriaOggetto);
        }
    }

    public List<String> getFotoUrls() {
        return fotoUrls;
    }

    public void setFotoUrls(List<String> fotoUrls) {
        this.fotoUrls = fotoUrls;
    }


    public void addFotoUrl(String fotoUrl) {
        if (this.fotoUrls.size() < 3) {
            this.fotoUrls.add(fotoUrl);
        } else {
            throw new IllegalStateException("Puoi aggiungere massimo 3 foto per oggetto");
        }
    }


    public Long getId() {
        return id;
    }

    public Utente getUtente() {
        return utente;
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }

    public String getNomeOggetto() {
        return nomeOggetto;
    }

    public void setNomeOggetto(String nomeOggetto) {
        this.nomeOggetto = nomeOggetto;
    }

    public String getDescrizioneOggetto() {
        return descrizioneOggetto;
    }

    public void setDescrizioneOggetto(String descrizioneOggetto) {
        this.descrizioneOggetto = descrizioneOggetto;
    }

    public Boolean getDisponibilita() {
        return disponibilita;
    }

    public void setDisponibilita(Boolean disponibilita) {
        this.disponibilita = disponibilita;
    }

    public double getPrezzoGiornaliero() {
        return prezzoGiornaliero;
    }

    public void setPrezzoGiornaliero(double prezzoGiornaliero) {
        this.prezzoGiornaliero = prezzoGiornaliero;
    }

    public List<CategoriaOggetto> getOggettoCategorie() {
        return oggettoCategorie;
    }

    public void setOggettoCategorie(List<CategoriaOggetto> oggettoCategorie) {
        this.oggettoCategorie = oggettoCategorie;
    }
}
