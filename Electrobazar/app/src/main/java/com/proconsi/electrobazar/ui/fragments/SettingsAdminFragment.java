package com.proconsi.electrobazar.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.proconsi.electrobazar.databinding.FragmentSettingsAdminBinding;
import com.proconsi.electrobazar.models.CompanySettings;
import com.proconsi.electrobazar.models.UpdatePinRequest;
import com.proconsi.electrobazar.network.RetrofitClient;
import com.proconsi.electrobazar.network.ApiService;
import com.proconsi.electrobazar.viewmodels.MainViewModel;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsAdminFragment extends Fragment {

    private FragmentSettingsAdminBinding binding;
    private ApiService apiService;
    private CompanySettings currentSettings;
    private MainViewModel mainViewModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsAdminBinding.inflate(inflater, container, false);
        apiService = RetrofitClient.getInstance().getApi();
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);


        // 1. Company Settings
        loadSettings();
        binding.btnSaveSettings.setOnClickListener(v -> saveSettings());

        // 2. Security (PIN)
        binding.btnUpdatePin.setOnClickListener(v -> updatePin());

        // 3. SMTP Settings
        loadMailSettings();
        binding.btnSaveMailSettings.setOnClickListener(v -> saveMailSettings());
        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        return binding.getRoot();
    }

    // --- Company Settings Logic ---

    private void loadSettings() {
        apiService.getCompanySettings().enqueue(new Callback<CompanySettings>() {
            @Override
            public void onResponse(Call<CompanySettings> call, Response<CompanySettings> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentSettings = response.body();
                    populateFields();
                } else {
                    Toast.makeText(getContext(), "Error al cargar configuración de empresa", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CompanySettings> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red al cargar empresa", Toast.LENGTH_SHORT).show();
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
        binding.etCompanyWebsite.setText(currentSettings.getWebsite());
        binding.etRegistroMercantil.setText(currentSettings.getRegistroMercantil());
        binding.etInvoiceFooter.setText(currentSettings.getInvoiceFooterText());
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
        currentSettings.setWebsite(binding.etCompanyWebsite.getText().toString());
        currentSettings.setRegistroMercantil(binding.etRegistroMercantil.getText().toString());
        currentSettings.setInvoiceFooterText(binding.etInvoiceFooter.getText().toString());

        apiService.saveCompanySettings(currentSettings).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Configuración de empresa guardada", Toast.LENGTH_SHORT).show();
                    // Refresh Main UI
                    mainViewModel.refreshCompanySettings();
                } else {

                    Toast.makeText(getContext(), "Error al guardar empresa", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- Security (PIN) Logic ---

    private void updatePin() {
        String currentPin = binding.etCurrentPin.getText().toString();
        String newPin = binding.etNewPin.getText().toString();
        String confirmPin = binding.etConfirmPin.getText().toString();

        if (currentPin.isEmpty() || newPin.isEmpty() || confirmPin.isEmpty()) {
            Toast.makeText(getContext(), "Todos los campos de PIN son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPin.equals(confirmPin)) {
            Toast.makeText(getContext(), "El nuevo PIN y la confirmación no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPin.length() < 4) {
            Toast.makeText(getContext(), "El nuevo PIN debe tener al menos 4 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        UpdatePinRequest request = new UpdatePinRequest(currentPin, newPin, confirmPin);

        apiService.updateAdminPin(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "PIN actualizado correctamente", Toast.LENGTH_SHORT).show();
                    binding.etCurrentPin.setText("");
                    binding.etNewPin.setText("");
                    binding.etConfirmPin.setText("");
                } else {
                    Toast.makeText(getContext(), "Error al actualizar PIN: PIN actual incorrecto", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- SMTP Settings Logic ---

    private void loadMailSettings() {
        apiService.getMailSettings().enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, String> s = response.body();
                    binding.etMailHost.setText(s.get("host"));
                    binding.etMailPort.setText(s.get("port"));
                    binding.etMailUsername.setText(s.get("username"));
                    binding.etMailPassword.setText(s.get("password"));
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                // Not strictly necessary to toast here as it's secondary
            }
        });
    }

    private void saveMailSettings() {
        Map<String, String> body = new HashMap<>();
        body.put("host", binding.etMailHost.getText().toString());
        body.put("port", binding.etMailPort.getText().toString());
        body.put("username", binding.etMailUsername.getText().toString());
        body.put("password", binding.etMailPassword.getText().toString());

        apiService.saveMailSettings(body).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Configuración SMTP guardada", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Error al guardar SMTP", Toast.LENGTH_SHORT).show();
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
