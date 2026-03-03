# AGENTS.md — WUD Games Website

Context for AI agents working on this codebase.

## Project Overview

WUD Games Website — an internal game/equipment management tool for the Wisconsin Union
Directorate (WUD) Games committee at UW-Madison. It tracks board games, console games,
and equipment inventory with checkout/return functionality.

- **Production URL**: `http://wudgames.minecraft.best:8000/`
- **Version**: 1.4.0
- **Authors**: Safyr Barlow, John Derr

## Tech Stack

| Layer    | Technology                                                                            |
| -------- | ------------------------------------------------------------------------------------- |
| Backend  | Java 17, Spring Boot 3.5.11, Spring Data JPA, Spring Security (JWT/OAuth2)            |
| Frontend | React 19.1, TypeScript 5.6.3, Tailwind CSS 4.1, shadcn/ui (new-york style), Webpack 5 |
| Database | H2 (file-based, `data/mydb.mv.db`)                                                    |
| Build    | Maven with frontend-maven-plugin (Node v22.16.0)                                      |

## Project Structure

```
├── src/main/java/edu/wisc/union/websiteBackend/
│   ├── WudGamesWebsiteBackendApplication.java
│   ├── SecurityConfig.java       # Security, CORS, JWT decoder, WebClient
│   ├── WebMvcConfig.java         # Client-side routing forwarding
│   ├── jpa/                      # Entities and repositories
│   ├── controllers/
│   ├── auth/
│   │   ├── AuthController.java   # /api/auth/login, /refresh, /version
│   │   ├── JwtUtil.java          # JWT generation (HMAC-SHA256, 10-day expiry)
│   │   ├── AuthDTO.java, LoginDTO.java
│   │   └── UserProperties.java   # YAML user config binding
│   └── exception/
│
├── src/main/webapp/               # Frontend source

│
└── src/main/resources/static/
    └── WudGamesWebsite.yaml       # OpenAPI 3.0 spec (779 lines)
```

## Key API Endpoints

### Board Games (`/api/games`)

- `GET /` — Filtered search (name, genre, playtime, playerCount, sort)
- `POST /` — Create game (ADMIN)
- `PUT /{id}` — Full update (ADMIN)
- `PATCH /{id}` — Partial update (ADMIN) — uses ReflectionUtils (fragile)
- `DELETE /{id}` — Delete (ADMIN)
- `POST /{id}/checkout` — Checkout (HOST+)
- `POST /{id}/return` — Return (HOST+)
- `PUT /return-all` — Return all games (HOST+)
- `GET /download-csv` — Export CSV
- `POST /import` — Import from CSV (ADMIN)
- `GET /stats` — Checkout statistics

### Console Games (`/api/consoles`)

- `GET /` — List consoles
- `GET /games` — List console games
- CRUD for consoles and games
- `GET /genres` — List genres

### External Search

- `GET /api/bgg/search?query=` — BoardGameGeek search
- `GET /api/bgg/details?id=` — BGG game details
- `GET /api/consoles/steam/search?query=` — Steam search
- `GET /api/consoles/vgg/search?query=` — VGG search

### Auth (`/api/auth`)

- `POST /login` — Returns JWT
- `GET /refresh` — Refresh JWT
- `GET /version` — App version

## Authentication & Authorization

- JWT (HMAC-SHA256), 10-day expiration, stored in cookies on frontend
- Two hardcoded users in `application.yaml`: `admin` (ADMIN role), `host` (HOST role)
- Three access levels: ANONYMOUS (read-only), HOST (checkout/return), ADMIN (full CRUD)
- `@PreAuthorize` annotations on controller methods, but `SecurityConfig` permits all
  requests by default — the method-level security is the real gate

## Known Issues & Gotchas

1. **TypeScript strict mode is OFF** (`tsconfig.json` line 30) — `any` types throughout
2. **JWT key hardcoded** in `application.yaml` — not in env vars
3. **Frontend fetches ALL games** then filters client-side — backend filtering params
   are available but not used by the frontend
4. **`Game` interface duplicated** between `GameManagerContext.tsx` and `GamePopups.tsx`
5. **CSV import column mismatch**: Code expects "Times Checked Out" but template has
   "Checkout Count"
6. **Potential NPE**: `BoardGameController.getBoardGames()` line 61 —
   `getCurrentAccessLevel()` returns `null` for anonymous users, compared with `.equals()`
7. **Double AuthProvider**: `ConsolegameMain.tsx` wraps children in a redundant
   second `AuthProvider`
8. **Missing React key**: `ConsoleGameCard` in `ConsolegameMain.tsx` line 202
9. **Deprecated**: `@EnableGlobalMethodSecurity` should be `@EnableMethodSecurity`
10. **PATCH endpoint** uses `ReflectionUtils` for field updates — no type safety
11. **No user-facing error messages** — 36 `console.error` calls with no UI feedback
12. **CORS allows all origins** — should be restricted in production
13. **Steam account passwords** stored in plaintext in `SteamAccount.java`

## Build & Run

```bash
# Full build (backend + frontend)
./mvnw clean package

# Frontend only (dev)
npm install
npm run build          # Production webpack build
npm run dev            # Dev webpack with watch mode

# Run the Spring Boot app
./mvnw spring-boot:run
```

Webpack outputs to `target/classes/static/` so Spring Boot serves the built frontend.

## Database

H2 file-based database at `data/mydb.mv.db`. No explicit configuration in
`application.yaml` — uses Spring Boot defaults. Access H2 console (if enabled) at
`/h2-console`.

**Entities**:

- `BoardGame` — id, name, minPlaytime, maxPlaytime, minPlayerCount, maxPlayerCount,
  availableCopies, genre (String), boxImageUrl, description (VARCHAR 1024), quantity,
  checkoutCount, internalNotes
- `BoardGameCheckout` — composite key (BoardGame + LocalDate), copiesOut (int)
- `ConsoleGame` — id, name, boxImageUrl, description, ManyToMany to Console and ConsoleGenre
- `Console` — id, name
- `ConsoleGenre` — id, name
- `SteamAccount`, `SteamGame`, `SteamAccountRequest` — exist but have no controllers

## Conventions

- Backend follows standard Spring Boot patterns (controller → service → repository)
- Frontend uses React Context for state management (one context per entity type)
- UI components are shadcn/ui (new-york style) — install new ones via:
  `npx shadcn@latest add <component>`
- Component files are large monoliths (800+ lines) — should be broken up when modified
- Java package: `edu.wisc.union.websiteBackend`

## Development Plan

See `Updated Wanted.md` for the full phased development plan. Summary of phases:

- **Phase 0**: Code quality fixes (TypeScript strict, security, dead code, error handling)
- **Phase 1**: UI/UX improvements (contrast, checkout buttons, filters, sorting)
- **Phase 2**: Data model enhancements (timestamps, enhanced checkout records, locations)
- **Phase 3**: Equipment support (new entity type, checkout flow, UI tab)
- **Phase 4**: Advanced features (statistics, similar games, real-time updates, multi-user)
