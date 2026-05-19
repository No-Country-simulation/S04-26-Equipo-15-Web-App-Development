# TalentCircle Content Bot

Sistema automatizado para la comunidad TalentCircle que monitorea actividad, identifica contribuciones relevantes mediante IA y genera borradores de contenido para múltiples canales.

## 🚀 Requisitos Previos

- **Java 21** o superior
- **Maven** (incluido en el proyecto mediante el Maven Wrapper)
- Una base de datos **PostgreSQL** (las credenciales actuales apuntan a la base de datos de Neon en la nube)
- IDE recomendado: IntelliJ IDEA o VS Code con el Extension Pack for Java

## 🛠️ Configuración Local

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/No-Country-simulation/S04-26-Equipo-15-Web-App-Development.git
   cd S04-26-Equipo-15-Web-App-Development/bot
   ```

2. **Variables de Entorno**
   El proyecto utiliza un archivo `.env` en la raíz de la carpeta `bot/`. Asegúrate de tenerlo configurado con las credenciales correctas:
   ```properties
   DB_URL=jdbc:postgresql://<tu-host>/bot-autmatizacion?sslmode=require
   DB_USERNAME=<tu-usuario>
   DB_PASSWORD=<tu-password>
   ```

3. **Ejecutar el proyecto**
   Puedes levantar el proyecto directamente utilizando el wrapper de Maven:

   *En Windows:*
   ```cmd
   mvnw.cmd spring-boot:run
   ```

   *En Linux/Mac:*
   ```bash
   ./mvnw spring-boot:run
   ```

## 🏗️ Arquitectura del Proyecto

El proyecto está estructurado modularmente (Package by Feature) para facilitar el desarrollo en paralelo:

- `ai`: Integración con Spring AI y prompts
- `collector`: Recopilación de actividad
- `draft`: Generación de borradores
- `publisher`: Publicación final
- `review`: Panel de revisión del editor
- `scheduler`: Automatización y cron jobs
- `config`: Configuraciones globales

## 🤝 Contribución

Por favor, revisa el archivo [CONTRIBUTING.md](../CONTRIBUTING.md) antes de empezar a trabajar. Ahí encontrarás las guías sobre:
- Flujo de trabajo con Git
- Convención de Commits (Conventional Commits)
- Nomenclatura de ramas
- Proceso de Pull Requests
