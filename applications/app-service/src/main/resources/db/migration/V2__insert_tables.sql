-- Inserta estados si no existen previamente
INSERT INTO estado (nombre, descripcion)
SELECT * FROM (
                  SELECT 'PENDIENTE_REVISION', 'El registro ha sido creado pero aún no ha pasado por el proceso de revisión.'
              ) AS tmp
WHERE NOT EXISTS (
    SELECT 1 FROM estado WHERE nombre = 'PENDIENTE_REVISION'
);

INSERT INTO estado (nombre, descripcion)
SELECT * FROM (
                  SELECT 'RECHAZADA', 'El registro fue evaluado y no cumple con los criterios establecidos.'
              ) AS tmp
WHERE NOT EXISTS (
    SELECT 1 FROM estado WHERE nombre = 'RECHAZADA'
);

INSERT INTO estado (nombre, descripcion)
SELECT * FROM (
                  SELECT 'REVISION_MANUAL', 'El registro requiere intervención humana para validar condiciones especiales o inconsistencias.'
              ) AS tmp
WHERE NOT EXISTS (
    SELECT 1 FROM estado WHERE nombre = 'REVISION_MANUAL'
);

INSERT INTO estado (nombre, descripcion)
SELECT * FROM (
                  SELECT 'APROBADA', 'El registro ha sido validado exitosamente y cumple con todos los requisitos.'
              ) AS tmp
WHERE NOT EXISTS (
    SELECT 1 FROM estado WHERE nombre = 'APROBADA'
);


-- Inserts seguros para Flyway: tipo_prestamo
INSERT INTO tipo_prestamo (nombre, monto_minimo, monto_maximo, tasa_interes, validacion_automatica)
SELECT * FROM (
                  SELECT 'CREDITO_DE_LIBRE_INVERSION', 500000.00, 100000000.00, 21.50, TRUE
              ) AS tmp
WHERE NOT EXISTS (
    SELECT 1 FROM tipo_prestamo WHERE nombre = 'CREDITO_DE_LIBRE_INVERSION'
);

INSERT INTO tipo_prestamo (nombre, monto_minimo, monto_maximo, tasa_interes, validacion_automatica)
SELECT * FROM (
                  SELECT 'CREDITO_VEHICULO', 10000000.00, 200000000.00, 15.20, FALSE
              ) AS tmp
WHERE NOT EXISTS (
    SELECT 1 FROM tipo_prestamo WHERE nombre = 'CREDITO_VEHICULO'
);

INSERT INTO tipo_prestamo (nombre, monto_minimo, monto_maximo, tasa_interes, validacion_automatica)
SELECT * FROM (
                  SELECT 'CREDITO_HIPOTECARIO', 30000000.00, 600000000.00, 11.50, FALSE
              ) AS tmp
WHERE NOT EXISTS (
    SELECT 1 FROM tipo_prestamo WHERE nombre = 'CREDITO_HIPOTECARIO'
);

INSERT INTO tipo_prestamo (nombre, monto_minimo, monto_maximo, tasa_interes, validacion_automatica)
SELECT * FROM (
                  SELECT 'CREDITO_EDUCATIVO', 1000000.00, 50000000.00, 10.30, TRUE
              ) AS tmp
WHERE NOT EXISTS (
    SELECT 1 FROM tipo_prestamo WHERE nombre = 'CREDITO_EDUCATIVO'
);

INSERT INTO tipo_prestamo (nombre, monto_minimo, monto_maximo, tasa_interes, validacion_automatica)
SELECT * FROM (
                  SELECT 'CREDITO_ROTATIVO', 500000.00, 20000000.00, 18.80, TRUE
              ) AS tmp
WHERE NOT EXISTS (
    SELECT 1 FROM tipo_prestamo WHERE nombre = 'CREDITO_ROTATIVO'
);