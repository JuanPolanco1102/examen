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

// --- 2. CSV (Texto con separadores) ---
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

     // --- 3. DAT (Binario - Serialización) ---
    // Guardar lista de proyectos en fichero binario
     public void exportarBinario(String ruta) {
      List<Proyecto> lista = daoProyecto.findAll();
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ruta))) {
        // Nota: Para que esto funcione, Proyecto debe implementar 'Serializable'
           oos.writeObject(lista);
    System.out.println("Datos exportados a binario correctamente.");
        } catch (IOException e) {
e.printStackTrace();
    }
   }
}
