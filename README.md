# LedgerCalc — Android Dual App

A modern Android application built with Jetpack Compose + Material3, featuring glass-morphism UI design with violet/teal color palette.

---

## Overview

LedgerCalc is a **dual-app** in one APK:

### (1) Ledger App (`com.example`)
Personal ledger/accounting tool. Create groups (folders), add transactions via a built-in calculator, track running balances, export to PDF.

### (2) CalcHub (`com.example.calchub`)
33 financial calculators in one place — SIP, EMI, FD, PPF, EPF, RD, NPS, SWP, SSY, NSC, SCSS, POMIS, GST, TDS, HRA, Income Tax, Gratuity, Retirement, Salary, Inflation, CAGR, Brokerage, Margin, Stock Average, XIRR, Simple Interest, Compound Interest, Lumpsum, APY, Step-Up SIP, Car Loan EMI, Home Loan EMI, Flat vs Reducing, MF Returns.

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin 2.2.10 |
| UI | Jetpack Compose + Material3 |
| Architecture | MVVM (AndroidViewModel + StateFlow + Room) |
| Local DB | Room v3 (SQLite) with migrations v1→v2→v3 |
| Sync DB | Supabase (Postgres) via REST API + OkHttp |
| Auth | None — device UUID + display name |
| Fonts | 7 fonts: Default, Inter, JetBrains Mono, VT323 (Terminal), Serif, Monospace, Sans-Serif |
| Build | AGP 9.2.1, Gradle 9.4.1, compileSdk 36 |
| HTTP | OkHttp 4.10 |
| JSON | org.json (built-in Android) |
| PDF | Custom PdfExporter with watermark option |

---

## Project Structure

```
app/src/main/java/com/example/
├── MainActivity.kt                  # Entry point, navigation, theme
├── data/
│   ├── AppDatabase.kt               # Room DB, DAO, Entities, Migrations
│   └── CalculatorRepository.kt      # Repository layer for Room
├── ui/
│   ├── CalculatorViewModel.kt       # Main ViewModel — all state + sync logic
│   ├── screens/
│   │   ├── CalculatorScreen.kt      # Calculator UI + save dialog
│   │   ├── GroupsScreen.kt          # Ledger groups list
│   │   ├── GroupDetailScreen.kt     # Transactions within a group
│   │   ├── GlobalHistoryScreen.kt   # All saved calculations
│   │   ├── SettingsScreen.kt        # Theme, Currency, Font, Name, Watermark
│   │   └── ShareDialogs.kt          # Share/Join folder dialogs
│   ├── components/
│   │   ├── CalculatorDisplay.kt     # Expression + result display
│   │   ├── CalculatorPad.kt         # Number pad + scientific pad
│   │   └── AppBackground.kt         # Glass-morphism background
│   └── theme/
│       ├── Color.kt                 # Violet/teal palette
│       ├── Theme.kt                 # GlassDark/GlassLight schemes
│       ├── Type.kt                  # Typography definitions
│       └── AppFont.kt              # Font enum (7 fonts)
├── utils/
│   ├── CalculatorUtils.kt           # Shunting-yard expression parser
│   ├── PdfExporter.kt              # PDF generation
│   └── BackupManager.kt            # DB backup/restore
├── sync/
│   ├── SupabaseClient.kt           # REST API client for Supabase
│   ├── SharedFolderModels.kt       # Data classes for sync tables
│   └── SharedFolderRepository.kt   # Business logic for sync
└── calchub/                         # 33 financial calculators
    ├── ui/screens/                  # Individual calculator screens
    ├── domain/logic/                # Calculator logic (SIP, EMI, FD, etc.)
    └── domain/model/               # Calculator metadata + icons
```

---

## Features Implemented (Latest)

### Core
- [x] Shunting-yard expression parser (+, -, ×, ÷, ^, %, sin, cos, tan, log, ln, sqrt, π)
- [x] Undo/Redo (50 levels)
- [x] Auto-save with 2s debounce
- [x] Blinking cursor in calculator display
- [x] Scientific mode (parentheses, trig, log)
- [x] Clipboard copy for results
- [x] Long-press shows full calculation expression

### Ledger
- [x] Create/edit/delete groups with color coding
- [x] Add transactions via calculator or manual entry
- [x] Running balance per group
- [x] Sort by newest/oldest/amount
- [x] PDF export with watermark + Android share sheet

### UI/UX
- [x] Glass-morphism design (dark + light mode)
- [x] System/Light/Dark theme toggle
- [x] 7 font options (applied app-wide)
- [x] 10 currency symbols
- [x] ₹ default currency
- [x] Sun/moon toggle in Calchub
- [x] BackHandler for system swipe gesture

### Shared Folders (NEW — v2)
- [x] Share any folder via 6-digit secret code
- [x] Read-only or Full-access permissions
- [x] Join shared folder via code
- [x] Real-time sync via auto-poll (10s interval)
- [x] Audit trail — every edit/delete logged with actor name
- [x] Audit chips shown inline (📝 edited, 🗑️ deleted)
- [x] Offline guard — internet required to edit shared folders
- [x] Cross-device delete sync
- [x] Currency sync across devices
- [x] Original timestamps preserved on pull

---

## Database Schema

### Local (Room — SQLite)

```sql
-- Table: global_history
-- Stores all saved calculations
global_history (id INT PK, expression TEXT, result REAL, note TEXT, timestamp INT)

-- Table: ledger_group
-- Folder/group for transactions
ledger_group (id INT PK, name TEXT, color INT, timestamp INT)

-- Table: transaction_entry
-- Individual entries within a group
transaction_entry (id INT PK, groupId INT FK, amount REAL, label TEXT, expression TEXT, timestamp INT)
```

### Remote (Supabase — PostgreSQL)

