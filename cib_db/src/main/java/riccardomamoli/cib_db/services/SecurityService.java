package riccardomamoli.cib_db.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import riccardomamoli.cib_db.entities.Utente;
import riccardomamoli.cib_db.exceptions.UnauthorizedException;
import riccardomamoli.cib_db.payloads.LoginDTO;
import riccardomamoli.cib_db.tools.JWT;

@Service
public class SecurityService {

    @Autowired
    private UtenteService utenteService;

    @Autowired
    private JWT jwt;

    @Autowired
    private PasswordEncoder bcrypt;

    public String checkCredentialsAndGenerateToken(LoginDTO body) {
        Utente found = this.utenteService.findByEmail(body.email());

        if (bcrypt.matches(body.password(), found.getPassword())) {
            return jwt.createToken(found);
        } else {
            throw new UnauthorizedException("Credenziali errate!");
        }
    }


}
