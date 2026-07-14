# Double Tap Lock Widget 2.0

A transparent, resizable Pixel Launcher widget that provides tap-sequence actions without a full-screen overlay.

## Actions

- Double tap: lock the screen
- Triple tap: toggle the flashlight
- Optional four taps: Recent apps, Notifications, or Quick Settings
- Configurable maximum gap between taps
- Configurable delay before the double-tap action
- Optional haptic feedback

## Setup

1. Install the APK.
2. Open the app and enable the accessibility service.
3. Allow Camera permission for flashlight control.
4. Long-press an empty home-screen area, choose Widgets, add Double Tap Lock Widget, and resize it over empty cells.

## Limitation

Android home-screen widgets use `RemoteViews`, so they receive click actions but not arbitrary raw multi-touch or swipe listeners. Two-finger swipe gestures are therefore not implemented. The widget does not use an overlay and cannot freeze Recents or the app drawer.
