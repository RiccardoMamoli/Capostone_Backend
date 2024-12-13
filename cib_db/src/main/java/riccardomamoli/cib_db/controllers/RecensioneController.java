package riccardomamoli.cib_db.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import riccardomamoli.cib_db.entities.Recensione;
import riccardomamoli.cib_db.exceptions.BadRequestException;
import riccardomamoli.cib_db.exceptions.UnauthorizedException;
import riccardomamoli.cib_db.payloads.NewRecensioneDTO;
import riccardomamoli.cib_db.services.RecensioneService;

import java.util.stream.Collectors;


@RestController
@RequestMapping("/recensioni")
public class RecensioneController {

    @Autowired
    private RecensioneService recensioneService;

    @GetMapping
    public Page<Recensione> getRecensioni(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(required = false) Long utenteId,
            @RequestParam(required = false) Long prenotazioneId,
            @RequestParam(required = false) Integer valutazioneRecensione,
            @RequestParam(required = false) String testoRecensione) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return recensioneService.findWithFilters(utenteId, prenotazioneId, valutazioneRecensione, testoRecensione, pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Recensione findById(@PathVariable Long id) {
        return this.recensioneService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Recensione saveRecensione (@RequestBody @Validated NewRecensioneDTO body, BindingResult validationResult){
        if(validationResult.hasErrors()){
            String message = validationResult.getAllErrors().stream().map(error -> error.getDefaultMessage()).collect(Collectors.joining(". "));
            throw new BadRequestException("Payload error: " + message);
        }
        return this.recensioneService.createRecensione(body);
    }

    private String getAuthenticatedUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public Recensione findByIdAndUpdate(
            @PathVariable Long id,
            @RequestBody @Validated NewRecensioneDTO body,
            BindingResult validationResult) {

        if (validationResult.hasErrors()) {
            String message = validationResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(". "));
            throw new BadRequestException("Errori nel payload: " + message);
        }

        String authenticatedUsername = getAuthenticatedUsername();


        Recensione recensione = recensioneService.findById(id);

        if (!recensione.getUtente().getUsername().equals(authenticatedUsername)) {
            throw new UnauthorizedException("Non sei autorizzato a modificare questo oggetto.");
        }

        return recensioneService.findByIdAndUpdate(id, body);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecensione(@PathVariable Long id) {
        String authenticatedUsername = getAuthenticatedUsername();

        Recensione recensione = recensioneService.findById(id);

        if (!recensione.getUtente().getUsername().equals(authenticatedUsername)) {
            throw new UnauthorizedException("Non sei autorizzato a eliminare questo oggetto.");
        }

        recensioneService.findByIdAndDelete(id);
    }


}
