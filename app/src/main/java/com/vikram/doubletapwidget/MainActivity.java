package com.vikram.doubletapwidget;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public final class MainActivity extends Activity {
    private static final int CAMERA_REQUEST = 20;
    private static final int[] ACTION_VALUES = {
            Preferences.ACTION_DISABLED,
            Preferences.ACTION_LOCK,
            Preferences.ACTION_TORCH,
            Preferences.ACTION_RECENTS,
            Preferences.ACTION_NOTIFICATIONS,
            Preferences.ACTION_QUICK_SETTINGS
    };

    private TextView serviceStatus;
    private TextView cameraStatus;
    private TextView actionCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serviceStatus = findViewById(R.id.serviceStatus);
        cameraStatus = findViewById(R.id.cameraStatus);
        actionCount = findViewById(R.id.actionCount);

        findViewById(R.id.openAccessibility).setOnClickListener(v ->
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
        findViewById(R.id.cameraPermission).setOnClickListener(v -> requestPermissions(
                new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST));
        findViewById(R.id.openWidgets).setOnClickListener(v -> Toast.makeText(this,
                R.string.widget_setup_help, Toast.LENGTH_LONG).show());
        findViewById(R.id.testHaptic).setOnClickListener(v -> ActionExecutor.testHaptic(this));
        findViewById(R.id.resetDefaults).setOnClickListener(v -> {
            Preferences.resetDefaults(this);
            DoubleTapWidgetProvider.refreshAll(this);
            recreate();
        });

        setupTiming();
        setupActions();
        setupFeedback();
        setupPreset();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshStatus();
    }

    private void setupTiming() {
        SeekBar interval = findViewById(R.id.tapInterval);
        SeekBar delay = findViewById(R.id.actionDelay);
        TextView intervalValue = findViewById(R.id.tapIntervalValue);
        TextView delayValue = findViewById(R.id.actionDelayValue);

        interval.setMax(600);
        interval.setProgress(Preferences.tapInterval(this) - 200);
        intervalValue.setText(getString(R.string.ms_value, Preferences.tapInterval(this)));
        interval.setOnSeekBarChangeListener(simpleListener(progress -> {
            int value = progress + 200;
            Preferences.setTapInterval(this, value);
            intervalValue.setText(getString(R.string.ms_value, value));
        }));

        delay.setMax(600);
        delay.setProgress(Preferences.actionDelay(this) - 100);
        delayValue.setText(getString(R.string.ms_value, Preferences.actionDelay(this)));
        delay.setOnSeekBarChangeListener(simpleListener(progress -> {
            int value = progress + 100;
            Preferences.setActionDelay(this, value);
            delayValue.setText(getString(R.string.ms_value, value));
        }));
    }

    private void setupActions() {
        String[] labels = getResources().getStringArray(R.array.gesture_actions);
        setupActionSpinner(findViewById(R.id.doubleTapAction), labels,
                Preferences.doubleAction(this), value -> Preferences.setDoubleAction(this, value));
        setupActionSpinner(findViewById(R.id.tripleTapAction), labels,
                Preferences.tripleAction(this), value -> Preferences.setTripleAction(this, value));
        setupActionSpinner(findViewById(R.id.fourTapAction), labels,
                Preferences.fourTapAction(this), value -> Preferences.setFourTapAction(this, value));
    }

    private void setupActionSpinner(Spinner spinner, String[] labels, int selectedAction,
                                    java.util.function.IntConsumer setter) {
        spinner.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, labels));
        spinner.setSelection(indexForAction(selectedAction));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setter.accept(ACTION_VALUES[position]);
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupFeedback() {
        CheckBox haptic = findViewById(R.id.enableHaptic);
        CheckBox widgetArea = findViewById(R.id.showWidgetArea);
        SeekBar hapticDuration = findViewById(R.id.hapticDuration);
        TextView hapticValue = findViewById(R.id.hapticDurationValue);

        haptic.setChecked(Preferences.hapticEnabled(this));
        haptic.setOnCheckedChangeListener((button, checked) ->
                Preferences.setHapticEnabled(this, checked));

        widgetArea.setChecked(Preferences.showWidgetArea(this));
        widgetArea.setOnCheckedChangeListener((button, checked) -> {
            Preferences.setShowWidgetArea(this, checked);
            DoubleTapWidgetProvider.refreshAll(this);
        });

        hapticDuration.setMax(90);
        hapticDuration.setProgress(Preferences.hapticDuration(this) - 10);
        hapticValue.setText(getString(R.string.ms_value, Preferences.hapticDuration(this)));
        hapticDuration.setOnSeekBarChangeListener(simpleListener(progress -> {
            int value = progress + 10;
            Preferences.setHapticDuration(this, value);
            hapticValue.setText(getString(R.string.ms_value, value));
        }));
    }

    private void setupPreset() {
        Spinner preset = findViewById(R.id.timingPreset);
        String[] labels = getResources().getStringArray(R.array.timing_presets);
        preset.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, labels));
        preset.setSelection(0);
        preset.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private boolean first = true;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (first) { first = false; return; }
                if (position == 1) applyPreset(300, 180);
                if (position == 2) applyPreset(420, 300);
                if (position == 3) applyPreset(600, 450);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void applyPreset(int gap, int delay) {
        Preferences.setTapInterval(this, gap);
        Preferences.setActionDelay(this, delay);
        recreate();
    }

    private void refreshStatus() {
        boolean service = ServiceStatus.isAccessibilityEnabled(this);
        boolean camera = checkSelfPermission(Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
        serviceStatus.setText(service ? R.string.status_enabled : R.string.status_disabled);
        cameraStatus.setText(camera ? R.string.status_granted : R.string.status_not_granted);
        actionCount.setText(getString(R.string.action_count_value, Preferences.actionCount(this)));
    }

    private int indexForAction(int action) {
        for (int i = 0; i < ACTION_VALUES.length; i++) {
            if (ACTION_VALUES[i] == action) return i;
        }
        return 0;
    }

    private SeekBar.OnSeekBarChangeListener simpleListener(java.util.function.IntConsumer consumer) {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) consumer.accept(progress);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        };
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        super.onRequestPermissionsResult(requestCode, permissions, results);
        if (requestCode == CAMERA_REQUEST) {
            boolean granted = results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED;
            Toast.makeText(this, granted ? R.string.flashlight_permission_enabled
                    : R.string.flashlight_permission_denied, Toast.LENGTH_SHORT).show();
            refreshStatus();
        }
    }
}
