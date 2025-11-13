# SID Gym ICESI – Proyecto de Persistencia Políglota

Aplicación web desarrollada con Spring Boot que permite a estudiantes y colaboradores crear y registrar sus rutinas de entrenamiento, llevar el control de su progreso, recibir retroalimentación de entrenadores certificados y gestionar la relación usuario–entrenador en el contexto del bienestar universitario de la “Universidad ICESI”.   

Proyecto académico para la asignatura **Sistemas Intensivos en Datos II (SID II)**, Universidad ICESI, enfocado en el diseño e implementación de una **arquitectura de persistencia polígloga** basada en **PostgreSQL** (BD relacional institucional) y **MongoDB** (BD NoSQL para la información operativa). 

---

### Integrantes
- **Luna Catalina Martínez Vasquez** - [LunaKtalina](https://github.com/LunaKtalina) - A00401964
- **Angy María Hurtado Osorio** - [LissaAN1](https://github.com/LissaAN1) - A00401755
- **Daniel Esteban Arcos Cerón** - [Darcos23](https://github.com/Darcos23) - A00400760
- **Hideki Tamura Hernández** - [Hisansei](https://github.com/Hisansei) - A00348618
- **Renzo Fernando Mosquera Daza** - [RenzoFernando](https://github.com/RenzoFernando) - A00401681

---

## Contexto y objetivos del proyecto

La Universidad Cali busca una plataforma que integre tecnología, salud y comunidad, permitiendo a la comunidad universitaria: crear rutinas de entrenamiento, registrar progreso, conectarse con entrenadores y acceder a actividades de bienestar en tiempo real. 

A partir del enunciado oficial del proyecto se identifican los siguientes objetivos principales: 

- Permitir el **inicio de sesión con cuenta institucional** (estudiantes, colaboradores, entrenadores) usando la información almacenada en **PostgreSQL**.
- Gestionar un **catálogo de ejercicios** con:
  - Nombre  
  - Tipo (cardio, fuerza, movilidad)  
  - Descripción  
  - Duración  
  - Dificultad  
  - Videos demostrativos
- Permitir a los usuarios:
  - Registrar y gestionar sus **rutinas de ejercicio**.
  - Usar ejercicios predefinidos o crear ejercicios personalizados.
  - Registrar su **progreso diario o semanal** (repeticiones, tiempo, nivel de esfuerzo, etc.).
- Permitir a los entrenadores:
  - Visualizar rutinas y progreso de sus usuarios asignados.
  - Crear **rutinas prediseñadas** (plantillas) para que los usuarios las adopten y ajusten.
  - Registrar nuevos ejercicios.
- Incluir un **módulo de administración** para gestionar la asignación de entrenadores a usuarios.
- En la BD relacional:
  - Crear tablas de **estadísticas mensuales** tanto para usuarios como para instructores (rutinas iniciadas, seguimientos, asignaciones nuevas).   
- Implementar **al menos dos informes** que aporten valor al usuario final.

---

## Requerimientos implementados

### Requerimientos funcionales

De acuerdo con el enunciado y el informe de justificación técnica de MongoDB, la aplicación implementa, entre otros, los siguientes requerimientos: 

1. **Autenticación con credenciales institucionales**
   - Los usuarios inician sesión utilizando el usuario institucional definido en la tabla `USERS` de la BD PostgreSQL. 

2. **Gestión de ejercicios (`exercises`)**
   - Consulta de ejercicios, filtrando por nombre, tipo y dificultad.
   - Creación de nuevos ejercicios (principalmente por entrenadores).
   - Ejercicios iniciales cargados desde `exercises.json`, con campos:
     - `_id`, `name`, `type`, `description`, `duration_seconds`, `difficulty`, `status`, `demo_videos`.

3. **Rutinas de usuario (`user_routines`)**
   - Creación y gestión de rutinas personalizadas por usuario.
   - Adopción y ajuste de **rutinas prediseñadas** (`routine_templates`) creadas por entrenadores. 

4. **Registro de progreso (`progress_logs`)**
   - Registro de sesiones de entrenamiento, con detalle por ejercicio.
   - Consultas de progreso histórico filtradas por usuario y fecha. 

5. **Módulo de administración**
   - Gestión de **asignaciones de entrenadores a usuarios** mediante la colección `trainer_assignments` y servicios asociados.   

6. **Informes**
   - **Reporte de consistencia de entrenamiento** por semana para un usuario (número de sesiones completadas y tasa de consistencia). 
   - **Reporte de tipos de ejercicio usados** por un usuario (distribución de tipos de ejercicio ejecutados a partir de los registros de progreso).   

### Requerimientos no funcionales

Basado en la justificación de MongoDB:

- **Escalabilidad**: MongoDB permite escalar horizontalmente la parte operativa de rutinas y progresos.
- **Rendimiento**: Se utilizan índices específicos por colección para acelerar las consultas frecuentes.
- **Consistencia y flexibilidad**: Modelo orientado a documentos que admite campos opcionales y estructuras heterogéneas sin necesidad de migraciones constantes.
- **Mantenibilidad**: Separación clara entre datos institucionales (relacional) y datos operativos (NoSQL) facilita el mantenimiento y la evolución del sistema.

---

## Arquitectura de la solución

### Stack tecnológico

La aplicación sigue una arquitectura **Spring Boot MVC** con capas bien definidas: 

- **Lenguaje:** Java 17.
- **Framework backend:** Spring Boot 3.3.4.
- **Patrón de presentación:** Spring MVC + Thymeleaf.
- **Seguridad:** Spring Security 6, con:
  - Autenticación basada en usuarios de PostgreSQL.
  - Integración con `CustomUserDetailsService`.
  - Soporte para JWT mediante `JwtServiceImpl` y filtros `JwtAuthenticationFilter` y `CustomAuthenticationFilter`.
- **Persistencia relacional:** Spring Data JPA + PostgreSQL.
- **Persistencia NoSQL:** Spring Data MongoDB.
- **Mappers:** MapStruct.
- **Utilidades:** Lombok, Jackson (JSR310), validación por anotaciones.
- **Empaquetado:** WAR (compatible con despliegue en contenedores de aplicaciones).

### Capas lógicas

La organización de paquetes refleja una arquitectura por capas: 

- `controller.mvc` y `controller.mvc.mongo`  
  Controladores MVC responsables de la lógica de interacción con el usuario y el enrutamiento de vistas (Thymeleaf).
- `services` e `impl`  
  Servicios de dominio (interfaces) y sus implementaciones, donde se agrupa la lógica de negocio.
- `model.postgres` / `model.mongo`  
  Entidades JPA para PostgreSQL y documentos para MongoDB, respectivamente.
- `repository.postgres` / `repository.mongo`  
  Repositorios Spring Data para cada tecnología de persistencia.
- `config`  
  Configuraciones de Spring: seguridad, perfiles, conexión a MongoDB, inicialización de datos y creación de índices.

### Estructura de directorios

```text
sid-gym-icesi/
├── HELP.md
├── README.md
├── mvnw.cmd
├── pom.xml
└── src
    ├── main
    │   ├── java
    │   │   └── co
    │   │       └── edu
    │   │           └── icesi
    │   │               └── sidgymicesi
    │   │                   ├── config
    │   │                   ├── controller
    │   │                   │   ├── mvc
    │   │                   │   │   ├── mongo
    │   │                   │   │   └── postgres
    │   │                   │   └── rest
    │   │                   │       ├── mongo
    │   │                   │       └── postgres
    │   │                   ├── model
    │   │                   │   ├── mongo
    │   │                   │   └── postgres
    │   │                   ├── repository
    │   │                   │   ├── mongo
    │   │                   │   └── postgres
    │   │                   ├── security
    │   │                   │   └── filters
    │   │                   ├── services
    │   │                   │   ├── impl
    │   │                   │   │   ├── mongo
    │   │                   │   │   └── postgres
    │   │                   │   ├── mongo
    │   │                   │   └── postgres
    │   │                   ├── util
    │   │                   └── web
    │   └── resources
    │       ├── application-mongo.properties
    │       ├── application-postgres.properties
    │       ├── application.properties
    │       ├── db
    │       │   ├── mongo
    │       │   └── postgres
    │       ├── graphql-client
    │       ├── static
    │       │   ├── css
    │       │   │   ├── auth
    │       │   │   ├── components
    │       │   ├── img
    │       │   └── js
    │       │       ├── components
    │       └── templates
    │           ├── admin
    │           │   ├── assignments
    │           │   └── users
    │           ├── auth
    │           ├── components
    │           ├── exercises
    │           ├── progress
    │           ├── reports
    │           ├── routine
    │           └── routine-templates
```

---

## Persistencia polígloga

La solución combina dos motores de base de datos con responsabilidades claramente diferenciadas: 

- **PostgreSQL (Neon):**  
  Almacena la información institucional de la Universidad (estudiantes, empleados, programas, materias, grupos, usuarios institucionales) y las **estadísticas agregadas mensuales** para usuarios e instructores.
- **MongoDB (Atlas):**  
  Almacena la información operativa y flexible relacionada con ejercicios, rutinas, progreso y asignaciones de entrenadores, con alta frecuencia de lectura y escritura.

### Base de datos relacional (PostgreSQL – Neon)

La BD relacional se construye a partir de los scripts provistos:

- `university_schema_postgresql.sql`  
- `university_full_data_postgresql.sql`  

Estos scripts definen tablas tales como `FACULTIES`, `CAMPUSES`, `EMPLOYEES`, `AREAS`, `PROGRAMS`, `SUBJECTS`, `GROUPS`, `STUDENTS`, `ENROLLMENTS` y el **módulo de usuarios** (`USERS`) con sus relaciones y datos de ejemplo.

Además, se crean dos tablas adicionales para estadísticas mensuales, según el enunciado:

- `user_monthly_stats`  
  - `user_username`  
  - `month` (DATE, primer día del mes)  
  - `routines_started`  
  - `progress_logs_count`  

- `trainer_monthly_stats`  
  - `trainer_employee_id`  
  - `month` (DATE)  
  - `new_assignments_count`  
  - `followups_count`  

Estas tablas permiten medir, por mes, la actividad de usuarios e instructores. 

La configuración de conexión a PostgreSQL (Neon) se encuentra en `application-postgres.properties`:

```properties
spring.datasource.url=jdbc:postgresql://ep-mute-brook-ah5opgvh-pooler.c-3.us-east-1.aws.neon.tech:5432/sid_gym_icesi?sslmode=require
spring.datasource.username=neondb_owner
spring.datasource.password=npg_UVx7ysgjo0YQ

spring.jpa.hibernate.ddl-auto=none
spring.sql.init.schema-locations=classpath:db/postgres/university_drops_postgresql.sql,classpath:db/postgres/university_schema_postgresql.sql
spring.sql.init.data-locations=classpath:db/postgres/university_full_data_postgresql.sql
```


### Base de datos NoSQL (MongoDB – Atlas)

La configuración de MongoDB se define en `application-mongo.properties`:

```properties
spring.data.mongodb.uri=mongodb+srv://renzofernandomd_db_user:YrNwANsqOC85YoRJ@clustersid.1e80tft.mongodb.net/?retryWrites=true&w=majority&appName=SidGymIcesi
spring.data.mongodb.database=sid_gym_icesi
spring.data.mongodb.auto-index-creation=true
spring.mongodb.uuid-representation=standard

app.mongo.init.drop=true
app.mongo.init.location=classpath:db/mongo/*.json
```

El perfil `mongo` habilita:

- `MongoConfig`: configuración del `MongoTemplate` y repositorios Spring Data. 
- `MongoDataInitializer`: carga inicial de datos desde archivos JSON (`db/mongo/*.json`) para colecciones como `exercises`, `routine_templates`, `user_routines`, `progress_logs`, `trainer_assignments`.  
- `MongoSchemaAndIndexesRunner`: creación explícita de índices en las colecciones clave.   

#### Colecciones principales y modelo de datos

Basado en el informe de justificación y en el código de configuración, el modelo de MongoDB se organiza así: 
1. **`exercises`**
   - Documentos reutilizables que representan el catálogo de ejercicios.
   - Campos básicos:
     - `_id` (identificador textual, ej. `ex_pushups`)
     - `name`, `type`, `description`
     - `duration_seconds`, `difficulty`
     - `status` (activo/inactivo)
     - `demo_videos` (lista de URLs).
   - Índices:
     - Único por `name` (`idx_ex_name`)
     - Por `type` (`idx_ex_type`)
     - Por `difficulty` (`idx_ex_diff`).

2. **`routine_templates`**
   - Plantillas de rutina creadas por entrenadores.
   - Campos:
     - `name`, `description`, `level`, `status`
     - `trainer_id` (referencia a empleado/instructor)
     - `updated_at`
     - Lista de ejercicios como referencias (patrón **Extended Reference**), con orden, series, repeticiones, descanso, etc. fileciteturn23file11turn23file7  
   - Índices:
     - Por `status`
     - Por `trainer_id`.

3. **`user_routines`**
   - Rutinas personalizadas de cada usuario.
   - Campos:
     - `ownerUsername`
     - `name`, `description`, `level`, `status`
     - `created_at`, `updated_at`
     - Lista embebida de ejercicios con parámetros específicos para esa rutina.
   - Índices:
     - Por `ownerUsername`
     - Por `created_at`.  

4. **`progress_logs`**
   - Registro de sesiones de entrenamiento.
   - Campos:
     - `ownerUsername`, `routine_id`
     - `date`
     - `entries`: detalles por ejercicio (sets, repeticiones, peso, esfuerzo, observaciones, etc.).
   - Índices:
     - Compuesto `(routine_id, date)`
     - Compuesto `(ownerUsername, date)`
     - Por `date`. 

5. **`trainer_assignments`**
   - Gestión de la relación entrenador–usuario.
   - Campos:
     - `trainer_id`
     - `user_username`
     - `assigned_at`
     - `active`
   - Índices:
     - Por `trainer_id`
     - Por `user_username`
     - Compuesto `(user_username, active)`. 

Este diseño aplica los patrones de modelado recomendados por MongoDB y se justifica en el informe técnico como la mejor alternativa NoSQL para los requerimientos del proyecto. 

---

## Funcionalidades de la aplicación web

### 1. Autenticación y seguridad

- **Inicio de sesión**:
  - Los usuarios se autentican mediante credenciales registradas en la tabla `USERS` de PostgreSQL.
- **Spring Security**:
  - Configuración centralizada en `WebSecurityConfig`, con:
    - `CustomUserDetailsService`.
    - `DaoAuthenticationProvider`.
    - `JwtAuthenticationFilter` y `CustomAuthenticationFilter` en la cadena de filtros.
  - Enfoque en sesiones HTTP para la parte MVC y soporte JWT para integración futura con APIs REST.  

### 2. Gestión de ejercicios

Controlador: `ExerciseMVCController` (`/mvc/exercises`).

- **Catálogo de ejercicios**:
  - Listado de todos los ejercicios de `exercises`.
  - Filtros opcionales por:
    - Texto de búsqueda (`q`) sobre el nombre.
    - Tipo (`type`).
    - Dificultad (`difficulty`).
- **Creación de ejercicios**:
  - Formulario para registrar nuevos ejercicios.
  - Conversión de un campo de texto multi-línea en una lista de URLs de video (`demo_videos`).
- **Edición y eliminación** (según roles):
  - Los entrenadores pueden mantener actualizado el catálogo, evitando duplicados gracias al índice único por nombre.

### 3. Rutinas prediseñadas y rutinas de usuario

Se manejan dos controladores principales (no se incluyen aquí todas las rutas para mantener el README manejable, pero el código las define en `controller.mvc.mongo`).   

- **Plantillas de rutina (`routine_templates`)**:
  - Creación por parte de entrenadores.
  - Inclusión de ejercicios mediante referencias (ID de `exercises`) y parámetros como series, repeticiones, descanso.
  - Control de estado (activa/inactiva) para facilitar el catálogo visible a los usuarios.

- **Rutinas de usuario (`user_routines`)**:
  - Los usuarios pueden:
    - Crear rutinas desde cero.
    - **Adoptar** una plantilla prediseñada y ajustarla a sus necesidades (nombre, nivel, ejercicios, etc.).
  - Las rutinas almacenan directamente la lista de ejercicios con sus parámetros, optimizando las lecturas de la vista de rutina y del registro de progreso.

### 4. Registro de progreso

Controlador: `ProgressLogMVCController` (rutas `/mvc/progress`, etc.).  

- Permite registrar sesiones de entrenamiento asociadas a una rutina y a un usuario.
- Cada registro contiene:
  - Fecha de la sesión.
  - Detalle por ejercicio (ejercicio, sets, repeticiones, peso, esfuerzo, comentarios).
- Las consultas se apoyan en los índices por usuario y fecha para mostrar el historial reciente.

### 5. Asignación de entrenadores

A través de la colección `trainer_assignments` y los servicios correspondientes, el sistema mantiene el vínculo entre usuario y entrenador. 

- Un usuario puede tener un entrenador activo asociado.
- El módulo de administración permite:
  - Crear nuevas asignaciones.
  - Marcar asignaciones como inactivas manteniendo historial (soft delete mediante campo `active`).

### 6. Informes

Controlador: `ReportController` (`/mvc/reports`). 

Implementa dos informes principales a partir de los servicios de MongoDB (`IReportService` + `ReportServiceImpl`):

1. **Reporte de consistencia de entrenamiento (`generateConsistencyReport`)**
   - Entrada:
     - `username`
     - `numberOfWeeks`
   - Lógica:
     - Recupera todos los `ProgressLog` del usuario.
     - Agrupa por semana (usando `WeekFields`).
     - Cuenta sesiones por semana y calcula una **tasa de consistencia** sobre 7 días.
   - Salida:
     - Lista de semanas ordenadas (más recientes primero), cada una con:
       - Fecha de inicio de la semana (`weekStartDate`).
       - Número de sesiones completadas (`sessionsCompleted`).
       - Porcentaje de consistencia (`consistencyRate`). 

2. **Reporte de tipos de ejercicio usados (`generateExerciseTypeReport`)**
   - Entrada:
     - `username`
   - Lógica:
     - Recupera las rutinas del usuario (`user_routines`) para construir un mapa `exerciseId -> type`.
     - Recupera todos los registros de progreso del usuario (`progress_logs`).
     - Cuenta cuántas veces se ha ejecutado cada tipo de ejercicio a partir de las entradas de progreso.
   - Salida:
     - Mapa `{tipoEjercicio -> cantidad de veces ejecutado}`.

Ambos informes se muestran mediante vistas Thymeleaf dedicadas (`reports/consistencyReport.html`, `reports/exerciseTypeReport.html`), accesibles desde la interfaz web.

---

## Manejo de errores y validaciones

El sistema aplica manejo de errores tanto en la capa de presentación como en la lógica de negocio:

- **Validaciones en controladores MVC**
  - En `ReportController`, si el usuario no selecciona `username`, se muestra un mensaje de error amigable y se retorna a la vista de selección.  
  - En `ExerciseMVCController`, se valida el contenido de los campos y se gestionan las listas de videos, evitando entradas vacías.  

- **Servicios**
  - Los servicios de dominio encapsulan la lógica de negocio y pueden lanzar excepciones específicas que son tratadas en los controladores, mostrando mensajes claros al usuario.

- **Seguridad**
  - `JwtServiceImpl` maneja errores en la validación de tokens, devolviendo `false` ante cualquier token inválido o malformado, evitando lanzar excepciones hacia la capa de presentación.

- **Integridad referencial en PostgreSQL**
  - La carga de datos inicial se realiza en el orden correcto para respetar las claves foráneas (facultades, campus, empleados, áreas, programas, materias, grupos, estudiantes, matrículas, usuarios).

---

## Configuración y ejecución

### Perfiles activos

En `application.properties` se activan por defecto los perfiles de bases de datos:

```properties
spring.application.name=sid-gym-icesi
spring.profiles.active=postgres, mongo

server.port=8081
server.servlet.context-path=/sid-gym-icesi
```



Esto implica que, para ejecutar la aplicación, deben estar disponibles tanto la BD PostgreSQL (Neon) como la BD MongoDB (Atlas o contenedores locales vía Docker Compose).

### Requisitos previos

- Java 17 instalado.
- Maven (o uso del wrapper `mvnw`).
- Acceso a:
  - Instancia de PostgreSQL configurada con los scripts del proyecto.
  - Clúster de MongoDB (Atlas o local).


### Comandos básicos de ejecución

1. **Compilar el proyecto**

```bash
./mvnw clean package
```

2. **Ejecutar con Spring Boot y perfiles por defecto**

```bash
./mvnw spring-boot:run
```

(Usando `spring.profiles.active=postgres,mongo` definidos en `application.properties`.)

3. **Acceder a la aplicación**

- URL base: `http://localhost:8081/sid-gym-icesi/`

Las rutas MVC principales se encuentran bajo:

- `/mvc/exercises`
- `/mvc/routines` 
- `/mvc/templates` 
- `/mvc/progress`
- `/mvc/reports`


---
