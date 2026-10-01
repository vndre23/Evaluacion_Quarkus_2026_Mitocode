# EVALUACIÓN QUARKUS: Service Center - Sistema de Reservas y Disponibilidad de Profesionales

Backend REST desarrollado con **Quarkus** para la gestión de profesionales, clientes, horarios disponibles y reservas en un centro de servicios profesionales, como psicología, mentorías, asesorías o tutorías.

El sistema permite gestionar la información de profesionales y clientes, registrar horarios disponibles y controlar reservas aplicando reglas de negocio que evitan solapamientos y validan la disponibilidad real de los profesionales.

---

# Funcionalidades

## 1. CRUD de Profesionales

Permite gestionar la información de los profesionales que forman parte del centro de servicios.

### Datos del profesional

* `id`: UUID
* `nombres`: String
* `apellidos`: String
* `especialidad`: String
* `estadoActivo`: Boolean

### Operaciones

* Crear profesional
* Consultar profesionales
* Consultar profesional por ID
* Actualizar profesional
* Eliminar profesional

---

## 2. CRUD de Clientes

Permite gestionar los clientes que pueden realizar reservas.

### Datos del cliente

* `id`: UUID
* `nombres`: String
* `apellidos`: String
* `email`: String
* `telefono`: String
* `estadoActivo`: Boolean

El correo electrónico debe ser **válido y único** dentro del sistema.

Las solicitudes son validadas utilizando `@Valid`.

### Operaciones

* Crear cliente
* Consultar clientes
* Consultar cliente por ID
* Actualizar cliente
* Eliminar cliente

---

# 3. Registro de horarios disponibles

Permite registrar los horarios en los que un profesional se encuentra disponible para atender reservas.

### Datos del horario

* `id`: UUID
* `profesional`: Profesional
* `fecha`: LocalDate
* `horaInicio`: LocalTime
* `horaFin`: LocalTime
* `estado`: Boolean

### Regla de negocio

No se permiten horarios que se solapen para un mismo profesional.

Por ejemplo, si existe el horario:

```text
09:00 - 10:00
```

no se puede registrar para el mismo profesional y fecha:

```text
09:30 - 11:00
```

porque ambos intervalos se encuentran solapados.

---

# 4. Registro de reservas

Permite registrar las reservas realizadas por los clientes.

### Datos de la reserva

* `id`: UUID
* `fecha`: LocalDate
* `horaInicio`: LocalTime
* `horaFin`: LocalTime
* `cliente`: Cliente
* `profesional`: Profesional
* `estado`: Enum

Los estados disponibles son:

```text
CREADA
CANCELADA
COMPLETADA
```

## Reglas de negocio

Una reserva solamente puede crearse cuando se cumplen todas las siguientes condiciones:

1. Existe un horario disponible para el profesional.
2. El horario disponible cubre completamente el intervalo solicitado.
3. El profesional se encuentra activo.
4. El cliente se encuentra activo.
5. El profesional no tiene otra reserva activa que se solape con el intervalo solicitado.

De esta manera se garantiza que una reserva solamente pueda realizarse dentro de la disponibilidad real del profesional.

---

# 5. Cancelación de reservas

Permite cancelar una reserva existente cambiando su estado a:

```text
CANCELADA
```

Al cancelar una reserva, esta deja de considerarse una reserva activa y, por lo tanto, deja de ocupar el intervalo correspondiente para efectos de disponibilidad.

---

# 6. Consultas de profesionales

El sistema implementa **dos consultas diferentes relacionadas con los profesionales**.

## 6.1 Profesionales ordenados por número de reservas activas

La primera consulta permite listar los profesionales ordenados de forma **descendente según el número de reservas activas**.

El conteo de las reservas se procesa **en memoria mediante programación funcional**.

Ejemplo:

```text
Profesional             Reservas activas
-----------------------------------------
Luis Salazar                    8
Ana Torres                      5
Marco Díaz                      2
Carlos Pérez                    0
```

El resultado se ordena tomando como criterio la cantidad de reservas activas asociadas a cada profesional.

---

