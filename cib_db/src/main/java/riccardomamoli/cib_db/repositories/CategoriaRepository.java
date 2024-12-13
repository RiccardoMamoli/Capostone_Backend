package riccardomamoli.cib_db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import riccardomamoli.cib_db.entities.Categoria;

import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Optional<Categoria> findByCategoria(String categoria);

}
