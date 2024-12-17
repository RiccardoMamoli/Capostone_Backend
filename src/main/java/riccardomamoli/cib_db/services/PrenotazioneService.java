package riccardomamoli.cib_db.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import riccardomamoli.cib_db.entities.*;
import riccardomamoli.cib_db.enums.StatoPrenotazione;
import riccardomamoli.cib_db.exceptions.BadRequestException;
import riccardomamoli.cib_db.exceptions.ConflictException;
import riccardomamoli.cib_db.exceptions.NotFoundException;
import riccardomamoli.cib_db.exceptions.UnauthorizedException;
import riccardomamoli.cib_db.payloads.NewPrenotazioneDTO;
import riccardomamoli.cib_db.repositories.CategoriaRepository;
import riccardomamoli.cib_db.repositories.OggettoRepository;
import riccardomamoli.cib_db.repositories.PrenotazioneRepository;
import riccardomamoli.cib_db.repositories.UtenteRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PrenotazioneService {

    @Autowired
    private OggettoRepository oggettoRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private PrenotazioneRepository prenotazioneRepository;


    public Page<Prenotazione> findWithFilters(Long utenteId, Long oggettoId, String nomeOggetto,
                                              LocalDate dataPrenotazione, LocalDate dataFinePrenotazione,
                                              LocalDate dataInizioRange, LocalDate dataFineRange,
                                              Pageable pageable) {
        Specification<Prenotazione> specs = Specification.where(null);

        if (utenteId != null) {
            specs = specs.and(PrenotazioneSpecifications.hasUtenteId(utenteId));
        }

        if (oggettoId != null) {
            specs = specs.and(PrenotazioneSpecifications.hasOggettoId(oggettoId));
        }

        if (nomeOggetto != null) {
            specs = specs.and(PrenotazioneSpecifications.hasNomeOggetto(nomeOggetto));
        }

        if (dataPrenotazione != null) {
            specs = specs.and(PrenotazioneSpecifications.hasDataPrenotazione(dataPrenotazione));
        }

        if (dataFinePrenotazione != null) {
            specs = specs.and(PrenotazioneSpecifications.hasDataFinePrenotazione(dataFinePrenotazione));
        }

        if (dataInizioRange != null && dataFineRange != null) {
            specs = specs.and(PrenotazioneSpecifications.isPrenotazioneInRange(dataInizioRange, dataFineRange));
        } else if (dataInizioRange != null) {
            specs = specs.and(PrenotazioneSpecifications.hasDataPrenotazioneAfter(dataInizioRange));
        } else if (dataFineRange != null) {
            specs = specs.and(PrenotazioneSpecifications.hasDataFineBefore(dataFineRange));
        }

        return prenotazioneRepository.findAll(specs, pageable);
    }



    public Prenotazione createPrenotazione(NewPrenotazioneDTO body) {


        Optional<Utente> utenteFound = utenteRepository.findById(body.utenteId());
        if (utenteFound.isEmpty()) {
            throw new NotFoundException("Utente non trovato!");
        }

        Optional<Oggetto> oggettoFound = oggettoRepository.findById(body.oggettoId());
        if (oggettoFound.isEmpty()) {
            throw new NotFoundException("Oggetto non trovato!");
        }

        List<Prenotazione> overlappingPrenotazioni = prenotazioneRepository.findByOggettoIdAndDataPrenotazioneBetween(
                body.oggettoId(),
                body.dataPrenotazione(),
                body.dataFinePrenotazione()
        );

        if (!overlappingPrenotazioni.isEmpty()) {
            throw new IllegalStateException("L'oggetto è già prenotato nelle date selezionate.");
        }

        Prenotazione prenotazione = new Prenotazione(
                utenteFound.get(),
                oggettoFound.get(),
                body.dataPrenotazione(),
                body.dataFinePrenotazione(),
                body.prezzoPrenotazione()
        );

        return prenotazioneRepository.save(prenotazione);
    }


    public Prenotazione findById(Long id) {
        return prenotazioneRepository.findById(id).orElseThrow(() -> new NotFoundException("Nessuna prenotazione trovata con questo ID: " + id));
    }

    public void findByIdAndDelete(Long id) {
        Prenotazione found = this.findById(id);
        prenotazioneRepository.delete(found);
    }

    private boolean disponibilitaOggeto (Long oggettoId, LocalDate dataPrenotazione, LocalDate dataFinePrenotazione) {
        List<Prenotazione> prenotazioni = prenotazioneRepository.findByOggettoIdAndDataPrenotazioneBetween(oggettoId, dataPrenotazione, dataFinePrenotazione);
        return prenotazioni.isEmpty();
    }

    public Prenotazione findByIdAndUpdate(Long id, NewPrenotazioneDTO body){


        Prenotazione prenotazione = this.findById(id);


        if (!disponibilitaOggeto(body.oggettoId(), body.dataPrenotazione(), body.dataFinePrenotazione())) {
            throw new ConflictException("L'oggetto non è disponibile nelle date selezionate.");
        }


        if (!body.dataPrenotazione().equals(prenotazione.getDataPrenotazione())) {
            if (body.dataPrenotazione().isAfter(prenotazione.getDataPrenotazione())) {
                prenotazione.setDataPrenotazione(body.dataPrenotazione());
            }
        }

        if (!body.dataFinePrenotazione().equals(prenotazione.getDataFinePrenotazione())) {
            if (body.dataFinePrenotazione().isAfter(prenotazione.getDataFinePrenotazione())) {
                prenotazione.setDataFinePrenotazione(body.dataFinePrenotazione());
            }
        }

        return prenotazioneRepository.save(prenotazione);
    }

    public Prenotazione updateStatoPrenotazione(Long id, StatoPrenotazione stato, String authenticatedUsername) {
        Prenotazione prenotazione = this.findById(id);

        if (!prenotazione.getOggetto().getUtente().getUsername().equals(authenticatedUsername)) {
            if (stato != StatoPrenotazione.ACCETTATA && stato != StatoPrenotazione.RIFIUTATA) {
                throw new UnauthorizedException("Non sei autorizzato a modificare questa prenotazione.");
            }
        }

        prenotazione.setStato(stato);

        return prenotazioneRepository.save(prenotazione);
    }



    public Page<Prenotazione> findPrenotazioniOggettiDiProprieta(Long proprietarioId, Pageable pageable) {
        return prenotazioneRepository.findByOggettoProprietarioId(proprietarioId, pageable);
    }


}
