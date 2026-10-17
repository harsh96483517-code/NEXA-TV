# Manual test checklist

The Android build has not been run by the author of this repository (no Android SDK was available
while it was written). Work through this list on an Android TV emulator and, ideally, a real TV
before relying on the app. Tick what you verified and file issues for anything that fails.

## Set-up

- Android Studio Ladybug or newer, with an **Android TV (1080p)** system image, API 26 or newer
  (API 34/35 recommended). Create it under *Device Manager → Create Device → TV*.
- `./gradlew testDebugUnitTest assembleDebug`, then install with
  `adb install -r app/build/outputs/apk/debug/app-debug.apk` (or press Run in Android Studio).

## Launch and navigation

- [ ] App appears in the TV launcher row and starts without crashing.
- [ ] Home shows top bar, sidebar, hero carousel, *Your Apps*, *Continue Watching*, *Trending Now*.
- [ ] D-pad moves focus in every direction; the focused item always has a visible ring and glow.
- [ ] Focus never vanishes when a row scrolls; Left from the first item of a row goes to the sidebar.
- [ ] OK activates the focused item; Back closes dialogs and Quick Settings, then returns to Home.
- [ ] Back on Home does not exit the launcher.
- [ ] Hero rotates by itself, pauses while it has focus, and its dots can be focused and activated.
- [ ] Keyboard: arrow keys, Enter and Esc/Back behave like the D-pad; the Search field accepts typing.
- [ ] Search: D-pad Up/Down always leave the text field; Left leaves it while it is empty (so the sidebar stays reachable).

## Launcher behaviour

- [ ] *Your Apps* lists only apps that are really installed, and never shows NEXA TV itself.
- [ ] OK on an app launches it; long-press OK (or Menu) opens *Open / App info*.
- [ ] Installing or uninstalling an app (`adb install`, `adb uninstall`) updates the lists live.
- [ ] Apps screen lists all installed launchable apps; Search finds apps by name.
- [ ] *Quick Settings → About NEXA TV → Set as default Home app* opens the system dialog (or Android's
      Home settings). After accepting, the Home button returns to NEXA TV and About reports it as the default.
- [ ] Pressing the remote's Home button while NEXA TV is in front returns to the Home screen.

## Settings

- [ ] Theme: switching the accent recolours the UI and survives an app restart.
- [ ] Theme: *Compact sidebar* collapses the sidebar to icons; it expands over the content while focused.
- [ ] Time format (Quick Settings or Theme): Follow system / 12-hour / 24-hour changes the clock; survives a restart.
- [ ] Wallpaper: each built-in preset applies and persists.
- [ ] Wallpaper: *Choose from device* opens the picker where the device has one; the image persists after
      a reboot. Where there is no picker, an image copied into the app's wallpaper folder is listed.
- [ ] Theme: *Reduce effects* switches off window blur, the drifting wallpaper and the auto-rotating banner.
- [ ] *Quick Settings → App management* opens Android's application settings.
- [ ] About shows the version, the Android version and the default-Home state.

## States

- [ ] Search with no matches shows the empty state.
- [ ] Turning Wi-Fi/Ethernet off changes the network indicator.
- [ ] The layout looks right at 720p, 1080p and 4K (density scaling; create one emulator per resolution).

## Honest-content checks

- [ ] Hero, *Continue Watching* and *Trending Now* are labelled as sample content; *Play* explains
      that no streaming service is connected instead of pretending to play.
- [ ] No progress bar is shown for any real installed app.
