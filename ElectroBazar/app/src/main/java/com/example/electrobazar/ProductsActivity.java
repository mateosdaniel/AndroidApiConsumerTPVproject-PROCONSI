package com.example.electrobazar;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.electrobazar.adapter.CategoryAdapter;
import com.example.electrobazar.adapter.ProductAdapter;
import com.example.electrobazar.adapter.TicketAdapter;
import com.example.electrobazar.models.Category;
import com.example.electrobazar.models.Product;
import com.example.electrobazar.models.Sale;
import com.example.electrobazar.models.SaleLine;
import com.example.electrobazar.models.Worker;
import com.example.electrobazar.network.RetrofitClient;
import com.example.electrobazar.utils.SessionManager;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductsActivity extends AppCompatActivity {

    // Core views (both orientations)
    private RecyclerView categoryRecyclerView, productRecyclerView, ticketRecyclerView;
    private CategoryAdapter categoryAdapter;
    private ProductAdapter productAdapter;
    private TicketAdapter ticketAdapter;

    private EditText searchInput;
    private TextView tvWorkerName, tvTicketTotal;
    private MaterialButton btnPay;
    private ImageButton btnMenu;

    // Landscape-only toolbar buttons (null in portrait)
    private LinearLayout btnInventory, btnCashClose, statsGroup;
    private ImageButton btnAdmin;
    private TextView tvSalesCount, tvSalesTotal;

    // Portrait-only views (null in landscape)
    private FloatingActionButton fabTicket;
    private ConstraintLayout ticketPanel;
    private ImageButton btnCloseTicket;

    // Portrait hamburger dropdown (null in landscape)
    private LinearLayout menuDropdown;
    private LinearLayout menuItemAdmin, menuItemInventory, menuItemCashClose;
    private LinearLayout menuItemPreferences, menuItemLogout;
    private TextView tvMenuSalesCount, tvMenuSalesTotal;

    private SessionManager sessionManager;
    private boolean isPortrait;
    private boolean menuOpen = false;

    private final List<Category> categoryList = new ArrayList<>();
    private final List<Product> productList = new ArrayList<>();
    private final List<SaleLine> ticketLines = new ArrayList<>();
    private double totalAmount = 0.0;
    private Long pendingDownloadId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_products);
        isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        initViews();
        applyPermissions();
        setupRecyclerViews();
        loadCategories();
        loadProducts(null);
        setupSearch();
        updateWorkerInfo();
        loadTodayStats();

        requestStoragePermissions();

        btnPay.setOnClickListener(v -> processCheckout());

        // Hamburger menu toggle (both orientations)
        btnMenu.setOnClickListener(v -> toggleMenu());

        // Portrait-specific
        if (isPortrait) {
            if (fabTicket != null)
                fabTicket.setOnClickListener(v -> showTicketPanel());
            if (btnCloseTicket != null)
                btnCloseTicket.setOnClickListener(v -> hideTicketPanel());
        }
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

    private void initViews() {
        categoryRecyclerView = findViewById(R.id.categoryRecyclerView);
        productRecyclerView = findViewById(R.id.productRecyclerView);
        ticketRecyclerView = findViewById(R.id.ticketRecyclerView);
        searchInput = findViewById(R.id.searchInput);
        tvWorkerName = findViewById(R.id.tvWorkerName);
        tvTicketTotal = findViewById(R.id.tvTicketTotal);
        btnPay = findViewById(R.id.btnPay);
        btnMenu = findViewById(R.id.btnMenu);

        // Landscape-only (null in portrait)
        btnAdmin = findViewById(R.id.btnAdmin);
        btnInventory = findViewById(R.id.btnInventory);
        btnCashClose = findViewById(R.id.btnCashClose);
        statsGroup = findViewById(R.id.statsGroup);
        tvSalesCount = findViewById(R.id.tvSalesCount);
        tvSalesTotal = findViewById(R.id.tvSalesTotal);

        // Portrait-only (null in landscape)
        fabTicket = findViewById(R.id.fabTicket);
        ticketPanel = isPortrait ? (ConstraintLayout) findViewById(R.id.ticketPanel) : null;
        btnCloseTicket = findViewById(R.id.btnCloseTicket);

        // Portrait dropdown (null in landscape)
        menuDropdown = findViewById(R.id.menuDropdown);
        menuItemAdmin = findViewById(R.id.menuItemAdmin);
        menuItemInventory = findViewById(R.id.menuItemInventory);
        menuItemCashClose = findViewById(R.id.menuItemCashClose);
        menuItemPreferences = findViewById(R.id.menuItemPreferences);
        menuItemLogout = findViewById(R.id.menuItemLogout);
        tvMenuSalesCount = findViewById(R.id.tvMenuSalesCount);
        tvMenuSalesTotal = findViewById(R.id.tvMenuSalesTotal);
    }

    /**
     * Show or hide toolbar buttons and menu items based on worker permissions.
     */
    private void applyPermissions() {
        boolean isAdmin = sessionManager.hasPermission("ADMIN_ACCESS");
        boolean canManageProducts = sessionManager.hasPermission("MANAGE_PRODUCTS_TPV") || isAdmin;
        boolean canCashClose = sessionManager.hasPermission("CASH_CLOSE");

        if (!isPortrait) {
            // Landscape: show buttons directly on toolbar
            if (btnAdmin != null) {
                btnAdmin.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
                btnAdmin.setOnClickListener(v -> openAdmin());
            }
            if (btnInventory != null) {
                btnInventory.setVisibility(canManageProducts ? View.VISIBLE : View.GONE);
                btnInventory.setOnClickListener(v -> openInventory());
            }
            if (btnCashClose != null) {
                btnCashClose.setVisibility(canCashClose ? View.VISIBLE : View.GONE);
                btnCashClose.setOnClickListener(v -> openCashClose());
            }
            if (statsGroup != null) {
                statsGroup.setVisibility(canCashClose ? View.VISIBLE : View.GONE);
            }
        } else {
            // Portrait: set visibility of dropdown items
            if (menuItemAdmin != null)
                menuItemAdmin.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
            if (menuItemInventory != null)
                menuItemInventory.setVisibility(canManageProducts ? View.VISIBLE : View.GONE);
            if (menuItemCashClose != null)
                menuItemCashClose.setVisibility(canCashClose ? View.VISIBLE : View.GONE);

            // Wire dropdown menu items
            if (menuItemAdmin != null)
                menuItemAdmin.setOnClickListener(v -> {
                    closeMenu();
                    openAdmin();
                });
            if (menuItemInventory != null)
                menuItemInventory.setOnClickListener(v -> {
                    closeMenu();
                    openInventory();
                });
            if (menuItemCashClose != null)
                menuItemCashClose.setOnClickListener(v -> {
                    closeMenu();
                    openCashClose();
                });
            if (menuItemPreferences != null)
                menuItemPreferences.setOnClickListener(v -> {
                    closeMenu();
                    openPreferences();
                });
            if (menuItemLogout != null)
                menuItemLogout.setOnClickListener(v -> {
                    closeMenu();
                    logout();
                });
        }
    }

    private void toggleMenu() {
        if (menuOpen)
            closeMenu();
        else
            openMenu();
    }

    private void openMenu() {
        if (menuDropdown != null) {
            menuDropdown.setVisibility(View.VISIBLE);
            menuOpen = true;
        } else {
            // Landscape: show popup/simple dropdown using android popup menu
            android.widget.PopupMenu popup = new android.widget.PopupMenu(this, btnMenu);
            if (sessionManager.hasPermission("ADMIN_ACCESS"))
                popup.getMenu().add(0, 1, 0, "Panel Admin");
            if (sessionManager.hasPermission("MANAGE_PRODUCTS_TPV") || sessionManager.hasPermission("ADMIN_ACCESS"))
                popup.getMenu().add(0, 2, 0, "Inventario");
            if (sessionManager.hasPermission("CASH_CLOSE"))
                popup.getMenu().add(0, 3, 0, "Cierre de caja");
            popup.getMenu().add(0, 4, 0, "Preferencias");
            popup.getMenu().add(0, 5, 0, "Cambiar usuario");
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case 1:
                        openAdmin();
                        return true;
                    case 2:
                        openInventory();
                        return true;
                    case 3:
                        openCashClose();
                        return true;
                    case 4:
                        openPreferences();
                        return true;
                    case 5:
                        logout();
                        return true;
                }
                return false;
            });
            popup.show();
        }
    }

    private void closeMenu() {
        if (menuDropdown != null) {
            menuDropdown.setVisibility(View.GONE);
            menuOpen = false;
        }
    }

    private void openAdmin() {
        startActivity(new Intent(this, AdminActivity.class));
    }

    private void openInventory() {
        startActivity(new Intent(this, InventoryActivity.class));
    }

    private void openCashClose() {
        startActivity(new Intent(this, CashCloseActivity.class));
    }

    private void openPreferences() {
        startActivity(new Intent(this, PreferencesActivity.class));
    }

    private void logout() {
        sessionManager.logout();
        startActivity(new Intent(this, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    private void showTicketPanel() {
        if (ticketPanel != null) {
            ticketPanel.setVisibility(View.VISIBLE);
            if (fabTicket != null)
                fabTicket.setVisibility(View.GONE);
        }
    }

    private void hideTicketPanel() {
        if (ticketPanel != null) {
            ticketPanel.setVisibility(View.GONE);
            if (fabTicket != null)
                fabTicket.setVisibility(View.VISIBLE);
        }
    }

    private void updateWorkerInfo() {
        Worker worker = sessionManager.getWorker();
        if (worker != null)
            tvWorkerName.setText(worker.getUsername());
    }

    private void loadTodayStats() {
        if (!sessionManager.hasPermission("CASH_CLOSE"))
            return;
        RetrofitClient.getApiService().getTodaySales().enqueue(new Callback<List<Sale>>() {
            @Override
            public void onResponse(Call<List<Sale>> call, Response<List<Sale>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Sale> sales = response.body();
                    int count = sales.size();
                    double total = 0;
                    for (Sale s : sales)
                        total += s.getTotalAmount();
                    String countStr = count + " ventas";
                    String totalStr = String.format("%.2f€", total);

                    // Landscape stats bar
                    if (tvSalesCount != null)
                        tvSalesCount.setText(String.valueOf(count));
                    if (tvSalesTotal != null)
                        tvSalesTotal.setText(totalStr);

                    // Portrait dropdown stats
                    if (tvMenuSalesCount != null)
                        tvMenuSalesCount.setText(countStr);
                    if (tvMenuSalesTotal != null)
                        tvMenuSalesTotal.setText(totalStr);
                }
            }

            @Override
            public void onFailure(Call<List<Sale>> call, Throwable t) {
                // Silently fail — stats are non-critical
            }
        });
    }

    private void setupRecyclerViews() {
        categoryAdapter = new CategoryAdapter(this, categoryList, category -> {
            searchInput.setText("");
            loadProducts(category.getId() == null ? null : category.getId());
        });
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoryRecyclerView.setAdapter(categoryAdapter);

        int columns = isPortrait ? 2 : 3;
        productAdapter = new ProductAdapter(this, productList, this::addToTicket);
        productRecyclerView.setLayoutManager(new GridLayoutManager(this, columns));
        productRecyclerView.setAdapter(productAdapter);

        ticketAdapter = new TicketAdapter(this, ticketLines, this::handleQuantityChange);
        ticketRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ticketRecyclerView.setAdapter(ticketAdapter);
    }

    private void addToTicket(Product product) {
        boolean found = false;
        for (SaleLine line : ticketLines) {
            if (line.getProduct().getId().equals(product.getId())) {
                line.setQuantity(line.getQuantity() + 1);
                line.setSubtotal(line.getQuantity() * line.getUnitPrice());
                found = true;
                break;
            }
        }
        if (!found) {
            SaleLine newLine = new SaleLine();
            newLine.setProduct(product);
            newLine.setQuantity(1);
            newLine.setUnitPrice(product.getPrice());
            newLine.setSubtotal(product.getPrice());
            ticketLines.add(newLine);
        }
        ticketAdapter.notifyDataSetChanged();
        calculateTotal();

        // Subtle bounce animation on FAB when adding to cart
        if (fabTicket != null && fabTicket.getVisibility() == View.VISIBLE) {
            animateFab();
        }
    }

    private void animateFab() {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(fabTicket, "scaleX", 1f, 1.3f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(fabTicket, "scaleY", 1f, 1.3f, 1f);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(scaleX, scaleY);
        set.setDuration(300);
        set.setInterpolator(new OvershootInterpolator());
        set.start();
    }

    private void handleQuantityChange(SaleLine line, int delta) {
        int newQty = line.getQuantity() + delta;
        if (newQty <= 0)
            ticketLines.remove(line);
        else {
            line.setQuantity(newQty);
            line.setSubtotal(newQty * line.getUnitPrice());
        }
        ticketAdapter.notifyDataSetChanged();
        calculateTotal();
    }

    private void calculateTotal() {
        totalAmount = 0.0;
        int totalItems = 0;
        for (SaleLine line : ticketLines) {
            totalAmount += line.getSubtotal();
            totalItems += line.getQuantity();
        }
        tvTicketTotal.setText(String.format("%.2f €", totalAmount));
        btnPay.setEnabled(!ticketLines.isEmpty());
        updateCartBadge(totalItems);
    }

    private void updateCartBadge(int count) {
        if (fabTicket == null)
            return;
        // Use a small TextView badge on top of the FAB
        TextView badge = findViewById(R.id.tvCartBadge);
        if (badge != null) {
            if (count > 0) {
                badge.setText(String.valueOf(count));
                badge.setVisibility(View.VISIBLE);
            } else {
                badge.setVisibility(View.GONE);
            }
        }
    }

    private void loadCategories() {
        RetrofitClient.getApiService().getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList.clear();
                    categoryList.add(new Category(null, "Todos"));
                    // Only add active categories
                    for (Category c : response.body()) {
                        if (c.getActive() == null || c.getActive()) {
                            categoryList.add(c);
                        }
                    }
                    categoryAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(ProductsActivity.this, "Error categorías", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProducts(Long categoryId) {
        Call<List<Product>> call = (categoryId == null)
                ? RetrofitClient.getApiService().getProducts()
                : RetrofitClient.getApiService().getProductsByCategory(categoryId);
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList.clear();
                    productList.addAll(response.body());
                    productAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(ProductsActivity.this, "Error productos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSearch() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (query.isEmpty())
                    loadProducts(null);
                else {
                    RetrofitClient.getApiService().searchProducts(query).enqueue(new Callback<List<Product>>() {
                        @Override
                        public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                productList.clear();
                                productList.addAll(response.body());
                                productAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Product>> call, Throwable t) {
                        }
                    });
                }
            }
        });
    }

    private void processCheckout() {
        if (ticketLines.isEmpty())
            return;

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_checkout, null);
        builder.setView(view);

        android.app.AlertDialog dialog = builder.create();

        android.widget.RadioGroup rgPaymentMethod = view.findViewById(R.id.rgPaymentMethod);
        com.google.android.material.textfield.TextInputEditText etCustomerName = view.findViewById(R.id.etCustomerName);
        com.google.android.material.textfield.TextInputEditText etNotes = view.findViewById(R.id.etNotes);
        com.google.android.material.button.MaterialButton btnConfirm = view.findViewById(R.id.btnConfirmCheckout);
        com.google.android.material.button.MaterialButton btnCancel = view.findViewById(R.id.btnCancelCheckout);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            String paymentMethod = rgPaymentMethod.getCheckedRadioButtonId() == R.id.rbCard ? "CARD" : "CASH";
            String customerName = etCustomerName.getText() != null ? etCustomerName.getText().toString().trim() : "";
            String notes = etNotes.getText() != null ? etNotes.getText().toString().trim() : "";

            Sale sale = new Sale();
            sale.setLines(new ArrayList<>(ticketLines));
            sale.setTotalAmount(totalAmount);
            sale.setPaymentMethod(paymentMethod);

            if (!notes.isEmpty()) {
                sale.setNotes(notes);
            }

            if (!customerName.isEmpty()) {
                com.example.electrobazar.models.Customer customer = new com.example.electrobazar.models.Customer();
                customer.setName(customerName);
                customer.setType("INDIVIDUAL"); // Defaulting to individual
                sale.setCustomer(customer);
            }

            dialog.dismiss();
            executeSale(sale);
        });

        dialog.show();
    }

    private void executeSale(Sale sale) {
        RetrofitClient.getApiService().createSale(sale).enqueue(new Callback<Sale>() {
            @Override
            public void onResponse(Call<Sale> call, Response<Sale> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ProductsActivity.this, "Venta realizada", Toast.LENGTH_SHORT).show();

                    // Download the ticket
                    downloadTicket(response.body().getId());

                    ticketLines.clear();
                    ticketAdapter.notifyDataSetChanged();
                    calculateTotal();
                    if (isPortrait)
                        hideTicketPanel();
                } else {
                    Toast.makeText(ProductsActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Sale> call, Throwable t) {
                Toast.makeText(ProductsActivity.this, "Error conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void downloadTicket(Long saleId) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                pendingDownloadId = saleId; // Store the ID for when permission is granted
                androidx.core.app.ActivityCompat.requestPermissions(this,
                        new String[] { android.Manifest.permission.WRITE_EXTERNAL_STORAGE }, 100);
                return;
            }
        }

        Toast.makeText(this, "Descargando ticket...", Toast.LENGTH_SHORT).show();
        RetrofitClient.getApiService().getSaleTicket(saleId).enqueue(new Callback<okhttp3.ResponseBody>() {
            @Override
            public void onResponse(Call<okhttp3.ResponseBody> call, Response<okhttp3.ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean success = com.example.electrobazar.utils.FileUtil.writeResponseBodyToDisk(
                            ProductsActivity.this, response.body(), "Ticket_" + saleId + ".pdf");
                    if (success) {
                        Toast.makeText(ProductsActivity.this, "Ticket descargado correctamente", Toast.LENGTH_LONG)
                                .show();
                    } else {
                        Toast.makeText(ProductsActivity.this, "Error al guardar el ticket", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    int code = response.code();
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null)
                            errorBody = response.errorBody().string();
                    } catch (Exception e) {
                    }

                    android.util.Log.e("ProductsActivity", "Error: " + code + " - " + errorBody);
                    Toast.makeText(ProductsActivity.this,
                            "Error al descargar ticket: " + code, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<okhttp3.ResponseBody> call, Throwable t) {
                Toast.makeText(ProductsActivity.this, "Error de red al descargar ticket", Toast.LENGTH_SHORT).show();
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
                    downloadTicket(pendingDownloadId);
                    pendingDownloadId = null;
                }
            } else {
                Toast.makeText(this, "Permiso de almacenamiento denegado. No se pueden descargar tickets.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (menuOpen) {
            closeMenu();
            return;
        }
        if (isPortrait && ticketPanel != null && ticketPanel.getVisibility() == View.VISIBLE) {
            hideTicketPanel();
            return;
        }
        super.onBackPressed();
    }
}