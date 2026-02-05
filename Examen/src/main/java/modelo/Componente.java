package modelo;

import javax.persistence.*;
import java.io.Serializable;

/**
 * CLASE COMPONENTE - Entidad Hija del modelo de datos
 * Representa una pieza o material de un proyecto STEAM (ej: Motor DC, Batería, Jeringuilla).
 * Varios componentes pertenecen a un mismo Proyecto (relación N:1).
 * Se mapea a la tabla "componentes" en la base de datos.
 */
@Entity
@Table(name = "componentes")
public class Componente implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;  // Nombre del componente (ej: "Motor DC")
    private double precio;  // Precio en euros

    // Relación N:1 - Muchos componentes pueden pertenecer a un mismo Proyecto
    // JoinColumn: crea columna proyecto_id en tabla componentes (clave foránea)
    @ManyToOne
    @JoinColumn(name = "proyecto_id")
    private Proyecto proyecto;

    public Componente() {} // Constructor vacío obligatorio para Hibernate

    public Componente(String nombre, double precio) {
        this.nombre = nombre;
        this.precio = precio;
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    public Proyecto getProyecto() { return proyecto; }
    public void setProyecto(Proyecto proyecto) { this.proyecto = proyecto; }
    
    @Override
    public String toString() { return "Componente: " + nombre + " (" + precio + "€)"; }
}
