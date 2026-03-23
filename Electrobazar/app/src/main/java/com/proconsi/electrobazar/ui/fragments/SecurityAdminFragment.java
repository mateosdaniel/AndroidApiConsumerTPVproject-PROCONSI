package com.proconsi.electrobazar.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.proconsi.electrobazar.databinding.FragmentSecurityAdminBinding;
import com.proconsi.electrobazar.models.UpdatePinRequest;
import com.proconsi.electrobazar.network.RetrofitClient;
import com.proconsi.electrobazar.network.ApiService;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SecurityAdminFragment extends Fragment {

    private FragmentSecurityAdminBinding binding;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSecurityAdminBinding.inflate(inflater, container, false);
        apiService = RetrofitClient.getInstance().getApi();

        binding.btnUpdatePin.setOnClickListener(v -> updatePin());

        return binding.getRoot();
    }

    private void updatePin() {
        String currentPin = binding.etCurrentPin.getText().toString();
        String newPin = binding.etNewPin.getText().toString();
        String confirmPin = binding.etConfirmPin.getText().toString();

        if (currentPin.isEmpty() || newPin.isEmpty() || confirmPin.isEmpty()) {
            Toast.makeText(getContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
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
                    String error = "Error al actualizar PIN: Puede que el PIN actual sea incorrecto.";
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
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
