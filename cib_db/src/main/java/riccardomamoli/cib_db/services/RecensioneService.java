package riccardomamoli.cib_db.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import riccardomamoli.cib_db.entities.*;
import riccardomamoli.cib_db.exceptions.NotFoundException;
import riccardomamoli.cib_db.payloads.NewRecensioneDTO;
import riccardomamoli.cib_db.repositories.PrenotazioneRepository;
import riccardomamoli.cib_db.repositories.RecensioneRepository;
import riccardomamoli.cib_db.repositories.UtenteRepository;

import java.util.Optional;

@Service
public class RecensioneService {

    @Autowired
    private RecensioneRepository recensioneRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private PrenotazioneRepository prenotazioneRepository;

    public Page<Recensione> findWithFilters(Long utenteId, Long prenotazioneId, Integer valutazioneRecensione, String testoRecensione, Pageable pageable) {
        Specification<Recensione> specs = Specification.where(null);

        if (utenteId != null) {
            specs = specs.and(RecensioneSpecifications.hasUtenteId(utenteId));
        }

        if (prenotazioneId != null) {
            specs = specs.and(RecensioneSpecifications.hasPrenotazioneId(prenotazioneId));
        }

        if (valutazioneRecensione != null) {
            specs = specs.and(RecensioneSpecifications.hasValutazioneRecensione(valutazioneRecensione));
        }

        if (testoRecensione != null && !testoRecensione.isEmpty()) {
            specs = specs.and(RecensioneSpecifications.hasTestoRecensione(testoRecensione));
        }

        return recensioneRepository.findAll(specs, pageable);
    }

    public Recensione createRecensione (NewRecensioneDTO body) {

        Optional<Prenotazione> prenotazioneFound = prenotazioneRepository.findById(body.prenotazioneId());

        if (prenotazioneFound.isEmpty()) {
            throw new NotFoundException("Prenotazione non trovata!");
        }

        Optional<Utente> utenteFound = utenteRepository.findById(body.utenteId());
        if(utenteFound.isEmpty()) {
            throw new NotFoundException("Utente non trovato!");
        }

        Recensione recensione = new Recensione(
                prenotazioneFound.get(),
                utenteFound.get(),
                body.testoRecensione(),
                body.valutazioneRecensione()
        );

        return recensioneRepository.save(recensione);
    }

    public Recensione findById(Long id) {
        return recensioneRepository.findById(id).orElseThrow(() -> new NotFoundException("Nessuna recensione trovata con questo ID: " + id));
    }

    public void findByIdAndDelete(Long id) {
        Recensione found = this.findById(id);
        recensioneRepository.delete(found);
    }

    public Recensione findByIdAndUpdate(Long id, NewRecensioneDTO body) {

        Recensione recensione = this.findById(id);

        if (!body.testoRecensione().equals(recensione.getTestoRecensione())) {
            recensione.setTestoRecensione(body.testoRecensione());
        }

        if (!body.valutazioneRecensione().equals(recensione.getValutazioneRecensione())) {
            recensione.setValutazioneRecensione(body.valutazioneRecensione());
        }

        return recensioneRepository.save(recensione);
    }


}
