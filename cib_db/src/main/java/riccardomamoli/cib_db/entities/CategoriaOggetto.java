package riccardomamoli.cib_db.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;


@Entity
@Table(name = "categoria_oggetto")
public class CategoriaOggetto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "categoria_id", referencedColumnName = "id")
    private Categoria categoria;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "oggetto_id", referencedColumnName = "id")
    @JsonBackReference
    @JsonIgnore
    private Oggetto oggetto;

    public CategoriaOggetto(){};


    public CategoriaOggetto(Categoria categoria, Oggetto oggetto) {
        this.categoria = categoria;
        this.oggetto = oggetto;
    }

    public Long getId() {
        return id;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Oggetto getOggetto() {
        return oggetto;
    }

    public void setOggetto(Oggetto oggetto) {
        this.oggetto = oggetto;
    }
}
