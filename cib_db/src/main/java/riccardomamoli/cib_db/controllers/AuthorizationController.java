package riccardomamoli.cib_db.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import riccardomamoli.cib_db.entities.Utente;
import riccardomamoli.cib_db.exceptions.BadRequestException;
import riccardomamoli.cib_db.payloads.LoginDTO;
import riccardomamoli.cib_db.payloads.NewUtenteDTO;
import riccardomamoli.cib_db.payloads.UtenteLoginResponseDTO;
import riccardomamoli.cib_db.services.SecurityService;
import riccardomamoli.cib_db.services.UtenteService;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthorizationController {

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UtenteService utenteService;

    @PostMapping("/login")
    public UtenteLoginResponseDTO LoginResponseDTO(@RequestBody @Validated LoginDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            String message = validationResult.getAllErrors().stream().map(objectError -> objectError.getDefaultMessage()).collect(Collectors.joining(". "));
            throw new BadRequestException("Ci sono stati errori nel payload! " + message);
        }
        return new UtenteLoginResponseDTO(this.securityService.checkCredentialsAndGenerateToken(body));
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Utente save(@RequestBody @Validated NewUtenteDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            String message = validationResult.getAllErrors().stream().map(objectError -> objectError.getDefaultMessage()).collect(Collectors.joining(". "));
            throw new BadRequestException("Ci sono stati errori nel payload! " + message);
        }
        return this.utenteService.save(body);
    }
}
