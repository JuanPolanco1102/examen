package main;

import dao.GenericDAO;
import modelo.Proyecto;
import util.GestorFicheros;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
     // Inicializamos herramientas
        Scanner sc = new Scanner(System.in);
   GestorFicheros gestor = new GestorFicheros();
   GenericDAO<Proyecto> dao = new GenericDAO<>(Proyecto.class);

 int opcion = -1;
        
      // Bucle del menú
  while (opcion != 0) {
System.out.println("\n--- EXAMEN STEAM ---");
 System.out.println("1. Cargar datos desde XML (con subnodos)");
     System.out.println("2. Cargar datos desde CSV");
  System.out.println("3. Exportar DB a Binario (.dat)");
           System.out.println("4. Mostrar todos los proyectos (HQL)");
 System.out.println("5. Buscar Proyecto por Autor (HQL - FindBy)");
         System.out.println("0. Salir");
    System.out.print("Elige opcion: ");
       
     // Control de errores básico por si meten letras
        try {
    opcion = Integer.parseInt(sc.nextLine());
      } catch (NumberFormatException e) {
      opcion = -1;
      }

 switch (opcion) {
                case 1:
              // Asegúrate de tener un archivo 'datos.xml' en la raíz del proyecto
         System.out.println("Cargando XML...");
                    gestor.cargarXML("datos.xml");
 break;
              case 2:
          System.out.println("Cargando CSV...");
  gestor.cargarCSV("datos.csv");
      break;
    case 3:
         gestor.exportarBinario("copia_seguridad.dat");
       break;
          case 4:
        System.out.println("\n--- LISTADO DE PROYECTOS ---");
            // El forEach imprime usando el toString() de la clase Proyecto
  dao.findAll().forEach(System.out::println);
     break;
     case 5:
     System.out.print("Introduce el nombre del autor: ");
             String autor = sc.nextLine();
    // Uso de la función estrella 'findBy'
         var resultados = dao.findBy("autor", autor);
       
                if(resultados.isEmpty()) System.out.println("No se encontraron proyectos.");
            else resultados.forEach(System.out::println);
 break;
        case 0:
    System.out.println("Saliendo... ¡Suerte en el examen!");
              break;
        default:
      System.out.println("Opción no válida");
         }
        }
   sc.close();
   }
}