## 6.2 Reservas agrupadas por fecha

La segunda consulta permite mostrar la relación entre las **fechas y las reservas correspondientes**.

La información se procesa mediante programación funcional y se agrupa utilizando una estructura basada en `Map`.

Conceptualmente:

```java
Map<LocalDate, List<Reserva>>
```

Ejemplo:

```text
2026-05-10

- Reserva 1
  Cliente: Ana Torres
  Profesional: Luis Salazar

- Reserva 2
  Cliente: Marco Díaz
  Profesional: Luis Salazar


2026-05-12

- Reserva 3
  Cliente: Mito X
  Profesional: Code Y
```

Esta consulta permite organizar las reservas cronológicamente y visualizar para cada fecha las reservas correspondientes junto con su cliente y profesional.

---

# Arquitectura

El proyecto utiliza una arquitectura basada en:

```text
Domain
Application
Infrastructure
```

siguiendo un enfoque de **DDD ligero** y separación de responsabilidades.

La arquitectura busca mantener separada la lógica de negocio de los detalles de infraestructura.

## Domain

Contiene los elementos relacionados directamente con el dominio del negocio.

Incluye:

* Entidades
* Modelos de dominio
* Reglas de negocio
* Excepciones propias

## Application

Contiene los casos de uso de la aplicación.

Entre ellos:

* Crear profesional
* Actualizar profesional
* Eliminar profesional
* Crear cliente
* Actualizar cliente
* Eliminar cliente
* Registrar horario disponible
* Registrar reserva
* Cancelar reserva
* Consultar profesionales
* Consultar reservas agrupadas por fecha

## Infrastructure

Contiene las implementaciones relacionadas con la infraestructura y comunicación con componentes externos.

Incluye:

* REST Resources
* Repositorios
* Persistencia
* Configuración
* Manejo de excepciones
* Interceptores
* Implementaciones relacionadas con la base de datos

---

# Tecnologías utilizadas

El proyecto utiliza las siguientes tecnologías:

* **Java 21**
* **Quarkus**
* **Maven**
* **Hibernate ORM / Panache**
* **Mutiny**
* **HQL**
* **Flyway**
* **Jakarta Bean Validation**
* **SmallRye Fault Tolerance**
* **OpenAPI / Swagger**
* **JUnit**
* **@QuarkusTest**
* **Docker**
* **GraalVM Native**
* **Postman**

---

# Mutiny

Se utiliza **Mutiny** mediante `Uni` para los endpoints REST y las interacciones con la base de datos.

El flujo general de la aplicación sigue la siguiente estructura:

```text
REST API
   │
   ▼
Application
   │
   ▼
Domain
   │
   ▼
Repository
   │
   ▼
Database
```

Los endpoints e interacciones con la base de datos utilizan `Uni` para representar las operaciones asíncronas.

---

# Persistencia y HQL

Las consultas y operaciones relacionadas con la base de datos utilizan **HQL**.

La persistencia se encuentra separada de la lógica de negocio mediante la capa de infraestructura.

---

# Flyway

El proyecto utiliza **Flyway** para el control de versiones y la gestión de migraciones de la base de datos.

Las migraciones se encuentran en:

```text
src/main/resources/db/migration
```

Las migraciones permiten mantener controlada la evolución de la estructura de la base de datos.

---

# Validaciones

Los requests de los servicios REST utilizan **Jakarta Bean Validation** mediante `@Valid`.

Las validaciones permiten controlar:

* Campos obligatorios
* Formatos
* Datos inválidos
* Email válido
* Restricciones propias de los requests

Además de las validaciones estructurales, las reglas de negocio se validan dentro de los casos de uso correspondientes.

---

# Manejo de excepciones

La aplicación implementa control de excepciones y excepciones propias para representar errores relacionados con las reglas de negocio.

Entre los escenarios contemplados se encuentran:

* Recurso no encontrado
* Cliente inexistente
* Profesional inexistente
* Cliente inactivo
* Profesional inactivo
* Horario no disponible
* Horarios solapados
* Reservas solapadas
* Reserva no válida
* Email duplicado
* Request inválido

