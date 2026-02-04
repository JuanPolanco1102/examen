package modelo;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "componentes")
public class Componente implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private double precio;

    // Relación inversa: Muchos componentes pertenecen a un Proyecto
    @ManyToOne
    @JoinColumn(name = "proyecto_id")
    private Proyecto proyecto;

    // Constructor vacío obligatorio para Hibernate
    public Componente() {}

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
