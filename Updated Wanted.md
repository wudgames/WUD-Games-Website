# WUD Games Website — Development Plan

This document transforms the original feature wishlist into a structured, prioritized plan
based on analysis of the existing codebase. Items are grouped into phases by dependency order
and impact.

---

## Phase 0: Code Quality & Foundation Fixes

These must happen first. The codebase has accumulated inconsistencies that will make
feature work harder if not addressed.

### 0.1 — TypeScript Strict Mode & Type Cleanup

- **Problem**: `tsconfig.json` has `strict: false` with many checks commented out. The `Game`
  interface is duplicated between `GameManagerContext.tsx` and `GamePopups.tsx`. `any` types
  scattered throughout.
- **Action**: Enable strict mode incrementally, consolidate the `Game` type into a shared
  `types.ts`, fix resulting type errors.
- **Files**: `tsconfig.json`, `app/boardgames/GameManagerContext.tsx`,
  `app/boardgames/GamePopups.tsx`, new `app/types.ts`

### 0.2 — Security Hardening

- **Problem**: JWT signing key is hardcoded in `application.yaml` (line 3). Steam account
  passwords stored in plaintext (`SteamAccount.java`). CSRF disabled, CORS allows all origins.
- **Action**: Move JWT key to environment variable. Encrypt stored passwords. Restrict CORS
  to production domain. Re-evaluate CSRF.
- **Files**: `application.yaml`, `SecurityConfig.java`, `SteamAccount.java`

### 0.3 — Dead Code Removal

- **Problem**: Unused `BoardGameNav` component, dead CSS in `App.css`, commented-out
  `FilterPopup` in `BoardgameMain.tsx` (line 823), duplicate filter components, unused
  Steam entities with no controllers.
- **Action**: Remove dead code. Keep Steam entities if Phase 4 is planned, otherwise remove.
- **Files**: `App.css`, `BoardgameMain.tsx`, any unused component files

### 0.4 — Error Handling

- **Problem**: 36 `console.error` calls with no user-facing feedback. `InputErrorAdvice`
  manually constructs JSON strings instead of returning objects. Potential NPE in
  `BoardGameController.getBoardGames()` (line 61) where `getCurrentAccessLevel()` returns
  `null` for anonymous users but is compared with `.equals()`.
- **Action**: Add toast/notification system for user-facing errors (shadcn/ui has an alert
  component already). Fix NPE. Clean up `InputErrorAdvice`.
- **Files**: `BoardGameController.java`, `InputErrorAdvice.java`, frontend components

### 0.5 — Fix Existing Bugs

- **CSV import mismatch**: Import code expects column "Times Checked Out"
  (`BoardGameController.java` line 382) but template CSV uses "Checkout Count" — one must
  match the other.
- **Deprecated annotation**: `@EnableGlobalMethodSecurity` should be `@EnableMethodSecurity`
  in `SecurityConfig.java`.
- **Double AuthProvider**: `ConsolegameMain.tsx` (line 323) wraps children in a redundant
  second `AuthProvider`.
- **Missing React key**: `ConsoleGameCard` rendered without `key` in `ConsolegameMain.tsx`
  (line 202).
- **Unsafe reflection**: PATCH endpoint uses `ReflectionUtils` for field updates
  (`BoardGameController.java` lines 160-166) — replace with explicit DTO mapping.

---

## Phase 1: UI/UX Improvements (Frontend-Only)

These are frontend changes that don't require backend modifications and address the most
visible user pain points.

### 1.1 — Increase UI Contrast

- **What**: Improve button hover/pressed states, outline contrast on cards and input fields
  against background.
- **How**: Update CSS variables in `index.css` and Tailwind theme. Audit all interactive
  elements for WCAG AA contrast ratios.
- **Files**: `index.css`, `app/components/ui/button.tsx`, `BoardgameMain.tsx`,
  `ConsolegameMain.tsx`

### 1.2 — Intuitive Checkout/Return Buttons

- **What**: Replace current checkout UI with clear "−" (checkout) and "+" (return) buttons.
  The current switch-style button is confusing.
- **How**: Redesign the checkout controls on `BoardGameCard`. Use Lucide `Minus`/`Plus` icons
  with color coding (red for checkout, green for return).
- **Files**: `BoardgameMain.tsx` (BoardGameCard component area)

### 1.3 — Return-All Confirmation Dialog

- **What**: Add a confirmation dialog before "Return All Games" executes.
- **How**: Use the existing shadcn `AlertDialog` component. Wire it to the return-all action
  in `GameManagerContext.tsx`.
