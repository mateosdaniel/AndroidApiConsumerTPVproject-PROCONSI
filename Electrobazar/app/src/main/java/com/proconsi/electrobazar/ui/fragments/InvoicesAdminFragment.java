package com.proconsi.electrobazar.ui.fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.chip.ChipGroup;
import com.proconsi.electrobazar.R;
import com.proconsi.electrobazar.models.Sale;
import com.proconsi.electrobazar.repositories.InvoicesAdminRepository;
import com.proconsi.electrobazar.ui.adapters.InvoicesAdminAdapter;
import com.proconsi.electrobazar.utils.SessionManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class InvoicesAdminFragment extends Fragment implements InvoicesAdminAdapter.OnSaleActionListener {

    private static final String TAG = "InvoicesAdminFragment";
    private InvoicesAdminRepository repository;
    private InvoicesAdminAdapter adapter;
    private List<Sale> allSales = new ArrayList<>();
    private List<Sale> filteredSales = new ArrayList<>();
    
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private EditText searchInput;
    private ChipGroup filterChipGroup;
    private TextView resultsCountText;
    private TextView selectedDateText;
    private ImageButton dateRangeBtn, clearFiltersBtn;
    
    private String currentSearch = "";
    private String selectedDate = null; // yyyy-MM-dd
    private int selectedFilterId = R.id.chipAll;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invoices_admin, container, false);
        
        repository = new InvoicesAdminRepository();
        
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        recyclerView = view.findViewById(R.id.invoicesRecyclerView);
        searchInput = view.findViewById(R.id.invoiceSearchInput);
        filterChipGroup = view.findViewById(R.id.filterChipGroup);
        resultsCountText = view.findViewById(R.id.resultsCountText);
        selectedDateText = view.findViewById(R.id.selectedDateText);
        dateRangeBtn = view.findViewById(R.id.dateRangeBtn);
        clearFiltersBtn = view.findViewById(R.id.clearFiltersBtn);
        
        setupRecyclerView();
        setupFilters();
        
        swipeRefresh.setOnRefreshListener(this::loadSales);
        loadSales();
        
        return view;
    }

    private void setupRecyclerView() {
        adapter = new InvoicesAdminAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupFilters() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearch = s.toString().toLowerCase().trim();
                applyFilters();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        filterChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == View.NO_ID) {
                group.check(R.id.chipAll);
                return;
            }
            selectedFilterId = checkedId;
            applyFilters();
        });

        dateRangeBtn.setOnClickListener(v -> showDatePicker());
        clearFiltersBtn.setOnClickListener(v -> resetFilters());
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year1, monthOfYear, dayOfMonth) -> {
                    selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth);
                    selectedDateText.setText(selectedDate);
                    loadSales(); // Fetch for specific date range or filter locally? 
                    // Web panel fetches ALL and filters locally for general list, but has specific range endpoint.
                    // For now, let's filter locally if possible, or use the range endpoint if dates are set.
                }, year, month, day);
        datePickerDialog.show();
    }

    private void resetFilters() {
        searchInput.setText("");
        filterChipGroup.check(R.id.chipAll);
        selectedDate = null;
        selectedDateText.setText("Cualquier fecha");
        loadSales();
    }

    private void loadSales() {
        swipeRefresh.setRefreshing(true);
        if (selectedDate != null) {
            // Use range endpoint for efficiency if we have a date
            String from = selectedDate + "T00:00:00";
            String to = selectedDate + "T23:59:59";
            repository.getSalesRange(from, to, new InvoicesAdminRepository.RepositoryCallback<List<Sale>>() {
                @Override
                public void onSuccess(List<Sale> result) {
                    allSales = result;
                    applyFilters();
                    swipeRefresh.setRefreshing(false);
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    swipeRefresh.setRefreshing(false);
                }
            });
        } else {
            repository.getSales(new InvoicesAdminRepository.RepositoryCallback<List<Sale>>() {
                @Override
                public void onSuccess(List<Sale> result) {
                    allSales = result;
                    applyFilters();
                    swipeRefresh.setRefreshing(false);
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    swipeRefresh.setRefreshing(false);
                }
            });
        }
    }

    private void applyFilters() {
        filteredSales.clear();
        for (Sale sale : allSales) {
            boolean matchesSearch = true;
            if (!currentSearch.isEmpty()) {
                String customer = sale.getCustomer() != null ? sale.getCustomer().getName().toLowerCase() : "consumidor final";
                String nif = sale.getCustomer() != null && sale.getCustomer().getTaxId() != null ? sale.getCustomer().getTaxId().toLowerCase() : "";
                String id = String.valueOf(sale.getId());
                matchesSearch = customer.contains(currentSearch) || nif.contains(currentSearch) || id.contains(currentSearch);
            }

            boolean matchesType = true;
            if (selectedFilterId == R.id.chipInvoices) {
                matchesType = sale.getInvoice() != null;
            } else if (selectedFilterId == R.id.chipTickets) {
                matchesType = sale.getInvoice() == null;
            } else if (selectedFilterId == R.id.chipCash) {
                matchesType = sale.getPaymentMethod() == com.proconsi.electrobazar.models.PaymentMethod.CASH;
            } else if (selectedFilterId == R.id.chipCard) {
                matchesType = sale.getPaymentMethod() == com.proconsi.electrobazar.models.PaymentMethod.CARD;
            }

            if (matchesSearch && matchesType) {
                filteredSales.add(sale);
            }
        }
        adapter.setSales(filteredSales);
        resultsCountText.setText(String.format(Locale.getDefault(), "%d ventas encontradas", filteredSales.size()));
    }

    @Override
    public void onDownloadInvoice(Sale sale) {
        boolean hasInvoice = sale.getInvoice() != null;
        String filename = hasInvoice
                ? "Factura_" + sale.getInvoice().getInvoiceNumber() + ".pdf"
                : "Ticket_" + sale.getId() + ".pdf";

        repository.downloadInvoice(sale.getId(), new InvoicesAdminRepository.RepositoryCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody body) {
                saveAndOpenFile(body, filename);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error al descargar PDF: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveAndOpenFile(ResponseBody body, String filename) {
        try {
            File file = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), filename);
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);

                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) break;
                    outputStream.write(fileReader, 0, read);
                }
                outputStream.flush();

                // Open File
                Uri fileUri = FileProvider.getUriForFile(
                        requireContext(),
                        requireContext().getPackageName() + ".provider",
                        file);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(fileUri, "application/pdf");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(intent, "Abrir Factura / Ticket"));

            } catch (IOException e) {
                Toast.makeText(requireContext(), "Error al guardar archivo", Toast.LENGTH_SHORT).show();
            } finally {
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "saveAndOpenFile: " + e.getMessage(), e);
        }
    }

    @Override
    public void onCancelSale(Sale sale) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Anular Venta")
                .setMessage("¿Estás seguro de que deseas anular la venta #" + sale.getId() + "?")
                .setPositiveButton("Sí, anular", (dialog, which) -> {
                    SessionManager session = new SessionManager(requireContext());
                    repository.cancelSale(sale.getId(), session.getWorkerId(), "Anulado desde Android", new InvoicesAdminRepository.RepositoryCallback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            Toast.makeText(getContext(), "Venta anulada correctamente", Toast.LENGTH_SHORT).show();
                            loadSales();
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onSaleClick(Sale sale) {
        // Show details dialog
        showSaleDetails(sale);
    }

    private void showSaleDetails(Sale sale) {
        // Simple details alert for now
        StringBuilder sb = new StringBuilder();
        sb.append("Atendido por: ").append(sale.getWorker() != null ? sale.getWorker().getUsername() : "Sistema").append("\n");
        sb.append("Pago: ").append(sale.getPaymentMethod()).append("\n\n");
        sb.append("Líneas:\n");
        if (sale.getLines() != null) {
            for (com.proconsi.electrobazar.models.SaleLine line : sale.getLines()) {
                sb.append("- ").append(line.getQuantity()).append("x ").append(line.getProduct().getName())
                        .append(" (").append(line.getUnitPrice()).append("€)\n");
            }
        }
        sb.append("\nTotal: ").append(sale.getTotalAmount()).append("€");

        new AlertDialog.Builder(requireContext())
                .setTitle("Detalle de Venta #" + sale.getId())
                .setMessage(sb.toString())
                .setPositiveButton("Cerrar", null)
                .show();
    }
}
