package main;

import dao.GenericDAO;
import modelo.Proyecto;
import util.GestorFicheros;
import java.util.Scanner;
import java.util.List;

/**
 * CLASE App - Punto de entrada de la aplicación "Examen STEAM"
 * Menú interactivo por consola para importar/exportar proyectos
 * en múltiples formatos y consultar la base de datos Hibernate.
 */
public class App {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        GestorFicheros gestor = new GestorFicheros();  // Importar/exportar archivos
        GenericDAO<Proyecto> dao = new GenericDAO<>(Proyecto.class);  // Acceso a BD

        int opcion = -1;
        while (opcion != 0) {
            System.out.println("\n========================================");
     System.out.println("        --- EXAMEN STEAM ---");
    System.out.println("========================================");
  System.out.println("1.  Cargar datos desde XML (con subnodos)");
       System.out.println("2.  Cargar datos desde CSV");
            System.out.println("3.  Cargar datos desde TXT");
            System.out.println("4.  Cargar datos desde DAT (binario)");
       System.out.println("-----");
  System.out.println("5.  Mostrar todos los proyectos (HQL)");
         System.out.println("6.  Buscar Proyecto por Autor (HQL - FindBy)");
     System.out.println("-----");
         System.out.println("7.  Exportar BD a CSV");
       System.out.println("8.  Exportar BD a TXT");
            System.out.println("9.  Exportar BD a DAT (binario)");
    System.out.println("-----");
  System.out.println("0.  Salir");
         System.out.println("========================================");
   System.out.print("Elige opción: ");
            
        // Control de entrada: si el usuario escribe texto, opcion=-1 y mostrará "no válida"
        try {
            opcion = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            opcion = -1;
        }

        switch (opcion) {
          case 1:
     // Cargar desde XML
  System.out.println("\nCargando datos desde XML...");
   gestor.cargarXML("datos.xml");
  break;
  
      case 2:
         // Cargar desde CSV
      System.out.println("\nCargando datos desde CSV...");
        gestor.cargarCSV("datos.csv");
              break;
       
     case 3:
      // Cargar desde TXT
      System.out.println("\nCargando datos desde TXT...");
   gestor.cargarTXT("datos.txt");
    break;
     
   case 4:
            // Cargar desde DAT (binario)
      System.out.println("\nCargando datos desde DAT (binario)...");
     gestor.cargarBinario("backup.dat");
      break;
        
        case 5:
            // findAll() ejecuta HQL: "FROM Proyecto"
            System.out.println("\n--- LISTADO DE PROYECTOS (HQL) ---");
            List<Proyecto> todos = dao.findAll();
 if (todos.isEmpty()) {
           System.out.println("No hay proyectos en la base de datos.");
      } else {
 todos.forEach(System.out::println);
    }
        break;
        
        case 6:
            // findBy genera HQL: "FROM Proyecto WHERE autor = :val"
            System.out.print("\nIntroduce el nombre del autor a buscar: ");
            String autor = sc.nextLine();
            System.out.println("\nBuscando proyectos de: " + autor);
            List<Proyecto> resultados = dao.findBy("autor", autor);
        
      if (resultados.isEmpty()) {
      System.out.println("No se encontraron proyectos del autor: " + autor);
    } else {
       System.out.println("Resultados encontrados: " + resultados.size());
       resultados.forEach(System.out::println);
     }
      break;
              
    case 7:
                    // Exportar a CSV
        System.out.println("\nExportando datos a CSV...");
    gestor.exportarCSV("exportacion.csv");
        break;
        
     case 8:
  // Exportar a TXT
 System.out.println("\nExportando datos a TXT...");
              gestor.exportarTXT("exportacion.txt");
       break;
            
     case 9:
       // Exportar a DAT (binario)
           System.out.println("\nExportando datos a DAT (binario)...");
    gestor.exportarBinario("backup.dat");
           break;
             
  case 0:
  System.out.println("\nSaliendo... ¡Suerte en el examen!");
      break;
         
        default:
    System.out.println("\nOpción no válida. Por favor, elige una opción del menú.");
 }
        }
        sc.close();
    }
}
