package util;

import modelo.Proyecto;
import modelo.Componente;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CLASE DE REFERENCIA: FicherosEjemplosCompletos
 *
 * Guía comentada de cómo gestionar los 4 tipos de ficheros que pueden entrar
 * en el examen: XML (leer, guardar, subnodos), TXT, CSV y DAT.
 *
 * ========== ENFOQUE EXAMEN ==========
 * - XML: solo entra LEER, CARGAR y GUARDAR, y SUBNODOS (padre-hijo en XML).
 * - Ficheros: entra UNO de los tres (.txt, .dat, .csv), algo sencillo pero "dado una vuelta".
 * - Nada de ventanas: todo consola o lógica interna.
 * ====================================
 *
 * CONTENIDO:
 * 1. XML - Leer, guardar, subnodos (DOM)
 * 2. TXT - Lectura y escritura, separadores, comentarios
 * 3. CSV - Lectura y escritura, encabezados, comas en campos
 * 4. DAT - Binario con ObjectInputStream / ObjectOutputStream
 */
public class FicherosEjemplosCompletos {

    // ============================================================================
    // SECCIÓN 1: XML (Leer, cargar, guardar, subnodos)
    // ============================================================================
    // En el examen entra: leer/cargar XML, guardar XML, y manejo de SUBNODOS.
    // Se usa DOM (Document Object Model): parseamos todo el documento en memoria.
    // ============================================================================

    /**
     * 1.1 LEER / CARGAR XML - Flujo básico con DOM
     *
     * Pasos: File -> DocumentBuilder -> parse -> Document.
     * Luego obtenemos nodos por nombre de etiqueta (getElementsByTagName).
     */
    public void xml_LeerCargarBasico(String ruta) throws Exception {
        // 1) Abrir el fichero
        File xmlFile = new File(ruta);

        // 2) Crear el parser DOM (factory + builder)
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        // 3) Parsear: lee todo el XML y lo convierte en un árbol de nodos
        Document doc = dBuilder.parse(xmlFile);

        // 4) Normalizar espacios en blanco y saltos de línea (opcional pero recomendable)
        doc.getDocumentElement().normalize();

        // 5) Obtener lista de nodos por nombre de etiqueta
        // Ejemplo: <proyecto>...</proyecto> -> getElementsByTagName("proyecto")
        NodeList listaProyectos = doc.getElementsByTagName("proyecto");

        for (int i = 0; i < listaProyectos.getLength(); i++) {
            Node nodo = listaProyectos.item(i);

            // Solo procesar nodos de tipo ELEMENTO (no texto, comentarios, etc.)
            if (nodo.getNodeType() != Node.ELEMENT_NODE) continue;

            Element elemento = (Element) nodo;

            // 6) Leer texto de un hijo por nombre de etiqueta
            // getElementsByTagName("nombre").item(0) -> primer <nombre> dentro de este nodo
            // getTextContent() -> texto que hay entre <nombre> y </nombre>
            String nombre = elemento.getElementsByTagName("nombre").item(0).getTextContent();
            String autor = elemento.getElementsByTagName("autor").item(0).getTextContent();

            // Aquí crearías tu objeto (Proyecto) y lo guardarías en BD, en lista, etc.
            // Proyecto p = new Proyecto(nombre, autor);
        }
    }

    /**
     * 1.2 LEER XML CON SUBNODOS (lo que entra en el examen)
     *
     * Estructura típica: <proyecto> tiene dentro <componentes><componente>...</componente></componentes>.
     * Para cada nodo padre, recorremos sus hijos con otro getElementsByTagName dentro del elemento.
     */
    public List<Proyecto> xml_LeerConSubnodos(String ruta) throws Exception {
        File xmlFile = new File(ruta);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(xmlFile);
        doc.getDocumentElement().normalize();

        List<Proyecto> proyectos = new ArrayList<>();
        NodeList nList = doc.getElementsByTagName("proyecto");

        for (int i = 0; i < nList.getLength(); i++) {
            Node nNode = nList.item(i);
            if (nNode.getNodeType() != Node.ELEMENT_NODE) continue;

            Element elemento = (Element) nNode;

            // Datos del nodo padre
            String nombre = elemento.getElementsByTagName("nombre").item(0).getTextContent();
            String autor = elemento.getElementsByTagName("autor").item(0).getTextContent();
            Proyecto pro = new Proyecto(nombre, autor);

            // --- SUBNODOS: buscar etiquetas hijas DENTRO del elemento actual
            NodeList listaHijos = elemento.getElementsByTagName("componente");
            for (int j = 0; j < listaHijos.getLength(); j++) {
                Node nodoHijo = listaHijos.item(j);
                if (nodoHijo.getNodeType() != Node.ELEMENT_NODE) continue;

                Element elHijo = (Element) nodoHijo;
                String nombreComp = elHijo.getElementsByTagName("nombreComp").item(0).getTextContent();
                double precio = Double.parseDouble(elHijo.getElementsByTagName("precio").item(0).getTextContent());

                pro.addComponente(new Componente(nombreComp, precio));
            }

            proyectos.add(pro);
        }
        return proyectos;
    }

