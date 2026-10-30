# Módulo de Identificación por Códigos QR - SIGELAB

**Universidad Mariano Gálvez de Guatemala**  
**Curso:** 017 - Programación II  
**Proyecto Final:** Sistema de Gestión y Control de Laboratorios (SIGELAB)  

---

## 1. Propósito del Módulo
En el sistema **SIGELAB**, la integración de códigos **QR** cumple con un propósito específico y controlado, alineado estrictamente a la guía oficial del curso: **identificar y cargar rápidamente un equipo dentro de los procesos transaccionales del sistema** (como en los módulos de Préstamos y Mantenimiento), utilizando su código único de inventario.

---

## 2. Especificaciones Técnicas y Librerías

* **Librería Externa:** Se implementó la librería oficial de Google **ZXing (Zebra Crossing)** a través de Maven (`core` y `javase`), la cual permite la codificación y decodificación de códigos de barras bidimensionales (QR).
* **Alcance de la Funcionalidad:** Conforme a la regla de negocio **RN-19**, el código QR actúa exclusivamente como un mecanismo de lectura y búsqueda rápida. **El QR por sí solo no modifica ningún estado en la base de datos**; su función es extraer el identificador único del activo para que la capa de servicio valide su disponibilidad.

---

## 3. Flujo de Ejecución y Operación

1. **Generación del QR:**
   * Al registrar un nuevo equipo en el inventario, el sistema genera automáticamente un identificador QR único que almacena el código de inventario (ej. `SIGELAB-EQ:INV-001`). Estos códigos se almacenan localmente para su impresión o gestión digital.
2. **Lectura mediante Carga de Imagen:**
   * En las vistas de **PrestamoView** y **MantenimientoEquipos**, el usuario dispone de la opción **"Cargar QR"**, la cual despliega un explorador de archivos (`JFileChooser`) para seleccionar la imagen del código QR del equipo.
3. **Decodificación y Procesamiento (Java):**
   * La aplicación convierte la imagen seleccionada en una matriz de píxeles (`BufferedImage`), y mediante las clases `RGBLuminanceSource` y `HybridBinarizer` de ZXing, decodifica el texto plano cifrado.
   * Un método de extracción limpia la cadena institucional para obtener el código exacto de inventario.
4. **Consulta y Validación de Negocio:**
   * El sistema realiza una consulta a la base de datos a través del repositorio y la capa de servicios.
   * Si el equipo está **`DISPONIBLE`**, se selecciona o agrega automáticamente al préstamo/taller.
   * Si el equipo se encuentra **`PRESTADO`**, **`DAÑADO`**, en **`MANTENIMIENTO`** o **`DADO_DE_BAJA`**, el sistema lo identifica de inmediato pero **bloquea la operación** emitiendo una advertencia en pantalla (`RN-08`, `RN-19`).

---

## 4. Casos de Prueba Asociados (Rúbrica Oficial)

| Caso de Prueba | Módulo | Descripción de la Prueba | Resultado Esperado |
| :--- | :--- | :--- | :--- |
| **CP-09** | Código QR | Escanear o cargar el QR de un equipo **DISPONIBLE**. | **Equipo identificado con éxito** y disponible para agregarse al préstamo o taller. |
| **CP-10** | Código QR | Escanear o cargar el QR de un equipo **PRESTADO** o no disponible. | **Equipo identificado pero bloqueado**, mostrando una advertencia de restricción de estado. |

---

## 5. Fragmento Clave de Implementación (Referencia Técnica)

```java
// Método implementado en las vistas para la decodificación de QR con ZXing
BufferedImage imagen = javax.imageio.ImageIO.read(archivoSeleccionado);
int ancho = imagen.getWidth();
int alto = imagen.getHeight();
int[] pixels = imagen.getRGB(0, 0, ancho, alto, null, 0, ancho);

com.google.zxing.LuminanceSource source = new com.google.zxing.RGBLuminanceSource(ancho, alto, pixels);
com.google.zxing.BinaryBitmap bitmap = new com.google.zxing.BinaryBitmap(new com.google.zxing.common.HybridBinarizer(source));

com.google.zxing.Result resultadoQR = new com.google.zxing.MultiFormatReader().decode(bitmap);
String codigoLeido = resultadoQR.getText();