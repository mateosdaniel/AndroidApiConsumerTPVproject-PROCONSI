package com.proconsi.electrobazar.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.proconsi.electrobazar.R;
import com.proconsi.electrobazar.viewmodels.AuthViewModel;
import com.proconsi.electrobazar.viewmodels.MainViewModel;


public class LoginActivity extends AppCompatActivity {

    private AuthViewModel authViewModel;
    private TextInputEditText usernameInput;
    private TextInputEditText passwordInput;
    private MaterialButton loginButton;
    private ProgressBar loginProgress;
    private TextView errorText;
    private TextView loginAppName;
    private MainViewModel mainViewModel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.proconsi.electrobazar.utils.ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        com.proconsi.electrobazar.utils.ThemeManager.applyColors(this);
        com.proconsi.electrobazar.utils.ThemeManager.applyFontToView(getWindow().getDecorView(), this);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        loginProgress = findViewById(R.id.loginProgress);
        errorText = findViewById(R.id.errorText);
        loginAppName = findViewById(R.id.loginAppName);
        
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        mainViewModel.getCompanySettings().observe(this, settings -> {
            if (settings != null && settings.getAppName() != null) {
                loginAppName.setText(settings.getAppName());
            }
        });


        loginButton.setOnClickListener(v -> handleLogin());

        authViewModel.getLoginResult().observe(this, result -> {
            loginProgress.setVisibility(View.GONE);
            loginButton.setEnabled(true);

            if (result.getSuccess() != null) {
                authViewModel.saveSession(result.getSuccess());
                Toast.makeText(this, "Bienvenido, " + result.getSuccess().getWorker().getUsername(), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                errorText.setText(result.getError());
                errorText.setVisibility(View.VISIBLE);
            }
        });
    }

    private void handleLogin() {
        String username = usernameInput.getText() != null ? usernameInput.getText().toString().trim() : "";
        String password = passwordInput.getText() != null ? passwordInput.getText().toString().trim() : "";

        if (username.isEmpty() || password.isEmpty()) {
            errorText.setText("Por favor, rellene todos los campos");
            errorText.setVisibility(View.VISIBLE);
            return;
        }

        errorText.setVisibility(View.GONE);
        loginButton.setEnabled(false);
        loginProgress.setVisibility(View.VISIBLE);
        
        authViewModel.login(username, password);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
