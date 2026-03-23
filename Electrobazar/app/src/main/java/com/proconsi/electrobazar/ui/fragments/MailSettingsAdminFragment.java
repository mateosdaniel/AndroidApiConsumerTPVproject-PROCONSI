package com.proconsi.electrobazar.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.proconsi.electrobazar.R;
import com.proconsi.electrobazar.network.RetrofitClient;
import com.proconsi.electrobazar.network.ApiService;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MailSettingsAdminFragment extends Fragment {

    private ApiService apiService;
    private EditText etHost, etPort, etUser, etPass;
    private Button btnSave;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mail_settings_admin, container, false);
        apiService = RetrofitClient.getInstance().getApi();

        etHost = view.findViewById(R.id.etMailHost);
        etPort = view.findViewById(R.id.etMailPort);
        etUser = view.findViewById(R.id.etMailUsername);
        etPass = view.findViewById(R.id.etMailPassword);
        btnSave = view.findViewById(R.id.btnSaveMailSettings);

        loadMailSettings();

        btnSave.setOnClickListener(v -> saveMailSettings());

        return view;
    }

    private void loadMailSettings() {
        apiService.getMailSettings().enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, String> s = response.body();
                    etHost.setText(s.get("host"));
                    etPort.setText(s.get("port"));
                    etUser.setText(s.get("username"));
                    etPass.setText(s.get("password"));
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Toast.makeText(getContext(), "Error al cargar configuración", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveMailSettings() {
        Map<String, String> body = new HashMap<>();
        body.put("host", etHost.getText().toString());
        body.put("port", etPort.getText().toString());
        body.put("username", etUser.getText().toString());
        body.put("password", etPass.getText().toString());

        apiService.saveMailSettings(body).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Configuración de correo guardada", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Error al guardar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
