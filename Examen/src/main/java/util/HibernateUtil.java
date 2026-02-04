package util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static final SessionFactory sessionFactory;

    static {
   try {
         // Carga la configuración del archivo hibernate.cfg.xml
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
