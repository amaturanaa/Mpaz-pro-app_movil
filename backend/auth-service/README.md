Auth Service – Microservicio de Autenticación
1. Descripción

El microservicio auth-service permite gestionar el registro y la autenticación de usuarios mediante correo electrónico y contraseña.

Expone dos endpoints REST:

Registro: permite crear un nuevo usuario.
Login: valida las credenciales y genera un token de autenticación.

2. Tecnologías
Java
Spring Boot
Spring Web
Maven
API REST
JSON
JWT (según la implementación de UserService)

3. URL base
http://localhost:9090/auth

El puerto puede variar según la configuración del microservicio.

4. Endpoints
Registrar usuario

POST /auth/register

json
{
    "email": "usuario@correo.com",
    "password": "123456"
}

Respuesta:

json
{
    "message": "Usuario registrado correctamente"
}

Iniciar sesión

POST /auth/login

json
{
    "email": "usuario@correo.com",
    "password": "123456"
}


Respuesta exitosa:

json
{
    "status": "ok",
    "token": "TOKEN_GENERADO"
}


Respuesta fallida:

json
{
    "status": "error",
    "token": ""
}

5. Pruebas con Postman

Se recomienda realizar las pruebas en el siguiente orden:

Ejecutar el microservicio auth-service.
Enviar una petición POST a /auth/register.
Registrar un usuario.
Enviar una petición POST a /auth/login.
Ingresar las credenciales registradas.
Obtener el token de autenticación.

6. Integración con otros microservicios

El token generado por auth-service puede utilizarse para autenticar solicitudes hacia otros microservicios protegidos.

Para enviarlo mediante HTTP, utilizar el encabezado:

http
Authorization: Bearer TU_TOKEN

Los microservicios que requieran autenticación deberán validar el token de acuerdo con la configuración de seguridad implementada.