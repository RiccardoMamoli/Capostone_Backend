package riccardomamoli.cib_db.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Entity
@Table(name = "utenti")
public class Utente implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeUtente;
    private String cognomeUtente;
    private String usernameUtente;
    private LocalDate dataNascita;
    private String emailUtente;

    private String passwordUtente;
    private String avatarUtente;
    private String indirizzoUtente;
    private String cittaUtente;
    private String statoUtente;
    private String codicePostaleUtente;
    private Double latitudine;
    private Double longitudine;



    @OneToMany(mappedBy = "utente", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<UtenteRuolo> utenteRuoli = new ArrayList<>();


    public Utente(String nomeUtente,
                  String cognomeUtente,
                  String usernameUtente,
                  LocalDate dataNascita,
                  String emailUtente,
                  String passwordUtente,
                  String indirizzoUtente,
                  String cittaUtente,
                  String statoUtente,
                  String codicePostaleUtente,
                  List<Ruolo> ruoli
    ) {
        this.nomeUtente = nomeUtente;
        this.cognomeUtente = cognomeUtente;
        this.usernameUtente = usernameUtente;
        this.dataNascita = dataNascita;
        this.emailUtente = emailUtente;
        this.passwordUtente = passwordUtente;
        this.avatarUtente = "https://ui-avatars.com/api/?name=" + this.nomeUtente + "+" + this.cognomeUtente;
        this.indirizzoUtente = indirizzoUtente;
        this.cittaUtente = cittaUtente;
        this.statoUtente = statoUtente;
        this.codicePostaleUtente = codicePostaleUtente;
        for (Ruolo ruolo : ruoli) {
            UtenteRuolo utenteRuolo = new UtenteRuolo(this, ruolo);
            this.utenteRuoli.add(utenteRuolo);
        }
        this.latitudine = null;
        this.longitudine = null;
    }

    public Utente(){};

    public String getIndirizzoUtente() {
        return indirizzoUtente;
    }

    public void setIndirizzoUtente(String indirizzoUtente) {
        this.indirizzoUtente = indirizzoUtente;
    }

    public String getCittaUtente() {
        return cittaUtente;
    }

    public void setCittaUtente(String cittaUtente) {
        this.cittaUtente = cittaUtente;
    }

    public String getStatoUtente() {
        return statoUtente;
    }

    public void setStatoUtente(String statoUtente) {
        this.statoUtente = statoUtente;
    }

    public String getCodicePostaleUtente() {
        return codicePostaleUtente;
    }

    public void setCodicePostaleUtente(String codicePostaleUtente) {
        this.codicePostaleUtente = codicePostaleUtente;
    }

    public Double getLatitudine() {
        return latitudine;
    }

    public void setLatitudine(Double latitudine) {
        this.latitudine = latitudine;
    }

    public Double getLongitudine() {
        return longitudine;
    }

    public void setLongitudine(Double longitudine) {
        this.longitudine = longitudine;
    }

    public String getAvatarUtente() {
        return avatarUtente;
    }

    public void setAvatarUtente(String avatatUtente) {
        this.avatarUtente = avatatUtente;
    }


    public Long getId() {
        return id;
    }

    public String getNomeUtente() {
        return nomeUtente;
    }

    public void setNomeUtente(String nomeUtente) {
        this.nomeUtente = nomeUtente;
    }

    public String getCognomeUtente() {
        return cognomeUtente;
    }

    public void setCognomeUtente(String cognomeUtente) {
        this.cognomeUtente = cognomeUtente;
    }

    public String getUsernameUtente() {
        return usernameUtente;
    }

    public void setUsernameUtente(String usernameUtente) {
        this.usernameUtente = usernameUtente;
    }

    public LocalDate getDataNascita() {
        return dataNascita;
    }

    public void setDataNascita(LocalDate dataNascita) {
        this.dataNascita = dataNascita;
    }

    public String getEmailUtente() {
        return emailUtente;
    }

    public void setEmailUtente(String emailUtente) {
        this.emailUtente = emailUtente;
    }

    public String getPasswordUtente() {
        return passwordUtente;
    }

    public void setPasswordUtente(String passwordUtente) {
        this.passwordUtente = passwordUtente;
    }


    public List<UtenteRuolo> getUtenteRuoli() {
        return utenteRuoli;
    }

    public void setUtenteRuoli(List<UtenteRuolo> utenteRuoli) {
        this.utenteRuoli = utenteRuoli;
    }

    @Override
    public String getPassword() {
        return this.passwordUtente;
    }

    @Override
    public String getUsername() {
        return this.usernameUtente;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        for (UtenteRuolo utenteRuolo : utenteRuoli) {
            Ruolo ruolo = utenteRuolo.getRuolo();
            if (ruolo != null) {
                authorities.add(new SimpleGrantedAuthority(ruolo.getTipo()));
            }
        }

        return authorities;
    }


}
