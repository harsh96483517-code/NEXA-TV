# NEXA TV — Your World On Screen

A native **Android TV home launcher** written in Kotlin and Jetpack Compose, with a "Liquid Glass"
look: translucent glass panels, thin borders, soft glow and cyan / blue / violet accents. It is built
for the remote control: every element is reachable with the D-pad and shows a clear focus state.

> ## ⚠️ Build status: NOT TESTED
> This project was written in an environment **without the Android SDK and without access to the
> Gradle / Google Maven repositories**, so the Android app has **never been compiled, installed or run**
> by its author. What *was* verified: the pure-Kotlin logic (search, app-row ranking, formatting,
> wallpaper parsing) was compiled with Kotlin 2.0.21 and its 29 unit tests pass. Everything else
> (Compose UI, manifest, Gradle configuration, Android platform code) is unverified and may need small
> fixes on first build. See [Build](#build) and [docs/TESTING.md](docs/TESTING.md), and report anything
> that fails.

## What is real and what is sample

| Real | Sample / placeholder |
| --- | --- |
| Installed-app discovery, icons, launching, "App info" | Hero banner, *Continue Watching*, *Trending Now*, Movies, TV Shows, Library titles |
| App search, live updates when apps are installed or removed | All artwork (drawn procedurally in code; no third-party images) |
| Wallpapers (built-in presets, images you choose or copy onto the device) | Titles, taglines and runtimes (invented) |
| Theme, time format, sidebar mode, reduce-effects, Library list (persisted) | "Play" buttons: they explain that no content source is connected |
| Wi-Fi / Ethernet status, clock and date | |
| Default-Home request through Android's own UI | |

Sample content is labelled as such in the UI. NEXA TV does **not** read watch progress from other apps
and never shows a progress bar for real apps.

## Features

- **Top bar:** logo, Home / Movies / TV Shows / Apps / Library tabs, network indicator, time and date, settings shortcut.
- **Sidebar:** Home, Search, Movies, TV Shows, Apps, Library, Settings; optional compact (icons-only) mode that expands over the content while focused.
- **Home:** auto-rotating featured hero (pauses while focused), *Your Apps*, *Continue Watching*, *Trending Now*.
- **Your Apps:** well-known streaming apps first *if installed* (YouTube, Netflix, Prime Video, Disney+, Spotify, JioHotstar, YouTube Kids), then other installed apps. NEXA TV itself is never listed.
- **Apps / Search:** every launchable installed app; ranked search by name; titles search for the sample catalogue.
- **Quick Settings** (top-bar gear, or Settings in the sidebar): Wallpaper, Theme, Time format, App management, System settings, About.
- **Wallpaper:** four built-in procedural presets, an image chosen with the system document picker, or images copied into the app's own folder (no storage permission needed).
- **Theme:** four accents, compact sidebar, reduce effects (disables window blur, drifting wallpaper, auto-rotation), time format.
- **About:** version, Android version, whether NEXA TV is the default Home app, and the real-vs-sample statement.
- **States:** loading, empty and error views; failures to launch an app are reported in a toast instead of crashing.

### Remote control

| Key | Action |
| --- | --- |
| D-pad / arrow keys | Move focus |
| OK / Enter | Activate |
| OK long-press or Menu on an app tile | Options (Open, App info) |
| Left from the first item of a row | Jump to the sidebar |
| Back | Close dialog / Quick Settings, then pop screens; on Home it stays on Home (a launcher never exits) |
| Home button while NEXA TV is in front | Returns to the Home screen |

## Becoming the default launcher

Android decides which app is Home, and only the user can choose. NEXA TV declares the `HOME`
intent filter and, from **Quick Settings → About NEXA TV → Set as default Home app**, asks Android to show its own
choice: the `RoleManager` Home-role dialog (API 29+) or, where that is missing, Android's Home / default-app
settings. It cannot silently replace the system launcher, and some TV builds hide or lock the Home
setting (see [Known limitations](#known-limitations)).

## Tech stack

Kotlin 2.0.21 · Jetpack Compose (BOM 2024.12.01) with foundation focus APIs · Material 3 for text and theme primitives · DataStore Preferences · Coil · coroutines / `StateFlow` · Gradle Kotlin DSL (AGP 8.7.3,
Gradle 8.10.2) · minSdk 26, targetSdk/compileSdk 35 · JDK 17.

Architecture is layered (`domain` → `data` / `platform` / `settings` → `ui`) with a hand-rolled container
instead of a DI framework; see [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md).

## Build

Requirements: JDK 17 and the Android SDK (platform 35 and its build-tools; Android Studio installs
them). Create `local.properties` with `sdk.dir=/path/to/Android/sdk` if Gradle cannot find the SDK (the file is git-ignored).

```bash
./gradlew testDebugUnitTest   # unit tests
./gradlew assembleDebug       # debug APK
```

Output: **`app/build/outputs/apk/debug/app-debug.apk`** (debug-signed; no release keys exist in this repo).

If you have no local toolchain, push the repository to GitHub: the **Android CI** workflow
(`.github/workflows/android.yml`) runs the unit tests, builds the debug APK and uploads it as the
`nexa-tv-debug-apk` artifact of the workflow run.

### Install and try

```bash
adb connect <tv-ip>:5555        # or start an Android TV emulator
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

NEXA TV then appears in the TV's app row. Work through [docs/TESTING.md](docs/TESTING.md).

## Project layout

```
app/src/main/java/com/nexatv/launcher/
  app/          Application, MainActivity, AppContainer, MessageBus, AppActions
  domain/       models, repository interfaces, use cases (pure Kotlin)
  data/         sample content repository, installed-apps repository
  platform/     PackageManager, launching, icons, network, default-Home, clock, wallpaper files
  settings/     DataStore-backed settings
  navigation/   Destination + back-stack navigator
  ui/theme      palette, type, density scaling
  ui/components Liquid Glass building blocks (GlassCard, hero, sidebar, top bar, dialogs ...)
  ui/screens    Home, Apps, Search, Catalog, Details, Wallpaper, Theme, About + ViewModels
app/src/test/   unit tests for the pure-Kotlin layers
docs/           architecture, manual test checklist, screenshots folder
```

## Screenshots

No screenshots are committed yet because none have been captured from a real build. See
[docs/screenshots/README.md](docs/screenshots/README.md) for how to add genuine ones.

## Known limitations

- **Not built or run** (see the notice at the top). Expect to fix small compile or runtime issues on the first build.
- **Content is sample-only.** There is no streaming catalogue, no playback and no real watch history. Third-party apps do not expose watch progress to launchers, so none is shown.
- **Brand package names are best-effort.** Known apps are matched by common package IDs and exact labels; a regional build with a different ID simply appears among the other apps.
- **Default Home:** the role dialog can be missing and Android's Home setting can be hidden or locked on some TVs and operator builds. NEXA TV then shows the nearest Android settings screen and explains it.
- **Wallpaper picker:** many TVs have no document picker. Use the app-specific wallpaper folder shown on the Wallpaper screen instead.
- **Blur** is applied behind dialogs and overlays through the window blur API on Android 12 (API 31)+ where the device compositor supports it; elsewhere (and everywhere on panels) the glass look is translucency and gradients. "Reduce effects" disables window blur.
- **Focus system:** uses Compose foundation focus rather than the (alpha) `tv-material` library, so behaviour is predictable but a few TV-specific niceties (for example automatic focus restoration on every edge case) are implemented by hand.
- **UI strings are hard-coded** in Kotlin, not yet moved to `strings.xml`, so the app is English-only.
- **Release builds** are unsigned/unconfigured on purpose; add your own signing config locally and never commit keys.

## Publishing to GitHub

```bash
cd nexa-tv
git init            # skip if the folder is already a git repository
git add -A
git commit -m "Initial commit: NEXA TV launcher"
git branch -M main
git remote add origin https://github.com/<you>/nexa-tv.git
git push -u origin main
```

Then open the repository's **Actions** tab, run (or wait for) **Android CI**, and download the
`nexa-tv-debug-apk` artifact. Choose and add a licence file before making the repository public.
