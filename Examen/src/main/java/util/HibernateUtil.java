package util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * CLASE HibernateUtil - Utilidad singleton para Hibernate
 * Crea y mantiene una única SessionFactory para toda la aplicación.
 * Lee la configuración desde src/main/resources/hibernate.cfg.xml.
 * SessionFactory = fábrica de sesiones (conexiones) a la BD.
 */
public class HibernateUtil {
    private static final SessionFactory sessionFactory;

    static {
        try {
            // configure() busca hibernate.cfg.xml en el classpath
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Fallo al crear SessionFactory." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
