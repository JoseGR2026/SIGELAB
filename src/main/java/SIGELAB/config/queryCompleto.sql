-- =====================================================================
-- SCRIPT COMPLETO: Sistema de Gestión y Control de Laboratorios (SIGELAB)
-- =====================================================================

-- Eliminar la base de datos si ya existe para evitar conflictos en pruebas
IF EXISTS (SELECT * FROM sys.databases WHERE name = 'SIGELAB_DB')
BEGIN
    ALTER DATABASE SIGELAB_DB SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE SIGELAB_DB;
END
GO

CREATE DATABASE SIGELAB_DB;
GO

USE SIGELAB_DB;
GO

-- ---------------------------------------------------------------------
-- 1. TABLA DE ROLES
-- ---------------------------------------------------------------------
CREATE TABLE Rol (
                     id INT IDENTITY(1,1) PRIMARY KEY,
                     nombre VARCHAR(50) NOT NULL UNIQUE
);
GO

-- ---------------------------------------------------------------------
-- 2. TABLA DE USUARIOS
-- ---------------------------------------------------------------------
CREATE TABLE Usuario (
                         id INT IDENTITY(1,1) PRIMARY KEY,
                         nombre VARCHAR(100) NOT NULL,
                         apellido VARCHAR(100) NOT NULL,
                         username VARCHAR(50) NOT NULL UNIQUE,
                         password_hash VARCHAR(255) NOT NULL,
                         correo VARCHAR(100) NOT NULL,
                         rol_id INT NOT NULL,
                         estado VARCHAR(20) NOT NULL CHECK (estado IN ('ACTIVO', 'INACTIVO')),
                         FOREIGN KEY (rol_id) REFERENCES Rol(id)
);
GO

-- ---------------------------------------------------------------------
-- 3. TABLA DE LABORATORIOS
-- ---------------------------------------------------------------------
CREATE TABLE Laboratorio (
                             id INT IDENTITY(1,1) PRIMARY KEY,
                             codigo VARCHAR(50) NOT NULL UNIQUE,
                             nombre VARCHAR(100) NOT NULL,
                             ubicacion VARCHAR(150),
                             capacidad INT,
                             estado VARCHAR(20) NOT NULL CHECK (estado IN ('ACTIVO', 'INACTIVO'))
);
GO

-- ---------------------------------------------------------------------
-- 4. TABLA DE TIPOS DE EQUIPO (Catálogo)
-- ---------------------------------------------------------------------
CREATE TABLE TipoEquipo (
                            id INT IDENTITY(1,1) PRIMARY KEY,
                            nombre VARCHAR(100) NOT NULL UNIQUE
);
GO

-- ---------------------------------------------------------------------
-- 5. TABLA DE EQUIPOS
-- ---------------------------------------------------------------------
CREATE TABLE Equipo (
                        id INT IDENTITY(1,1) PRIMARY KEY,
                        codigo_inventario VARCHAR(50) NOT NULL UNIQUE,
                        nombre VARCHAR(100) NOT NULL,
                        tipo_equipo_id INT NOT NULL,
                        marca VARCHAR(50),
                        modelo VARCHAR(50),
                        numero_serie VARCHAR(100),
                        laboratorio_id INT NOT NULL,
                        estado VARCHAR(20) NOT NULL CHECK (estado IN ('DISPONIBLE', 'PRESTADO', 'DANADO', 'MANTENIMIENTO', 'BAJA')),
                        observaciones TEXT,
                        FOREIGN KEY (tipo_equipo_id) REFERENCES TipoEquipo(id),
                        FOREIGN KEY (laboratorio_id) REFERENCES Laboratorio(id)
);
GO

-- ---------------------------------------------------------------------
-- 6. TABLA DE PRÉSTAMOS (Encabezado)
-- ---------------------------------------------------------------------
CREATE TABLE Prestamo (
                          id INT IDENTITY(1,1) PRIMARY KEY,
                          solicitante VARCHAR(150) NOT NULL,
                          fecha_prestamo DATETIME NOT NULL DEFAULT GETDATE(),
                          fecha_devolucion_esperada DATETIME NOT NULL,
                          usuario_id INT NOT NULL,
                          estado VARCHAR(20) NOT NULL CHECK (estado IN ('ACTIVO', 'FINALIZADO', 'VENCIDO')),
                          observaciones TEXT,
                          FOREIGN KEY (usuario_id) REFERENCES Usuario(id)
);
GO