- **Files**: `BoardgameMain.tsx`, `app/components/ui/alert-dialog.tsx` (already exists)

### 1.4 — Show Logged-In User

- **What**: Display the current username in the nav bar when authenticated.
- **How**: The JWT already contains user info. Decode it in `AuthContext.tsx` and expose the
  username. Display it next to the login/logout button in `App.tsx`.
- **Files**: `App.tsx`, `AuthContext.tsx`

### 1.5 — Auto-Refresh Filters (Search on Change)

- **What**: Trigger search/filter automatically when any filter value changes instead of
  requiring a manual search button press.
- **How**: The filtering already happens client-side in `GameManagerContext.tsx`
  (`applyFiltersAndSort` at line 72). Add `useEffect` hooks that call this function when
  filter state changes. Currently the frontend fetches ALL games and filters client-side,
  so this is just a matter of re-running the filter function reactively.
- **Files**: `GameManagerContext.tsx`, `BoardgameMain.tsx` (InlineFilters)

### 1.6 — Fix Sorting

- **What**: Add separate sort options for min/max player count. Fix playtime sorting to sort
  by average or allow min/max selection. Add "sort by date added" (requires Phase 2.1).
- **How**: Update the sort dropdown options in `BoardgameMain.tsx`. Update
  `applyFiltersAndSort` in `GameManagerContext.tsx` to handle new sort keys.
- **Files**: `GameManagerContext.tsx`, `BoardgameMain.tsx`

### 1.7 — Genre Dropdown with Suggestions

- **What**: Replace the free-text genre filter with a dropdown populated from existing genres
  in the database.
- **How**: Add a backend endpoint to return distinct genres (simple query on `BoardGame.genre`).
  Use shadcn `Select` or a combobox on the frontend.
- **Files**: `BoardGameRepository.java` (new query), `BoardGameController.java` (new endpoint),
  `BoardgameMain.tsx`

### 1.8 — "No Results" Prompt to Add Game

- **What**: When search returns zero results, show a prompt offering to add the game.
- **How**: Detect empty results in `GameManagerContext` and render an "Add this game?" CTA
  in `BoardgameMain.tsx` that opens the Add Game dialog pre-filled with the search term.
- **Files**: `BoardgameMain.tsx`, `GamePopups.tsx`

---

## Phase 2: Backend Data Model Enhancements

These require database schema changes. Plan migrations carefully since H2 is the current DB.

### 2.1 — Add Timestamps to Games

- **What**: Record when a game was added to the database. Needed for "sort by new".
- **How**: Add `createdAt` (LocalDateTime) field to `BoardGame.java` with `@CreationTimestamp`.
  Add to `ConsoleGame.java` as well. For existing records, backfill with a migration script
  or default to epoch.
- **Files**: `BoardGame.java`, `ConsoleGame.java`, `GameDTO.java`, frontend types

### 2.2 — Enhanced Checkout Records

- **What**: Track who checked out a game, when, player count, and return timestamp.
- **How**: Redesign `BoardGameCheckout.java`. Currently it uses a composite key of
  (BoardGame + LocalDate) which limits to one checkout record per game per day. Replace with:
  - Auto-generated ID
  - Foreign key to BoardGame
  - `checkedOutAt` (LocalDateTime)
  - `returnedAt` (LocalDateTime, nullable)
  - `checkedOutBy` (String — username or "anonymous")
  - `playerCount` (Integer, nullable)
  - `isActive` (Boolean) — whether currently checked out
- **Files**: `BoardGameCheckout.java`, `BoardGameCheckoutRepository.java`,
  `BoardGameController.java` (checkout/return endpoints)

### 2.3 — Physical Location Tracking

- **What**: Track where a game copy is physically stored (e.g., "Rec Room", "Cages").
- **How**: Add `location` field (String or enum) to `BoardGame.java`. Expose in UI as a
  filter and display on cards.
- **Files**: `BoardGame.java`, `GameDTO.java`, `BoardgameMain.tsx`

### 2.4 — Multiple Copies Distinction

- **What**: Distinguish individual copies of the same game.
- **How**: Currently `quantity` and `availableCopies` exist as integers on BoardGame. To
  truly distinguish copies, each copy would need its own record or a child `GameCopy` entity
  with individual location and status. Evaluate whether the simpler integer approach is
  sufficient or if individual tracking is needed.
- **Decision needed**: Simple (keep quantity integer, add location as a comma-separated list)
  vs. full (new `GameCopy` entity with individual tracking). Recommend starting simple unless
  per-copy checkout history is required.

---

## Phase 3: Equipment & Expanded Item Types

### 3.1 — Data Model for Equipment

