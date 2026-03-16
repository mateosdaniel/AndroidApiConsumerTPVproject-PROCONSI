package com.proconsi.electrobazar.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.proconsi.electrobazar.R;
import com.proconsi.electrobazar.models.PaymentMethod;
import com.proconsi.electrobazar.models.ReturnCheckResponse;
import com.proconsi.electrobazar.models.ReturnLineRequest;
import com.proconsi.electrobazar.models.ReturnRequest;
import com.proconsi.electrobazar.models.Sale;
import com.proconsi.electrobazar.models.SaleLine;
import com.proconsi.electrobazar.models.SaleReturn;
import com.proconsi.electrobazar.network.ApiClient;
import com.proconsi.electrobazar.network.ApiService;
import com.proconsi.electrobazar.network.ReturnsRepository;
import com.proconsi.electrobazar.network.SalesRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReturnsFragment extends Fragment {

    private static final String TAG = "ReturnsFragment";

    private View root;
    private SalesRepository salesRepository;
    private ReturnsRepository returnsRepository;

    private TextView returnSearchInput;
    private View btnSearchTicket;
    private ProgressBar loadingProgress;
    private View returnDetailsContainer;

    private TextView saleInfoText, saleCustomerText, saleTotalText;
    private RecyclerView returnLinesRecyclerView;
    private Spinner refundMethodSpinner;
    private TextView returnReasonInput;
    private TextView totalRefundPreviewText;
    private View btnSubmitReturn;

    private Sale currentSale;
    private ReturnLinesAdapter linesAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_returns, container, false);

        ApiService apiService = com.proconsi.electrobazar.network.RetrofitClient.getInstance().getApi();
        salesRepository = new SalesRepository(apiService);
        returnsRepository = new ReturnsRepository(apiService);

        initViews();
        setupRefundSpinner();


        return root;
    }

    private void initViews() {
        returnSearchInput = root.findViewById(R.id.returnSearchInput);
        btnSearchTicket = root.findViewById(R.id.btnSearchTicket);
        loadingProgress = root.findViewById(R.id.loadingProgress);
        returnDetailsContainer = root.findViewById(R.id.returnDetailsContainer);

        saleInfoText = root.findViewById(R.id.saleInfoText);
        saleCustomerText = root.findViewById(R.id.saleCustomerText);
        saleTotalText = root.findViewById(R.id.saleTotalText);

        returnLinesRecyclerView = root.findViewById(R.id.returnLinesRecyclerView);
        returnLinesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        refundMethodSpinner = root.findViewById(R.id.refundMethodSpinner);
        returnReasonInput = root.findViewById(R.id.returnReasonInput);
        totalRefundPreviewText = root.findViewById(R.id.totalRefundPreviewText);
        btnSubmitReturn = root.findViewById(R.id.btnSubmitReturn);

        btnSearchTicket.setOnClickListener(v -> performSearch());
        btnSubmitReturn.setOnClickListener(v -> processReturn());
    }

    private void setupRefundSpinner() {
        List<String> methods = new ArrayList<>();
        for (PaymentMethod pm : PaymentMethod.values()) {
            methods.add(pm.name());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, methods);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        refundMethodSpinner.setAdapter(adapter);
    }

    private void performSearch() {
        String query = returnSearchInput.getText().toString().trim();
        if (query.isEmpty()) {
            Toast.makeText(getContext(), "Introduce un número de ticket", Toast.LENGTH_SHORT).show();
            return;
        }

        loadingProgress.setVisibility(View.VISIBLE);
        returnDetailsContainer.setVisibility(View.GONE);

        returnsRepository.checkReturn(query).enqueue(new Callback<ReturnCheckResponse>() {
            @Override
            public void onResponse(Call<ReturnCheckResponse> call, Response<ReturnCheckResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    loadSaleDetails(response.body().getSaleId());
                } else {
                    loadingProgress.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Ticket no encontrado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ReturnCheckResponse> call, Throwable t) {
                loadingProgress.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadSaleDetails(Long saleId) {
        salesRepository.getSaleById(saleId).enqueue(new Callback<Sale>() {
            @Override
            public void onResponse(Call<Sale> call, Response<Sale> response) {
                loadingProgress.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    currentSale = response.body();
                    displaySale(currentSale);
                } else {
                    Toast.makeText(getContext(), "Error al cargar detalles de la venta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Sale> call, Throwable t) {
                loadingProgress.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displaySale(Sale sale) {
        returnDetailsContainer.setVisibility(View.VISIBLE);
        saleInfoText.setText("Ticket #" + (sale.getTicket() != null ? sale.getTicket().getTicketNumber() : sale.getId()));
        saleCustomerText.setText("Cliente: " + (sale.getCustomer() != null ? sale.getCustomer().getName() : "Sin cliente"));
        saleTotalText.setText("Total: " + String.format("%.2f€", sale.getTotalAmount()));

        linesAdapter = new ReturnLinesAdapter(sale.getLines(), this::onQuantityChanged);
        returnLinesRecyclerView.setAdapter(linesAdapter);
        updateTotalRefund();
    }

    private void onQuantityChanged() {
        updateTotalRefund();
    }

    private void updateTotalRefund() {
        BigDecimal total = BigDecimal.ZERO;
        if (linesAdapter != null) {
            for (ReturnLineItem item : linesAdapter.getItems()) {
                if (item.returnQuantity > 0) {
                    total = total.add(item.saleLine.getUnitPrice().multiply(new BigDecimal(item.returnQuantity)));
                }
            }
        }
        totalRefundPreviewText.setText("Total a devolver: " + String.format("%.2f€", total));
    }

    private void processReturn() {
        if (currentSale == null) return;

        List<ReturnLineRequest> returnLines = new ArrayList<>();
        for (ReturnLineItem item : linesAdapter.getItems()) {
            if (item.returnQuantity > 0) {
                returnLines.add(new ReturnLineRequest(item.saleLine.getId(), item.returnQuantity));
            }
        }

        if (returnLines.isEmpty()) {
            Toast.makeText(getContext(), "Selecciona al menos un artículo para devolver", Toast.LENGTH_SHORT).show();
            return;
        }

        String reason = returnReasonInput.getText().toString().trim();
        PaymentMethod method = PaymentMethod.valueOf(refundMethodSpinner.getSelectedItem().toString());

        ReturnRequest request = new ReturnRequest(currentSale.getId(), returnLines, reason, method);

        loadingProgress.setVisibility(View.VISIBLE);
        btnSubmitReturn.setEnabled(false);

        returnsRepository.processReturn(request).enqueue(new Callback<SaleReturn>() {
            @Override
            public void onResponse(Call<SaleReturn> call, Response<SaleReturn> response) {
                loadingProgress.setVisibility(View.GONE);
                btnSubmitReturn.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    showReturnSuccess(response.body());
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Toast.makeText(getContext(), "Error: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Error al procesar la devolución", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<SaleReturn> call, Throwable t) {
                loadingProgress.setVisibility(View.GONE);
                btnSubmitReturn.setEnabled(true);
                Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showReturnSuccess(SaleReturn saleReturn) {
        new AlertDialog.Builder(getContext())
                .setTitle("Devolución Completada")
                .setMessage("Se ha procesado la devolución " + saleReturn.getReturnNumber() + 
                        "\nTotal reembolsado: " + String.format("%.2f€", saleReturn.getTotalRefunded()))
                .setPositiveButton("DESCARGAR PDF", (dialog, which) -> downloadReturnPdf(saleReturn.getId()))
                .setNegativeButton("CERRAR", (dialog, which) -> {
                    returnDetailsContainer.setVisibility(View.GONE);
                    returnSearchInput.setText("");
                })
                .show();
    }

    private void downloadReturnPdf(Long returnId) {
        returnsRepository.downloadReturnPdf(returnId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    saveAndOpenPdf(response.body(), "Devolucion_" + returnId + ".pdf");
                } else {
                    Toast.makeText(getContext(), "Error al descargar PDF", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveAndOpenPdf(ResponseBody body, String filename) {
        try {
            File path = new File(getContext().getExternalFilesDir(null), "returns");
            if (!path.exists()) path.mkdirs();
            File file = new File(path, filename);

            try (InputStream inputStream = body.byteStream();
                 OutputStream outputStream = new FileOutputStream(file)) {

                byte[] data = new byte[4096];
                int read;
                while ((read = inputStream.read(data)) != -1) {
                    outputStream.write(data, 0, read);
                }
                outputStream.flush();
            }

            Uri uri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".provider", file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);

        } catch (IOException e) {
            Log.e(TAG, "Error saving PDF", e);
            Toast.makeText(getContext(), "Error al guardar el archivo", Toast.LENGTH_SHORT).show();
        }
    }

    // --- Adapter classes ---

    private static class ReturnLineItem {
        SaleLine saleLine;
        int returnQuantity = 0;

        ReturnLineItem(SaleLine line) {
            this.saleLine = line;
        }
    }

    private static class ReturnLinesAdapter extends RecyclerView.Adapter<ReturnLinesAdapter.ViewHolder> {
        private final List<ReturnLineItem> items;
        private final Runnable onQuantityChanged;

        ReturnLinesAdapter(List<SaleLine> lines, Runnable onQuantityChanged) {
            this.items = new ArrayList<>();
            for (SaleLine line : lines) {
                items.add(new ReturnLineItem(line));
            }
            this.onQuantityChanged = onQuantityChanged;
        }

        List<ReturnLineItem> getItems() { return items; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_return_line, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ReturnLineItem item = items.get(position);
            holder.productName.setText(item.saleLine.getProduct().getName());
            holder.originalQuantity.setText(String.valueOf(item.saleLine.getQuantity()));
            holder.returnQuantity.setText(String.valueOf(item.returnQuantity));

            holder.btnDecrease.setOnClickListener(v -> {
                if (item.returnQuantity > 0) {
                    item.returnQuantity--;
                    notifyItemChanged(position);
                    onQuantityChanged.run();
                }
            });

            holder.btnIncrease.setOnClickListener(v -> {
                if (item.returnQuantity < item.saleLine.getQuantity()) {
                    item.returnQuantity++;
                    notifyItemChanged(position);
                    onQuantityChanged.run();
                }
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView productName, originalQuantity, returnQuantity;
            ImageButton btnDecrease, btnIncrease;

            ViewHolder(View itemView) {
                super(itemView);
                productName = itemView.findViewById(R.id.productName);
                originalQuantity = itemView.findViewById(R.id.originalQuantity);
                returnQuantity = itemView.findViewById(R.id.returnQuantity);
                btnDecrease = itemView.findViewById(R.id.btnDecrease);
                btnIncrease = itemView.findViewById(R.id.btnIncrease);
            }
        }
    }
}
