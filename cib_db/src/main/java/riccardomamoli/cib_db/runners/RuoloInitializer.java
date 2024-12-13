package riccardomamoli.cib_db.runners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import riccardomamoli.cib_db.entities.Ruolo;
import riccardomamoli.cib_db.repositories.RuoloRepository;

@Component
public class RuoloInitializer implements CommandLineRunner {

    @Autowired
    private RuoloRepository ruoloRepository;

    @Override
    public void run(String... args) {
        if (ruoloRepository.findByTipo("USER").isEmpty()) {
            ruoloRepository.save(new Ruolo("USER"));
        }
        if (ruoloRepository.findByTipo("ADMIN").isEmpty()) {
            ruoloRepository.save(new Ruolo("ADMIN"));
        }
    }

}