- **What**: Support non-game equipment: controllers, joy-cons, RPG equipment, etc.
- **How**: Create an `Equipment` entity with fields:
  - `id`, `name`, `type` (enum: CONTROLLER, JOYCON, RPG_EQUIPMENT, OTHER),
  - `quantity`, `availableCopies`, `description`, `createdAt`
- **Alternative**: Use a shared `Item` supertype with `BoardGame`, `ConsoleGame`, `Equipment`
  as subtypes (JPA inheritance). This is cleaner but a larger refactor.
- **Recommendation**: Start with a separate `Equipment` entity and controller. Unify later
  if the patterns converge.
- **Files**: New `Equipment.java`, `EquipmentRepository.java`, `EquipmentController.java`

### 3.2 — Equipment Checkout Tab (Frontend)

- **What**: New tab in the UI showing equipment with quantity checked out.
- **How**: Add a new route `/equipment` with its own context provider and list view, following
  the same pattern as board games and console games.
- **Files**: New `app/equipment/` directory with components mirroring the board game structure

### 3.3 — Equipment Checkout Tracking

- **What**: Same checkout/return flow as board games but for equipment.
- **How**: Create `EquipmentCheckout` entity and endpoints mirroring the board game pattern.

---

## Phase 4: Advanced Features

### 4.1 — Real Statistics Dashboard

- **What**: Show actual usage statistics — most checked out games, checkout trends over time,
  popular player counts, peak usage days.
- **How**: The `BoardGameCheckoutRepository` already has a `findCheckoutStats()` query and
  there's a `/api/games/stats` endpoint. Enhance the queries and build a proper stats view
  on the frontend. After Phase 2.2, checkout records will have timestamps enabling time-based
  analysis.
- **Files**: `BoardGameCheckoutRepository.java`, `BoardGameController.java`, new frontend
  stats component

### 4.2 — Similar Game Suggestions

- **What**: When searching, suggest similar games based on genre, player count, playtime.
- **How**: Implement a simple similarity score in the backend: match on genre, overlapping
  player count range, similar playtime. Return alongside search results or when no exact
  match is found.
- **Files**: `BoardGameRepository.java` (new query), `BoardGameController.java`

### 4.3 — CSV Export Enhancement

- **What**: CSV export already exists at `GET /api/games/download-csv`. Ensure it includes
  all new fields (timestamps, location, etc.) as they are added.
- **Files**: `BoardGameController.java` (export logic around line 350+)

### 4.4 — Real-Time Frontend Updates

- **What**: Refresh the frontend when the database changes (another user checks out a game).
- **How**: Options ranked by complexity:
  1. **Polling**: Simple — re-fetch games every N seconds. Low effort, good enough.
  2. **SSE (Server-Sent Events)**: Spring supports this. Push update notifications to clients.
  3. **WebSocket**: Most responsive but highest complexity.
- **Recommendation**: Start with polling (30-60 second interval). Move to SSE if needed.
- **Files**: `GameManagerContext.tsx` (add polling interval to fetch)

### 4.5 — Multiple User Support

- **What**: Support more than the two hardcoded users.
- **How**: Move user storage from `application.yaml` to the database. Create a `User` entity,
  registration/management endpoints. Consider OAuth2 integration (UW-Madison SSO?).
- **Note**: This is a significant undertaking. The current two-user model (admin + host) may
  be sufficient for now. Evaluate whether this is truly needed or if just adding a few more
  hardcoded users would suffice.

### 4.6 — Steam Account / Digital Game Management

- **What**: Entities already exist (`SteamAccount`, `SteamGame`, `SteamAccountRequest`) but
  have no controllers or UI.
- **How**: Build out controllers and frontend views. The external search integration for
  Steam games already exists in `ConsoleSearchController.java`.
- **Files**: New controller for Steam entities, new frontend components

---

## Phase Summary & Recommended Order

| Phase               | Effort      | Impact                    | Dependency                     |
| ------------------- | ----------- | ------------------------- | ------------------------------ |
| **0: Code Quality** | Medium      | High (prevents bugs)      | None — do first                |
| **1: UI/UX**        | Low-Medium  | High (user-facing)        | None (1.6 partially needs 2.1) |
| **2: Data Model**   | Medium-High | High (enables phases 3-4) | Phase 0                        |
| **3: Equipment**    | Medium      | Medium                    | Phase 2                        |
| **4: Advanced**     | High        | Medium-High               | Phases 2-3                     |

**Recommended execution**: Phase 0 → Phase 1 (in parallel with Phase 2) → Phase 3 → Phase 4.
Within each phase, items are roughly ordered by priority/dependency.
