package riccardomamoli.cib_db.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import riccardomamoli.cib_db.entities.Oggetto;
import riccardomamoli.cib_db.exceptions.BadRequestException;
import riccardomamoli.cib_db.exceptions.UnauthorizedException;
import riccardomamoli.cib_db.payloads.NewOggettoDTO;
import riccardomamoli.cib_db.services.OggettoService;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/oggetti")

public class OggettoController {
    @Autowired
    private OggettoService oggettoService;


    @GetMapping
    public Page<Oggetto> getOggetti(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(required = false) Long utenteId,
            @RequestParam(required = false) String nomeOggetto,
            @RequestParam(required = false) Boolean disponibilita,
            @RequestParam(required = false) Double prezzoOggetto) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

        return oggettoService.findWithFilters(utenteId, nomeOggetto, disponibilita, prezzoOggetto, pageable);
    }

    @PatchMapping("/{oggettoId}/foto")
    public ResponseEntity<String> aggiungiFotoOggetto(@PathVariable Long oggettoId, @RequestParam("foto") MultipartFile file) {
        String fotoUrl = oggettoService.uploadFotoOggetto(file, oggettoId);
        return ResponseEntity.ok(fotoUrl);
    }

    @GetMapping("/{id}")
    public Oggetto findById(@PathVariable Long id) {
        return this.oggettoService.findById(id);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Oggetto saveOggetto(@RequestBody @Validated NewOggettoDTO body, BindingResult validationResult){
        if(validationResult.hasErrors()){
            String message = validationResult.getAllErrors().stream().map(error -> error.getDefaultMessage()).collect(Collectors.joining(". "));
            throw new BadRequestException("Payload error: " + message);
        }
        return this.oggettoService.createOggetto(body);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public Oggetto findByIdAndUpdate(
            @PathVariable Long id,
            @RequestBody @Validated NewOggettoDTO body,
            BindingResult validationResult) {

        if (validationResult.hasErrors()) {
            String message = validationResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(". "));
            throw new BadRequestException("Errori nel payload: " + message);
        }

        String authenticatedUsername = getAuthenticatedUsername();


        Oggetto oggetto = oggettoService.findById(id);

        if (!oggetto.getUtente().getUsername().equals(authenticatedUsername)) {
            throw new UnauthorizedException("Non sei autorizzato a modificare questo oggetto.");
        }

        return oggettoService.findByIdAndUpdate(id, body);
    }

    private String getAuthenticatedUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }


    @DeleteMapping("/{oggettoId}")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOggetto(@PathVariable Long oggettoId) {
        String authenticatedUsername = getAuthenticatedUsername();

        Oggetto oggetto = oggettoService.findById(oggettoId);

        if (!oggetto.getUtente().getUsername().equals(authenticatedUsername)) {
            throw new UnauthorizedException("Non sei autorizzato a eliminare questo oggetto.");
        }

        oggettoService.findByIdAndDelete(oggettoId);
    }


}
