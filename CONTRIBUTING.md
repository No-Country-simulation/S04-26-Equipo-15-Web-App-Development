# Guía de Contribución

¡Gracias por contribuir a este proyecto! Por favor lee este documento antes de empezar a trabajar.

---

## Tabla de contenidos

- [Descripción del proyecto](#descripción-del-proyecto)
- [Stack tecnológico](#stack-tecnológico)
- [Flujo de trabajo con Git](#flujo-de-trabajo-con-git)
- [Convención de commits](#convención-de-commits)
- [Convención de ramas](#convención-de-ramas)
- [Pull Requests](#pull-requests)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Operaciones comunes con Git](#operaciones-comunes-con-git)
- [Reportar problemas](#reportar-problemas)

---

## Descripción del proyecto

**TalentCircle Content Bot** es un sistema automatizado que monitorea la actividad semanal de la comunidad, identifica las contribuciones más relevantes mediante IA y genera borradores de contenido para múltiples canales (newsletter, LinkedIn, Twitter y feeds internos), listos para revisión humana antes de su publicación.

El pipeline corre automáticamente cada viernes, recopila la actividad de la semana, el LLM analiza y selecciona las contribuciones más relevantes, genera borradores por canal, y el lunes el editor puede revisarlos, editarlos, aprobarlos y publicarlos desde el panel de revisión.

---

## Stack tecnológico

- **Backend:** Java con Spring Boot
- **IA:** Spring AI
- **Arquitectura:** Capas (Controller → Service → Repository)
- **Base de datos:** (definir según configuración del equipo)
- **APIs externas:** Reddit

---

## Flujo de trabajo con Git

1. Clona el repositorio y asegúrate de estar actualizado:
   ```bash
   git clone https://github.com/No-Country-simulation/S04-26-Equipo-15-Web-App-Development.git
   git checkout main
   git pull origin main
   ```

2. Crea una rama nueva para tu tarea (ver [Convención de ramas](#convención-de-ramas)):
   ```bash
   git checkout -b feature/NombreApellido
   ```

3. Trabaja en tu rama, haciendo commits pequeños y frecuentes.

4. Antes de abrir un PR, sincroniza con `dev`:
   ```bash
   git checkout feature/NombreApellido
   git fetch origin
   git rebase origin/main
   ```

5. Abre un Pull Request hacia `dev` y solicita revisión a otro miembro del equipo.

> ⚠️ **Nunca hagas push directo a `main`.** Todo cambio debe pasar por PR.

---

## Convención de commits

Usamos [Conventional Commits](https://www.conventionalcommits.org/es/). El formato es:

```
<tipo>(<ámbito>): <descripción breve en imperativo>
```

### Tipos permitidos

| Tipo       | Cuándo usarlo                                                      |
|------------|--------------------------------------------------------------------|
| `feat`     | Nueva funcionalidad, endpoint, servicio o integración              |
| `fix`      | Corrección de un bug o comportamiento incorrecto                   |
| `docs`     | Cambios solo en documentación o README                             |
| `style`    | Formato, indentación o comentarios (sin cambio lógico)             |
| `refactor` | Reestructuración de código sin cambiar el comportamiento           |
| `test`     | Agregar o modificar tests                                          |
| `chore`    | Tareas de mantenimiento, configuración del repo o dependencias     |
| `ci`       | Cambios en configuración de CI/CD o pipelines                      |

### Ámbitos sugeridos

| Ámbito           | Descripción                                              |
|------------------|----------------------------------------------------------|
| `collector`      | Lógica de recopilación de actividad semanal              |
| `ai`             | Integración con Spring AI / prompts / procesamiento LLM  |
| `draft`          | Generación de borradores por canal                       |
| `review-panel`   | Panel de revisión y aprobación del editor                |
| `publisher`      | Integración con APIs de publicación (LinkedIn, etc.)     |
| `scheduler`      | Automatización del pipeline semanal (cron jobs)          |
| `auth`           | Autenticación y autorización                             |
| `config`         | Configuración de la aplicación                           |

### Ejemplos

```
feat(collector): agregar recopilación de posts más reaccionados de la semana
fix(ai): corregir prompt para generación de resumen en formato newsletter
feat(draft): implementar generación de borradores diferenciados por canal
feat(review-panel): agregar endpoint para aprobación de borradores
docs(README.md): agregar diagrama de arquitectura del pipeline
test(publisher): agregar tests unitarios al servicio de publicación en LinkedIn
chore(config): actualizar dependencias de Spring Boot a la última versión
```

---

## Convención de ramas

```
feature/<NombreApellido>
fix/<NombreApellido>
```

### Integrantes del equipo

| Nombre                  | Rama base                    |
|-------------------------|------------------------------|
| Ariel Plaza             | `feature/ArielPlaza/`        |
| Elias Arroyo            | `feature/EliasArroyo/`       |
| Juan Quiroz             | `feature/JuanQuiroz/`        |
| Katherine Alemán        | `feature/KatherineAleman/`   |
| Lisandro Sánchez Morales| `feature/LisandroSanchez/`   |
| Ricardo Leandro Avila   | `feature/RicardoAvila/`      |

### Ejemplos completos

```
feature/ArielPlaza/collector-posts-reaccionados
fix/JuanQuiroz/correccion-prompt-linkedin
feat/KatherineAleman/panel-revision-borradores
```

---

## Pull Requests

Antes de abrir un PR asegúrate de:

- [ ] Tu rama está actualizada con `main`
- [ ] El código compila sin errores (`./mvnw package` o `./gradlew build`)
- [ ] Los tests existentes siguen pasando
- [ ] Seguiste las convenciones de commits y ramas
- [ ] El PR tiene un título descriptivo siguiendo la convención de commits
- [ ] Agregaste una descripción breve de qué cambia y por qué
- [ ] Solicitaste revisión a al menos un compañero del equipo

Un PR debe ser revisado y aprobado antes de hacer merge.

Para crear un PR, luego de hacer push a tu rama, ingresa al [repositorio en GitHub](https://github.com/No-Country-simulation/S04-26-Equipo-15-Web-App-Development) y haz clic en el botón verde que aparece para crear la PR.

### Plantilla de descripción de la PR

En la descripción de la Pull Request copia y pega este texto, agrega la descripción breve y marca con una `x` lo que aplique.

```markdown
## Descripción
Breve explicación del cambio: qué funcionalidad se agrega, corrige o refactoriza y por qué.

## Módulo/Capa afectada
- [ ] Collector (recopilación de actividad)
- [ ] AI / Prompts (procesamiento con LLM)
- [ ] Draft Generator (generación de borradores)
- [ ] Review Panel (panel del editor)
- [ ] Publisher (publicación / exportación)
- [ ] Scheduler (automatización semanal)
- [ ] Configuración / Infraestructura

## Tipo de cambio
- [ ] Fix (corrección de bug)
- [ ] Feature (nueva funcionalidad)
- [ ] Docs (documentación o README)
- [ ] Refactor / Style / Chore / Test

## Checklist
- [ ] Compila sin errores
- [ ] Tests existentes siguen pasando
- [ ] Se agregaron tests para la nueva funcionalidad (si aplica)
- [ ] Sin romper funcionalidades ya existentes
- [ ] Actualicé el README si fue necesario
```

Las opciones que no marques deben quedar con el espacio vacío `[ ]`. Las que sí marques deben reemplazar el espacio con una `x` → `[x]`.

> No deben quedar espacios entre los corchetes y la x.

---

## Estructura del proyecto

```
src/
└── main/
    └── java/
        └── com/talentcircle/
            ├── collector/       ← recopilación de actividad semanal
            ├── ai/              ← integración Spring AI, prompts, procesamiento
            ├── draft/           ← generación de borradores por canal
            ├── review/          ← panel de revisión del editor
            ├── publisher/       ← integración con APIs de publicación
            ├── scheduler/       ← cron jobs y automatización
            └── config/          ← configuración de la aplicación
```

Respeta esta estructura al agregar nuevas clases. Si necesitas crear un nuevo módulo, consúltalo con el equipo antes.

---

## Operaciones comunes con Git

### Traer los últimos cambios de `main` a tu rama

Mientras trabajas, `main` puede avanzar. Para mantener tu rama actualizada:

```bash
git checkout feature/NombreApellido
git fetch origin
git rebase origin/main
```

Si hay conflictos, Git te indicará qué archivos resolver. Una vez resueltos:

```bash
git add <archivos-resueltos>
git rebase --continue
```

> 💡 Preferir `rebase` sobre `merge` para mantener el historial limpio y lineal.

---

### Deshacer el último commit (sin perder los cambios)

Si hiciste un commit antes de tiempo:

```bash
git reset --soft HEAD~1
```

Tus cambios quedan en staging, listos para volver a commitear.

---

### Guardar cambios temporalmente sin commitear

Si necesitas cambiar de rama pero no quieres perder tu trabajo en progreso:

```bash
git stash
# cambia de rama, haz lo que necesites...
git stash pop   # recupera tus cambios
```

---

### Ver el estado de tu rama vs. `main`

```bash
git log origin/main..HEAD --oneline   # commits que tienes tú y main no
git diff origin/main                  # diferencia en el código
```

---

## Reportar problemas

Si encuentras un error o tienes una sugerencia:

1. Revisa si ya existe un issue abierto sobre el tema.
2. Si no existe, abre uno con:
   - **Descripción clara** del problema o sugerencia
   - **Módulo afectado** (ej: "Módulo AI / generación de borradores")
   - **Resultado obtenido vs. resultado esperado**
   - **Pasos para reproducir el error**
   - Captura de pantalla o stack trace del error si aplica

---

## ¿Dudas?

Consulta al equipo en el canal del grupo. ¡Todas las dudas son bienvenidas!