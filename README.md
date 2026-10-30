# SIGELAB - Sistema de Gestión y Control de Laboratorios Universitarios

**Universidad Mariano Gálvez de Guatemala**  
**Facultad de Ingeniería en Sistemas**  
**Curso:** 017 - Programación II  
**Catedrático:** Ing. Alejandro Díaz  
**Ciclo:** Segundo Semestre 2026  

---

## 👥 1. Nombre del Proyecto e Integrantes
* **Nombre del Proyecto:** Sistema de Gestión y Control de Laboratorios Universitarios (SIGELAB)
* Integrantes del Grupo:
* JOSÉ ANTONIO GONZALEZ REYES -------------------------3590-25-4454
* DERECK ABRAHAM CARRANZA ACIFUINA----------------3590-25-760
* JEREMY ALEXANDER PAIZ CASTILLO----------------------3590-25-13128
* EVI MERARI ELIZABETH SEQUÉN JIMÉNEZ -------------3590-25-3900

---

## 💻 2. Versiones Requeridas de Java y SQL Server
* **Java:** JDK 17 o superior (Se recomienda OpenJDK 17 o Temurin).
* **Base de Datos:** Microsoft SQL Server (Versión 2019 o superior, compatible con SQL Server Express).

---

## 📦 3. Dependencias Externas Necesarias (`pom.xml`)
El proyecto gestiona sus dependencias mediante **Maven**. Las librerías externas integradas obligatoriamente son:
* **Conector JDBC para SQL Server:** `com.microsoft.sqlserver:mssql-jdbc`
* **Librería para Códigos QR:** `com.google.zxing:core` y `com.google.zxing:javase`
* **Control de Fechas (Calendarios):** `com.toedter:jcalendar`
* **Interfaz Gráfica (Look and Feel Moderno):** `com.formdev:flatlaf`
* **Visión Artificial / Biometría:** Python 3.x con OpenCV (`opencv-python`) para el componente facial.

---

## 🗄️ 4. Pasos para Crear la Base de Datos
1. Abra **SQL Server Management Studio (SSMS)** o su gestor de bases de datos preferido conectado a su instancia de SQL Server.
2. Cree una nueva base de datos ejecutando el comando:
   ```sql
   CREATE DATABASE SIGELAB_DB;
   GO
   USE SIGELAB_DB;
   GO
   ```
3. Ejecute el script DDL completo provisto en el archivo del modelo de datos para crear todas las tablas institucionales requeridas: `Usuario`, `PerfilFacial`, `AuditoriaFacial`, `Laboratorio`, `TipoEquipo`, `Equipo`, `Prestamo`, `DetallePrestamo`, `Mantenimiento` y `Auditoria`.

---

## 🔌 5. Configuración de Conexión
1. Ubique la clase encargada de la conexión a la base de datos dentro del paquete `SIGELAB.config` (ej. `DatabaseConnection.java`).
2. Verifique y ajuste los parámetros de conexión según su entorno local o servidor:
   ```java
   private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=SIGELAB_DB;encrypt=true;trustServerCertificate=true;";
   private static final String USER = "tu_usuario_sql";
   private static final String PASSWORD = "tu_contraseña_sql";
   ```

---

## ⚙️ 6. Cómo Ejecutar la Aplicación
1. Clone o descargue el repositorio del proyecto en su equipo local.
2. Abra el proyecto utilizando un entorno de desarrollo compatible con Java y Maven (como **IntelliJ IDEA** o **Eclipse**).
3. Espere a que Maven descargue y sincronice automáticamente todas las dependencias declaradas en el archivo `pom.xml`.
4. Ubique la clase principal de inicio con interfaz gráfica de usuario (`LoginForm.java`) dentro del paquete `SIGELAB.view`.
5. Ejecute la clase para lanzar el sistema de escritorio.

---

## 👤 7. Cómo Ejecutar el Componente Facial
El subsistema de reconocimiento facial opera como un segundo factor obligatorio en el inicio de sesión:
1. Asegúrese de tener instalado **Python 3.x** en su sistema operativo.
2. Instale la librería OpenCV ejecutando en su terminal:
   ```bash
   pip install opencv-python
   ```
3. Verifique que la cámara web de su equipo se encuentre conectada y disponible.
4. Al ingresar credenciales correctas en el `LoginForm` de Java, el sistema invocará de forma automatizada el script de verificación facial conectado a la tabla `PerfilFacial` y `AuditoriaFacial` en la base de datos.

---

## 🔑 8. Usuarios de Prueba
### 👤 Comportamiento en el Primer Ingreso (Sin Perfil Facial Registrado)
Si un usuario intenta iniciar sesión por primera vez y el sistema detecta mediante la consulta a la base de datos que aún no posee un registro en la tabla `PerfilFacial` (`perfil == null`), el sistema gestiona la situación de manera controlada:
1. **Asistente de Registro (Enrollment):** Se notifica al operador que no se encuentra biometría asociada a su cuenta y se le da la opción de realizar el enrolamiento de su rostro por única vez.
2. **Validación posterior:** Una vez registrado el embedding inicial, los subsiguientes accesos requerirán obligatoriamente la coincidencia biométrica exitosa (`VERIFY_EXITOSO`) para otorgar el ingreso al sistema (`RN-02`).
El sistema incluye por defecto las siguientes cuentas sembradas en la base de datos para pruebas institucionales:
* **Administrador (Acceso Completo):**
  * *Usuario:* `admin`
  * *Contraseña:* `admin`
* **Encargado (Acceso Operativo Restringido):**
  * *Usuario:* `evi`
  * *Contraseña:* `encargado123`

---

## 📊 9. Funciones Principales del Sistema
* **Autenticación Biométrica:** Doble factor de seguridad con usuario, contraseña y reconocimiento facial en el login.
* **Control de Accesos por Roles:** Adaptación dinámica del menú según el perfil (`ADMINISTRADOR` o `ENCARGADO`) con validación estricta en la capa de negocio.
* **Gestión de Inventario y QR:** Administración de laboratorios y equipos con sus 5 estados obligatorios (`DISPONIBLE`, `PRESTADO`, `DAÑADO`, `MANTENIMIENTO`, `DADO_DE_BAJA`) e identificación rápida mediante códigos **QR**.
* **Préstamos Multiequipo y Devoluciones:** Encabezado y detalle para préstamos, control de devoluciones parciales/totales y estados de condición (`BUENO`, `DAÑADO`).
* **Mantenimiento y Taller:** Registro y seguimiento de equipos enviados a mantenimiento correctivo o preventivo.
* **Auditoría y Reportes:** Trazabilidad institucional de eventos críticos y consultas con filtros avanzados por fechas y estados.
