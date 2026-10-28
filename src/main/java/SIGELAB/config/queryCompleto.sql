-- =====================================================================
-- Sistema de Gestión y Control de Laboratorios (SIGELAB)
-- =====================================================================

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

-- 1. Tabla de Roles
CREATE TABLE Rol (
                     id INT IDENTITY(1,1) PRIMARY KEY,
                     nombre VARCHAR(50) NOT NULL UNIQUE
);
GO

-- 2. Tabla de Usuarios
CREATE TABLE Usuario (
                         id INT IDENTITY(1,1) PRIMARY KEY,
                         nombre VARCHAR(100) NOT NULL,
                         apellido VARCHAR(100) NOT NULL,
                         username VARCHAR(50) NOT NULL UNIQUE,
                         password_hash VARCHAR(255) NOT NULL,
                         correo VARCHAR(100) NOT NULL,
                         rol_id INT NOT NULL,
                         estado VARCHAR(20) NOT NULL CHECK (estado IN ('ACTIVO', 'INACTIVO')),
                         fecha_creacion DATETIME NOT NULL DEFAULT GETDATE(),
                         FOREIGN KEY (rol_id) REFERENCES Rol(id)
);
GO

-- 3. Tabla de Laboratorios
CREATE TABLE Laboratorio (
                             id INT IDENTITY(1,1) PRIMARY KEY,
                             codigo VARCHAR(50) NOT NULL UNIQUE,
                             nombre VARCHAR(100) NOT NULL,
                             ubicacion VARCHAR(150),
                             capacidad INT,
                             estado VARCHAR(20) NOT NULL CHECK (estado IN ('ACTIVO', 'INACTIVO'))
);
GO

-- 4. Tabla de Tipos de Equipo
CREATE TABLE TipoEquipo (
                            id INT IDENTITY(1,1) PRIMARY KEY,
                            nombre VARCHAR(100) NOT NULL UNIQUE
);
GO

-- 5. Tabla de Equipos
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

-- 6. Tabla de Préstamos
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

-- 7. Tabla de Detalle de Préstamos
CREATE TABLE DetallePrestamo (
                                 id INT IDENTITY(1,1) PRIMARY KEY,
                                 prestamo_id INT NOT NULL,
                                 equipo_id INT NOT NULL,
                                 estado_devolucion VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE' CHECK (estado_devolucion IN ('PENDIENTE', 'BUENO', 'DANADO')),
                                 FOREIGN KEY (prestamo_id) REFERENCES Prestamo(id),
                                 FOREIGN KEY (equipo_id) REFERENCES Equipo(id)
);
GO

-- 8. Tabla de Mantenimiento
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

-- 9. Tabla de Auditoría
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

-- Roles
INSERT INTO Rol (nombre) VALUES ('ADMIN'), ('ENCARGADO');

-- Usuario Administrador por defecto (Contraseña plana '123456')
INSERT INTO Usuario (nombre, apellido, username, password_hash, correo, rol_id, estado, referencia_facial)
VALUES ('José', 'González', 'admin', 'admin', 'admin@sigelab.com', 1, 'ACTIVO', 'jose');

-- Laboratorios de Prueba
INSERT INTO Laboratorio (codigo, nombre, ubicacion, capacidad, estado)
VALUES ('LAB-01', 'Laboratorio de Redes', 'Edificio A - Nivel 2', 30, 'ACTIVO');
GO


SELECT * FROM Laboratorio;

UPDATE Usuario
SET password_hash = '1234',
    estado = 'ACTIVO'
WHERE username = 'jos';

DELETE FROM Laboratorio ;

DBCC CHECKIDENT ('Usuario', RESEED, 0);


USE SIGELAB_DB;

-- 1. Crea la tabla principal
CREATE TABLE dbo.Usuario (
                             id_usuario INT IDENTITY(1,1) PRIMARY KEY,
                             username NVARCHAR(50) NOT NULL
);

-- 2. Inserta tu usuario admin de prueba

INSERT INTO dbo.Usuario (username) VALUES ('admin');

INSERT INTO Equipo (codigo_inventario, nombre, tipo_equipo_id, marca, modelo, numero_serie, laboratorio_id, estado, observaciones)
VALUES ('INV-001', 'Computadora Dell OptiPlex 7070', 1, 'Dell', 'OptiPlex', 'SN-12345', 1, 'DISPONIBLE', 'Buen estado general');
GO