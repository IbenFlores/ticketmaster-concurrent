# Ticketmaster Concurrent - Sistema de Gestion de Eventos

## Descripcion del Proyecto

Este proyecto es una aplicacion backend desarrollada en Java con Spring Boot para el curso de Base de Datos II. El objetivo principal es simular un sistema de venta de entradas capaz de manejar transacciones concurrentes, asegurando la integridad de los datos y evitando la sobreventa de asientos mediante tecnicas de bloqueo optimista y control de concurrencia.

## Requisitos Previos

Para ejecutar este proyecto necesitas tener instalado:

* Java JDK 17 o superior.
* PostgreSQL 16 o 17 (Servidor de base de datos).
* Maven (Opcional, se incluye el wrapper mvnw).
* Git.
* Un cliente API como Postman o cURL.

## Configuracion de la Base de Datos

Antes de iniciar la aplicacion, es necesario preparar el entorno de base de datos.

1. Accede a tu servidor PostgreSQL.
2. Crea una base de datos vacia con el nombre exacto configurado en la aplicacion:
   
   Nombre de la BD: ticketmaster_db

## Configuracion de Seguridad (Variables de Entorno)

Este proyecto utiliza perfiles de Spring Boot para proteger las credenciales. La configuracion base esta en `application.yaml`, pero las contraseñas deben configurarse en un archivo local que no se sube al repositorio.

1. Navega a la carpeta: `src/main/resources/`
2. Crea un archivo llamado: `application-local.yaml`
3. Agrega el siguiente contenido, reemplazando los valores con tus credenciales de PostgreSQL:

```yaml
spring:
  datasource:
    username: tu_usuario_postgres
    password: tu_contraseña_postgres

app:
  jwt:
    secret: "escribe_aqui_una_clave_secreta_larga_para_jwt_seguridad"
