# WUD Games

## Run the application

Install Java 25, Node.js 24, and Docker Desktop. Start Docker Desktop, then open a terminal in this repository and run:

```powershell
npm run server
```

The first run creates `config/.env` from `config/.env.example`, builds the frontend and backend, starts PostgreSQL and Ollama with Docker Compose, and launches the Spring Boot app. Open <http://localhost:8080>. The first startup can take a few minutes while Ollama downloads its models. Both database files and downloaded models persist under `database/data/` between runs.

Press **Ctrl+C** in the same terminal to stop the app, then run `npm run stop` to stop its Docker services. Your database files and downloaded models remain under `database/data/`.

The example config uses local development credentials. Google sign-in requires valid Google OAuth credentials; edit `config/.env` to set them. Keep `config/.env` private and do not commit it.

## Project folders

- `frontend/` contains the Thymeleaf templates, static assets, npm dependencies, and webpack configuration.
- `backend/` contains the Spring Boot source, Maven wrapper, and API test collection. Java code is grouped by domain; shared HTTP controllers live in `backend/src/main/java/edu/wisc/wud/games/wud_games_website/controller/`.
- `database/` contains the Docker Compose definition and local database data.
- `config/` contains the shareable environment example and ignored local `.env` files.

Spring maps routes with annotations on controller methods, so routes are kept with their controllers rather than duplicated in a separate routes folder. Security and request configuration live in the existing `config/` packages; there is no separate middleware layer to organize.

Backend unit tests belong under `backend/src/test/java/`, mirroring the production package structure. Add controller or service test packages there as those tests are introduced; empty folders are not needed.

## Frontend development

Run `npm --prefix frontend install` once, then `npm run devserver` to start the webpack development server at <http://localhost:3000>. It uses `config/.env.development` when present, otherwise the example config.

## Tests

Run backend unit tests with `.\backend\mvnw.cmd -f .\backend\pom.xml test`. Frontend tests can be run with `npm test` from the repository root; no frontend test files have been added yet.

## More information

See the [Bootify next steps](https://bootify.io/next-steps/) and the [Spring Boot reference](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/).
