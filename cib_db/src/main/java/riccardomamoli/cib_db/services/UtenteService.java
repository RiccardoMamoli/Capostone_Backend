package riccardomamoli.cib_db.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import io.jsonwebtoken.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import riccardomamoli.cib_db.entities.Ruolo;
import riccardomamoli.cib_db.entities.Utente;
import riccardomamoli.cib_db.entities.UtenteSpecifications;
import riccardomamoli.cib_db.exceptions.BadRequestException;
import riccardomamoli.cib_db.exceptions.NotFoundException;
import riccardomamoli.cib_db.payloads.NewUtenteDTO;
import riccardomamoli.cib_db.repositories.RuoloRepository;
import riccardomamoli.cib_db.repositories.UtenteRepository;

import java.util.List;

@Service
public class UtenteService {

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private RuoloRepository ruoloRepository;

    @Autowired
    private GeocodingService geocodingService;

    @Autowired
    private PasswordEncoder bcrypt;

    @Autowired
    private Cloudinary cloudinaryUploader;

    public Page<Utente> findWithFilters(String usernameUtente, String indirizzo, String citta, String stato, String codicePostale, Pageable pageable) {

        Specification<Utente> specs = Specification.where(null);

        if (usernameUtente != null && !usernameUtente.isEmpty()) {
            specs = specs.and(UtenteSpecifications.hasUsername(usernameUtente));
        }
        if (indirizzo != null && !indirizzo.isEmpty()) {
            specs = specs.and(UtenteSpecifications.hasIndirizzo(indirizzo));
        }
        if (citta != null && !citta.isEmpty()) {
            specs = specs.and(UtenteSpecifications.hasCitta(citta));
        }
        if (stato != null && !stato.isEmpty()) {
            specs = specs.and(UtenteSpecifications.hasStato(stato));
        }
        if (codicePostale != null && !codicePostale.isEmpty()) {
            specs = specs.and(UtenteSpecifications.hasCodicePostale(codicePostale));
        }

        return utenteRepository.findAll(specs, pageable);
    }

    public Utente findById(Long id) {
        return this.utenteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Nessun utente trovato con ID: " + id));
    }

    public Utente findByEmail(String email) {
        return this.utenteRepository.findByEmailUtente(email)
                .orElseThrow(() -> new NotFoundException("Nessun utente registrato con questa email"));
    }

    public Utente save(NewUtenteDTO body) {

        this.utenteRepository.findByEmailUtente(body.emailUtente()).ifPresent(
                user -> {
                    throw new BadRequestException("Email " + body.emailUtente() + " già in uso!");
                }
        );

        this.utenteRepository.findByUsernameUtente(body.usernameUtente()).ifPresent(
                user -> {
                    throw new BadRequestException("Username " + body.usernameUtente() + " già in uso!");
                }
        );

        Ruolo ruolo = ruoloRepository.findByTipo("USER").orElseThrow(() -> new RuntimeException("Ruolo USER non trovato"));

        Utente newUser = new Utente(
                body.nomeUtente(),
                body.cognomeUtente(),
                body.usernameUtente(),
                body.dataDiNascita(),
                body.emailUtente(),
                bcrypt.encode(body.password()),
                body.indirizzo(),
                body.citta(),
                body.stato(),
                body.codicePostale(),
                List.of(ruolo)
        );

        if (newUser.getIndirizzoUtente() != null && !newUser.getIndirizzoUtente().isEmpty()) {
            String indirizzoFormattato = newUser.getIndirizzoUtente() + ", " + newUser.getCodicePostaleUtente() + ", " + newUser.getCittaUtente() + ", " + newUser.getStatoUtente();
            double[] coordinates = geocodingService.getCoordinatesFromAddress(indirizzoFormattato);
            newUser.setLatitudine(coordinates[0]);
            newUser.setLongitudine(coordinates[1]);
        }



        Utente savedUser = this.utenteRepository.save(newUser);
        

        return savedUser;
    }

    public String uploadAvatar(MultipartFile file, Long idUtente) {
        String url = null;
        try {
            url = (String) cloudinaryUploader.uploader().upload(file.getBytes(), ObjectUtils.emptyMap()).get("url");
        } catch (IOException | java.io.IOException e) {
            throw new BadRequestException("Ci sono stati problemi con l'upload del file!");
        }
        Utente found = this.findById(idUtente);
        found.setAvatarUtente(url);
        utenteRepository.save(found);
        return url;
    }

    public Utente findByIdAndUpdate(Long id, NewUtenteDTO body) {
        Utente utente = utenteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Utente non trovato con ID: " + id));

        if (!body.nomeUtente().equals(utente.getNomeUtente())) {
            utente.setNomeUtente(body.nomeUtente());
        }

        if (!body.cognomeUtente().equals(utente.getCognomeUtente())) {
            utente.setCognomeUtente(body.cognomeUtente());
        }

        if (!body.usernameUtente().equals(utente.getUsernameUtente())) {
            if (utenteRepository.existsByUsernameUtente(body.usernameUtente())) {
                throw new IllegalArgumentException("Lo username è già in uso da un altro utente!");
            }
            utente.setUsernameUtente(body.usernameUtente());
        }

        if (!body.emailUtente().equals(utente.getEmailUtente())) {
            if (utenteRepository.existsByEmailUtente(body.emailUtente())) {
                throw new IllegalArgumentException("L'email è già in uso da un altro utente!");
            }
            utente.setEmailUtente(body.emailUtente());
        }

        if (body.password() != null && !body.password().isEmpty()) {
            utente.setPasswordUtente(bcrypt.encode(body.password()));
        }


        if (!body.indirizzo().equals(utente.getIndirizzoUtente())) {
            double[] coordinates = geocodingService.getCoordinatesFromAddress(body.indirizzo());
            utente.setIndirizzoUtente(body.indirizzo());
            utente.setLatitudine(coordinates[0]);
            utente.setLongitudine(coordinates[1]);
        }

        if (!body.citta().equals(utente.getCittaUtente())) {
            utente.setCittaUtente(body.citta());
        }

        if (!body.stato().equals(utente.getStatoUtente())) {
            utente.setStatoUtente(body.stato());
        }

        if (!body.codicePostale().equals(utente.getCodicePostaleUtente())) {
            utente.setCodicePostaleUtente(body.codicePostale());
        }

        return utenteRepository.save(utente);
    }

    public Utente findByUsername(String username) {
        return this.utenteRepository.findByUsernameUtente(username)
                .orElseThrow(() -> new NotFoundException("Nessun utente trovato con nome: " + username));
    }

    public void deleteUser(Long id) {
        Utente utente = utenteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Utente non trovato con ID: " + id));

        utenteRepository.delete(utente);
    }

}
