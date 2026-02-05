package util;

import dao.GenericDAO;
import modelo.Componente;
import modelo.Proyecto;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.List;

/**
 * CLASE GestorFicheros - Gestión de importación/exportación de datos
 * Soporta 4 formatos: XML (con subnodos), CSV, TXT y DAT (binario serializado).
 * Todos los métodos leen o escriben Proyectos y los persisten en BD vía GenericDAO.
 */
public class GestorFicheros {

    GenericDAO<Proyecto> daoProyecto = new GenericDAO<>(Proyecto.class);

    /**
     * Carga proyectos desde XML usando DOM (Document Object Model).
     * Lee nodos <proyecto> y sus subnodos <componente> para proyectos con piezas.
     * Formato esperado: <proyecto><nombre/><autor/><componentes><componente>...
     */
    public void cargarXML(String ruta) {
      try {
File xmlFile = new File(ruta);
         DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    Document doc = dBuilder.parse(xmlFile);
        doc.getDocumentElement().normalize();

     // Obtenemos lista de nodos <proyecto>
    NodeList nList = doc.getElementsByTagName("proyecto");

       for (int i = 0; i < nList.getLength(); i++) {
     Node nNode = nList.item(i);
   if (nNode.getNodeType() == Node.ELEMENT_NODE) {
              Element elemento = (Element) nNode;

      // Leer datos simples
   String nombre = elemento.getElementsByTagName("nombre").item(0).getTextContent();
    String autor = elemento.getElementsByTagName("autor").item(0).getTextContent();

   // Crear Objeto Padre
         Proyecto pro = new Proyecto(nombre, autor);

    // --- LEER SUBNODOS (Componentes) ---
    // Buscamos los tags <componente> DENTRO del proyecto actual
       NodeList listaHijos = elemento.getElementsByTagName("componente");
    for (int j = 0; j < listaHijos.getLength(); j++) {
        Element elHijo = (Element) listaHijos.item(j);
    String nombreComp = elHijo.getElementsByTagName("nombreComp").item(0).getTextContent();
      double precio = Double.parseDouble(elHijo.getElementsByTagName("precio").item(0).getTextContent());

    // Añadir hijo al padre
         pro.addComponente(new Componente(nombreComp, precio));
   }

  // Guardamos en BD (Hibernate guarda padre e hijos por CascadeType.ALL)
       daoProyecto.save(pro);
       System.out.println("Proyecto importado: " + nombre);
  }
       }
        } catch (Exception e) {
  e.printStackTrace();
   }
        }

    /**
     * Carga proyectos desde CSV. Formato: nombreProyecto,autor
     * Filtro: no importa líneas donde autor sea "Anonimo" (case insensitive).
     */
    public void cargarCSV(String ruta) {
  try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
String linea;
       while ((linea = br.readLine()) != null) {
      String[] datos = linea.split(",");
      // El "Giro": Solo importar si el autor no es "Anonimo"
     if (!datos[1].equalsIgnoreCase("Anonimo")) {
        Proyecto p = new Proyecto(datos[0], datos[1]);
        daoProyecto.save(p);
    System.out.println("CSV Importado: " + datos[0]);
  }
    }
       } catch (IOException e) {
         e.printStackTrace();
        }
    }

    /**
     * Exporta todos los proyectos de la BD a CSV.
     * Formato: primera línea encabezados "nombre,autor", luego una línea por proyecto.
     */
    public void exportarCSV(String ruta) {
        List<Proyecto> lista = daoProyecto.findAll();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ruta))) {
         // Escribir encabezados
     bw.write("nombre,autor");
            bw.newLine();
            
      // Escribir datos
          for (Proyecto p : lista) {
       String linea = p.getNombre() + "," + p.getAutor();
        bw.write(linea);
        bw.newLine();
            }
   System.out.println("Datos exportados a CSV correctamente: " + ruta);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Carga proyectos desde archivo binario (serialización Java).
     * Lee una List<Proyecto> previamente guardada con exportarBinario.
     */
    public void cargarBinario(String ruta) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ruta))) {
     // Leer lista de proyectos del archivo binario
         @SuppressWarnings("unchecked")
        List<Proyecto> lista = (List<Proyecto>) ois.readObject();
    
            // Guardar cada proyecto en la BD
      for (Proyecto p : lista) {
     daoProyecto.save(p);
    System.out.println("Proyecto cargado desde binario: " + p.getNombre());
            }
      System.out.println("Total proyectos cargados desde binario: " + lista.size());
} catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
        }
    }

    /**
     * Exporta todos los proyectos a archivo binario (backup).
     * Serializa una List<Proyecto> para recuperación posterior.
     */
    public void exportarBinario(String ruta) {
        List<Proyecto> lista = daoProyecto.findAll();
   try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ruta))) {
  // Escribir lista completa de proyectos
     oos.writeObject(lista);
  System.out.println("Datos exportados a binario correctamente: " + ruta);
    System.out.println("Total proyectos guardados: " + lista.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Carga proyectos desde TXT. Formato: nombre|autor (separador |)
     * Ignora líneas vacías y las que empiezan por # (comentarios).
     * No importa si nombre está vacío.
     */
    public void cargarTXT(String ruta) {
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            int contador = 0;
        
            while ((linea = br.readLine()) != null) {
          // Saltar líneas vacías y comentarios (que comienzan con #)
              if (linea.trim().isEmpty() || linea.startsWith("#")) {
  continue;
           }
                
    // Formato: nombre|autor
                String[] datos = linea.split("\\|");
     if (datos.length == 2) {
        String nombre = datos[0].trim();
         String autor = datos[1].trim();
       
   // Lógica: No cargar si el nombre está vacío
  if (!nombre.isEmpty()) {
       Proyecto p = new Proyecto(nombre, autor);
        daoProyecto.save(p);
    System.out.println("TXT Importado: " + nombre);
   contador++;
          }
    }
       }
  System.out.println("Total proyectos cargados desde TXT: " + contador);
   } catch (IOException e) {
     e.printStackTrace();
        }
    }

    /**
     * Exporta proyectos a TXT con formato legible.
     * Incluye encabezado con comentarios (#) y formato nombre|autor.
     */
    public void exportarTXT(String ruta) {
List<Proyecto> lista = daoProyecto.findAll();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ruta))) {
          // Escribir encabezado
            bw.write("# Archivo de Proyectos STEAM");
 bw.newLine();
     bw.write("# Formato: nombre|autor");
            bw.newLine();
            bw.write("# ===========================");
 bw.newLine();
    bw.newLine();
 
            // Escribir datos
            for (Proyecto p : lista) {
    String linea = p.getNombre() + "|" + p.getAutor();
        bw.write(linea);
   bw.newLine();
            }
            System.out.println("Datos exportados a TXT correctamente: " + ruta);
            System.out.println("Total proyectos guardados: " + lista.size());
        } catch (IOException e) {
    e.printStackTrace();
        }
    }
}
