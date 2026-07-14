# Double Tap Lock Widget

A transparent, resizable Android home-screen widget. Double tap inside its area to lock the screen.

## Why a widget

A widget does not install a full-screen touch overlay. It therefore does not freeze Recents, the app drawer, app shortcuts, or other applications. Only the space occupied by the widget is reserved for double-tap detection.

## Setup

1. Install the APK.
2. Open the app and enable **Double Tap Lock Service** in Accessibility settings.
3. Long-press an empty area of the Pixel Launcher home screen.
4. Choose **Widgets**.
5. Find **Double Tap Lock Widget**.
6. Add it and resize it over the empty home-screen space.
7. Double tap inside that transparent area to lock.

## Limitations

Android widgets do not receive raw touch streams. Each widget tap launches a transparent no-history activity, which records the tap time. Two taps within 450 ms trigger the accessibility lock action. Single taps inside the widget area are consumed by the widget and will not interact with wallpaper or icons underneath.
