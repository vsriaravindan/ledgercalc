# LedgerCalc

**Android app** — Combined ledger/accounting system + 33 financial calculators.

## Project Location
`C:\Users\vrsri\Downloads\cloud_files\ledgercalc`

## GitHub
`git@github.com:vsriaravindan/ledgercalc.git`

## App Structure

### 1. Home App (Ledger + Calculator) — `com.example.*`
Bottom nav: **Calculator | Ledgers | History**

- **Calculator Screen** — Full scientific calculator (glass-morphism UI)
- **Ledgers Screen** — Create named ledger groups with color pickers
- **Group Detail** — Running balance, transaction list, sortable, export PDF
- **Global History** — All calculations ever saved, push to any ledger group
- **Settings Screen** — Theme, Currency, Font, PDF Watermark

### 2. CalcHub (Financial Calculator) — `com.example.calchub.*`
Drawer menu → "Financial Calculator"

- 33 financial calculators in searchable 3-column grid
- SIP, Lumpsum, FD, PPF, RD, SSY, EPF, NSC, SWP, MF Returns, EMI, Home/Car Loan, Income Tax, GST, HRA, TDS, NPS, Retirement, Gratuity, APY, Brokerage, CAGR, XIRR, Margin, etc.
- Favorites tab, Tools tab (grouped by category)
- Share button on each calculator (SIP wired up as example)

### 3. Drawer Menu
Swipe from left → Home (Calculator) | Financial Calculator | Settings

---

## 🔧 All Changes Made

### Design
- **Unified dark glass-morphism** theme (violet/teal/rose palette)
- Proper dark + light mode support
- Full Material3 typography scale
- Ambient glow background in dark mode

### UI Overhaul
- NeonComponents → themed `Surface` with elevation
- Calculator glass buttons with press states
- Clean calculator grid layout (5 rows)
- Blinking cursor `|` in calculator display
- Gradient floating headers (no scroll-through)
- Modern Material icons for all 33 calculators

### Calculator Engine
- **Shunting-yard** expression parser (handles + - × ÷ ^ % sin cos tan log ln sqrt π)
- Percentage support (`%` → `/100`)
- Incomplete expression preview (shows last number when trailing operator)
- Error state shown as red "Error" text
- Auto-save after 2s idle → "Not saved" in global history

### Ledger
- DB migration v2→v3: `expression` field in `TransactionEntry`
- Long-press transaction → shows full calculation expression
- PDF export → auto-saves + opens Android share sheet
- Sort order persists per group (SharedPreferences)

### Settings
- Theme: System / Light / Dark
- Currency: 10 currency symbols
- Font Style: 7 fonts (Default, Inter, JetBrains Mono, VT323 Terminal, Serif, Monospace, Sans-Serif)
- PDF watermark toggle
- Font applies globally via Theme

### Navigation
- Side drawer: Home, Financial Calculator, Settings
- Sun/moon theme toggle in Financial Calculator top bar
- CalcHub renamed → "Financial Calculator"

### Quick Features
| Feature | Detail |
|---------|--------|
| Auto-save | 2s debounce → "Not saved" in history |
| Blinking cursor | Purple `\|` at end of expression |
| Share PDF | Saves then opens Android share sheet |
| Share CalcHub results | Share button in calculator header |
| PDF watermark | Optional in Settings |
| Font system | 7 fonts, applies globally |
| Light/dark toggle | ☀️/🌙 icon |

---

## ⚠️ Known Issues

### Settings Screen Crash
When opening Settings from the drawer menu, the app crashes. Possible causes:

1. **Font state flow collection** — `fontFamily` is a `StateFlow<String>` in `CalculatorViewModel`. If the preference key `"font_family"` hasn't been set yet (first launch after font feature was added), `prefs.getString("font_family", "Default")` should return "Default". Verify it compiles and the default value is valid.

2. **AppFont enum mismatch** — The SettingsScreen compares `fontFamily == font.displayName`. Ensure `AppFont.DEFAULT.displayName` is `"Default"` matching the preference default.

3. **R.font references** — The `AppFont` enum references `R.font.inter`, `R.font.jetbrains_mono`, `R.font.vt323`. These are auto-generated from the `.ttf` files in `res/font/`. If the files aren't valid or the R class wasn't regenerated, it crashes.

### Other
- Compose `MaterialTheme` calls inside `Canvas {}` draw scopes will crash — extract colors outside
- `replace_all` patches can corrupt fully-qualified `androidx.compose.ui.graphics.Color.White` references

---

## Build Instructions

### Option A: Android Studio
1. Open `C:\Users\vrsri\Downloads\cloud_files\ledgercalc`
2. Wait for Gradle sync
3. If compileSdk error, set in `app/build.gradle.kts`:
   ```kotlin
   compileSdk = 36
   buildToolsVersion = "36.1.0"
   ```
4. Build → Build APK(s)

### Option B: CLI
```bash
export JAVA_HOME=~/jdk17/jdk-17.0.19+10
export PATH=$JAVA_HOME/bin:$PATH
cd /c/Users/vrsri/Downloads/cloud_files/ledgercalc
./gradlew assembleDebug --no-daemon -Dorg.gradle.jvmargs="-Xmx2g"
```

## Key Files

| File | Purpose |
|------|---------|
| `CalculatorViewModel.kt` | Main state/logic hub |
| `CalculatorUtils.kt` | Shunting-yard expression parser |
| `AppDatabase.kt` | Room DB + Migrations |
| `MainActivity.kt` | Drawer + Nav + Theme |
| `Theme.kt` | Glass dark/light color schemes |
| `AppFont.kt` | Font enum (7 fonts) |
| `Color.kt` | Color palette |
| `CalculatorPad.kt` | Glass calculator buttons |
| `NeonComponents.kt` | CalcHub cards, nav, headers |
| `SettingsScreen.kt` | Settings page |
| `GroupDetailScreen.kt` | Ledger group detail |
| `GroupsScreen.kt` | Ledger group list |
| `AppBackground.kt` | Ambient glow background |