Los errores son tratados de manera centralizada para proporcionar respuestas consistentes a los consumidores de la API.

---

# # Resiliencia

La aplicación utiliza **SmallRye Fault Tolerance** para implementar mecanismos de resiliencia.

Como parte de los requerimientos de la evaluación, se aplicaron las siguientes estrategias sobre el método `findById`:

```java
@Fallback(fallbackMethod = "recoverFindById")
@CircuitBreaker(
        requestVolumeThreshold = 4,
        failureRatio = 0.5,
        delay = 10000
)
@Retry(
        maxRetries = 2,
        delay = 500
)
@Timeout(2000)
```

Estas anotaciones permiten demostrar el uso de los mecanismos de tolerancia a fallos proporcionados por SmallRye Fault Tolerance:

* `@Fallback`: permite definir un método alternativo cuando la operación no puede completarse correctamente.
* `@CircuitBreaker`: controla la cantidad de fallos antes de abrir el circuito.
* `@Retry`: permite reintentar la operación cuando se presenta un fallo.
* `@Timeout`: establece un tiempo máximo de espera para la ejecución.

> **Nota:** 
> Esta implementación se realizó específicamente como parte de los requisitos de la evaluación final. 
> Actualmente el proyecto no consume ningún microservicio externo ni presenta una comunicación entre microservicios 
> que requiera mecanismos de resiliencia. 
> Por este motivo, las estrategias de `Fallback`, `CircuitBreaker`, `Retry` y `Timeout` 
> fueron incorporadas sobre `findById` con fines demostrativos y para cumplir con el requisito 
> de la evaluación relacionado, es por ello que al momento de realizar la consulta por id, devuelve el fallback 
> del `CircuitBreaker` no es por una falla del endpoint solo por fines del examén.
> con **SmallRye Fault Tolerance**.

---

# Interceptores y logs

La aplicación implementa interceptores de entrada y salida para generar logs estructurados de las operaciones realizadas sobre los servicios REST.

Los logs permiten registrar información como:

* Inicio de la petición
* Método HTTP
* Endpoint
* Finalización de la petición
* Resultado de la operación
* Tiempo de procesamiento

Esto permite facilitar el seguimiento y diagnóstico de las operaciones realizadas por la API.

---

# OpenAPI / Swagger

Los endpoints REST se encuentran documentados mediante **OpenAPI**.

Al ejecutar la aplicación localmente, Swagger UI está disponible en:

```text
http://localhost:8080/q/swagger-ui
```

La especificación OpenAPI está disponible en:

```text
http://localhost:8080/q/openapi
```

Desde Swagger UI se pueden consultar los endpoints disponibles y ejecutar las operaciones directamente contra la aplicación.

---

# Ejecución local

## Requisitos

Para ejecutar el proyecto localmente se requiere:

* Java 21
* Maven 3.9.x o superior
* Base de datos configurada de acuerdo con `application.properties`

Para verificar Java:

```bash
java -version
```

Para verificar Maven:

```bash
mvn -version
```

---

## Ejecutar en modo desarrollo

Desde la raíz del proyecto ejecutar:

```bash
mvn quarkus:dev
```

La aplicación estará disponible en:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/q/swagger-ui
```

OpenAPI:

```text
http://localhost:8080/q/openapi
```

---

# Ejecución mediante Docker

El proyecto incluye configuración Docker para ejecutar la aplicación en dos modalidades:

* JVM
* Native / GraalVM

Los Dockerfiles se encuentran en:

```text
docker/
```

Los scripts para construir y ejecutar los contenedores se encuentran en:

```text
scripts/
```

---

# Docker JVM

Para ejecutar la aplicación utilizando Java/JVM:

Desde la raíz del proyecto:

```bash
cd scripts
```

Luego ejecutar:

```bash
sh deploy-jvm-in-docker.sh
```

El script realiza las siguientes acciones:

1. Detiene el contenedor anterior si existe.
2. Elimina el contenedor anterior.
3. Construye la imagen Docker.
4. Utiliza el `Dockerfile.jvm`.
5. Crea el nuevo contenedor.
6. Ejecuta la aplicación.
7. Muestra los logs del contenedor.

La aplicación estará disponible en:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/q/swagger-ui
```

