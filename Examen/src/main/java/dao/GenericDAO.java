package dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import util.HibernateUtil;
import java.util.List;

// <T> significa que funciona para cualquier clase (Proyecto, Componente, Alumno...)
public class GenericDAO<T> {
    private Class<T> entityClass;

    public GenericDAO(Class<T> entityClass) {
  this.entityClass = entityClass;
    }

// GUARDAR (Sirve para Insertar y Actualizar)
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

    // LISTAR TODOS (HQL)
    public List<T> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
  // HQL: "FROM NombreClase" (No nombre tabla)
           return session.createQuery("FROM " + entityClass.getSimpleName(), entityClass).list();
        }
  }

// BUSCAR POR CAMPO ESPECÍFICO (EL "findBy" QUE LE GUSTA)
    // Ejemplo de uso: findBy("autor", "Juan");
 public List<T> findBy(String campo, Object valor) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
     // Construimos la query HQL dinámicamente
      // "FROM Proyecto WHERE autor = :val"
           String hql = "FROM " + entityClass.getSimpleName() + " WHERE " + campo + " = :val";
            
    Query<T> query = session.createQuery(hql, entityClass);
         query.setParameter("val", valor); // Seteamos el parámetro (seguro contra inyección)
        
            return query.list();
    }
    }
 
    // BORRAR
    public void delete(T entity) {
         Session session = HibernateUtil.getSessionFactory().openSession();
    Transaction tx = session.beginTransaction();
         session.delete(entity);
         tx.commit();
session.close();
    }
}
