# Screenshots

This folder is intentionally empty of images: no screenshots are committed until they have been
captured from a real build of the app. Please do not add mock-ups here.

Capture genuine screenshots from an Android TV emulator or device:

```bash
adb exec-out screencap -p > docs/screenshots/apps.png
```

Suggested set: `apps.png`, `settings.png`, `wallpaper.png`, `theme.png`, `about.png`. Then reference
them from the main README, e.g. `![Apps](docs/screenshots/apps.png)`.
