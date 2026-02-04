package modelo;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "proyectos")
public class Proyecto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID Autoincremental
    private Long id;

    @Column(name = "nombre_proyecto")
    private String nombre;

private String autor;

 // Relación 1 a N: Un proyecto tiene una lista de componentes
    // Cascade Type ALL: Si guardo el Proyecto, se guardan solos sus componentes
    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Componente> componentes = new ArrayList<>();

    public Proyecto() {}

    public Proyecto(String nombre, String autor) {
 this.nombre = nombre;
        this.autor = autor;
    }

    // Método helper para añadir componentes y mantener la coherencia
    public void addComponente(Componente c) {
    componentes.add(c);
        c.setProyecto(this);
    }

    // Getters y Setters
    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getAutor() { return autor; }
    public List<Componente> getComponentes() { return componentes; }
    
    @Override
    public String toString() {
        return "Proyecto [ID=" + id + ", Nombre=" + nombre + ", Autor=" + autor + "]";
    }
}
