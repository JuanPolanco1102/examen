package util;

import modelo.Proyecto;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;

/**
 * REFERENCIA EXAMEN - Solo lo que entra de Hibernate:
 *
 * 1. CRUD básico contra MySQL (todo con Hibernate, sin SQL a mano).
 * 2. PROHIBIDO PreparedStatement (0 puntos). Solo HQL (Hibernate Query Language).
 * 3. Patrón DAO Genérico + funciones findBy (ej: buscarPorDni, buscarPorAutor).
 *
 * Esta clase es solo guía; el DAO real está en dao.GenericDAO.
 */
public class HibernateEjemplosCompletos {

    // ============================================================================
    // CRUD BÁSICO (Create, Read, Update, Delete) - Sin PreparedStatement, solo Hibernate
    // ============================================================================

    /**
     * CREATE (Insert) - Guardar entidad nueva en BD.
     * session.save(entity) -> INSERT en MySQL.
     */
    public void crud_Save(Proyecto p) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.save(p);   // INSERT
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException(e);
        } finally {
            session.close();
        }
    }

    /**
     * READ por ID - Obtener una entidad por su clave primaria.
     * session.get(Clase.class, id) -> SELECT ... WHERE id = ?
     */
    public Proyecto crud_GetById(Long id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            return session.get(Proyecto.class, id);  // null si no existe
        } finally {
            session.close();
        }
    }

    /**
     * UPDATE - Modificar entidad ya existente.
     * session.update(entity) -> UPDATE en MySQL.
     * Alternativa: session.saveOrUpdate(entity) hace INSERT o UPDATE según tenga id o no.
     */
    public void crud_Update(Proyecto p) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.update(p);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException(e);
        } finally {
            session.close();
        }
    }

    /**
     * DELETE - Borrar entidad de la BD.
     * session.delete(entity) -> DELETE en MySQL.
     */
    public void crud_Delete(Proyecto p) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.delete(p);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException(e);
        } finally {
            session.close();
        }
    }

    /**
     * SaveOrUpdate - Lo que suele usarse en DAO genérico: si id es null -> INSERT, si no -> UPDATE.
     */
    public void crud_SaveOrUpdate(Proyecto p) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.saveOrUpdate(p);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException(e);
        } finally {
            session.close();
        }
    }

    // ============================================================================
    // HQL - Listar todos (equivalente SELECT * FROM tabla)
    // HQL usa nombre de CLASE (Proyecto), no nombre de tabla (proyectos).
    // ============================================================================

    /**
     * Listar todos los registros con HQL.
     * "FROM Proyecto" -> Hibernate traduce a SELECT * FROM proyectos.
     */
    public List<Proyecto> hql_FindAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Proyecto> query = session.createQuery("FROM Proyecto", Proyecto.class);
            return query.list();
        }
    }

    // ============================================================================
    // FIND BY (lo que le gustó: buscarPorDni, buscarPorAutor, etc.)
    // Siempre con parámetros nombrados (:param) en HQL, NUNCA concatenar strings.
    // ============================================================================

    /**
     * Buscar por un campo (ej: buscarPorAutor).
     * HQL: "FROM Proyecto WHERE autor = :autor"
     * setParameter("autor", valor) -> seguro, sin inyección SQL.
     */
    public List<Proyecto> findBy_Autor(String autor) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Proyecto> query = session.createQuery(
                "FROM Proyecto WHERE autor = :autor",
                Proyecto.class
            );
            query.setParameter("autor", autor);
            return query.list();
        }
    }

    /**
     * Buscar por otro campo (ej: buscarPorNombre).
     * Mismo patrón: WHERE campo = :param y setParameter.
     */
    public List<Proyecto> findBy_Nombre(String nombre) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Proyecto> query = session.createQuery(
                "FROM Proyecto WHERE nombre = :nombre",
                Proyecto.class
            );
            query.setParameter("nombre", nombre);
            return query.list();
        }
    }

    /**
     * Buscar por DNI (ejemplo típico en un DAO de Alumno).
     * Si la entidad tuviera "dni", sería: WHERE dni = :dni.
     */
    public List<Proyecto> findBy_DniEjemplo(String dni) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Ejemplo con alias "p" (opcional pero habitual en HQL)
            Query<Proyecto> query = session.createQuery(
                "FROM Proyecto p WHERE p.autor = :dni",  // En Proyecto no hay dni; sería p.dni en Alumno
                Proyecto.class
            );
            query.setParameter("dni", dni);
            return query.list();
        }
    }

    /**
     * FindBy GENÉRICO (como en tu GenericDAO): campo y valor variables.
     * Así se hace un solo método findBy(String campo, Object valor) para cualquier campo.
     */
    public List<Proyecto> findBy_Generico(String campo, Object valor) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // "FROM Proyecto WHERE " + campo + " = :val" -> campo es nombre de atributo (autor, nombre...)
            String hql = "FROM Proyecto WHERE " + campo + " = :val";
            Query<Proyecto> query = session.createQuery(hql, Proyecto.class);
            query.setParameter("val", valor);
            return query.list();
        }
    }

    /**
     * FindBy que devuelve un único resultado (ej: buscarPorDni cuando DNI es único).
     * uniqueResult() -> un solo objeto o null.
     */
    public Proyecto findBy_AutorUnico(String autor) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Proyecto> query = session.createQuery(
                "FROM Proyecto WHERE autor = :autor",
                Proyecto.class
            );
            query.setParameter("autor", autor);
            return query.uniqueResult();
        }
    }

    // ============================================================================
    // PATRÓN DAO GENÉRICO - Resumen para calcar en el examen
    // ============================================================================
    /*
     * 1. Clase genérica: public class GenericDAO<T>
     * 2. Constructor recibe Class<T>: public GenericDAO(Class<T> entityClass)
     * 3. save(T entity): session.saveOrUpdate(entity) dentro de transacción
     * 4. findAll(): session.createQuery("FROM " + entityClass.getSimpleName(), entityClass).list()
     * 5. findBy(String campo, Object valor):
     *      String hql = "FROM " + entityClass.getSimpleName() + " WHERE " + campo + " = :val";
     *      Query<T> q = session.createQuery(hql, entityClass);
     *      q.setParameter("val", valor);
     *      return q.list();
     * 6. delete(T entity): session.delete(entity) dentro de transacción
     *
     * Uso: GenericDAO<Proyecto> dao = new GenericDAO<>(Proyecto.class);
     *      dao.save(proyecto);
     *      List<Proyecto> todos = dao.findAll();
     *      List<Proyecto> deJuan = dao.findBy("autor", "Juan");
     *
     * PROHIBIDO: PreparedStatement, Connection, ResultSet, SQL con strings.
     * TODO con Session, createQuery (HQL), setParameter, save/update/delete/get.
     */
}
