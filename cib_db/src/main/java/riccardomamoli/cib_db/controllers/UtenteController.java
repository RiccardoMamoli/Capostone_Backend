package riccardomamoli.cib_db.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import riccardomamoli.cib_db.entities.Utente;
import riccardomamoli.cib_db.exceptions.UnauthorizedException;
import riccardomamoli.cib_db.payloads.NewUtenteDTO;
import riccardomamoli.cib_db.services.UtenteService;

@RestController
@RequestMapping("/utenti")
public class UtenteController {

    @Autowired
    private UtenteService utenteService;

    @GetMapping
    public Page<Utente> getUtenti(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(required = false) String usernameUtente,
            @RequestParam(required = false) String indirizzo,
            @RequestParam(required = false) String citta,
            @RequestParam(required = false) String stato,
            @RequestParam(required = false) String codicePostale) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

        return utenteService.findWithFilters(usernameUtente, indirizzo, citta, stato, codicePostale, pageable);
    }


    @GetMapping("/me")
    public Utente getProfile(@AuthenticationPrincipal Utente currentUtente) {
        if (currentUtente == null) {
            throw new UnauthorizedException("Utente non autenticato");
        }
        System.out.println("Utente corrente: " + currentUtente.getUsername());
        return currentUtente;
    }


    @PutMapping("/me")
    public Utente updateProfile(@AuthenticationPrincipal Utente currentUtente, @RequestBody @Validated NewUtenteDTO body) {
        return utenteService.findByIdAndUpdate(currentUtente.getId(), body);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Utente> getUserById(@PathVariable Long id) {
        Utente user = utenteService.findById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{username}")
    public ResponseEntity<Utente> getUserByUsername(@PathVariable String username) {
        Utente user = utenteService.findByUsername(username);
        return ResponseEntity.ok(user);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        utenteService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{utenteId}/avatar")
    public String addLogo(@PathVariable("utenteId") Long utenteId, @RequestParam("avatar") MultipartFile file) {
        return this.utenteService.uploadAvatar(file, utenteId);
    }
}