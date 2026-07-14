Continue the LedgerCalc Android project at C:\Users\vrsri\Downloads\cloud_files\ledgercalc
GitHub: git@github.com:vsriaravindan/ledgercalc.git

Read the README.md first for full project context.

CURRENT STATE:
- Android app: Ledger (personal/group accounting) + CalcHub (33 financial calculators)
- Built with Jetpack Compose + Material3, glass-morphism violet/teal theme
- Local: Room DB v3 with migrations (global_history, ledger_group, transaction_entry)
- Sync: Supabase (shared_folders, shared_members, shared_entries, sync_events tables)
- Share folders via 6-digit code, audit trail for all edits/deletes
- Auto-poll every 10s for cross-device sync

KNOWN ISSUES / TODOS:
1. Replace auto-poll with Supabase Realtime WebSocket for instant sync
2. Add "Manage Members" UI — owner can remove/unshare someone
3. Add "Leave Shared Folder" option for joiner
4. Add push notifications for shared folder changes
5. Add deep link sharing for codes
6. Conflict resolution UI when offline edits clash
7. The processRemoteDeletes matches by amount+label+expression — could be more robust

SUPABASE CREDENTIALS:
- URL: https://iuhbtmfdvfuurtkszvar.supabase.co
- Anon key is in app/src/main/java/com/example/sync/SupabaseClient.kt

IMPORTANT PATCH RULES:
- Replace_all for Color.White → MaterialTheme.colorScheme.onSurface can corrupt fully-qualified imports
- MaterialTheme.colorScheme calls inside Canvas {} draw scopes crash (extract color outside)
- Keep all R.font references valid
- Do NOT break existing Room/local functionality when modifying sync code
