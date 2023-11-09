# scsCaptcha - Lado del servidor (back-end).

## 1. Introducción.

Este proyecto contiene una implementación del lado servidor de los servicios requeridos por el proyecto **visualcaptcha**. En concreto, se implementa una API rest con los siguientes endpoints:

**Endpoint**: GET /start/:howmany

**Descripción**: Es la primera llamada que se hace desde el lado del cliente para iniciar una sesión.

**Parámetros**: (obligatorio) howmany: indica el número de imágenes a generar.

---

**Endpoint**: GET /image/:index

**Descripción**: Se emplea para obtener y mostrar una imagen identificada por un índice.

**Parámetros**: (obligatorio) index: indica el índice de la imagen que se quiere obtener.

**Endpoint**: GET /audio(/:type)

---

**Descripción**: Se emplea para obtener y reproducir el archivo de audio según el tipo de formato (mp3 u ogg). El tipo de formato de audio por defecto es mp3.

**Parámetros**: (opcional) type: indica el formato del archivo de audio.

**Endpoint**: POST /try

**Descripción**: Se emplea para recibir, validar y procesar los datos enviados desde el formulario.

## 2. Desarrollo.

Los autores de **visualcaptcha** proporcionan diversas soluciones para el back-end pero ninguna en Java. Actualmente, en dicho lenguaje existen dos implementaciones desarrolladas por terceros ajenos al proyecto **visualcaptcha**:

* **bdotzour/visualCaptcha-java**: Proporciona una implementación basada en servlet con licencia [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0). El proyecto se puede obtener desde este repositorio git: [https://github.com/bdotzour/visualCaptcha-java](https://github.com/bdotzour/visualCaptcha-java)
* **tillkuhn/visualCaptcha-java-rest**: Proporciona una implementación basada en una API REST desarrollada con una versión antigua de Spring boot. Esta implementación esta basada a su vez, en el desarrollo realizado en **bdotzour/visualCaptcha-java**. El repositorio git es: [https://github.com/tillkuhn/visualCaptcha-java-rest](https://github.com/tillkuhn/visualCaptcha-java-rest) y el proyecto dispone de licencia [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0). Este proyecto aún está en desarrollo, pero el último cambio fue hace 7 años.

En este proyecto desarrollaremos una API REST en Spring Boot basada en estos desarrollos.

### 2.1. CORS.

CORS (*Cross-origin resource sharing* o *intercambio de recursos de origen cruzado*) es una especificación definida inicialmente por W3C que permite que se puedan solicitar recursos restringidos (hojas de estilos, scripts, imágenes,...) en una página web desde un dominio diferente del dominio que sirvió el primer recurso. Se trata de un mecanismo que utiliza cabeceras HTTP adicionales para permitir a un cliente (User-Agent) acceder a recursos desde un (servidor) origen diferente al sitio (servidor) actual.

Los atributos que nos proporciona spring boot para la anotacion **CORS** son los siguientes:

* **origins**: Lista de orígenes permitidos. Su valor se coloca en la cabecera **Access-Control-Allow-Origin** tanto de la respuesta previa como de la respuesta real. "*" - significa que todos los orígenes están permitidos. Si no se define, se permiten todos los orígenes.
* **allowedHeaders**: Lista de cabeceras de solicitud que pueden utilizarse durante la solicitud real. El valor se utiliza en la cabecera de respuesta de preflight **Access-Control-Allow-Headers**. "*" - significa que todas las cabeceras solicitadas por el cliente están permitidas. Si no se define, se permiten todas las cabeceras solicitadas.
* **methods**: Lista de métodos de petición HTTP soportados. Si no se define, se utilizan los métodos definidos por la anotación RequestMapping.
* **exposedHeaders**: Lista de cabeceras de respuesta a las que el navegador permitirá acceder al cliente. El valor se establece en la cabecera de respuesta real **Access-Control-Expose-Headers**. Si no se define, se utiliza una lista de cabeceras expuestas vacía.
* **allowCredentials**: Determina si el navegador debe incluir cualquier cookie asociada a la solicitud.
* **maxAge**: Edad máxima (en segundos) de la duración de la caché para las respuestas previas al vuelo. El valor se establece en la cabecera **Access-Control-Max-Age**. Si no se define, la edad máxima se establece en 1800 segundos (30 minutos).

### 2.2. Problemas con las solicitudes de tipo POST.

Los servicios mencionados anteriormente no son consumidos directamente sino por medio de intermediario que se encarga de redireccionar las peticiones. En el caso de las solicitudes de tipo **POST** el cuerpo de la petición no se adjunta en la redirección. 

Para resolver este problema, el que hace de intermediario propone que en las solicitudes de tipo **POST**, el cuerpo sea encapsulada en un parámetro denominado **request** en las cabeceras de la petición HTTP. 



## 3. Compilación, depuración y generación del war.

Este proyecto no dispone de perfiles de compilación. Para compilar y ejecutar en local, ejecutar las siguientes comandos en la línea de comandos:

```bash
mvn clean package
mvn spring-boot:run 
```

Con la primera línea, generaremos el fichero **war** en el directorio **target**. Con la segunda línea, desplegaremos el fichero *war* generado en el tomcat embebido en spring boot.

Para probar la API Rest en local se puede usar la herramienta **curl**. Un ejemplo de uso:

```bash
curl -i http://localhost:8080/start/5
HTTP/1.1 200
Set-Cookie: JSESSIONID=DAD5872B5143EA3D18EE2CBAAFDE3332; Path=/; HttpOnly
Content-Type: application/json
Transfer-Encoding: chunked
Date: Sat, 23 Apr 2022 11:48:42 GMT

{"imageName":"Flag","imageFieldName":"7553c1993e0bff94aacfb8002a79e817","values":["7fe3db8d66732f38fe3a34a291077ea6","ef2cc5825f7b125de8c010cd6239a0bb","776838da9fa33661438b77c4bd541f8e","111ed20d50beee09ed69d6dd1b1ee9b8","2fb11df588429c839436820c66685b34"],"audioFieldName":"c3558ccc669cd6ea48e6a9ce693805fd"}
```

Para depurar en local, ejecutar la siguiente línea:

```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000"
```