```sql
-- shared_folders: Links a local folder to a shared code
shared_folders (id BIGINT PK, local_folder_id BIGINT, folder_name TEXT,
  secret_code TEXT UNIQUE, owner_device_id TEXT, owner_name TEXT,
  permission TEXT DEFAULT 'full', currency TEXT DEFAULT '₹',
  created_at TIMESTAMPTZ, used_count INT DEFAULT 0)

-- shared_members: Who has joined
shared_members (id BIGINT PK, shared_folder_id BIGINT FK, device_id TEXT,
  display_name TEXT, is_owner BOOLEAN DEFAULT FALSE,
  is_active BOOLEAN DEFAULT TRUE, joined_at TIMESTAMPTZ)

-- shared_entries: Synced transaction entries
shared_entries (id BIGINT PK, shared_folder_id BIGINT FK, local_entry_id BIGINT,
  amount DOUBLE PRECISION, label TEXT, expression TEXT,
  created_by TEXT, created_at TIMESTAMPTZ,
  updated_at TIMESTAMPTZ, updated_by TEXT,
  deleted_at TIMESTAMPTZ, deleted_by TEXT)

-- sync_events: Full audit log
sync_events (id BIGINT PK, shared_folder_id BIGINT FK, event_type TEXT,
  entry_id BIGINT, actor_name TEXT,
  amount DOUBLE PRECISION, label TEXT, expression TEXT,
  old_amount DOUBLE PRECISION, old_label TEXT, old_expression TEXT,
  created_at TIMESTAMPTZ)
```

**Realtime enabled on all 4 tables.**

---

## Key Bug Fixes (Historical)

| Issue | Fix |
|-------|-----|
| Settings screen crash on open | `inter.ttf` and `jetbrains_mono.ttf` were HTML files, not real TTF |
| Calculator crash on `-55` | `applyOp()` guard `output.size < 2` blocked unary ops — infinite loop |
| Font change not taking effect | `selectedFontFamily` getter read `_fontFamily.value` directly — Compose doesn't track raw `.value` reads. Fixed with `collectAsState()` + `remember()` |
| Shared folder: 3 entries → only 2 showed | `uploadExistingEntries` read `activeGroupTransactions.value` StateFlow which was empty (folder wasn't opened). Fixed to query DB directly |
| Add calculation not showing on other device | Added auto-poll every 10s |
| Settings swipe back closes app | Added `BackHandler(onBack = onBack)` |
| Endless duplicate entries on auto-poll | `getSharedEntryId(remote.id)` looked up shared ID as LOCAL key — always null. Fixed with reverse lookup `getLocalEntryIdBySharedId()` |
| Delete sync deleting wrong entries | Old `pullRemoteEntries` did ADD+DELETE in one pass with stale snapshot. Split into `pullRemoteEntries` (add only) and `processRemoteDeletes` (delete via sync_events matching) |

---

## Shared Folder Sync Architecture

```
┌─────────────────────────┐        ┌─────────────────────────┐
│      Phone A (Owner)    │        │      Phone B (Joiner)   │
│                         │        │                         │
│  Add entry → Room       │        │  Auto-poll (10s)        │
│          ↓              │        │      ↓                  │
│  POST shared_entries    │───────▶│  pullRemoteEntries()    │
│  POST sync_events       │  REST  │      ↓                  │
│                         │        │  Create Room entry      │
│  Delete entry → Room    │        │                         │
│          ↓              │        │  Auto-poll (10s)        │
│  PATCH shared_entries   │───────▶│  processRemoteDeletes() │
│  (deleted_at=now())     │  REST  │      ↓                  │
│  POST sync_events       │        │  Delete Room entry      │
└─────────────────────────┘        └─────────────────────────┘
```

---

## Supabase Setup

**Project URL:** `https://iuhbtmfdvfuurtkszvar.supabase.co`  
**Anon Key:** (in SupabaseClient.kt)

Run in Supabase SQL Editor to create tables (already done):

```sql
-- See full SQL in the git history or supabase_setup.sql
CREATE TABLE shared_folders ( ... );
CREATE TABLE shared_members ( ... );
CREATE TABLE shared_entries ( ... );
CREATE TABLE sync_events ( ... );
ALTER PUBLICATION supabase_realtime ADD TABLE shared_folders;
ALTER PUBLICATION supabase_realtime ADD TABLE shared_members;
ALTER PUBLICATION supabase_realtime ADD TABLE shared_entries;
ALTER PUBLICATION supabase_realtime ADD TABLE sync_events;
GRANT USAGE ON SCHEMA public TO anon;
GRANT SELECT, INSERT, UPDATE ON shared_folders TO anon;
GRANT SELECT, INSERT ON shared_members TO anon;
GRANT SELECT, INSERT, UPDATE ON shared_entries TO anon;
GRANT SELECT, INSERT ON sync_events TO anon;
```

---

## Build

```bash
# Prerequisites: JDK 17+, Android SDK 36, ANDROID_HOME set
./gradlew assembleDebug
# APK at: app/build/outputs/apk/debug/app-debug.apk
```

---

## GitHub

**Repo:** `git@github.com:vsriaravindan/ledgercalc.git`  
**Branch:** `master`

---

## Remaining / Potential Improvements

1. **Real-time WebSocket** — Replace auto-poll with Supabase Realtime subscription for instant sync
2. **Unshare member** — Owner can remove a member (currently code is single-use but no removal UI)
3. **Conflict resolution UI** — Show "discarded change" notification on offline conflict
4. **Push notifications** — Notify when someone edits/deletes in a shared folder
5. **Deep link sharing** — Generate a link instead of manual code entry
6. **Multiple shared folders per group** — Currently 1:1 mapping
7. **Leave shared folder** — User can remove themselves
