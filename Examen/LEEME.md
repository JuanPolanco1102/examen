# Solución Examen Hibernate - STEAM

## Estructura del Proyecto

```
Examen/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── dao/
│   │   │   │   └── GenericDAO.java      # DAO Genérico (CRUD)
│   │   │   ├── main/
│   │   │   │   └── App.java # Clase Principal
│   │   │   ├── modelo/
│   │   │   │   ├── Componente.java          # Modelo Hijo
│   │   │   │   └── Proyecto.java            # Modelo Padre
│   │   │   └── util/
│   │   │       ├── GestorFicheros.java      # Gestión XML, CSV, DAT
│   │   │       └── HibernateUtil.java       # Conexión BD
│   │   └── resources/
│   │       └── hibernate.cfg.xml            # Configuración Hibernate
│   ├── pom.xml    # Dependencias Maven
│   ├── datos.xml     # Archivo ejemplo XML
│   ├── datos.csv            # Archivo ejemplo CSV
│   └── copia_seguridad.dat  # Se genera al exportar

```

## Pasos Previos a la Ejecución

### 1. Instalar MySQL (si no lo tienes)
- Descargar desde: https://dev.mysql.com/downloads/mysql/
- Instalar y crear usuario `root` con contraseña `root`

### 2. Crear la Base de Datos
```sql
CREATE DATABASE examen_steam;
```

### 3. Descargar Dependencias
En la carpeta del proyecto:
```bash
mvn clean install
```

### 4. Compilar el Proyecto
```bash
mvn compile
```

### 5. Ejecutar la Aplicación
```bash
mvn exec:java -Dexec.mainClass="main.App"
```

## Características del Código

### ✅ Relación 1:N
- **Proyecto** (Padre) tiene muchos **Componentes** (Hijos)
- CascadeType.ALL: Al guardar proyecto, se guardan sus componentes automáticamente

### ✅ DAO Genérico con HQL
```java
GenericDAO<Proyecto> dao = new GenericDAO<>(Proyecto.class);
dao.save(proyecto);         // Insert/Update
dao.findAll();       // SELECT * FROM proyectos
dao.findBy("autor", "Juan");     // WHERE con parámetros seguros
dao.delete(proyecto);  // DELETE
```

### ✅ Gestión de Ficheros
1. **XML**: Lee subnodos `<componente>` dentro de `<proyecto>`
2. **CSV**: Filtro que evita importar autores "Anonimo"
3. **DAT**: Exportación binaria serializada

### ✅ Sin SQL Crudo
- Todo mediante HQL y parámetros nombrados
- Seguro contra inyección SQL
- Evita PreparedStatement

## Menú Principal

```
--- EXAMEN STEAM ---
1. Cargar datos desde XML (con subnodos)
2. Cargar datos desde CSV
3. Exportar DB a Binario (.dat)
4. Mostrar todos los proyectos (HQL)
5. Buscar Proyecto por Autor (HQL - FindBy)
0. Salir
```

## Notas Importantes

⚠️ **Cambiar Credenciales MySQL**
- Si tu usuario no es `root/root`, edita `hibernate.cfg.xml`

⚠️ **Archivos de Entrada**
- Asegúrate que `datos.xml` y `datos.csv` estén en la raíz del proyecto

⚠️ **Versión Hibernate**
- Usa Hibernate 5.6.15 (compatible con Java 8+)
- Si necesitas Java 17+, sube a Hibernate 6.x

## Examen - Puntos Clave

1. **DAO Genérico** → Reutilizable para cualquier entidad
2. **HQL** → Queries en el lenguaje de objetos, no SQL
3. **Relación 1:N** → Demuestra comprensión de asociaciones
4. **Cascada** → saveOrUpdate automático en relaciones
5. **XML/CSV/DAT** → Múltiples formatos de entrada/salida
6. **Transacciones** → rollback en excepciones

---

**Código preparado para defender en examen con comentarios en cada línea.**
