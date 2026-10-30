# Cambios de auditoría en SIGELAB

Se modificó únicamente el registro de eventos relacionados con auditoría y los pasos necesarios para identificar al usuario que realiza las operaciones.

## Eventos registrados
- `LOGIN_EXITOSO`: después de validar contraseña y verificación facial.
- `LOGIN_FALLIDO`: credenciales incorrectas, usuario inactivo o verificación facial no coincidente.
- `PRESTAMO_CREADO`: al registrar un préstamo.
- `EQUIPO_DEVUELTO`: al registrar la devolución.
- `EQUIPO_ENVIADO_MANTENIMIENTO`: al enviar un equipo a mantenimiento.
- `MANTENIMIENTO_FINALIZADO`: al cerrar el mantenimiento, tanto si el equipo queda disponible como si se da de baja.
- `USUARIO_DESACTIVADO`: al desactivar una cuenta.
- Se conserva `MARCAR_VENCIDO`.

## Detalles
- Los eventos de préstamo, devolución, desactivación de usuario y mantenimiento se registran dentro de la transacción de su operación cuando corresponde.
- Los intentos de inicio de sesión con un nombre inexistente guardan `usuario_id` como `NULL`.
- No se agregaron registros para consultas de lectura (`SELECT`).
- Se conserva la tabla `Auditoria` y sus columnas actuales; no hace falta crear otra tabla si la base de datos ya tiene el esquema descrito en `src/main/java/SIGELAB/config/queryCompleto.sql`.

## Comprobación
Las clases Java del proyecto se compilaron con Java 21 usando `javac`.

## Nota de distribución
Se omitieron archivos generados (`target`), el entorno virtual de Python (`facial-service/.venv`), archivos de configuración local de IntelliJ y el archivo `facial-service/.env` para no incluir credenciales locales. Se conserva `facial-service/.env.example` como plantilla.
