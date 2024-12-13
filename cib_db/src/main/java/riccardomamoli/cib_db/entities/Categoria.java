package riccardomamoli.cib_db.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categorie")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String categoria;

    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<CategoriaOggetto> categoriaOggetto = new ArrayList<>();

    public Categoria(){};

    public Categoria(String categoria) {
        this.categoria = categoria;
    }

    public List<CategoriaOggetto> getCategoriaOggetto() {
        return categoriaOggetto;
    }

    public void setCategoriaOggetto(List<CategoriaOggetto> categoriaOggetto) {
        this.categoriaOggetto = categoriaOggetto;
    }

    public Long getId() {
        return id;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    @Override
    public String toString() {
        return "Categoria{" +
                "id=" + id +
                ", categoria='" + categoria + '\'' +
                '}';
    }
}
