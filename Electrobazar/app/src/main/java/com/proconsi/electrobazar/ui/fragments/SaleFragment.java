package com.proconsi.electrobazar.ui.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.proconsi.electrobazar.R;
import com.proconsi.electrobazar.models.TicketLine;
import com.proconsi.electrobazar.ui.adapters.CategoryAdapter;
import com.proconsi.electrobazar.ui.adapters.ProductAdapter;
import com.proconsi.electrobazar.ui.adapters.TicketAdapter;
import com.proconsi.electrobazar.viewmodels.ProductViewModel;
import com.proconsi.electrobazar.viewmodels.SaleViewModel;
import com.proconsi.electrobazar.models.SaleWithTaxRequest;
import com.proconsi.electrobazar.models.PaymentMethod;
import com.proconsi.electrobazar.utils.SessionManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SaleFragment extends Fragment {

    private ProductViewModel productViewModel;
    private SaleViewModel saleViewModel;
    private ProductAdapter productAdapter;
    private CategoryAdapter categoryAdapter;
    private TicketAdapter ticketAdapter;
    private View emptyState, ticketContainer, ticketOverlay, btnCloseTicket;
    private TextView ticketTotalText, ticketCountBadge;
    private View btnCobrar, btnClearTicket;
    private boolean isLandscape;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sale, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        saleViewModel = new ViewModelProvider(requireActivity()).get(SaleViewModel.class);
        
        emptyState = view.findViewById(R.id.emptyState);
        ticketContainer = view.findViewById(R.id.ticketContainer);
        ticketOverlay = view.findViewById(R.id.ticketOverlay);
        btnCloseTicket = view.findViewById(R.id.btnCloseTicket);
        ticketTotalText = view.findViewById(R.id.ticketTotalText);
        ticketCountBadge = view.findViewById(R.id.ticketCountBadge);
        btnCobrar = view.findViewById(R.id.btnCobrar);
        btnClearTicket = view.findViewById(R.id.btnClearTicket);

        isLandscape = getResources().getConfiguration().orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE;

        setupRecyclerViews(view);
        setupSearch(view);
        setupActions();
        observeViewModel();
        
        // Initial load
        productViewModel.loadProducts();
    }

    private void setupRecyclerViews(View view) {
        // Products Grid
        RecyclerView productsRv = view.findViewById(R.id.productsRecyclerView);
        int columns = getResources().getConfiguration().orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE ? 4 : 2;
        productsRv.setLayoutManager(new GridLayoutManager(getContext(), columns));
        
        productAdapter = new ProductAdapter(product -> {
            saleViewModel.addProduct(product);
        });
        productsRv.setAdapter(productAdapter);

        // Categories Bar
        RecyclerView categoriesRv = view.findViewById(R.id.categoriesRecyclerView);
        categoryAdapter = new CategoryAdapter(category -> {
            productViewModel.filterByCategory(category);
            categoryAdapter.setSelectedCategoryId(category.getId());
        });
        categoriesRv.setAdapter(categoryAdapter);
        categoryAdapter.setSelectedCategoryId(null); // "Todos" selected by default

        // Ticket List
        RecyclerView ticketRv = view.findViewById(R.id.ticketRecyclerView);
        ticketAdapter = new TicketAdapter(new TicketAdapter.OnTicketLineInteractionListener() {
            @Override
            public void onIncreaseQty(TicketLine line) {
                saleViewModel.addProduct(line.getProduct());
            }

            @Override
            public void onDecreaseQty(TicketLine line) {
                saleViewModel.removeProduct(line.getProduct());
            }

            @Override
            public void onRemoveLine(TicketLine line) {
                saleViewModel.deleteLine(line);
            }
        });
        ticketRv.setAdapter(ticketAdapter);
    }

    private void setupSearch(View view) {
        EditText searchInput = view.findViewById(R.id.searchInput);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                productViewModel.searchProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    
    private void setupActions() {
        btnCloseTicket.setOnClickListener(v -> saleViewModel.setTicketVisible(false));
        ticketOverlay.setOnClickListener(v -> saleViewModel.setTicketVisible(false));
        btnCobrar.setOnClickListener(v -> showCheckoutDialog());
        btnClearTicket.setOnClickListener(v -> saleViewModel.clearTicket());
    }

    private void observeViewModel() {
        productViewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            boolean isEmpty = products == null || products.isEmpty();
            emptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            productAdapter.setProducts(products);
        });

        productViewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            categoryAdapter.setCategories(categories);
        });

        saleViewModel.getTicketLines().observe(getViewLifecycleOwner(), lines -> {
            ticketAdapter.setLines(lines);
            btnCobrar.setEnabled(!lines.isEmpty());
        });

        saleViewModel.getTotalAmount().observe(getViewLifecycleOwner(), amount -> {
            ticketTotalText.setText(String.format(Locale.getDefault(), "%.2f€", amount));
        });

        saleViewModel.getTotalItems().observe(getViewLifecycleOwner(), count -> {
            ticketCountBadge.setText(String.valueOf(count));
        });

        saleViewModel.isTicketVisible().observe(getViewLifecycleOwner(), this::toggleTicketPanel);

        // Observe sale result
        saleViewModel.getSaleResponse().observe(getViewLifecycleOwner(), response -> {
            if (response != null) {
                Toast.makeText(getContext(), "Venta realizada con éxito (" + response.getGrandTotal() + " €)", Toast.LENGTH_LONG).show();
            }
        });

        saleViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void toggleTicketPanel(boolean visible) {
        if (ticketContainer == null) return;

        if (visible) {
            ticketOverlay.setVisibility(View.VISIBLE);
            ticketOverlay.setAlpha(0f);
            ticketOverlay.animate().alpha(1f).setDuration(250).start();

            ticketContainer.setVisibility(View.VISIBLE);
            if (isLandscape) {
                ticketContainer.setTranslationX(ticketContainer.getWidth() > 0 ? ticketContainer.getWidth() : 1000f);
                ticketContainer.animate().translationX(0).setDuration(250).start();
            } else {
                ticketContainer.setTranslationY(ticketContainer.getHeight() > 0 ? ticketContainer.getHeight() : 2000f);
                ticketContainer.animate().translationY(0).setDuration(250).start();
            }
        } else {
            ticketOverlay.animate().alpha(0f).setDuration(250).withEndAction(() -> ticketOverlay.setVisibility(View.GONE)).start();

            if (isLandscape) {
                ticketContainer.animate().translationX(ticketContainer.getWidth()).setDuration(250).withEndAction(() -> ticketContainer.setVisibility(View.GONE)).start();
            } else {
                ticketContainer.animate().translationY(ticketContainer.getHeight()).setDuration(250).withEndAction(() -> ticketContainer.setVisibility(View.GONE)).start();
            }
        }
    }

    private void showCheckoutDialog() {
        BigDecimal total = saleViewModel.getTotalAmount().getValue();
        if (total == null || total.compareTo(BigDecimal.ZERO) <= 0) return;

        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_checkout, null);
        dialog.setContentView(dialogView);

        TextView totalText = dialogView.findViewById(R.id.checkoutTotalAmount);
        totalText.setText(String.format(Locale.getDefault(), "%.2f €", total));

        View cashSection = dialogView.findViewById(R.id.cashSection);
        TextInputEditText receivedInput = dialogView.findViewById(R.id.receivedAmountInput);
        TextView changeText = dialogView.findViewById(R.id.changeAmountText);
        View btnSetExactAmount = dialogView.findViewById(R.id.btnSetExactAmount);
        MaterialButtonToggleGroup paymentToggle = dialogView.findViewById(R.id.paymentToggleGroup);
        View btnConfirm = dialogView.findViewById(R.id.btnConfirmCheckout);

        receivedInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                try {
                    BigDecimal received = new BigDecimal(s.toString());
                    BigDecimal change = received.subtract(total);
                    changeText.setText(String.format(Locale.getDefault(), "%.2f €", change.max(BigDecimal.ZERO)));
                } catch (Exception e) {
                    changeText.setText("0.00 €");
                }
            }
        });

        btnSetExactAmount.setOnClickListener(v -> {
            // Use a very large amount to ensure it always covers the backend's total + taxes (Recargo)
            receivedInput.setText("999999.99");
            changeText.setText("0.00 €");
        });

        paymentToggle.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) return;
            if (checkedId == R.id.btnPayCash) {
                cashSection.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.btnPayCard) {
                cashSection.setVisibility(View.GONE);
            }
        });

        btnConfirm.setOnClickListener(v -> {
            SaleWithTaxRequest request = new SaleWithTaxRequest();
            request.setWorkerId(new SessionManager(requireContext()).getWorkerId());
            
            List<SaleWithTaxRequest.SaleLineRequest> lines = new ArrayList<>();
            for (TicketLine line : saleViewModel.getTicketLines().getValue()) {
                SaleWithTaxRequest.SaleLineRequest lr = new SaleWithTaxRequest.SaleLineRequest();
                lr.setProductId(line.getProduct().getId());
                lr.setQuantity(line.getQuantity());
                // We send null for overridePrice to let the backend use temporal pricing
                lr.setOverridePrice(null); 
                lines.add(lr);
            }
            request.setLines(lines);

            int checkedId = paymentToggle.getCheckedButtonId();
            if (checkedId == R.id.btnPayCash) {
                request.setPaymentMethod(PaymentMethod.CASH);
                String receivedStr = receivedInput.getText().toString().replace(",", ".");
                if (receivedStr.isEmpty()) {
                    // Default to a large amount to ensure it covers backend totals (base + taxes + recargos)
                    request.setReceivedAmount(new BigDecimal("999999.99"));
                } else {
                    try {
                        request.setReceivedAmount(new BigDecimal(receivedStr));
                    } catch (Exception e) {
                        request.setReceivedAmount(new BigDecimal("999999.99"));
                    }
                }
            } else {
                request.setPaymentMethod(PaymentMethod.CARD);
                // Also use a large amount for card to avoid any potential "less than total" checks
                request.setReceivedAmount(new BigDecimal("999999.99"));
            }

            saleViewModel.confirmSale(request);
            dialog.dismiss();
        });

        dialog.show();
    }
}
