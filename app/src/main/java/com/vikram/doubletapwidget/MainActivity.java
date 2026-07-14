package com.vikram.doubletapwidget;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public final class MainActivity extends Activity {
    private static final int CAMERA_REQUEST = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button accessibility = findViewById(R.id.openAccessibility);
        Button cameraPermission = findViewById(R.id.cameraPermission);
        Button widgets = findViewById(R.id.openWidgets);
        SeekBar interval = findViewById(R.id.tapInterval);
        SeekBar delay = findViewById(R.id.doubleDelay);
        TextView intervalValue = findViewById(R.id.tapIntervalValue);
        TextView delayValue = findViewById(R.id.doubleDelayValue);
        CheckBox triple = findViewById(R.id.enableTripleTap);
        CheckBox haptic = findViewById(R.id.enableHaptic);
        Spinner fourAction = findViewById(R.id.fourTapAction);

        accessibility.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
        widgets.setOnClickListener(v -> Toast.makeText(this,
                "Long-press an empty home-screen area, choose Widgets, then resize this widget over empty cells.",
                Toast.LENGTH_LONG).show());
        cameraPermission.setOnClickListener(v -> requestPermissions(
                new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST));

        interval.setMax(500);
        interval.setProgress(Preferences.tapInterval(this) - 200);
        intervalValue.setText(Preferences.tapInterval(this) + " ms");
        interval.setOnSeekBarChangeListener(simpleListener(value -> {
            int actual = value + 200;
            Preferences.setTapInterval(this, actual);
            intervalValue.setText(actual + " ms");
        }));

        delay.setMax(500);
        delay.setProgress(Preferences.doubleActionDelay(this) - 100);
        delayValue.setText(Preferences.doubleActionDelay(this) + " ms");
        delay.setOnSeekBarChangeListener(simpleListener(value -> {
            int actual = value + 100;
            Preferences.setDoubleActionDelay(this, actual);
            delayValue.setText(actual + " ms");
        }));

        triple.setChecked(Preferences.tripleEnabled(this));
        triple.setOnCheckedChangeListener((button, checked) -> Preferences.setTripleEnabled(this, checked));

        haptic.setChecked(Preferences.hapticEnabled(this));
        haptic.setOnCheckedChangeListener((button, checked) -> Preferences.setHapticEnabled(this, checked));

        String[] actions = {"Disabled", "Recent apps", "Notifications", "Quick Settings"};
        fourAction.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, actions));
        fourAction.setSelection(Preferences.fourTapAction(this));
        fourAction.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view,
                                                 int position, long id) {
                Preferences.setFourTapAction(MainActivity.this, position);
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
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
            Toast.makeText(this, granted ? "Flashlight permission enabled" : "Flashlight permission denied",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
