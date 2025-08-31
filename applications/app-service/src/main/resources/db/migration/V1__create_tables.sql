-- Tabla de estado
CREATE TABLE IF NOT EXISTS estado (
    id_estado INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla de tipo prestamo
CREATE TABLE IF NOT EXISTS tipo_prestamo (
    id_tipo_prestamo INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    monto_minimo DECIMAL(20, 2) NOT NULL,
    monto_maximo DECIMAL(20, 2) NOT NULL,
    tasa_interes DECIMAL(5, 2) NOT NULL,
    validacion_automatica BOOLEAN NOT NULL

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla de solicitud
CREATE TABLE IF NOT EXISTS solicitud (
    id_solicitud CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    monto DECIMAL(20, 2) NOT NULL,
    plazo INT NOT NULL,
    documento_identidad VARCHAR(20) NOT NULL,
    fk_id_estado INT,
    fk_id_tipo_prestamo INT,
    fecha DATE DEFAULT (CURRENT_DATE),

    CONSTRAINT fk_solicitud_estado FOREIGN KEY (fk_id_estado)
       REFERENCES estado(id_estado)
       ON DELETE SET NULL
       ON UPDATE CASCADE,
    CONSTRAINT fk_solicitud_tipo_prestamo FOREIGN KEY (fk_id_tipo_prestamo)
        REFERENCES tipo_prestamo(id_tipo_prestamo)
        ON DELETE SET NULL
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
