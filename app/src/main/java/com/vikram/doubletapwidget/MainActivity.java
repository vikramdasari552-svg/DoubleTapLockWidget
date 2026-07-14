package com.vikram.doubletapwidget;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public final class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button accessibility = findViewById(R.id.openAccessibility);
        Button widgets = findViewById(R.id.openWidgets);
        TextView status = findViewById(R.id.statusText);

        accessibility.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
        widgets.setOnClickListener(v -> Toast.makeText(
                this,
                "Long-press an empty home-screen area, then choose Widgets",
                Toast.LENGTH_LONG
        ).show());

        status.setText("The widget does not monitor the entire launcher. Only taps inside the widget's resized area are used.");
    }
}
