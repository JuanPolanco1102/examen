package util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * CLASE UTILITARIA PARA ARRANCAR HIBERNATE.
 * Su única misión es leer el hibernate.cfg.xml y preparar la maquinaria.
 */
public class HibernateUtil {

    // La SessionFactory es la fábrica de conexiones. Solo debe haber una en toda la app.
    private static final SessionFactory sessionFactory;

    // Bloque estático: Se ejecuta UNA sola vez al iniciar el programa.
    static {
        try {
            System.out.println("--- INICIANDO HIBERNATE ---");
            
            // 1. Crea una configuración vacía
            // 2. .configure(): Busca automáticamente 'hibernate.cfg.xml' en 'src/main/resources'
            // 3. .buildSessionFactory(): Intenta conectar a MySQL.
            // SI ESTO FALLA, EL PROGRAMA REVIENTA AQUÍ.
            sessionFactory = new Configuration().configure().buildSessionFactory();
            
            System.out.println("--- CONEXIÓN EXITOSA ---");
            
        } catch (Throwable ex) {
            // Si entra aquí, revisa: 
            // 1. Que MySQL esté encendido (XAMPP/Docker).
            // 2. Que el usuario/pass del XML sean correctos.
            // 3. Que hayas creado la BD vacía 'examen_steam'.
            System.err.println("FALLO CRÍTICO: No se pudo conectar a Hibernate.");
            System.err.println(ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    // Método para pedir la fábrica desde el Main o el DAO
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}