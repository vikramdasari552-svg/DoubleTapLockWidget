# Double Tap Lock Widget 2.1

A transparent, resizable home-screen widget that performs configurable multi-tap actions without a full-screen overlay.

## Phase 1 — stabilization

- Deterministic gesture state machine: one settled tap sequence produces one action.
- Central action executor for lock, flashlight, Recents, Notifications, and Quick Settings.
- Accessibility and camera-permission status checks.
- Safe defaults: double tap locks, triple tap toggles the flashlight, four taps are disabled.
- Settings validation and reset-to-default support.
- Successful-action counter for basic diagnostics.

## Phase 2 — premium settings

- Card-based settings screen.
- Configurable actions for double, triple, and four taps.
- Fast, Balanced, and Relaxed timing presets.
- Manual tap-gap and decision-delay controls.
- Adjustable haptic duration and test button.
- Optional visible widget boundary while positioning; fully transparent during normal use.
- Live setup status for accessibility and flashlight permission.

## Setup

1. Install the APK.
2. Open the app and enable the accessibility service.
3. Allow Camera permission only if a flashlight action is configured.
4. Long-press an empty home-screen area, choose **Widgets**, add **Double Tap Lock Widget**, and resize it over empty cells.
5. Configure actions and timing in the app.

## Technical boundary

Android widgets use `RemoteViews`; they receive click actions but not arbitrary raw swipe, long-press, or multi-touch streams. This app intentionally uses no overlay, so it cannot freeze Recents, the app drawer, shortcut menus, or other applications.
