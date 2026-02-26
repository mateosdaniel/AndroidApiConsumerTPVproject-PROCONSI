package com.example.electrobazar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.electrobazar.network.RetrofitClient;
import com.example.electrobazar.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class PreferencesActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private SwitchMaterial switchDarkMode;
    private android.widget.EditText etServerUrl;
    private TextView tvSessionInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        sessionManager = new SessionManager(this);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        switchDarkMode = findViewById(R.id.switchDarkMode);
        etServerUrl = findViewById(R.id.etServerUrl);
        tvSessionInfo = findViewById(R.id.tvSessionInfo);
        MaterialButton btnLogout = findViewById(R.id.btnLogout);
        MaterialButton btnSave = findViewById(R.id.btnSavePrefs);

        // Load current server URL from shared preferences
        android.content.SharedPreferences prefs = getSharedPreferences("ElectroBazarPrefs", MODE_PRIVATE);
        String savedUrl = prefs.getString("server_url", RetrofitClient.getBaseUrl());
        etServerUrl.setText(savedUrl);

        // Show current worker in session info
        if (sessionManager.getWorker() != null) {
            tvSessionInfo.setText("Sesión activa: " + sessionManager.getWorker().getUsername());
        }

        btnLogout.setOnClickListener(v -> {
            sessionManager.logout();
            startActivity(new Intent(this, LoginActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        });

        btnSave.setOnClickListener(v -> {
            String url = etServerUrl.getText().toString().trim();
            if (!url.isEmpty()) {
                if (!url.endsWith("/"))
                    url += "/";
                prefs.edit().putString("server_url", url).apply();
                RetrofitClient.setBaseUrl(url);
                Toast.makeText(this, "Configuración guardada", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "La URL no puede estar vacía", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
