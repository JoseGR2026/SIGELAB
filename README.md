# SIGELAB - Sistema de Gestión y Control de Laboratorios Universitarios

**Universidad Mariano Gálvez de Guatemala**  
**Curso:** 017 - Programación II  
**Catedrático:** Ing. Alejandro Díaz  
**Ciclo:** Segundo Semestre 2026  

---

## 📋 Descripción del Proyecto
**SIGELAB** es una aplicación de escritorio desarrollada en **Java Swing** bajo una estricta arquitectura por capas (`View` -> `Controller` -> `Service` -> `Repository` -> `JDBC`), con persistencia en **Microsoft SQL Server**. El sistema está diseñado para administrar de forma íntegra el ciclo de vida de los activos tecnológicos en laboratorios universitarios, abarcando inventario, códigos QR, préstamos con múltiples equipos, devoluciones parciales/totales, control de taller y mantenimiento, auditoría institucional y un sistema de autenticación de doble factor mediante **reconocimiento facial (Python + OpenCV)**.

---

## 🚀 Características Principales y Reglas de Negocio

1. **Autenticación y Seguridad Biométrica:**
   * Inicio de sesión validando credenciales de usuario, contraseña y estado activo (`RN-01`).
   * Segundo factor obligatorio de **reconocimiento facial** consumiendo un script local de Python con OpenCV (`RN-02`).
2. **Control de Accesos por Roles:**
   * **Administrador:** Acceso completo a todo el sistema (usuarios, catálogos, equipos, préstamos, devoluciones, mantenimiento, auditoría y reportes).
   * **Encargado:** Operación restringida a inventario, préstamos, devoluciones, mantenimiento y consultas (con avisos automáticos para funciones de nivel superior).
3. **Gestión de Equipos y Estados:**
   * Control estricto de 5 estados obligatorios: `DISPONIBLE`, `PRESTADO`, `DAÑADO`, `MANTENIMIENTO` y `DADO_DE_BAJA` (`RN-08`).
   * Identificación rápida de equipos mediante códigos **QR** generados con la librería ZXing (`RN-19`).
4. **Préstamos y Devoluciones:**
   * Registro de préstamos de uno o varios equipos mediante encabezado y tabla de detalle (`RN-05`, `RN-06`).
   * Cambio automático de estados: al prestar un equipo pasa a `PRESTADO` (`RN-09`); al devolverlo en buen estado regresa a `DISPONIBLE` (`RN-12`) o `DAÑADO` si presenta averías (`RN-13`).
   * Devoluciones parciales y seguimiento de préstamos vencidos (`RN-16`, `RN-17`).
5. **Mantenimiento y Auditoría:**
   * Envío de equipos dañados a taller para mantenimiento preventivo o correctivo (`RN-14`).
   * Cierre de mantenimiento con transición automática a `DISPONIBLE` (si fue reparado) o `BAJA` (si es irreparable) (`RN-15`).
   * Registro transversal de auditoría y eventos biométricos en base de datos (`RN-18`).

---

## 🛠️ Tecnologías y Arquitectura

* **Lenguaje Principal:** Java (JDK 17+)
* **Interfaz Gráfica:** Java Swing (con FlatLaf para diseño moderno)
* **Base de Datos:** Microsoft SQL Server (JDBC)
* **Gestión de Dependencias:** Maven (`pom.xml`)
* **Control de Versiones:** Git / GitHub (con commits incrementales y trazables)
* **Librerías Externas:** 
  * `ZXing` (Google) para generación y lectura de códigos QR.
  * `JCalendar` para control de fechas de devolución.
  * `Python + OpenCV` para el subsistema de reconocimiento facial local.

---

## 📂 Estructura del Proyecto (Arquitectura Limpia)

* `SIGELAB.config`: Configuración de la conexión a SQL Server (`DatabaseConnection`).
* `SIGELAB.controller`: Coordinación de acciones entre vistas y servicios.
* `SIGELAB.model`: Clases de entidad (Modelos y Enums como `EstadoEquipo` y `RolUsuario`).
* `SIGELAB.repository`: Consultas SQL directas y transaccionales.
* `SIGELAB.service`: Lógica de negocio y validaciones estrictas independientes de la interfaz (`RN-20`).
* `SIGELAB.view`: Formularios y componentes gráficos en Java Swing.

---

## ⚙️ Configuración y Ejecución

### 1. Base de Datos (SQL Server)
1. Ejecute el script SQL completo para crear la base de datos `SIGELAB_DB` y sus tablas correspondientes (`Usuario`, `PerfilFacial`, `AuditoriaFacial`, `Laboratorio`, `TipoEquipo`, `Equipo`, `Prestamo`, `DetallePrestamo`, `Mantenimiento`, `Auditoria`).
2. Verifique las credenciales de conexión en la clase `DatabaseConnection.java`.

### 2. Ejecución de la Aplicación Java
1. Abra el proyecto en su IDE (IntelliJ IDEA o Eclipse).
2. Asegúrese de que Maven haya descargado todas las dependencias del archivo `pom.xml`.
3. Ejecute la clase principal de inicio (`LoginForm.java`).

### 3. Configuración del Reconocimiento Facial (Python)
1. Asegúrese de tener Python instalado con la librería OpenCV (`pip install opencv-python`).
2. Configure la ruta de acceso al script de verificación facial en la capa de servicio de autenticación de Java.

---

## 👥 Usuarios de Prueba
* **Administrador:** `admin` / `admin123` (Acceso total)
* **Encargado:** `encargado` / `encargado123` (Acceso operativo restringido)
