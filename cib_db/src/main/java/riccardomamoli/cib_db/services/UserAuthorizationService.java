package riccardomamoli.cib_db.services;

import org.springframework.stereotype.Service;
import riccardomamoli.cib_db.entities.Prenotazione;
import riccardomamoli.cib_db.exceptions.NotFoundException;
import riccardomamoli.cib_db.repositories.PrenotazioneRepository;

@Service
public class UserAuthorizationService {

    private final PrenotazioneRepository prenotazioneRepository;

    public UserAuthorizationService(PrenotazioneRepository prenotazioneRepository) {
        this.prenotazioneRepository = prenotazioneRepository;
    }

    public boolean isOwnerOfObject(Long id, String username) {
        Prenotazione prenotazione = prenotazioneRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Prenotazione non trovata"));

        return prenotazione.getOggetto().getUtente().getUsername().equals(username);
    }
}
