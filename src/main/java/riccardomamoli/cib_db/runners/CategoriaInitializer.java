package riccardomamoli.cib_db.runners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import riccardomamoli.cib_db.entities.Categoria;
import riccardomamoli.cib_db.repositories.CategoriaRepository;

@Component
public class CategoriaInitializer implements CommandLineRunner {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Override
    public void run(String... args) {
        if (categoriaRepository.findByCategoria("ELETTRONICA").isEmpty()) {
            categoriaRepository.save(new Categoria("ELETTRONICA"));
        }
        if (categoriaRepository.findByCategoria("ARTICOLI_CASA").isEmpty()) {
            categoriaRepository.save(new Categoria("ARTICOLI_CASA"));
        }
        if (categoriaRepository.findByCategoria("MEZZI").isEmpty()) { // Corretto "MEZZi" a "MEZZI"
            categoriaRepository.save(new Categoria("MEZZI"));
        }
        if (categoriaRepository.findByCategoria("ABBIGLIAMENTO").isEmpty()) {
            categoriaRepository.save(new Categoria("ABBIGLIAMENTO"));
        }
        if (categoriaRepository.findByCategoria("SPORT_TEMPO_LIBERO").isEmpty()) { // Corretto il nome della categoria
            categoriaRepository.save(new Categoria("SPORT_TEMPO_LIBERO"));
        }
        if (categoriaRepository.findByCategoria("MUSICA").isEmpty()) {
            categoriaRepository.save(new Categoria("MUSICA"));
        }
        if (categoriaRepository.findByCategoria("SPORT").isEmpty()) {
            categoriaRepository.save(new Categoria("SPORT"));
        }
    }
}
