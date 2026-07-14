package com.vikram.doubletapwidget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Toast;

public final class TapReceiver extends BroadcastReceiver {
    public static final String ACTION_WIDGET_TAP = "com.vikram.doubletapwidget.WIDGET_TAP";

    private static final Handler HANDLER = new Handler(Looper.getMainLooper());
    private static long lastTapAt;
    private static int tapCount;
    private static Runnable pendingAction;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!ACTION_WIDGET_TAP.equals(intent.getAction())) return;

        Context app = context.getApplicationContext();
        long now = SystemClock.elapsedRealtime();
        int interval = Preferences.tapInterval(app);

        if (lastTapAt == 0L || now - lastTapAt > interval) {
            reset();
            tapCount = 1;
        } else {
            tapCount++;
        }
        lastTapAt = now;

        if (pendingAction != null) HANDLER.removeCallbacks(pendingAction);

        if (tapCount == 2) {
            if (!Preferences.tripleEnabled(app)) {
                performLock(app);
                reset();
                return;
            }
            pendingAction = () -> {
                performLock(app);
                reset();
            };
            HANDLER.postDelayed(pendingAction, Preferences.doubleActionDelay(app));
        } else if (tapCount == 3 && Preferences.tripleEnabled(app)) {
            if (Preferences.fourTapAction(app) == 0) {
                toggleTorch(app);
                reset();
            } else {
                pendingAction = () -> {
                    toggleTorch(app);
                    reset();
                };
                HANDLER.postDelayed(pendingAction, Preferences.doubleActionDelay(app));
            }
        } else if (tapCount >= 4) {
            performFourTapAction(app);
            reset();
        }
    }

    private static void performLock(Context context) {
        haptic(context);
        if (!LockAccessibilityService.lockScreen()) {
            Toast.makeText(context, "Enable Double Tap Lock Service first", Toast.LENGTH_SHORT).show();
        }
    }

    private static void toggleTorch(Context context) {
        if (context.checkSelfPermission(android.Manifest.permission.CAMERA)
                != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Allow Camera permission for flashlight control", Toast.LENGTH_LONG).show();
            return;
        }

        CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraId = findFlashCamera(manager);
            if (cameraId == null) {
                Toast.makeText(context, "No flashlight was found", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean next = !context.getSharedPreferences("torch_state", Context.MODE_PRIVATE)
                    .getBoolean("enabled", false);
            manager.setTorchMode(cameraId, next);
            context.getSharedPreferences("torch_state", Context.MODE_PRIVATE)
                    .edit().putBoolean("enabled", next).apply();
            haptic(context);
            Toast.makeText(context, next ? "Flashlight on" : "Flashlight off", Toast.LENGTH_SHORT).show();
        } catch (CameraAccessException | SecurityException e) {
            Toast.makeText(context, "Flashlight is temporarily unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    private static String findFlashCamera(CameraManager manager) throws CameraAccessException {
        for (String id : manager.getCameraIdList()) {
            CameraCharacteristics c = manager.getCameraCharacteristics(id);
            Boolean flash = c.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            Integer facing = c.get(CameraCharacteristics.LENS_FACING);
            if (Boolean.TRUE.equals(flash) && facing != null
                    && facing == CameraCharacteristics.LENS_FACING_BACK) {
                return id;
            }
        }
        return null;
    }

    private static void performFourTapAction(Context context) {
        int action = Preferences.fourTapAction(context);
        boolean ok = true;
        switch (action) {
            case 1:
                ok = LockAccessibilityService.performAction(
                        android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_RECENTS);
                break;
            case 2:
                ok = LockAccessibilityService.performAction(
                        android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS);
                break;
            case 3:
                ok = LockAccessibilityService.performAction(
                        android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_QUICK_SETTINGS);
                break;
            default:
                return;
        }
        haptic(context);
        if (!ok) Toast.makeText(context, "Enable Double Tap Lock Service first", Toast.LENGTH_SHORT).show();
    }

    private static void haptic(Context context) {
        if (!Preferences.hapticEnabled(context)) return;
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE));
        }
    }

    private static void reset() {
        if (pendingAction != null) HANDLER.removeCallbacks(pendingAction);
        pendingAction = null;
        lastTapAt = 0L;
        tapCount = 0;
    }
}
