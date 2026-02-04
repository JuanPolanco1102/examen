# Solución Examen Hibernate - STEAM

## Estructura del Proyecto

```
Examen/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── dao/
│   │   │   │   └── GenericDAO.java           # DAO Genérico (CRUD)
│   │   │   ├── main/
│   │   │   │   └── App.java       # Clase Principal con menú
│   │   │   ├── modelo/
│   ││   │   ├── Componente.java   # Modelo Hijo
│ │   │   │   └── Proyecto.java        # Modelo Padre
│   │   │   └── util/
│   │   │   ├── GestorFicheros.java       # Gestión XML, CSV, TXT, DAT
│   │   │       └── HibernateUtil.java        # Conexión BD
│   │   └── resources/
│   │    └── hibernate.cfg.xml  # Configuración Hibernate
│   ├── pom.xml                   # Dependencias Maven
│   ├── datos.xml        # Ejemplo XML con subnodos
│   ├── datos.csv # Ejemplo CSV
│   ├── datos.txt   # Ejemplo TXT
│   ├── backup.dat          # Se genera al exportar (binario)
│   └── exportacion.*     # Archivos generados al exportar

```

## Funcionalidades Principales

### 🔄 Lectura de Archivos (Importar)
1. **XML** - Lee proyectos con subnodos (componentes)
2. **CSV** - Formato: `nombre,autor`
3. **TXT** - Formato: `nombre|autor` (con comentarios #)
4. **DAT** - Carga binaria serializada desde backup

### 💾 Escritura de Archivos (Exportar)
1. **CSV** - Exporta todos los proyectos a formato texto
2. **TXT** - Exporta con formato legible y comentarios
3. **DAT** - Exporta serializado para backup binario

### 🗄️ Operaciones en BD
1. **findAll()** - Lista todos los proyectos (HQL)
2. **findBy()** - Busca proyectos por autor (HQL)
3. **save()** - Inserta o actualiza (CRUD genérico)
4. **delete()** - Elimina registros

---

## Pasos Previos a la Ejecución

### 1. Instalar MySQL
```bash
# Descargar desde: https://dev.mysql.com/downloads/mysql/
# Crear usuario: root / root
```

### 2. Crear la Base de Datos
```sql
CREATE DATABASE examen_steam;
```

### 3. Descargar Dependencias
```bash
mvn clean install
```

### 4. Compilar
```bash
mvn compile
```

### 5. Ejecutar
```bash
# Eclipse: Click derecho → Run As → Java Application
# Terminal: mvn exec:java -Dexec.mainClass="main.App"
```

---

## Menú Completo de la Aplicación

```
========================================
        --- EXAMEN STEAM ---
========================================
1.  Cargar datos desde XML (con subnodos)
2.  Cargar datos desde CSV
3.  Cargar datos desde TXT
4.  Cargar datos desde DAT (binario)
-----
5.  Mostrar todos los proyectos (HQL)
6.  Buscar Proyecto por Autor (HQL - FindBy)
-----
7.  Exportar BD a CSV
8.  Exportar BD a TXT
9.  Exportar BD a DAT (binario)
-----
0.  Salir
========================================
```

---

## Ejemplos de Uso

### Cargar desde XML
```
Opción: 1
> Se leen proyectos con sus componentes (subnodos)
> Se guardan automáticamente en BD
```

### Cargar desde CSV
```
Opción: 2
> Formato: nombre,autor
> Filtra autores "Anonimo"
```

### Cargar desde TXT
```
Opción: 3
> Formato: nombre|autor
> Ignora líneas con # y vacías
```

### Buscar por Autor (findBy)
```
Opción: 6
> Introduce: Juan
> Retorna todos los proyectos de Juan usando HQL
```

### Exportar a CSV
```
Opción: 7
> Genera: exportacion.csv
> Contiene encabezados y datos
```

### Exportar a TXT
```
Opción: 8
> Genera: exportacion.txt
> Formato legible con comentarios
```

### Exportar a DAT
```
Opción: 9
> Genera: backup.dat
> Binario serializado (para respaldo)
```

---

## Código Destacado

### DAO Genérico con HQL
```java
// Crear DAO
GenericDAO<Proyecto> dao = new GenericDAO<>(Proyecto.class);

// CRUD
dao.save(proyecto);           // INSERT/UPDATE
dao.findAll();// SELECT *
dao.findBy("autor", "Juan");     // WHERE con parámetros
dao.delete(proyecto);            // DELETE
```

### Lectura de XML con Subnodos
```java
// Lee proyectos y sus componentes (relación 1:N)
gestor.cargarXML("datos.xml");
```

### Lectura/Escritura de TXT
```java
// Lectura
gestor.cargarTXT("datos.txt");   // Formato: nombre|autor

// Escritura
gestor.exportarTXT("exportacion.txt");  // Con comentarios
```

### Lectura/Escritura Binaria
```java
// Lectura desde backup
gestor.cargarBinario("backup.dat");

// Escritura para respaldo
gestor.exportarBinario("backup.dat");
```

---

## Características Técnicas

✅ **HQL Puro** - Sin SQL crudo, sin PreparedStatement  
✅ **DAO Genérico** - Reutilizable para cualquier entidad  
✅ **findBy()** - Búsquedas con parámetros seguros  
✅ **Relación 1:N** - Proyectos con componentes  
✅ **CascadeType.ALL** - Guardado automático en relaciones  
✅ **Transacciones** - rollback en excepciones  
✅ **Múltiples Formatos** - XML, CSV, TXT, DAT  
✅ **Consola** - Sin GUI, todo por menú interactivo  

---

## Archivos de Ejemplo

### datos.xml
```xml
<proyecto>
    <nombre>Robotica Basica</nombre>
    <autor>Maria</autor>
    <componentes>
        <componente>
        <nombreComp>Motor DC</nombreComp>
            <precio>12.50</precio>
        </componente>
    </componentes>
</proyecto>
```

### datos.csv
```
Proyecto IA,Juan
Videojuego 3D,Maria
App Móvil,Anonimo
```

### datos.txt
```
# Archivo de Proyectos STEAM
# Formato: nombre|autor
Inteligencia Artificial|Carlos
Aplicación Web|Sofia
```

---

## Notas Importantes

⚠️ **Cambiar credenciales MySQL en `hibernate.cfg.xml`**  
⚠️ **Los archivos de entrada deben estar en la raíz del proyecto**  
⚠️ **Los archivos exportados se generan también en la raíz**  
⚠️ **Java 8+ compatible**  

---

## Puntos Clave para Defenderlo en Examen

1. **DAO Genérico** - Explica `<T>` y reutilización
2. **HQL** - Muestra cómo evita SQL crudo
3. **findBy()** - Búsquedas dinámicas seguras
4. **CascadeType.ALL** - Explica ahorro de código
5. **Múltiples Formatos** - Demuestra versatilidad
6. **Transacciones** - Explain commit/rollback
7. **Lectura de Subnodos** - Muestra comprensión de XML

---

**Código preparado para defender en examen. ¡Sota, Caballo y Rey!** 🚀