---

# Docker Native / GraalVM

Para ejecutar la aplicación en modo Native utilizando GraalVM:

Desde la raíz del proyecto:

```bash
cd scripts
```

Luego ejecutar:

```bash
sh deploy-native-community-in-docker.sh
```

El script realiza el proceso necesario para construir y ejecutar la versión Native de la aplicación mediante Docker.

Esta modalidad permite ejecutar la aplicación como un ejecutable nativo generado para GraalVM.

La aplicación estará disponible en el puerto configurado por el script.

---

# Dockerfiles

El proyecto contiene las configuraciones necesarias para soportar ambas modalidades.

```text
docker/
├── Dockerfile.jvm
└── Dockerfile.native-community
```

## Dockerfile JVM

`Dockerfile.jvm` permite construir una imagen basada en Java 21.

El proceso utiliza Maven para compilar la aplicación y posteriormente copia los artefactos generados por Quarkus a la imagen de ejecución.

## Dockerfile Native

`Dockerfile.native-community` permite generar y ejecutar la versión Native de la aplicación utilizando GraalVM/Quarkus Native.

---

# Pruebas

El proyecto incluye pruebas para validar la lógica de negocio y los principales endpoints.

Se utilizan:

* JUnit
* `@QuarkusTest`

Las pruebas contemplan principalmente:

* Lógica de negocio
* Creación de reservas
* Validación de disponibilidad
* Validación de solapamientos
* Cancelación de reservas
* Validaciones
* Endpoints principales

Para ejecutar las pruebas:

```bash
mvn test
```

También se pueden ejecutar mediante:

```bash
mvn verify
```

---

# Colección de pruebas Postman

El proyecto incluye una colección de pruebas de **Postman** para validar los servicios REST.

La colección se encuentra en el directorio:

```text
postman_collection/
```

El archivo de colección es:

```text
postman_collection/collection.json
```

## Importar colección en Postman

1. Ejecutar la aplicación:

```bash
mvn quarkus:dev
```

2. Abrir Postman.

3. Seleccionar:

```text
Import
```

4. Seleccionar el archivo:

```text
postman_collection/collection.json
```

5. Postman importará la colección.

6. Ejecutar las peticiones para validar los servicios REST.

La aplicación estará disponible por defecto en:

```text
http://localhost:8080
```

## Casos incluidos

La colección contiene pruebas para los principales casos de uso:

* CRUD de profesionales.
* CRUD de clientes.
* Registro de horarios disponibles.
* Validación de horarios solapados.
* Registro de reservas.
* Validación de disponibilidad.
* Validación de reservas solapadas.
* Validación de clientes activos.
* Validación de profesionales activos.
* Cancelación de reservas.
* Consulta de profesionales ordenados por reservas activas.
* Consulta de reservas agrupadas por fecha.
* Validaciones y escenarios de error.

---

# Seguridad

El uso de JWT **no es obligatorio** para la evaluación.

Por este motivo, el proyecto no requiere una integración obligatoria con Keycloak o JWT para ejecutar los servicios.

---

# Flujo de registro de una reserva

El flujo principal para crear una reserva es:

```text
Cliente
   │
   ▼
REST API
   │
   ▼
Validación del Request
   │
   ▼
Validar Cliente activo
   │
   ▼
Validar Profesional activo
   │
   ▼
Buscar Horario Disponible
   │
   ▼
Validar cobertura del intervalo
   │
   ▼
Validar solapamiento con reservas activas
   │
   ▼
Crear Reserva
   │
   ▼
Respuesta REST
```

De esta forma se evita registrar reservas fuera del horario disponible o que entren en conflicto con otras reservas existentes.

---

# Estructura del proyecto

La estructura general del proyecto es:

