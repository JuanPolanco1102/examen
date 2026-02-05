package modelo;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * CLASE PROYECTO - Entidad Padre del modelo de datos
 * Representa un proyecto STEAM con nombre, autor y lista de componentes.
 * Se mapea a la tabla "proyectos" en la base de datos MySQL.
 */
@Entity
@Table(name = "proyectos")
public class Proyecto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID Autoincremental en BD
    private Long id;

    @Column(name = "nombre_proyecto") // Nombre de columna en BD diferente al atributo
    private String nombre;

    private String autor; // No usa @Column: se mapea a columna "autor" por defecto

    // Relación 1 a N: Un proyecto tiene muchas componentes (ej: Robot tiene Motor, Batería)
    // mappedBy="proyecto": el dueño de la relación es Componente.proyecto
    // cascade=CascadeType.ALL: guardar/actualizar/borrar Proyecto propaga a Componentes
    // fetch=EAGER: al cargar Proyecto, trae sus componentes automáticamente
    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Componente> componentes = new ArrayList<>();

    public Proyecto() {} // Constructor vacío requerido por Hibernate/JPA

    public Proyecto(String nombre, String autor) {
        this.nombre = nombre;
        this.autor = autor;
    }

    /**
     * Añade un componente al proyecto y mantiene la relación bidireccional.
     * Evita tener que llamar manualmente a c.setProyecto(this).
     */
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
