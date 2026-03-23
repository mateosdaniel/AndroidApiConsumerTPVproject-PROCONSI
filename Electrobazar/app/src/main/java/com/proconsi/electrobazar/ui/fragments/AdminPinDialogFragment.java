package com.proconsi.electrobazar.ui.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.proconsi.electrobazar.R;
import com.proconsi.electrobazar.models.LoginResponse;
import com.proconsi.electrobazar.network.RetrofitClient;
import com.proconsi.electrobazar.utils.SessionManager;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminPinDialogFragment extends DialogFragment {

    public interface AdminPinListener {
        void onPinVerified(String token);
        void onCancel();
    }

    private AdminPinListener listener;
    private SessionManager sessionManager;
    private boolean isVerified = false;
    private TextInputEditText pinEditText;
    private TextView errorText;
    private MaterialButton btnVerify;

    public static AdminPinDialogFragment newInstance() {
        return new AdminPinDialogFragment();
    }

    public void setListener(AdminPinListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_admin_pin, container, false);
        com.proconsi.electrobazar.utils.ThemeManager.applyFontToView(view, requireContext());

        pinEditText = view.findViewById(R.id.pinEditText);
        errorText = view.findViewById(R.id.errorText);
        btnVerify = view.findViewById(R.id.btnVerify);
        MaterialButton btnCancel = view.findViewById(R.id.btnCancel);

        btnVerify.setOnClickListener(v -> verifyPin());
        btnCancel.setOnClickListener(v -> dismiss());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    private void verifyPin() {
        String pin = pinEditText.getText().toString().trim();
        if (pin.isEmpty()) {
            Toast.makeText(getContext(), "Introduce el PIN", Toast.LENGTH_SHORT).show();
            return;
        }

        btnVerify.setEnabled(false);
        errorText.setVisibility(View.GONE);

        Map<String, String> body = new HashMap<>();
        body.put("pin", pin);

        RetrofitClient.getInstance().getApi().verifyAdminPin(body).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                btnVerify.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    isVerified = true;
                    sessionManager.saveToken(response.body().getToken());
                    if (listener != null) {
                        listener.onPinVerified(response.body().getToken());
                    }
                    dismiss();
                } else {
                    errorText.setText("PIN incorrecto. Reintente.");
                    errorText.setVisibility(View.VISIBLE);
                    pinEditText.setText("");
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                btnVerify.setEnabled(true);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDismiss(@NonNull android.content.DialogInterface dialog) {
        super.onDismiss(dialog);
        if (!isVerified && listener != null) {
            listener.onCancel();
        }
    }
}