    /**
     * 1.3 GUARDAR / ESCRIBIR XML (generar un XML desde cero o desde datos)
     *
     * Crear Document, crear elementos con createElement, añadir con appendChild,
     * y luego usar Transformer para volcar el Document a un fichero.
     */
    public void xml_Guardar(String ruta, List<Proyecto> proyectos) throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();

        // Nodo raíz (ej: <lista>)
        Element raiz = doc.createElement("lista");
        doc.appendChild(raiz);

        for (Proyecto p : proyectos) {
            Element proyectoEl = doc.createElement("proyecto");

            Element nombreEl = doc.createElement("nombre");
            nombreEl.setTextContent(p.getNombre());
            proyectoEl.appendChild(nombreEl);

            Element autorEl = doc.createElement("autor");
            autorEl.setTextContent(p.getAutor());
            proyectoEl.appendChild(autorEl);

            // Opcional: subnodos (componentes)
            Element componentesEl = doc.createElement("componentes");
            for (Componente c : p.getComponentes()) {
                Element compEl = doc.createElement("componente");
                Element nombreComp = doc.createElement("nombreComp");
                nombreComp.setTextContent(c.getNombre());
                Element precioEl = doc.createElement("precio");
                precioEl.setTextContent(String.valueOf(c.getPrecio()));
                compEl.appendChild(nombreComp);
                compEl.appendChild(precioEl);
                componentesEl.appendChild(compEl);
            }
            proyectoEl.appendChild(componentesEl);
            raiz.appendChild(proyectoEl);
        }

