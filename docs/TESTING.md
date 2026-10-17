# Manual test checklist

The Android build has not been run by the author of this repository (no Android SDK was available
while it was written). Work through this list on an Android TV emulator and, ideally, a real TV
before relying on the app. Tick what you verified and file issues for anything that fails.

## Set-up

- Android Studio Ladybug or newer, with an **Android TV (1080p)** system image, API 26 or newer
  (API 34/35 recommended). Create it under *Device Manager → Create Device → TV*.
- `./gradlew testDebugUnitTest assembleRelease`, then install with
  `adb install -r app/build/outputs/apk/release/app-release.apk`. The debug APK
  (`assembleDebug`) works too but is slower; judge smoothness on the **release** APK.

## Launch and navigation

- [ ] App appears in the TV launcher row and starts without crashing.
- [ ] The Apps screen shows the top bar (logo, network, clock), the sidebar (Apps, Settings) and the app grid.
- [ ] D-pad moves focus in every direction; the focused item always has a visible ring.
- [ ] Focus never vanishes when the grid scrolls; Left from the first app goes to the sidebar.
- [ ] OK activates the focused item; Back closes dialogs and Settings, then returns to Apps.
- [ ] Back on the Apps screen does not exit the launcher.
- [ ] Keyboard: arrow keys, Enter and Esc/Back behave like the D-pad.

## Launcher behaviour

- [ ] The grid lists only apps that are really installed, and never shows Harsh Reality itself.
- [ ] OK on an app launches it; long-press OK (or Menu) opens *Open / App info*.
- [ ] Installing or uninstalling an app (`adb install`, `adb uninstall`) updates the grid live.
- [ ] *Settings → About → Set as default Home app* opens the system dialog (or Android's Home settings).
      After accepting, the Home button returns to Harsh Reality and About reports it as the default.
- [ ] Pressing the remote's Home button while Harsh Reality is in front returns to the Apps screen.

## Settings

- [ ] Theme: switching the accent recolours the UI and survives an app restart.
- [ ] Theme: *Compact sidebar* collapses the sidebar to icons; it expands over the content while focused.
- [ ] Time format (Settings or Theme): Follow system / 12-hour / 24-hour changes the clock; survives a restart.
- [ ] Wallpaper: each built-in preset applies and persists.
- [ ] Wallpaper: *Choose from device* opens the picker where the device has one; the image persists after
      a reboot. Where there is no picker, an image copied into the app's wallpaper folder is listed.
- [ ] Theme: *Reduce effects* is on by default; turning it off adds the focus glow and slide animations.
- [ ] *Settings → App management* opens Android's application settings.
- [ ] About shows the version, the Android version and the default-Home state.

## States and performance

- [ ] Turning Wi-Fi/Ethernet off changes the network indicator.
- [ ] The layout looks right at 720p, 1080p and 4K (density scaling; create one emulator per resolution).
- [ ] Scrolling a large app grid and opening Settings feels smooth on the release APK.

## Honest-content checks

- [ ] There is no movie, show, search or banner content anywhere in the app.
- [ ] No progress bar is shown for any installed app.
