-- ============================================
-- CLIENTES
-- ============================================

INSERT INTO clientes (
    id,
    nombres,
    apellidos,
    email,
    telefono,
    estado_activo,
    created_at,
    updated_at
) VALUES
      (
          '11111111-1111-1111-1111-111111111111',
          'Juan',
          'Perez',
          'juan.perez@gmail.com',
          '999111111',
          true,
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
      ),
      (
          '22222222-2222-2222-2222-222222222222',
          'Maria',
          'Garcia',
          'maria.garcia@gmail.com',
          '999222222',
          true,
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
      ),
      (
          '33333333-3333-3333-3333-333333333333',
          'Carlos',
          'Ramirez',
          'carlos.ramirez@gmail.com',
          '999333333',
          true,
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
      ),
      (
          '44444444-4444-4444-4444-444444444444',
          'Ana',
          'Torres',
          'ana.torres@gmail.com',
          '999444444',
          true,
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
      ),
      (
          '55555555-5555-5555-5555-555555555555',
          'Luis',
          'Flores',
          'luis.flores@gmail.com',
          '999555555',
          true,
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
      );


-- ============================================
-- PROFESIONALES / PRESTADORES DE SERVICIOS
-- ============================================

INSERT INTO profesionales (
    id,
    nombres,
    apellidos,
    especialidad,
    estado_activo,
    created_at,
    updated_at
) VALUES
      (
          'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
          'Roberto',
          'Martinez',
          'Psicología',
          true,
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
      ),
      (
          'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
          'Laura',
          'Sanchez',
          'Mentorías',
          true,
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
      ),
      (
          'cccccccc-cccc-cccc-cccc-cccccccccccc',
          'Miguel',
          'Gonzales',
          'Asesorías',
          true,
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
      ),
      (
          'dddddddd-dddd-dddd-dddd-dddddddddddd',
          'Patricia',
          'Vargas',
          'Tutorías',
          true,
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
      ),
      (
          'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee',
          'Fernando',
          'Castillo',
          'Coaching',
          true,
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
      ),
      (
          'ffffffff-ffff-ffff-ffff-ffffffffffff',
          'Sofia',
          'Mendoza',
          'Orientación profesional',
          true,
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
      );

-- ============================================
-- RESERVAS
-- ============================================

INSERT INTO reservas (
    id,
    profesional_id,
    cliente_id,
    fecha,
    hora_inicio,
    hora_fin,
    estado
) VALUES

-- ============================================
-- ROBERTO - 5 RESERVAS
-- ============================================

(
    '20000000-0000-0000-0000-000000000001',
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    '11111111-1111-1111-1111-111111111111',
    '2026-10-01',
    '08:00:00',
    '09:00:00',
    'CREADA'
),
(
    '20000000-0000-0000-0000-000000000002',
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    '22222222-2222-2222-2222-222222222222',
    '2026-10-01',
    '09:00:00',
    '10:00:00',
    'CREADA'
),
(
    '20000000-0000-0000-0000-000000000003',
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    '33333333-3333-3333-3333-333333333333',
    '2026-10-01',
    '10:00:00',
    '11:00:00',
    'CREADA'
),
(
    '20000000-0000-0000-0000-000000000004',
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    '44444444-4444-4444-4444-444444444444',
    '2026-10-02',
    '14:00:00',
    '15:00:00',
    'CREADA'
),
(
    '20000000-0000-0000-0000-000000000005',
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    '55555555-5555-5555-5555-555555555555',
    '2026-10-02',
    '15:00:00',
    '16:00:00',
    'CREADA'
),

-- ============================================
-- LAURA - 3 RESERVAS
-- ============================================

(
    '20000000-0000-0000-0000-000000000006',
    'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
    '11111111-1111-1111-1111-111111111111',
    '2026-10-01',
    '09:00:00',
    '10:00:00',
    'CREADA'
),
(
    '20000000-0000-0000-0000-000000000007',
    'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
    '22222222-2222-2222-2222-222222222222',
    '2026-10-01',
    '10:00:00',
    '11:00:00',
    'CREADA'
),
(
    '20000000-0000-0000-0000-000000000008',
    'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
    '33333333-3333-3333-3333-333333333333',
    '2026-10-03',
    '14:00:00',
    '15:00:00',
    'CREADA'
),

-- ============================================
-- MIGUEL - 7 RESERVAS
-- ============================================

(
    '20000000-0000-0000-0000-000000000009',
    'cccccccc-cccc-cccc-cccc-cccccccccccc',
    '11111111-1111-1111-1111-111111111111',
    '2026-10-01',
    '08:00:00',
    '09:00:00',
    'CREADA'
),
(
    '20000000-0000-0000-0000-000000000010',
    'cccccccc-cccc-cccc-cccc-cccccccccccc',
    '22222222-2222-2222-2222-222222222222',
    '2026-10-01',
    '09:00:00',
    '10:00:00',
    'CREADA'
),
(
    '20000000-0000-0000-0000-000000000011',
    'cccccccc-cccc-cccc-cccc-cccccccccccc',
    '33333333-3333-3333-3333-333333333333',
    '2026-10-01',
    '10:00:00',
    '11:00:00',
    'CREADA'
),
(
    '20000000-0000-0000-0000-000000000012',
    'cccccccc-cccc-cccc-cccc-cccccccccccc',
    '44444444-4444-4444-4444-444444444444',
    '2026-10-02',
    '08:00:00',
    '09:00:00',
    'CREADA'
),
(
    '20000000-0000-0000-0000-000000000013',
    'cccccccc-cccc-cccc-cccc-cccccccccccc',
    '55555555-5555-5555-5555-555555555555',
    '2026-10-02',
    '09:00:00',
    '10:00:00',
    'CREADA'
),
(
    '20000000-0000-0000-0000-000000000014',
    'cccccccc-cccc-cccc-cccc-cccccccccccc',
    '11111111-1111-1111-1111-111111111111',
    '2026-10-03',
    '14:00:00',
    '15:00:00',
    'CREADA'
),
(
    '20000000-0000-0000-0000-000000000015',
    'cccccccc-cccc-cccc-cccc-cccccccccccc',
    '22222222-2222-2222-2222-222222222222',
    '2026-10-03',
    '15:00:00',
    '16:00:00',
    'CREADA'
),

-- ============================================
-- PATRICIA - 1 RESERVA
-- ============================================

(
    '20000000-0000-0000-0000-000000000016',
    'dddddddd-dddd-dddd-dddd-dddddddddddd',
    '33333333-3333-3333-3333-333333333333',
    '2026-10-01',
    '10:00:00',
    '11:00:00',
    'CREADA'
),

-- ============================================
-- FERNANDO - 4 RESERVAS
-- ============================================

(
    '20000000-0000-0000-0000-000000000017',
    'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee',
    '11111111-1111-1111-1111-111111111111',
    '2026-10-02',
    '08:00:00',
    '09:00:00',
    'CREADA'
),
(
    '20000000-0000-0000-0000-000000000018',
    'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee',
    '22222222-2222-2222-2222-222222222222',
    '2026-10-02',
    '09:00:00',
    '10:00:00',
    'CREADA'
),
(
    '20000000-0000-0000-0000-000000000019',
    'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee',
    '33333333-3333-3333-3333-333333333333',
    '2026-10-03',
    '14:00:00',
    '15:00:00',
    'CREADA'
),
(
    '20000000-0000-0000-0000-000000000020',
    'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee',
    '44444444-4444-4444-4444-444444444444',
    '2026-10-03',
    '15:00:00',
    '16:00:00',
    'CREADA'
);
