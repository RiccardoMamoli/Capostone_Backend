package riccardomamoli.cib_db.tools;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import riccardomamoli.cib_db.entities.Utente;
import riccardomamoli.cib_db.exceptions.UnauthorizedException;
import riccardomamoli.cib_db.services.UtenteService;

@Component
public class JWTChecker extends OncePerRequestFilter {

    @Autowired
    private JWT jwt;

    @Autowired
    private UtenteService utenteService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, java.io.IOException {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Inserire token nell' Authorization Header nel formato corretto !");
        }

        String accessToken = authorizationHeader.split(" ")[1];

        try {
            jwt.verifyToken(accessToken);
        } catch (Exception e) {
            throw new UnauthorizedException("Token non valido");
        }

        Long idUtente = Long.valueOf(jwt.getIdFromToken(accessToken));

        System.out.println("Token valido per l'utente con ID: " + idUtente);

        Utente utenteCorrente = this.utenteService.findById(idUtente);

        if (utenteCorrente == null) {
            throw new UnauthorizedException("Utente non trovato");
        }


        System.out.println("Autenticato con successo utente: " + utenteCorrente.getUsername());

        Authentication authentication = new UsernamePasswordAuthenticationToken(utenteCorrente, null, utenteCorrente.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        if (request.getMethod().equals("GET") && new AntPathMatcher().match("/oggetti/**", request.getServletPath())) {
            return true;
        }

        if (request.getMethod().equals("GET") && new AntPathMatcher().match("/prenotazioni/dates/**", request.getServletPath())) {
            return true;
        }

        if (new AntPathMatcher().match("/utenti/me", request.getServletPath())) {
            return false;
        }

        if (request.getMethod().equals("GET") && new AntPathMatcher().match("/utenti/**", request.getServletPath())) {
            return true;
        }

        return new AntPathMatcher().match("/auth/**", request.getServletPath());
    }
}
