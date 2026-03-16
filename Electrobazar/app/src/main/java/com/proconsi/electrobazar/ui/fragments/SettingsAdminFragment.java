package com.proconsi.electrobazar.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.proconsi.electrobazar.databinding.FragmentSettingsAdminBinding;
import com.proconsi.electrobazar.models.CompanySettings;
import com.proconsi.electrobazar.network.RetrofitClient;
import com.proconsi.electrobazar.network.ApiService;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsAdminFragment extends Fragment {

    private FragmentSettingsAdminBinding binding;
    private ApiService apiService;
    private CompanySettings currentSettings;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsAdminBinding.inflate(inflater, container, false);
        apiService = RetrofitClient.getInstance().getApi();

        loadSettings();

        binding.btnSaveSettings.setOnClickListener(v -> saveSettings());

        return binding.getRoot();
    }

    private void loadSettings() {
        apiService.getCompanySettings().enqueue(new Callback<CompanySettings>() {
            @Override
            public void onResponse(Call<CompanySettings> call, Response<CompanySettings> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentSettings = response.body();
                    populateFields();
                } else {
                    Toast.makeText(getContext(), "Error al cargar configuración", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CompanySettings> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateFields() {
        binding.etAppName.setText(currentSettings.getAppName());
        binding.etCompanyName.setText(currentSettings.getName());
        binding.etCompanyCif.setText(currentSettings.getCif());
        binding.etCompanyAddress.setText(currentSettings.getAddress());
        binding.etCompanyCity.setText(currentSettings.getCity());
        binding.etCompanyPostalCode.setText(currentSettings.getPostalCode());
        binding.etCompanyPhone.setText(currentSettings.getPhone());
        binding.etCompanyEmail.setText(currentSettings.getEmail());
    }

    private void saveSettings() {
        if (currentSettings == null) return;

        currentSettings.setAppName(binding.etAppName.getText().toString());
        currentSettings.setName(binding.etCompanyName.getText().toString());
        currentSettings.setCif(binding.etCompanyCif.getText().toString());
        currentSettings.setAddress(binding.etCompanyAddress.getText().toString());
        currentSettings.setCity(binding.etCompanyCity.getText().toString());
        currentSettings.setPostalCode(binding.etCompanyPostalCode.getText().toString());
        currentSettings.setPhone(binding.etCompanyPhone.getText().toString());
        currentSettings.setEmail(binding.etCompanyEmail.getText().toString());

        apiService.saveCompanySettings(currentSettings).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Configuración guardada correctamente", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