```text
.
├── docker/
│   ├── Dockerfile.jvm
│   └── Dockerfile.native-community
│
├── postman_collection/
│   └── collection.json
│
├── scripts/
│   ├── deploy-jvm-in-docker.sh
│   └── deploy-native-community-in-docker.sh
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── ...
│   │   └── resources/
│   │       ├── application.properties
│   │       └── db/
│   │           └── migration/
│   │
│   └── test/
│       └── java/
│
├── pom.xml
└── README.md
```

---

# Flujo de ejecución

## Ejecución local

```bash
mvn quarkus:dev
```

Aplicación:

```text
http://localhost:8080
```

Swagger:

```text
http://localhost:8080/q/swagger-ui
```

---

## Ejecución Docker JVM

Desde la raíz:

```bash
cd scripts
sh deploy-jvm-in-docker.sh
```

---

## Ejecución Docker Native

Desde la raíz:

```bash
cd scripts
sh deploy-native-community-in-docker.sh
```

---

# Decisiones técnicas

Las principales decisiones técnicas del proyecto son:

* **Quarkus** como framework para el desarrollo del backend REST.
* **Java 21** como versión del lenguaje.
* **Arquitectura Domain / Application / Infrastructure** para separar responsabilidades.
* **DDD ligero** para organizar el dominio y las reglas de negocio.
* **Mutiny** mediante `Uni` para los endpoints y las interacciones con la base de datos.
* **Hibernate ORM / Panache** para la persistencia.
* **HQL** para las consultas y transacciones con la base de datos.
* **Flyway** para controlar las versiones y migraciones de la base de datos.
* **Bean Validation** mediante `@Valid` para validar los requests.
* **Excepciones propias** para representar errores de negocio.
* **OpenAPI** para documentar los endpoints REST.
* **SmallRye Fault Tolerance** para implementar mecanismos de resiliencia.
* **Interceptores** para registrar logs estructurados de entrada y salida.
* **JUnit y `@QuarkusTest`** para las pruebas.
* **Postman** para las pruebas funcionales de los endpoints.
* **Docker** para facilitar la ejecución de la aplicación.
* **GraalVM Native** para proporcionar una alternativa de ejecución nativa.

El proyecto no utiliza Kafka ni requiere despliegue en la nube.

---

# Resumen de comandos

| Acción                | Comando                                                 |
| --------------------- | ------------------------------------------------------- |
| Ejecutar localmente   | `mvn quarkus:dev`                                       |
| Ejecutar pruebas      | `mvn test`                                              |
| Ejecutar verificación | `mvn verify`                                            |
| Docker JVM            | `cd scripts && sh deploy-jvm-in-docker.sh`              |
| Docker Native         | `cd scripts && sh deploy-native-community-in-docker.sh` |
| Swagger UI            | `http://localhost:8080/q/swagger-ui`                    |
| OpenAPI               | `http://localhost:8080/q/openapi`                       |
| Colección Postman     | `postman_collection/collection.json`                    |

---

# Evaluación

Este proyecto implementa los requerimientos solicitados para la evaluación final:

* CRUD de Profesionales.
* CRUD de Clientes.
* Registro de horarios disponibles.
* Validación de solapamiento de horarios.
* Registro de reservas.
* Validación de disponibilidad real.
* Validación de solapamiento de reservas.
* Validación de clientes y profesionales activos.
* Cancelación de reservas.
* Consulta de profesionales ordenados por número de reservas activas.
* Consulta de reservas agrupadas por fecha.
* Programación funcional.
* Persistencia mediante base de datos.
* Migraciones mediante Flyway.
* Validación mediante `@Valid`.
* Manejo de excepciones.
* Excepciones propias.
* Documentación OpenAPI.
* Arquitectura Domain / Application / Infrastructure.
* Mutiny para endpoints e interacciones con base de datos.
* Consultas y transacciones mediante HQL.
* Resiliencia con SmallRye Fault Tolerance.
* Pruebas con JUnit y `@QuarkusTest`.
* Colección de pruebas Postman.
* Interceptores para logs estructurados.
* Docker para ejecución JVM.
* Docker para ejecución Native/GraalVM.
* Sin dependencia obligatoria de JWT.
* Sin Kafka.
* Sin despliegue en la nube.
