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
    private View btnCobrar, btnClearTicket, btnSuspender;
    private View customerSearchContainer, selectedCustomerCard;
    private TextView customerNameText, customerTaxIdText, customerTariffBadge;
    private android.widget.AutoCompleteTextView customerSearchInput;
    private View btnNewCustomer, btnClearCustomer;
    private boolean isLandscape;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sale, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        com.proconsi.electrobazar.utils.ThemeManager.applyFontToView(view, requireContext());

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
        btnSuspender = view.findViewById(R.id.btnSuspender);

        customerSearchContainer = view.findViewById(R.id.customerSearchContainer);
        selectedCustomerCard = view.findViewById(R.id.selectedCustomerCard);
        customerNameText = view.findViewById(R.id.customerNameText);
        customerTaxIdText = view.findViewById(R.id.customerTaxIdText);
        customerTariffBadge = view.findViewById(R.id.customerTariffBadge);
        customerSearchInput = view.findViewById(R.id.customerSearchInput);
        btnNewCustomer = view.findViewById(R.id.btnNewCustomer);
        btnClearCustomer = view.findViewById(R.id.btnClearCustomer);

        isLandscape = getResources().getConfiguration().orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE;

        setupRecyclerViews(view);
        setupSearch(view);
        setupCustomerSection();
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
            boolean added = saleViewModel.addProduct(product);
            if (!added) {
                Toast.makeText(getContext(), "Stock insuficiente", Toast.LENGTH_SHORT).show();
            }
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
                boolean added = saleViewModel.addProduct(line.getProduct());
                if (!added) {
                    Toast.makeText(getContext(), "Stock insuficiente", Toast.LENGTH_SHORT).show();
                }
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

    private void setupCustomerSection() {
        // Threshold 1 so dropdown appears after typing just 1 char.
        // But we also show all customers on focus (like the web TPV).
        customerSearchInput.setThreshold(1);

        // Custom ArrayAdapter that displays customer.getName() in the dropdown
        final java.util.List<com.proconsi.electrobazar.models.Customer> allCustomers = new java.util.ArrayList<>();
        android.widget.ArrayAdapter<com.proconsi.electrobazar.models.Customer> customerAdapter =
            new android.widget.ArrayAdapter<com.proconsi.electrobazar.models.Customer>(
                    requireContext(), android.R.layout.simple_dropdown_item_1line, allCustomers) {

                @Override
                public android.view.View getView(int position, android.view.View convertView, android.view.ViewGroup parent) {
                    android.view.View view = super.getView(position, convertView, parent);
                    com.proconsi.electrobazar.models.Customer c = getItem(position);
                    if (view instanceof android.widget.TextView && c != null) {
                        String label = c.getName();
                        if (c.getTaxId() != null && !c.getTaxId().isEmpty()) {
                            label += " — " + c.getTaxId();
                        }
                        ((android.widget.TextView) view).setText(label);
                    }
                    return view;
                }

                @Override
                public android.widget.Filter getFilter() {
                    return new android.widget.Filter() {
                        @Override
                        protected FilterResults performFiltering(CharSequence constraint) {
                            FilterResults results = new FilterResults();
                            java.util.List<com.proconsi.electrobazar.models.Customer> filtered = new java.util.ArrayList<>();
                            if (constraint == null || constraint.length() == 0) {
                                filtered.addAll(allCustomers);
                            } else {
                                String query = constraint.toString().toLowerCase();
                                for (com.proconsi.electrobazar.models.Customer c : allCustomers) {
                                    boolean nameMatch = c.getName() != null && c.getName().toLowerCase().contains(query);
                                    boolean taxIdMatch = c.getTaxId() != null && c.getTaxId().toLowerCase().contains(query);
                                    if (nameMatch || taxIdMatch) {
                                        filtered.add(c);
                                    }
                                }
                            }
                            results.values = filtered;
                            results.count = filtered.size();
                            return results;
                        }

                        @Override
                        @SuppressWarnings("unchecked")
                        protected void publishResults(CharSequence constraint, FilterResults results) {
                            clear();
                            if (results.values != null) {
                                addAll((java.util.List<com.proconsi.electrobazar.models.Customer>) results.values);
                            }
                            notifyDataSetChanged();
                        }

                        @Override
                        public CharSequence convertResultToString(Object resultValue) {
                            // When user selects, don't fill text — we clear it
                            return "";
                        }
                    };
                }
            };
        customerSearchInput.setAdapter(customerAdapter);

        // On focus: load all customers immediately (like web TPV)
        customerSearchInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                saleViewModel.searchCustomers("").observe(getViewLifecycleOwner(), customers -> {
                    if (customers != null) {
                        allCustomers.clear();
                        allCustomers.addAll(customers);
                        customerAdapter.notifyDataSetChanged();
                        customerSearchInput.showDropDown();
                    }
                });
            }
        });

        // On typing: filter via the adapter's built-in filter (which uses our custom getFilter)
        // and also refresh the master list from the API for server-side filtering
        customerSearchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 2) {
                    saleViewModel.searchCustomers(s.toString()).observe(getViewLifecycleOwner(), customers -> {
                        if (customers != null) {
                            allCustomers.clear();
                            allCustomers.addAll(customers);
                            customerAdapter.getFilter().filter(s);
                        }
                    });
                } else if (s.length() == 0) {
                    // When cleared, reset to full list
                    saleViewModel.searchCustomers("").observe(getViewLifecycleOwner(), customers -> {
                        if (customers != null) {
                            allCustomers.clear();
                            allCustomers.addAll(customers);
                            customerAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        customerSearchInput.setOnItemClickListener((parent, v, position, id) -> {
            com.proconsi.electrobazar.models.Customer customer =
                (com.proconsi.electrobazar.models.Customer) parent.getItemAtPosition(position);
            saleViewModel.setCustomer(customer);
            customerSearchInput.setText("");
            // Dismiss keyboard
            android.view.inputmethod.InputMethodManager imm =
                (android.view.inputmethod.InputMethodManager) requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(customerSearchInput.getWindowToken(), 0);
        });

        btnNewCustomer.setOnClickListener(v -> showNewCustomerDialog());
        btnClearCustomer.setOnClickListener(v -> saleViewModel.clearCustomer());
    }
    
    private void setupActions() {
        btnCloseTicket.setOnClickListener(v -> saleViewModel.setTicketVisible(false));
        ticketOverlay.setOnClickListener(v -> saleViewModel.setTicketVisible(false));
        btnCobrar.setOnClickListener(v -> showCheckoutDialog());
        btnClearTicket.setOnClickListener(v -> saleViewModel.clearTicket());
        btnSuspender.setOnClickListener(v -> showSuspendDialog());
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
            boolean hasItems = !lines.isEmpty();
            btnCobrar.setEnabled(hasItems);
            
            SessionManager sessionManager = new SessionManager(requireContext());
            if (hasItems && sessionManager.hasPermission("HOLD_SALES")) {
                btnSuspender.setVisibility(View.VISIBLE);
            } else {
                btnSuspender.setVisibility(View.GONE);
            }
        });

        saleViewModel.getTotalAmount().observe(getViewLifecycleOwner(), amount -> {
            ticketTotalText.setText(String.format(Locale.getDefault(), "%.2f€", amount));
        });

        saleViewModel.getTotalItems().observe(getViewLifecycleOwner(), count -> {
            ticketCountBadge.setText(String.valueOf(count));
        });

        saleViewModel.isTicketVisible().observe(getViewLifecycleOwner(), this::toggleTicketPanel);

        saleViewModel.getSelectedCustomer().observe(getViewLifecycleOwner(), customer -> {
            if (customer != null) {
                customerSearchContainer.setVisibility(View.GONE);
                selectedCustomerCard.setVisibility(View.VISIBLE);
                customerNameText.setText(customer.getName());
                customerTaxIdText.setText(customer.getTaxId() != null ? customer.getTaxId() : "Sin NIF");
                
                if (customer.getTariff() != null) {
                    customerTariffBadge.setVisibility(View.VISIBLE);
                    customerTariffBadge.setText(customer.getTariff().getName());
                } else if (Boolean.TRUE.equals(customer.getHasRecargoEquivalencia())) {
                    customerTariffBadge.setVisibility(View.VISIBLE);
                    customerTariffBadge.setText("RE");
                } else {
                    customerTariffBadge.setVisibility(View.GONE);
                }
            } else {
                customerSearchContainer.setVisibility(View.VISIBLE);
                selectedCustomerCard.setVisibility(View.GONE);
            }
        });

        // Observe sale result
        saleViewModel.getSaleResponse().observe(getViewLifecycleOwner(), response -> {
            if (response != null) {
                Toast.makeText(getContext(), "Venta realizada con éxito (" + response.getGrandTotal() + " €)", Toast.LENGTH_LONG).show();
            }
        });

        saleViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                if (error.equals("SUCCESS_HOLD")) {
                    Toast.makeText(getContext(), "Venta puesta en espera", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                }
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

        // Customer Summary in Checkout
        View summaryWithCustomer = dialogView.findViewById(R.id.summaryWithCustomer);
        View summaryNoCustomer = dialogView.findViewById(R.id.summaryNoCustomer);
        TextView checkoutCustomerName = dialogView.findViewById(R.id.checkoutCustomerName);
        TextView checkoutCustomerTaxId = dialogView.findViewById(R.id.checkoutCustomerTaxId);

        com.proconsi.electrobazar.models.Customer selectedCustomer = saleViewModel.getSelectedCustomer().getValue();
        if (selectedCustomer != null) {
            summaryWithCustomer.setVisibility(View.VISIBLE);
            summaryNoCustomer.setVisibility(View.GONE);
            checkoutCustomerName.setText(selectedCustomer.getName());
            checkoutCustomerTaxId.setText(selectedCustomer.getTaxId() != null ? selectedCustomer.getTaxId() : "Sin NIF");
        } else {
            summaryWithCustomer.setVisibility(View.GONE);
            summaryNoCustomer.setVisibility(View.VISIBLE);
        }

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
            // Do not set any receivedAmount value, just clear it and indicate "Dinero justo"
            receivedInput.setText("");
            changeText.setText("Dinero justo");
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
            
            com.proconsi.electrobazar.models.Customer currentCustomer = saleViewModel.getSelectedCustomer().getValue();
            if (currentCustomer != null) {
                request.setCustomerId(currentCustomer.getId());
                // requestInvoice=true triggers factura generation on the backend
                // (same flag used by the web TPV's requestInvoice form field)
                request.setRequestInvoice(true);
            }
            
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
                BigDecimal received;
                if (receivedStr.isEmpty()) {
                    // Send null for "Dinero justo" (backend skips validation)
                    received = null;
                } else {
                    try {
                        received = new BigDecimal(receivedStr);
                    } catch (Exception e) {
                        received = null;
                    }
                }

                if (received != null && received.compareTo(total) < 0) {
                    Toast.makeText(getContext(), "La cantidad entregada es menor al total a cobrar.", Toast.LENGTH_SHORT).show();
                    return;
                }
                request.setReceivedAmount(received);
            } else {
                request.setPaymentMethod(PaymentMethod.CARD);
                // Card is also "Dinero justo" by default
                request.setReceivedAmount(null);
            }

            saleViewModel.confirmSale(request);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showNewCustomerDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        dialog.setContentView(R.layout.dialog_new_customer);

        android.widget.RadioGroup typeGroup = dialog.findViewById(R.id.rgCustomerType);
        EditText nameInput = dialog.findViewById(R.id.etCustomerName);
        EditText taxIdInput = dialog.findViewById(R.id.etCustomerTaxId);
        EditText emailInput = dialog.findViewById(R.id.etCustomerEmail);
        EditText phoneInput = dialog.findViewById(R.id.etCustomerPhone);
        EditText addressInput = dialog.findViewById(R.id.etCustomerAddress);
        EditText cityInput = dialog.findViewById(R.id.etCustomerCity);
        EditText postalCodeInput = dialog.findViewById(R.id.etCustomerPostalCode);
        android.widget.CheckBox reToggle = dialog.findViewById(R.id.cbHasRecargo);
        View reSection = dialog.findViewById(R.id.reSection);
        View btnSave = dialog.findViewById(R.id.btnSaveCustomer);

        typeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            boolean isCompany = checkedId == R.id.rbCompany;
            reSection.setVisibility(isCompany ? View.VISIBLE : View.GONE);
            if (!isCompany) reToggle.setChecked(false);
        });

        btnSave.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            if (name.isEmpty()) {
                nameInput.setError("Obligatorio");
                return;
            }

            com.proconsi.electrobazar.models.CustomerRequest request = new com.proconsi.electrobazar.models.CustomerRequest();
            request.setName(name);
            request.setTaxId(taxIdInput.getText().toString().trim());
            request.setEmail(emailInput.getText().toString().trim());
            request.setPhone(phoneInput.getText().toString().trim());
            request.setAddress(addressInput.getText().toString().trim());
            request.setCity(cityInput.getText().toString().trim());
            request.setPostalCode(postalCodeInput.getText().toString().trim());
            request.setType(typeGroup.getCheckedRadioButtonId() == R.id.rbCompany ? "COMPANY" : "INDIVIDUAL");
            request.setHasRecargoEquivalencia(reToggle.isChecked());
            request.setActive(true);

            new com.proconsi.electrobazar.repositories.CustomerRepository().createCustomer(request).observe(getViewLifecycleOwner(), customer -> {
                if (customer != null) {
                    saleViewModel.setCustomer(customer);
                    dialog.dismiss();
                } else {
                    Toast.makeText(getContext(), "Error al crear cliente", Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    private void showSuspendDialog() {
        android.widget.EditText input = new android.widget.EditText(requireContext());
        input.setHint("Ej: Cliente esperando tarjeta");
        
        new androidx.appcompat.app.AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
                .setTitle("Suspender venta")
                .setView(input)
                .setPositiveButton("SUSPENDER", (dialog, which) -> {
                    String label = input.getText().toString().trim();
                    saleViewModel.holdSale(label);
                })
                .setNegativeButton("CANCELAR", null)
                .show();
    }
}


