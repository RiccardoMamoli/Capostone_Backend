package riccardomamoli.cib_db.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import riccardomamoli.cib_db.entities.Prenotazione;
import riccardomamoli.cib_db.entities.Utente;
import riccardomamoli.cib_db.enums.StatoPrenotazione;
import riccardomamoli.cib_db.exceptions.BadRequestException;
import riccardomamoli.cib_db.exceptions.UnauthorizedException;
import riccardomamoli.cib_db.payloads.NewPrenotazioneDTO;
import riccardomamoli.cib_db.repositories.PrenotazioneRepository;
import riccardomamoli.cib_db.services.PrenotazioneService;
import riccardomamoli.cib_db.services.UtenteService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/prenotazioni")
public class PrenotazioneController {

    @Autowired
    private PrenotazioneService prenotazioneService;

    @Autowired
    private UtenteService utenteService;

    @Autowired
    private PrenotazioneRepository prenotazioneRepository;

    @GetMapping
    public Page<Prenotazione> getPrenotazioni(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(required = false) Long oggettoId,
            @RequestParam(required = false) String nomeOggetto,
            @RequestParam(required = false) LocalDate dataPrenotazione,
            @RequestParam(required = false) LocalDate dataFinePrenotazione,
            @RequestParam(required = false) LocalDate dataInizioRange,
            @RequestParam(required = false) LocalDate dataFineRange) {


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Utente utenteLoggato = utenteService.findByUsername(username);
        if (utenteLoggato == null) {
            throw new IllegalArgumentException("Utente non trovato.");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

        return prenotazioneService.findWithFilters(utenteLoggato.getId(), oggettoId, nomeOggetto,
                dataPrenotazione, dataFinePrenotazione, dataInizioRange, dataFineRange, pageable);
    }

    @GetMapping("/prenotazioni-oggetti")
    public Page<Prenotazione> getPrenotazioniOggetti(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Utente utenteLoggato = utenteService.findByUsername(username);
        if (utenteLoggato == null) {
            throw new IllegalArgumentException("Utente non trovato.");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

        return prenotazioneService.findPrenotazioniOggettiDiProprieta(utenteLoggato.getId(), pageable);
    }

    @GetMapping("/dates/{oggettoId}")
    public List<LocalDate> getBookedDates(@PathVariable Long oggettoId) {
        List<Prenotazione> prenotazioni = prenotazioneRepository.findByOggettoId(oggettoId);

        return prenotazioni.stream()
                .flatMap(prenotazione -> prenotazione.getDataPrenotazione()
                        .datesUntil(prenotazione.getDataFinePrenotazione().plusDays(1)))
                .toList();
    }


    @PutMapping("/{id}/stato")
    @PreAuthorize("hasAuthority('ADMIN') or @userAuthorizationService.isOwnerOfObject(#id, authentication.name)")
    public Prenotazione updateStatoPrenotazione(@PathVariable Long id,
                                                @RequestBody StatoPrenotazione stato) {
        String authenticatedUsername = getAuthenticatedUsername();
        return prenotazioneService.updateStatoPrenotazione(id, stato, authenticatedUsername);
    }



    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Prenotazione findById(@PathVariable Long id) {
        return this.prenotazioneService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Prenotazione savePrenotazione (@RequestBody @Validated NewPrenotazioneDTO body, BindingResult validationResult){
        if(validationResult.hasErrors()){
            String message = validationResult.getAllErrors().stream().map(error -> error.getDefaultMessage()).collect(Collectors.joining(". "));
            throw new BadRequestException("Payload error: " + message);
        }
        return this.prenotazioneService.createPrenotazione(body);
    }

    private String getAuthenticatedUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public Prenotazione findByIdAndUpdate(
            @PathVariable Long id,
            @RequestBody @Validated NewPrenotazioneDTO body,
            BindingResult validationResult) {

        if (validationResult.hasErrors()) {
            String message = validationResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(". "));
            throw new BadRequestException("Errori nel payload: " + message);
        }

        String authenticatedUsername = getAuthenticatedUsername();


        Prenotazione prenotazione = prenotazioneService.findById(id);

        if (!prenotazione.getUtente().getUsername().equals(authenticatedUsername)) {
            throw new UnauthorizedException("Non sei autorizzato a modificare questo oggetto.");
        }

        return prenotazioneService.findByIdAndUpdate(id, body);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePrenotazione(@PathVariable Long id) {
        String authenticatedUsername = getAuthenticatedUsername();

        Prenotazione prenotazione = prenotazioneService.findById(id);

        if (!prenotazione.getUtente().getUsername().equals(authenticatedUsername)) {
            throw new UnauthorizedException("Non sei autorizzato a eliminare questo oggetto.");
        }

        prenotazioneService.findByIdAndDelete(id);
    }


}
