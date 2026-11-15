cd api-gateway
call .\mvnw clean -s ..\configuracion-maven\settings.xml

cd ../auth-service
call .\mvnw clean -s ..\configuracion-maven\settings.xml

cd ../cliente-service
call .\mvnw clean -s ..\configuracion-maven\settings.xml

cd ../compra-service
call .\mvnw clean -s ..\configuracion-maven\settings.xml

cd ../producto-service
call .\mvnw clean -s ..\configuracion-maven\settings.xml

cd ../pago-service
call .\mvnw clean -s ..\configuracion-maven\settings.xml
