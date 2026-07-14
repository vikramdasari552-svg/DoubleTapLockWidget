package com.vikram.doubletapwidget;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Toast;

public final class ActionExecutor {
    private ActionExecutor() {}

    public static void execute(Context context, int action) {
        Context app = context.getApplicationContext();
        boolean completed;
        switch (action) {
            case Preferences.ACTION_LOCK:
                completed = LockAccessibilityService.lockScreen();
                if (!completed) showAccessibilityMessage(app);
                break;
            case Preferences.ACTION_TORCH:
                completed = toggleTorch(app);
                break;
            case Preferences.ACTION_RECENTS:
                completed = performGlobal(app, AccessibilityService.GLOBAL_ACTION_RECENTS);
                break;
            case Preferences.ACTION_NOTIFICATIONS:
                completed = performGlobal(app, AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS);
                break;
            case Preferences.ACTION_QUICK_SETTINGS:
                completed = performGlobal(app, AccessibilityService.GLOBAL_ACTION_QUICK_SETTINGS);
                break;
            default:
                return;
        }

        if (completed) {
            Preferences.incrementActionCount(app);
            vibrate(app);
        }
    }

    public static void testHaptic(Context context) {
        vibrate(context.getApplicationContext());
    }

    private static boolean performGlobal(Context context, int action) {
        boolean ok = LockAccessibilityService.performAction(action);
        if (!ok) showAccessibilityMessage(context);
        return ok;
    }

    private static void showAccessibilityMessage(Context context) {
        Toast.makeText(context, R.string.enable_service_first, Toast.LENGTH_SHORT).show();
    }

    private static boolean toggleTorch(Context context) {
        if (context.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, R.string.allow_camera_first, Toast.LENGTH_LONG).show();
            return false;
        }

        CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        if (manager == null) return false;
        try {
            String cameraId = findFlashCamera(manager);
            if (cameraId == null) {
                Toast.makeText(context, R.string.no_flashlight, Toast.LENGTH_SHORT).show();
                return false;
            }
            boolean current = context.getSharedPreferences("torch_state", Context.MODE_PRIVATE)
                    .getBoolean("enabled", false);
            boolean next = !current;
            manager.setTorchMode(cameraId, next);
            context.getSharedPreferences("torch_state", Context.MODE_PRIVATE)
                    .edit().putBoolean("enabled", next).apply();
            Toast.makeText(context, next ? R.string.flashlight_on : R.string.flashlight_off,
                    Toast.LENGTH_SHORT).show();
            return true;
        } catch (CameraAccessException | SecurityException e) {
            Toast.makeText(context, R.string.flashlight_unavailable, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private static String findFlashCamera(CameraManager manager) throws CameraAccessException {
        for (String id : manager.getCameraIdList()) {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(id);
            Boolean flash = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
            if (Boolean.TRUE.equals(flash)
                    && facing != null
                    && facing == CameraCharacteristics.LENS_FACING_BACK) {
                return id;
            }
        }
        return null;
    }

    private static void vibrate(Context context) {
        if (!Preferences.hapticEnabled(context)) return;
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(VibrationEffect.createOneShot(
                    Preferences.hapticDuration(context),
                    VibrationEffect.DEFAULT_AMPLITUDE));
        }
    }
}
