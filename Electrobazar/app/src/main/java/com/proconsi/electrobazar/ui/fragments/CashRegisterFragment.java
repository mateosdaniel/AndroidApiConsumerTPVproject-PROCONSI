package com.proconsi.electrobazar.ui.fragments;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.proconsi.electrobazar.R;
import com.proconsi.electrobazar.databinding.FragmentCashRegisterBinding;
import com.proconsi.electrobazar.models.CashCloseInfoDTO;
import com.proconsi.electrobazar.models.CashRegister;
import com.proconsi.electrobazar.models.CashRegisterOpenSuggestion;
import com.proconsi.electrobazar.models.CashWithdrawal;
import com.proconsi.electrobazar.network.ApiService;
import com.proconsi.electrobazar.network.CashRegisterRepository;
import com.proconsi.electrobazar.network.RetrofitClient;
import com.proconsi.electrobazar.ui.adapters.MovementAdapter;
import com.proconsi.electrobazar.utils.SessionManager;

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

public class CashRegisterFragment extends Fragment {
    public static final String ARG_INITIAL_STATE = "initial_state";
    public static final String STATE_DASHBOARD = "DASHBOARD";
    public static final String STATE_MOVEMENT = "MOVEMENT";
    public static final String STATE_CLOSE = "CLOSE";


    private FragmentCashRegisterBinding binding;
    private CashRegisterRepository repository;
    private SessionManager sessionManager;
    private CashRegister currentRegister;
    private CashCloseInfoDTO closeInfo;
    private MovementAdapter movementAdapter;
    private List<CashWithdrawal> movementsList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCashRegisterBinding.inflate(inflater, container, false);
        com.proconsi.electrobazar.utils.ThemeManager.applyFontToView(binding.getRoot(), requireContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ApiService api = RetrofitClient.getInstance().getApi();
        repository = new CashRegisterRepository(api);
        sessionManager = new SessionManager(requireContext());

        setupRecyclerView();
        setupListeners();
        
        String initialState = getArguments() != null ? getArguments().getString(ARG_INITIAL_STATE) : STATE_DASHBOARD;
        
        if (STATE_MOVEMENT.equals(initialState)) {
             showState(LayoutState.MOVEMENT_FORM);
        } else if (STATE_CLOSE.equals(initialState)) {
             loadCloseInfo(); // This shows close form
        } else {
             loadStatus();
        }
        
        updateLayoutForOrientation();
    }

    private void setupRecyclerView() {
        movementAdapter = new MovementAdapter(movementsList);
        binding.movementsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.movementsRecyclerView.setAdapter(movementAdapter);
    }

