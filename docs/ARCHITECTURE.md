# Architecture

## Layers

```
ui (Compose screens, components, ViewModels)
  └── domain (models, repository interfaces)   ← pure Kotlin, unit-tested
        ├── data      InstalledAppsRepository
        ├── settings  DataStoreSettingsRepository
        └── platform  PackageManager, AppLauncher, icons, network, default Home, clock, wallpapers
app    Application, MainActivity, AppContainer (manual DI), MessageBus, AppActions
```

`AppContainer` builds the singletons once (repositories, launcher, icon loader) and exposes a single
shared `installedApps` `StateFlow` (`stateIn` with `WhileSubscribed`) so every screen sees the same list.
ViewModels are created through `NexaViewModelFactory` (`AppsViewModel`, `WallpaperViewModel`, `SettingsViewModel`).

## Navigation

The launcher has one root screen, **Home = the Apps grid**. `NexaNavigator` is a tiny back stack with Home
at the bottom; the wallpaper, theme and about screens are *pushed* on top of it. The sidebar has two
entries: *Apps* (back to Home) and *Settings* (opens the Quick Settings overlay, which is a flag, not a
destination). Back pops overlays first, then screens; on Home it is a no-op, because a launcher must never exit.

Screens are switched with a plain `when` (no `AnimatedContent`), so only one screen is composed at a time.

## Focus and D-pad

- `GlassCard` is the single interactive building block: focus ring, glow and scale, OK = click,
  long-press OK / Menu = secondary action.
- Screens request initial focus with `RequestFocusOnEnter`, which retries per frame so it also works for
  items of a lazy list that are not composed yet.
- The first app redirects *Left* to the selected sidebar entry (`focusProperties`).
- The expanded sidebar overlays content; content is laid out against the compact width so nothing reflows while navigating.
- Compose `Dialog` provides the focus trap and Back handling for overlays.

## Launcher specifics

- **Package visibility:** a manifest `<queries>` block for `MAIN` + `LEANBACK_LAUNCHER` / `LAUNCHER` / `HOME`.
  `QUERY_ALL_PACKAGES` is deliberately not requested.
- **Discovery:** `queryIntentActivities` for leanback and launcher activities, merged and de-duplicated by
  package/activity; refreshed from package add / remove / change broadcasts.
- **Default Home:** `RoleManager` `ROLE_HOME` request on API 29+, otherwise `Settings.ACTION_HOME_SETTINGS`,
  otherwise general settings. The state is re-read whenever the activity resumes.
- **Wallpapers:** built-in presets are drawn with Canvas; user images come from `OpenDocument` (persistable URI
  grant) or from `getExternalFilesDir("wallpapers")`, which needs no permission.

## Performance choices

- The wallpaper (`WallpaperBackground`) is static and sits in its own `graphicsLayer`, so moving focus over
  the apps never repaints it. `ArtCanvas` computes its paths once per size inside `drawWithCache`.
- No window blur, no infinite animations, no animated screen transitions.
- App icons are decoded at 96 px; user wallpaper images are decoded at 1280x720.
- `reduceEffects` defaults to `true` and zeroes the focus and sidebar animation durations.
- The release build type enables R8 minification and resource shrinking.

## Look and feel

`NexaTheme` scales density so a 960 dp-wide design canvas fills any TV resolution (clamped 0.75–2.25).
All artwork is procedural (`ArtCanvas`), so the repository contains no binary assets.
