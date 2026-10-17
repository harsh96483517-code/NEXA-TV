# Architecture

## Layers

```
ui (Compose screens, components, ViewModels)
  └── domain (models, repository interfaces, use cases)   ← pure Kotlin, unit-tested
        ├── data      SampleContentRepository, InstalledAppsRepository
        ├── settings  DataStoreSettingsRepository
        └── platform  PackageManager, AppLauncher, icons, network, default Home, clock, wallpapers
app    Application, MainActivity, AppContainer (manual DI), MessageBus, AppActions
```

`AppContainer` builds the singletons once (repositories, launcher, icon loader, use cases) and exposes a
single shared `installedApps` `StateFlow` (`stateIn` with `WhileSubscribed`) so every screen sees the same list.
ViewModels are created through `NexaViewModelFactory`.

## Navigation

`NexaNavigator` is a tiny back stack. Sidebar / top-bar entries *replace* the stack (`openRoot`); detail,
wallpaper, theme and about screens are *pushed*. Quick Settings is an overlay flag, not a destination.
Back pops overlays first, then screens; on Home it is a no-op, because a launcher must never exit.

## Focus and D-pad

- `GlassCard` is the single interactive building block: focus ring, glow and scale, OK = click,
  long-press OK / Menu = secondary action.
- Screens request initial focus with `RequestFocusOnEnter`, which retries per frame so it also works for
  items of a `LazyRow` that are not composed yet.
- The first item of each row redirects *Left* to the selected sidebar entry (`focusProperties`).
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

## Look and feel

`NexaTheme` scales density so a 960 dp-wide design canvas fills any TV resolution (clamped 0.75–2.25).
All artwork is procedural (`ArtCanvas`), so the repository contains no binary assets. Window blur
(`FLAG_BLUR_BEHIND`, API 31+) is applied to dialogs when the device supports it and "Reduce effects" is off.