    private void setupListeners() {
        binding.btnOpenRegister.setOnClickListener(v -> openRegister());
        binding.btnNewMovement.setOnClickListener(v -> showState(LayoutState.MOVEMENT_FORM));
        binding.btnGotoClose.setOnClickListener(v -> loadCloseInfo());
        binding.btnCancelMovement.setOnClickListener(v -> showState(LayoutState.ACTIVE_DASHBOARD));
        
        binding.btnSubmitMovement.setOnClickListener(v -> submitMovement());
        binding.btnSubmitClose.setOnClickListener(v -> submitClose());

        binding.retainToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            binding.retainedAmountLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (isChecked && binding.retainedAmountInput.getText().toString().isEmpty()) {
                binding.retainedAmountInput.setText(binding.closeRealAmountInput.getText().toString());
            }
        });

        binding.closeRealAmountInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateCloseDifference();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadStatus() {
        showLoading(true);
        repository.getOpenRegister().enqueue(new Callback<CashRegister>() {
            @Override
            public void onResponse(Call<CashRegister> call, Response<CashRegister> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    currentRegister = response.body();
                    updateDashboard(currentRegister);
                    showState(LayoutState.ACTIVE_DASHBOARD);
                } else if (response.code() == 204) {
                    loadOpenSuggestion();
                } else {
                    Toast.makeText(requireContext(), "Error al cargar estado de caja", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CashRegister> call, Throwable t) {
                showLoading(false);
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadOpenSuggestion() {
        repository.getOpenSuggestion().enqueue(new Callback<CashRegisterOpenSuggestion>() {
            @Override
            public void onResponse(Call<CashRegisterOpenSuggestion> call, Response<CashRegisterOpenSuggestion> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isHasSuggestion()) {
                    binding.suggestionText.setText("Sugerido del turno anterior: " + response.body().getSuggestedBalance() + "€");
                    binding.suggestionText.setVisibility(View.VISIBLE);
                    binding.openingBalanceInput.setText(response.body().getSuggestedBalance().toString());
                }
                showState(LayoutState.OPEN_FORM);
            }

            @Override
            public void onFailure(Call<CashRegisterOpenSuggestion> call, Throwable t) {
                showState(LayoutState.OPEN_FORM);
            }
        });
    }

    private void openRegister() {
        String balStr = binding.openingBalanceInput.getText().toString();
        if (balStr.isEmpty()) {
            Toast.makeText(requireContext(), "Indica el saldo inicial", Toast.LENGTH_SHORT).show();
            return;
        }

        BigDecimal balance = new BigDecimal(balStr);
        showLoading(true);
        repository.openCashRegister(balance, sessionManager.getWorkerId()).enqueue(new Callback<CashRegister>() {
            @Override
            public void onResponse(Call<CashRegister> call, Response<CashRegister> response) {
                showLoading(false);
                if (response.isSuccessful()) {
                    currentRegister = response.body();
                    updateDashboard(currentRegister);
                    showState(LayoutState.ACTIVE_DASHBOARD);
                } else {
                    Toast.makeText(requireContext(), "Error al abrir caja", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CashRegister> call, Throwable t) {
                showLoading(false);
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateDashboard(CashRegister register) {
        binding.shiftWorkerText.setText("Trabajador: " + (register.getWorker() != null ? register.getWorker().getUsername() : "Desconocido"));
        binding.shiftOpeningTimeText.setText("Apertura: " + register.getOpeningTime());
        
        binding.cashSalesText.setText(String.format("%.2f€", register.getCashSales()));
        binding.cardSalesText.setText(String.format("%.2f€", register.getCardSales()));
        binding.withdrawalsText.setText(String.format("-%.2f€", register.getTotalWithdrawals()));
        
        // Expected Balance Calculation logic (should ideally match backend exactly)
        BigDecimal expected = register.getOpeningBalance()
                .add(register.getCashSales())
                .add(register.getTotalEntries() != null ? register.getTotalEntries() : BigDecimal.ZERO)
                .subtract(register.getTotalWithdrawals())
                .subtract(register.getCashRefunds() != null ? register.getCashRefunds() : BigDecimal.ZERO);
        
        binding.expectedBalanceText.setText(String.format("%.2f€", expected));

        movementsList.clear();
        if (register.getWithdrawals() != null) {
            movementsList.addAll(register.getWithdrawals());
        }
        movementAdapter.notifyDataSetChanged();
    }

    private void submitMovement() {
        String amount = binding.movementAmountInput.getText().toString();
        String reason = binding.movementReasonInput.getText().toString();
        boolean isEntry = binding.btnEntry.isChecked();
        String type = isEntry ? "ENTRY" : "WITHDRAWAL";

        if (amount.isEmpty()) return;

        showLoading(true);
        repository.createCashMovement(amount, reason, type).enqueue(new Callback<CashWithdrawal>() {
            @Override
            public void onResponse(Call<CashWithdrawal> call, Response<CashWithdrawal> response) {
                if (response.isSuccessful()) {
                    loadStatus(); // Reload full status to update totals
                    binding.movementAmountInput.setText("");
                    binding.movementReasonInput.setText("");
                } else {
                    showLoading(false);
                    Toast.makeText(requireContext(), "Error al procesar movimiento", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CashWithdrawal> call, Throwable t) {
                showLoading(false);
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCloseInfo() {
        showLoading(true);
        repository.getCashCloseInfo().enqueue(new Callback<CashCloseInfoDTO>() {
            @Override
            public void onResponse(Call<CashCloseInfoDTO> call, Response<CashCloseInfoDTO> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    closeInfo = response.body();
                    
                    // Populate stats grid (9 items)
                    binding.closeTotalSales.setText(String.format("%.2f€", closeInfo.getTotalToday()));
                    binding.closeOpCount.setText(String.valueOf(closeInfo.getCountToday()));
                    binding.closeCardSales.setText(String.format("%.2f€", closeInfo.getCardSalesToday()));
                    binding.closeCardRefunds.setText(String.format("%.2f€", closeInfo.getCardRefundsToday()));
                    binding.closeCashSales.setText(String.format("%.2f€", closeInfo.getCashSalesToday()));
                    binding.closeCashRefunds.setText(String.format("%.2f€", closeInfo.getCashRefundsToday()));
                    binding.closeEntries.setText(String.format("%.2f€", closeInfo.getTotalEntries()));
                    binding.closeWithdrawals.setText(String.format("%.2f€", closeInfo.getTotalWithdrawals()));
                    binding.closeCancelledStats.setText(String.format("%d / %.2f€", 
                            closeInfo.getCancelledCount(), closeInfo.getCancelledTotal()));

                    // Populate detailed summary
                    binding.summaryOpening.setText(String.format("%.2f€", closeInfo.getOpeningBalance()));
                    binding.summaryCashSales.setText(String.format("+%.2f€", closeInfo.getCashSalesToday()));
                    binding.summaryRefunds.setText(String.format("-%.2f€", closeInfo.getCashRefundsToday()));
                    
                    BigDecimal netMovements = closeInfo.getTotalEntries().subtract(closeInfo.getTotalWithdrawals());
                    binding.summaryMovements.setText(String.format("%s%.2f€", 
                            netMovements.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "", 
                            netMovements));

                    binding.closeTheoryAmountText.setText(String.format("%.2f€", closeInfo.getExpectedCashInDrawer()));
                    binding.closeRealAmountInput.setText(closeInfo.getExpectedCashInDrawer().toString());
                    
                    binding.summaryCancelled.setText(String.format("Anuladas: %d ops / %.2f€", 
                            closeInfo.getCancelledCount(), 
                            closeInfo.getCancelledTotal()));

                    if (closeInfo.getReturnsToday() != null && !closeInfo.getReturnsToday().isEmpty()) {
                        StringBuilder sb = new StringBuilder();
                        for (com.proconsi.electrobazar.models.SaleReturn ret : closeInfo.getReturnsToday()) {
                            String method = (ret.getPaymentMethod() == com.proconsi.electrobazar.models.PaymentMethod.CASH) ? "E" : "T";
                            sb.append(String.format("• %s (%s): -%.2f€\n", 
                                ret.getReturnNumber(), method, ret.getTotalRefunded()));
                        }
                        binding.returnsListText.setText(sb.toString().trim());
                        binding.returnsSummaryContainer.setVisibility(View.VISIBLE);
                    } else {
                        binding.returnsSummaryContainer.setVisibility(View.GONE);
                    }
                    
                    updateLayoutForOrientation();
                    
                    updateCloseDifference();
                    showState(LayoutState.CLOSE_FORM);
                }
            }

            @Override
            public void onFailure(Call<CashCloseInfoDTO> call, Throwable t) {
                showLoading(false);
                Toast.makeText(requireContext(), "Error al cargar info de cierre", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCloseDifference() {
        if (closeInfo == null) return;
        String realStr = binding.closeRealAmountInput.getText().toString();
        BigDecimal real = realStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(realStr);
        BigDecimal theory = closeInfo.getExpectedCashInDrawer();
        BigDecimal diff = real.subtract(theory);

        binding.closeDifferenceText.setText(String.format("Diferencia: %.2f€", diff));
        if (diff.compareTo(BigDecimal.ZERO) == 0) {
            binding.closeDifferenceText.setTextColor(getResources().getColor(R.color.success));
        } else if (diff.compareTo(BigDecimal.ZERO) > 0) {
            binding.closeDifferenceText.setTextColor(getResources().getColor(R.color.info));
        } else {
            binding.closeDifferenceText.setTextColor(getResources().getColor(R.color.danger));
        }

        if (binding.retainToggle.isChecked() && !binding.retainedAmountInput.isFocused()) {
            binding.retainedAmountInput.setText(realStr);
        }
    }

    private void submitClose() {
        String realStr = binding.closeRealAmountInput.getText().toString();
        if (realStr.isEmpty()) return;

        BigDecimal real = new BigDecimal(realStr);
        String notes = binding.closeNotesInput.getText().toString();
        BigDecimal retained = null;
        if (binding.retainToggle.isChecked()) {
            String retStr = binding.retainedAmountInput.getText().toString();
            if (!retStr.isEmpty()) retained = new BigDecimal(retStr);
        }

        showLoading(true);
        repository.closeCashRegister(real, notes, retained, sessionManager.getWorkerId()).enqueue(new Callback<CashRegister>() {
            @Override
            public void onResponse(Call<CashRegister> call, Response<CashRegister> response) {
                showLoading(false);
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Caja cerrada correctamente", Toast.LENGTH_LONG).show();
                    if (currentRegister != null) {
                        downloadClosurePdf(currentRegister.getId());
                    }
                    loadStatus();
                } else {
                    Toast.makeText(requireContext(), "Error al cerrar caja", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CashRegister> call, Throwable t) {
                showLoading(false);
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void downloadClosurePdf(Long id) {
        repository.downloadCashRegisterTicket(id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    saveAndOpenFile(response.body(), "Cierre_Caja_" + id + ".pdf");
                } else {
                    Toast.makeText(requireContext(), "Error al descargar PDF del cierre", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(requireContext(), "Error de red al descargar PDF", Toast.LENGTH_SHORT).show();
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
                Uri fileUri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".provider", file);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(fileUri, "application/pdf");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(intent, "Abrir Informe de Cierre"));

            } catch (IOException e) {
                Toast.makeText(requireContext(), "Error al guardar informe PDF", Toast.LENGTH_SHORT).show();
            } finally {
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
            }
        } catch (IOException e) {
            Log.e("CashRegister", "Error saving PDF", e);
        }
    }

    private void showState(LayoutState state) {
        binding.openFormContainer.setVisibility(state == LayoutState.OPEN_FORM ? View.VISIBLE : View.GONE);
        binding.activeRegisterContainer.setVisibility(state == LayoutState.ACTIVE_DASHBOARD ? View.VISIBLE : View.GONE);
        binding.movementFormContainer.setVisibility(state == LayoutState.MOVEMENT_FORM ? View.VISIBLE : View.GONE);
        binding.closeFormContainer.setVisibility(state == LayoutState.CLOSE_FORM ? View.VISIBLE : View.GONE);
    }

    private void showLoading(boolean loading) {
        binding.loadingProgress.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private enum LayoutState {
        OPEN_FORM, ACTIVE_DASHBOARD, MOVEMENT_FORM, CLOSE_FORM
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    @Override
    public void onConfigurationChanged(@NonNull android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateLayoutForOrientation();
    }

    private void updateLayoutForOrientation() {
        if (binding == null) return;
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
            binding.cashCloseBodyRow.setOrientation(android.widget.LinearLayout.HORIZONTAL);
            binding.cashCloseLeftCol.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                    0, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 2.0f));
            binding.cashCloseRightCol.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                    0, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1.2f));
            binding.closeStatsGrid.setColumnCount(3);
        } else {
            binding.cashCloseBodyRow.setOrientation(android.widget.LinearLayout.VERTICAL);
            binding.cashCloseLeftCol.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 0.0f));
            binding.cashCloseRightCol.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 0.0f));
            binding.closeStatsGrid.setColumnCount(2);
        }
    }
}
