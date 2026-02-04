package util;

import dao.GenericDAO;
import modelo.Componente;
import modelo.Proyecto;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.List;

public class GestorFicheros {

    GenericDAO<Proyecto> daoProyecto = new GenericDAO<>(Proyecto.class);

    // --- 1. XML con SUBNODOS (DOM) ---
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

// --- 2. CSV (Texto con separadores) - LECTURA ---
     // Formato esperado: nombreProyecto,autor
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

    // --- 2b. CSV (Texto con separadores) - ESCRITURA ---
    // Exporta todos los proyectos a CSV formato: nombre,autor
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

     // --- 3. DAT (Binario - Serialización) - LECTURA ---
    // Cargar lista de proyectos desde fichero binario
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

 // --- 3b. DAT (Binario - Serialización) - ESCRITURA ---
    // Guardar lista de proyectos en fichero binario
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

    // --- 4. TXT (Texto plano) - LECTURA ---
    // Formato esperado: Linea por proyecto "nombre|autor"
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

    // --- 4b. TXT (Texto plano) - ESCRITURA ---
    // Exporta todos los proyectos a TXT formato legible: nombre|autor
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
