# TalentCircle AI Bot

El **TalentCircle AI Bot** es el corazón del backend encargado de la automatización, recolección de actividad comunitaria, generación de resúmenes con Inteligencia Artificial (LLMs) y publicación de borradores adaptados a distintas redes sociales.

## Arquitectura y Módulos 🏗️

El sistema se divide en responsabilidades claras, siguiendo una arquitectura modular:

1. **Collector (`/api/collector`)**
   - Recolecta información relevante de programación y tecnología desde fuentes externas (actualmente **Reddit**).
   - Filtra los mejores resultados de la semana y normaliza los datos (títulos, puntajes, autores, contenido) en el modelo `WeeklyActivityDTO`.

2. **AI & Prompts (`/api/ai`)**
   - Integración nativa con la API ultrarrápida de **Groq** (`llama-3.3-70b-versatile`).
   - Diseñado para recibir el DTO del Collector y generar resúmenes coherentes y estructurados.

3. **Draft Generator (`/api/draft`)**
   - Motor generador de borradores impulsado por el *Patrón Strategy*.
   - Estrategias específicas ya implementadas: **LinkedIn, Twitter y Newsletter**.
   - Seguimiento histórico del proceso mediante entidades de "Pipeline".

4. **Review Panel (`/api/drafts`)**
   - Endpoints para editores humanos. Permite listar borradores pendientes (`?status=PENDING_REVIEW`), editarlos y exportarlos en formato JSON o Markdown.

5. **Security & Auth (`/auth`)**
   - Sistema robusto de seguridad basado en tokens **JWT**.
   - Control de acceso por Roles (`ADMIN`, `EDITOR`).

6. **Base de Datos & Flyway**
   - Almacenamiento en **PostgreSQL** alojado en la nube (NeonDB).
   - Control estricto de migraciones de la base de datos gestionado por **Flyway**, garantizando que todos los desarrolladores tengan siempre las mismas tablas.

---

## Setup Local 🚀

1. Asegúrate de tener **Java 21** instalado.
2. Clona el repositorio y navega hasta la carpeta `/bot`.
3. Crea un archivo `.env` en la misma carpeta `/bot` con el siguiente formato:

```env
DB_URL=jdbc:postgresql://<tu_url_neon>?sslmode=require&channelBinding=require
DB_USERNAME=<tu_usuario>
DB_PASSWORD=<tu_password>
GROQ_API_KEY=<tu_api_key_de_groq>
```

4. Ejecuta el servidor (descargará las dependencias de Maven automáticamente):
```bash
./mvnw spring-boot:run
```

*(Nota: Nuestro `application.properties` tiene rutas inteligentes, por lo que detectará tu archivo `.env` sin importar si corres la app desde tu IDE en la raíz del workspace o desde la consola en la carpeta bot).*

---

## Swagger UI (Documentación Interactiva) 📖

La API está documentada automáticamente. Una vez que levantes el servidor, simplemente visita:
👉 **`http://localhost:8080/swagger-ui/index.html`**

Aquí podrás probar el endpoint de Reddit, revisar borradores y probar la autenticación.