-- ---------------------------------------------------------------------
-- 7. TABLA DE DETALLE DE PRÉSTAMOS
-- ---------------------------------------------------------------------
CREATE TABLE DetallePrestamo (
                                 id INT IDENTITY(1,1) PRIMARY KEY,
                                 prestamo_id INT NOT NULL,
                                 equipo_id INT NOT NULL,
                                 estado_devolucion VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE' CHECK (estado_devolucion IN ('PENDIENTE', 'BUENO', 'DANADO')),
                                 FOREIGN KEY (prestamo_id) REFERENCES Prestamo(id),
                                 FOREIGN KEY (equipo_id) REFERENCES Equipo(id)
);
GO

-- ---------------------------------------------------------------------
-- 8. TABLA DE MANTENIMIENTO
-- ---------------------------------------------------------------------
CREATE TABLE Mantenimiento (
                               id INT IDENTITY(1,1) PRIMARY KEY,
                               equipo_id INT NOT NULL,
                               fecha_ingreso DATETIME NOT NULL DEFAULT GETDATE(),
                               tipo_mantenimiento VARCHAR(50) NOT NULL CHECK (tipo_mantenimiento IN ('PREVENTIVO', 'CORRECTIVO')),
                               descripcion TEXT,
                               responsable VARCHAR(100),
                               fecha_salida DATETIME,
                               resultado VARCHAR(50) CHECK (resultado IN ('REPARADO', 'NO_REPARABLE')),
                               estado VARCHAR(20) NOT NULL CHECK (estado IN ('EN_PROCESO', 'FINALIZADO')),
                               observaciones TEXT,
                               FOREIGN KEY (equipo_id) REFERENCES Equipo(id)
);
GO

-- ---------------------------------------------------------------------
-- 9. TABLA DE AUDITORÍA
-- ---------------------------------------------------------------------
CREATE TABLE Auditoria (
                           id INT IDENTITY(1,1) PRIMARY KEY,
                           usuario_id INT,
                           fecha_hora DATETIME NOT NULL DEFAULT GETDATE(),
                           accion VARCHAR(100) NOT NULL,
                           entidad VARCHAR(50) NOT NULL,
                           registro_id INT,
                           descripcion TEXT,
                           FOREIGN KEY (usuario_id) REFERENCES Usuario(id)
);
GO

-- =====================================================================
-- INSERCIÓN DE DATOS INICIALES (SEEDS)
-- =====================================================================

-- 1. Roles
INSERT INTO Rol (nombre) VALUES ('ADMIN'), ('ENCARGADO');

-- 2. Usuarios de Prueba (Contraseña en texto plano o hash según configures tu app)
INSERT INTO Usuario (nombre, apellido, username, password_hash, correo, rol_id, estado)
VALUES
    ('José', 'González', 'admin', '123456', 'admin@sigelab.com', 1, 'ACTIVO'),
    ('Carlos', 'Pérez', 'encargado1', '123456', 'carlos@sigelab.com', 2, 'ACTIVO');

-- 3. Laboratorios
INSERT INTO Laboratorio (codigo, nombre, ubicacion, capacidad, estado)
VALUES
    ('LAB-01', 'Laboratorio de Redes', 'Edificio A - Nivel 2', 30, 'ACTIVO'),
    ('LAB-02', 'Laboratorio de Software', 'Edificio B - Nivel 1', 25, 'ACTIVO');

-- 4. Tipos de Equipo
INSERT INTO TipoEquipo (nombre)
VALUES ('Laptop'), ('Proyector'), ('Cámara'), ('Router'), ('Tablet');

-- 5. Equipos (Incluyendo el CAM-001 de los casos de prueba de la guía)
INSERT INTO Equipo (codigo_inventario, nombre, tipo_equipo_id, marca, modelo, numero_serie, laboratorio_id, estado, observaciones)
VALUES
    ('CAM-001', 'Cámara Sony Alpha', 3, 'Sony', 'A6400', 'SN-987654', 1, 'DISPONIBLE', 'Excelente estado general'),
    ('LAP-001', 'Laptop Latitude 3420', 1, 'Dell', 'Latitude', 'DL-554433', 2, 'DISPONIBLE', 'Pantalla de 14 pulgadas'),
    ('PRO-001', 'Proyector PowerLite', 2, 'Epson', 'X49', 'EP-112233', 1, 'DISPONIBLE', 'Incluye control remoto');

GO