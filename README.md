# Harsh Reality — Your World On Screen

A small, fast native **Android TV home launcher** written in Kotlin and Jetpack Compose. It does two
things: it shows the **apps** installed on the TV, and it has a **Settings** panel. Nothing else.
Everything is reachable with the D-pad and shows a clear focus state.

> ## ⚠️ Build status: NOT TESTED
> This project was written in an environment **without the Android SDK and without access to the
> Gradle / Google Maven repositories**, so the Android app has **never been compiled, installed or run**
> by its author. What *was* verified: the pure-Kotlin logic (wallpaper parsing, clock formatting) was
> compiled with Kotlin 2.0.21 and its 12 unit tests pass, and the sources were cross-checked for
> missing imports and resources. Everything else (Compose UI, manifest, Gradle configuration, Android
> platform code) is unverified and may need small fixes on first build. See [Build](#build) and
> [docs/TESTING.md](docs/TESTING.md), and report anything that fails.

## Why it is light

An earlier version had a movie / TV-show home, search, hero banner and blur effects, and it lagged on
low-end TV boxes. This version removes all of that and keeps only what a launcher needs:

- Only two screens of content: the **Apps** grid and **Settings**.
- A **static wallpaper** drawn once into its own layer (no drifting, no cross-fade, no blur).
- One screen composed at a time (no animated screen transitions).
- Small app icons (96 px) and a 720p cap on user wallpapers.
- **Reduce effects is on by default** (no focus glow or slide animations).
- A **release APK** (R8-minified) is built next to the debug one. Use it on the TV: debug builds of
  Compose apps are noticeably slower.

## Features

- **Apps (home):** every launchable installed app in a grid. OK opens it; long-press OK or Menu opens
  *Open / App info*. The list updates live when apps are installed or removed. Harsh Reality itself is not listed.
- **Settings** (sidebar *Settings*): Wallpaper, Theme, Time format, App management, System settings, About.
- **Wallpaper:** four built-in procedural presets, an image chosen with the system document picker, or
  images copied into the app's own folder (no storage permission needed).
- **Theme:** four accents, compact sidebar, reduce effects, time format.
- **About:** version, Android version and whether Harsh Reality is the default Home app.
- **Top bar:** logo, network indicator, time and date. **Sidebar:** Apps and Settings.
- A failure to launch an app is reported in a toast instead of crashing.

There is no content catalogue, no search, no banner and no playback. Harsh Reality does not read watch
progress from other apps and never shows a progress bar.

### Remote control

| Key | Action |
| --- | --- |
| D-pad / arrow keys | Move focus |
| OK / Enter | Activate |
| OK long-press or Menu on an app tile | Options (Open, App info) |
| Left from the first app | Jump to the sidebar |
| Back | Close dialog / Settings, then pop screens; on the Apps screen it stays put (a launcher never exits) |
| Home button while Harsh Reality is in front | Returns to the Apps screen |

## Becoming the default launcher

Android decides which app is Home, and only the user can choose. Harsh Reality declares the `HOME`
intent filter and, from **Settings → About → Set as default Home app**, asks Android to show its own
choice: the `RoleManager` Home-role dialog (API 29+) or, where that is missing, Android's Home / default-app
settings. It cannot silently replace the system launcher, and some TV builds hide or lock the Home
setting (see [Known limitations](#known-limitations)).

## Tech stack

Kotlin 2.0.21 · Jetpack Compose (BOM 2024.12.01) with foundation focus APIs · Material 3 for text and theme
primitives · DataStore Preferences · Coil · coroutines / `StateFlow` · Gradle Kotlin DSL (AGP 8.7.3,
Gradle 8.10.2) · minSdk 26, targetSdk/compileSdk 35 · JDK 17.

Architecture is layered (`domain` → `data` / `platform` / `settings` → `ui`) with a hand-rolled container
instead of a DI framework; see [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md).

## Build

Requirements: JDK 17 and the Android SDK (platform 35 and its build-tools; Android Studio installs
them). Create `local.properties` with `sdk.dir=/path/to/Android/sdk` if Gradle cannot find the SDK (the file is git-ignored).

```bash
./gradlew testDebugUnitTest   # unit tests
./gradlew assembleRelease     # fast APK: app/build/outputs/apk/release/app-release.apk
./gradlew assembleDebug       # debug APK: app/build/outputs/apk/debug/app-debug.apk
```

The release APK is signed with the local auto-generated Android debug key so it installs for sideloading;
no key file is stored in this repo. For a Play Store build, add your own git-ignored signing config.

If you have no local toolchain, push the repository to GitHub: the **Android CI** workflow
(`.github/workflows/android.yml`) runs the unit tests, builds both APKs and uploads them as the
`harsh-reality-release-apk` and `harsh-reality-debug-apk` artifacts of the workflow run.

### Install and try

```bash
adb connect <tv-ip>:5555        # or start an Android TV emulator
adb install -r app/build/outputs/apk/release/app-release.apk
```

If you installed a debug build before, uninstall it first (`adb uninstall com.nexatv.launcher`): the two
builds are signed with different keys on a fresh CI runner. Then work through [docs/TESTING.md](docs/TESTING.md).

## Project layout

```
app/src/main/java/com/nexatv/launcher/
  app/          Application, MainActivity, AppContainer, MessageBus, AppActions
  domain/       models and repository interfaces (pure Kotlin)
  data/         installed-apps repository
  platform/     PackageManager, launching, icons, network, default-Home, clock, wallpaper files
  settings/     DataStore-backed settings
  navigation/   Destination + back-stack navigator
  ui/theme      palette, type, density scaling
  ui/components GlassCard, sidebar, top bar, dialogs, wallpaper ...
  ui/screens    Apps, Wallpaper, Theme, About + ViewModels
app/src/test/   unit tests for the pure-Kotlin layers
docs/           architecture, manual test checklist, screenshots folder
```

## Screenshots

No screenshots are committed yet because none have been captured from a real build. See
[docs/screenshots/README.md](docs/screenshots/README.md) for how to add genuine ones.

## Known limitations

- **Not built or run** (see the notice at the top). Expect to fix small compile or runtime issues on the first build.
- **Performance is untested on real hardware.** The changes above remove the known heavy parts, but
  how smooth it feels depends on the TV box; try the release APK first.
- **Brand artwork:** the wallpapers are procedural, so there are no photos. Your own images work.
- **Default Home:** the role dialog can be missing and Android's Home setting can be hidden or locked on some TVs and operator builds. Harsh Reality then shows the nearest Android settings screen and explains it.
- **Wallpaper picker:** many TVs have no document picker. Use the app-specific wallpaper folder shown on the Wallpaper screen instead.
- **Focus system:** uses Compose foundation focus rather than the (alpha) `tv-material` library, so behaviour is predictable but a few TV-specific niceties are implemented by hand.
- **UI strings are hard-coded** in Kotlin, not yet moved to `strings.xml`, so the app is English-only.
- **The package id is still `com.nexatv.launcher`** (only the display name changed to Harsh Reality).

## Publishing to GitHub

```bash
cd harsh-reality
git init            # skip if the folder is already a git repository
git add -A
git commit -m "Initial commit: Harsh Reality launcher"
git branch -M main
git remote add origin https://github.com/<you>/harsh-reality.git
git push -u origin main
```

Then open the repository's **Actions** tab, run (or wait for) **Android CI**, and download the
`harsh-reality-release-apk` artifact. Choose and add a licence file before making the repository public.