        // Escribir Document a fichero
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(ruta));
        transformer.transform(source, result);
    }

    /**
     * 1.4 Evitar NullPointer si falta un nodo (examen "dado una vuelta")
     */
    public String xml_LeerTextoSeguro(Element padre, String nombreTag) {
        NodeList nl = padre.getElementsByTagName(nombreTag);
        if (nl.getLength() == 0) return "";
        Node n = nl.item(0);
        return n.getTextContent() != null ? n.getTextContent().trim() : "";
    }

    // ============================================================================
    // SECCIÓN 2: TXT (texto plano)
    // ============================================================================
    // Formato libre: líneas, separadores (|, ;, etc.), comentarios (#).
    // "Dado una vuelta": ignorar comentarios, líneas vacías, validar campos.
    // ============================================================================

    /**
     * 2.1 LEER TXT - Línea a línea con BufferedReader
     */
    public List<String> txt_LeerLineas(String ruta) throws IOException {
        List<String> lineas = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                lineas.add(linea);
            }
        }
        return lineas;
    }

    /**
     * 2.2 LEER TXT con formato "nombre|autor" y saltar comentarios/vacías
     */
    public List<Proyecto> txt_LeerConFormato(String ruta) throws IOException {
        List<Proyecto> proyectos = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                // "Dado una vuelta": ignorar líneas vacías y comentarios
                if (linea.trim().isEmpty() || linea.trim().startsWith("#")) continue;

                // Separador | (escapar en regex: \\|)
                String[] datos = linea.split("\\|");
                if (datos.length >= 2) {
                    String nombre = datos[0].trim();
                    String autor = datos[1].trim();
                    if (!nombre.isEmpty())
                        proyectos.add(new Proyecto(nombre, autor));
                }
            }
        }
        return proyectos;
    }

    /**
     * 2.3 ESCRIBIR TXT - Con BufferedWriter (encabezados y datos)
     */
    public void txt_Escribir(String ruta, List<Proyecto> proyectos) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ruta))) {
            bw.write("# Archivo de Proyectos");
            bw.newLine();
            bw.write("# Formato: nombre|autor");
            bw.newLine();
            for (Proyecto p : proyectos) {
                bw.write(p.getNombre() + "|" + p.getAutor());
                bw.newLine();
            }
        }
    }

    /**
     * 2.4 Otros separadores (; o tab) - mismo patrón, cambiar split
     */
    public List<String[]> txt_LeerConSeparador(String ruta, String separador) throws IOException {
        List<String[]> filas = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                // split(";") o split("\t") para tab
                filas.add(linea.split(separador));
            }
        }
        return filas;
    }

    // ============================================================================
    // SECCIÓN 3: CSV (valores separados por comas)
    // ============================================================================
    // Primera línea suele ser encabezado. Cuidado: comas dentro de campos (comillas).
    // ============================================================================

    /**
     * 3.1 LEER CSV - Sin encabezado, formato simple "nombre,autor"
     */
    public List<Proyecto> csv_LeerSimple(String ruta) throws IOException {
        List<Proyecto> proyectos = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length >= 2) {
                    proyectos.add(new Proyecto(datos[0].trim(), datos[1].trim()));
                }
            }
        }
        return proyectos;
    }

    /**
     * 3.2 LEER CSV - Con encabezado (primera línea nombre,autor)
     */
    public List<Proyecto> csv_LeerConEncabezado(String ruta) throws IOException {
        List<Proyecto> proyectos = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea = br.readLine(); // Leer y descartar encabezado
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length >= 2)
                    proyectos.add(new Proyecto(datos[0].trim(), datos[1].trim()));
            }
        }
        return proyectos;
    }

    /**
     * 3.3 ESCRIBIR CSV - Con encabezado
     */
    public void csv_Escribir(String ruta, List<Proyecto> proyectos) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ruta))) {
            bw.write("nombre,autor");
            bw.newLine();
            for (Proyecto p : proyectos) {
                bw.write(p.getNombre() + "," + p.getAutor());
                bw.newLine();
            }
        }
    }

    /**
     * 3.4 "Dado una vuelta": filtrar al leer (ej: no importar si autor es "Anonimo")
     */
    public List<Proyecto> csv_LeerConFiltro(String ruta) throws IOException {
        List<Proyecto> proyectos = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length >= 2 && !datos[1].trim().equalsIgnoreCase("Anonimo"))
                    proyectos.add(new Proyecto(datos[0].trim(), datos[1].trim()));
            }
        }
        return proyectos;
    }

    // ============================================================================
    // SECCIÓN 4: DAT (fichero binario - serialización Java)
    // ============================================================================
    // ObjectOutputStream.writeObject / ObjectInputStream.readObject.
    // Las clases deben implementar Serializable (Proyecto, Componente, etc.).
    // ============================================================================

    /**
     * 4.1 GUARDAR / EXPORTAR a DAT (escribir objetos en binario)
     */
    public void dat_Escribir(String ruta, List<Proyecto> proyectos) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ruta))) {
            oos.writeObject(proyectos);
        }
    }

    /**
     * 4.2 LEER / CARGAR desde DAT
     */
    @SuppressWarnings("unchecked")
    public List<Proyecto> dat_Leer(String ruta) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ruta))) {
            return (List<Proyecto>) ois.readObject();
        }
    }

    /**
     * 4.3 Escribir un solo objeto (no lista)
     */
    public void dat_EscribirUno(String ruta, Proyecto p) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ruta))) {
            oos.writeObject(p);
        }
    }

    public Proyecto dat_LeerUno(String ruta) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ruta))) {
            return (Proyecto) ois.readObject();
        }
    }

    // ============================================================================
    // RESUMEN EXAMEN - Qué tener claro
    // ============================================================================
    /*
     * XML:
     *   - DocumentBuilderFactory + DocumentBuilder + parse(File)
     *   - getElementsByTagName("etiqueta") -> NodeList
     *   - item(i), getNodeType() == ELEMENT_NODE, getTextContent()
     *   - Subnodos: dentro de cada Element, otro getElementsByTagName("hijo")
     *   - Guardar: Document, createElement, appendChild, Transformer.transform
     *
     * TXT:
     *   - BufferedReader + FileReader, readLine(), split("\\|") o split(";")
     *   - BufferedWriter + FileWriter, write(), newLine()
     *   - Ignorar vacías y líneas que empiecen por #
     *
     * CSV:
     *   - Mismo BufferedReader/Writer; separador coma
     *   - Primera línea encabezado (leer y no usar como dato, o usar como nombres)
     *   - Posible filtro (ej: no cargar si columna X es "Anonimo")
     *
     * DAT:
     *   - ObjectOutputStream(FileOutputStream) -> writeObject(lista u objeto)
     *   - ObjectInputStream(FileInputStream) -> readObject() (cast a List<Proyecto> o Proyecto)
     *   - Clases deben ser Serializable
     */
}
