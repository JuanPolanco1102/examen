package dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import util.HibernateUtil;
import java.util.List;

/**
 * CLASE GenericDAO - Patrón DAO (Data Access Object) genérico
 * Proporciona operaciones CRUD reutilizables para cualquier entidad JPA.
 * Usa HQL (Hibernate Query Language) en lugar de SQL crudo.
 * <T> = tipo genérico: puede ser Proyecto, Componente, etc.
 */
public class GenericDAO<T> {
    private Class<T> entityClass; // Ej: Proyecto.class

    public GenericDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * Guarda o actualiza una entidad en BD.
     * saveOrUpdate: si tiene ID null -> INSERT, si existe -> UPDATE.
     */
    public void save(T entity) {
    Session session = HibernateUtil.getSessionFactory().openSession();
   Transaction tx = null;
        try {
  tx = session.beginTransaction();
session.saveOrUpdate(entity); // Hibernate decide si es insert o update
    tx.commit();
        } catch (Exception e) {
           if (tx != null) tx.rollback(); // Si falla, deshacer cambios
          e.printStackTrace();
        } finally {
            session.close(); // Siempre cerrar sesión
   }
    }

    /**
     * Lista todas las entidades de la tabla.
     * HQL: "FROM Proyecto" usa nombre de clase, no de tabla.
     */
    public List<T> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM " + entityClass.getSimpleName(), entityClass).list();
        }
    }

    /**
     * Busca entidades por un campo y valor concretos.
     * Ej: findBy("autor", "Juan") -> todos los proyectos cuyo autor sea "Juan".
     * Usa parámetros enlazados (:val) para evitar inyección SQL.
     */
    public List<T> findBy(String campo, Object valor) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM " + entityClass.getSimpleName() + " WHERE " + campo + " = :val";
            Query<T> query = session.createQuery(hql, entityClass);
            query.setParameter("val", valor);
            return query.list();
        }
    }

    /**
     * Elimina una entidad de la base de datos.
     */
    public void delete(T entity) {
         Session session = HibernateUtil.getSessionFactory().openSession();
    Transaction tx = session.beginTransaction();
         session.delete(entity);
         tx.commit();
session.close();
    }
}
