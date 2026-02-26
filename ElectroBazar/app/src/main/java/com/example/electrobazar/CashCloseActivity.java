package com.example.electrobazar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.electrobazar.models.Sale;
import com.example.electrobazar.network.RetrofitClient;
import com.example.electrobazar.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CashCloseActivity extends AppCompatActivity {

    private TextView tvTodayCount, tvTodayTotal;
    private EditText etClosingBalance, etNotes;
    private MaterialButton btnCloseCash;
    private SessionManager sessionManager;
    private Long pendingDownloadId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_close);

        sessionManager = new SessionManager(this);

        tvTodayCount = findViewById(R.id.tvTodayCount);
        tvTodayTotal = findViewById(R.id.tvTodayTotal);
        etClosingBalance = findViewById(R.id.etClosingBalance);
        etNotes = findViewById(R.id.etNotes);
        btnCloseCash = findViewById(R.id.btnCloseCash);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        loadTodayStats();

        requestStoragePermissions();

        btnCloseCash.setOnClickListener(v -> performCashClose());
    }

    private void requestStoragePermissions() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                androidx.core.app.ActivityCompat.requestPermissions(this,
                        new String[] { android.Manifest.permission.WRITE_EXTERNAL_STORAGE }, 100);
            }
        }
    }

    private void loadTodayStats() {
        RetrofitClient.getApiService().getTodaySales().enqueue(new Callback<List<Sale>>() {
            @Override
            public void onResponse(Call<List<Sale>> call, Response<List<Sale>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Sale> sales = response.body();
                    tvTodayCount.setText(String.valueOf(sales.size()));
                    double total = 0;
                    for (Sale s : sales)
                        total += s.getTotalAmount();
                    tvTodayTotal.setText(String.format("%.2f€", total));
                }
            }

            @Override
            public void onFailure(Call<List<Sale>> call, Throwable t) {
                Toast.makeText(CashCloseActivity.this, "Error al cargar ventas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performCashClose() {
        String balanceStr = etClosingBalance.getText().toString().trim();
        if (balanceStr.isEmpty()) {
            etClosingBalance.setError("Introduce el saldo de cierre");
            return;
        }

        double closingBalance;
        try {
            closingBalance = Double.parseDouble(balanceStr);
        } catch (NumberFormatException e) {
            etClosingBalance.setError("Saldo inválido");
            return;
        }

        String notes = etNotes.getText().toString().trim();
        btnCloseCash.setEnabled(false);

        RetrofitClient.getApiService().closeCashRegister(closingBalance, notes)
                .enqueue(new Callback<com.example.electrobazar.models.CashRegister>() {
                    @Override
                    public void onResponse(Call<com.example.electrobazar.models.CashRegister> call,
                            Response<com.example.electrobazar.models.CashRegister> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(CashCloseActivity.this, "Cierre realizado correctamente", Toast.LENGTH_LONG)
                                    .show();
                            downloadCashCloseTicket(response.body().getId());
                        } else {
                            Toast.makeText(CashCloseActivity.this, "Error al cerrar caja: " + response.code(),
                                    Toast.LENGTH_SHORT).show();
                            btnCloseCash.setEnabled(true);
                        }
                    }

                    @Override
                    public void onFailure(Call<com.example.electrobazar.models.CashRegister> call, Throwable t) {
                        Toast.makeText(CashCloseActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                        btnCloseCash.setEnabled(true);
                    }
                });
    }

    private void downloadCashCloseTicket(Long id) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                pendingDownloadId = id; // Store the ID for when permission is granted
                androidx.core.app.ActivityCompat.requestPermissions(this,
                        new String[] { android.Manifest.permission.WRITE_EXTERNAL_STORAGE }, 100);
                return;
            }
        }

        Toast.makeText(this, "Descargando ticket de cierre...", Toast.LENGTH_SHORT).show();
        RetrofitClient.getApiService().getCashCloseTicket(id).enqueue(new Callback<okhttp3.ResponseBody>() {
            @Override
            public void onResponse(Call<okhttp3.ResponseBody> call, Response<okhttp3.ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean success = com.example.electrobazar.utils.FileUtil.writeResponseBodyToDisk(
                            CashCloseActivity.this, response.body(), "Cierre_Caja_" + id + ".pdf");
                    if (success) {
                        Toast.makeText(CashCloseActivity.this, "Ticket de cierre descargado", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(CashCloseActivity.this, "Error al guardar el ticket de cierre",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    int code = response.code();
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null)
                            errorBody = response.errorBody().string();
                    } catch (Exception e) {
                    }

                    android.util.Log.e("CashClose", "Error: " + code + " - " + errorBody);
                    Toast.makeText(CashCloseActivity.this,
                            "Error " + code, Toast.LENGTH_SHORT).show();
                    finish();
                }
                finish();
            }

            @Override
            public void onFailure(Call<okhttp3.ResponseBody> call, Throwable t) {
                Toast.makeText(CashCloseActivity.this, "Error de red al descargar ticket", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @androidx.annotation.NonNull String[] permissions,
            @androidx.annotation.NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                if (pendingDownloadId != null) {
                    downloadCashCloseTicket(pendingDownloadId);
                    pendingDownloadId = null;
                }
            } else {
                Toast.makeText(this, "Permiso de almacenamiento denegado. No se pueden descargar tickets.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
