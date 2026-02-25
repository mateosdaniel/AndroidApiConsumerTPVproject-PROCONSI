package com.example.electrobazar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductsActivity extends AppCompatActivity {

    private RecyclerView categoryRecyclerView;
    private RecyclerView productRecyclerView;
    private RecyclerView ticketRecyclerView;

    private CategoryAdapter categoryAdapter;
    private ProductAdapter productAdapter;
    private TicketAdapter ticketAdapter;

    private EditText searchInput;
    private TextView tvWorkerName;
    private TextView tvTicketTotal;
    private MaterialButton btnPay;
    private ImageButton btnMenu;

    private final List<Category> categoryList = new ArrayList<>();
    private final List<Product> productList = new ArrayList<>();
    private final List<SaleLine> ticketLines = new ArrayList<>();

    private SessionManager sessionManager;
    private double totalAmount = 0.0;

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

        initViews();
        setupRecyclerViews();
        loadCategories();
        loadProducts(null);
        setupSearch();
        updateWorkerInfo();

        btnPay.setOnClickListener(v -> processCheckout());
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
    }

    private void updateWorkerInfo() {
        Worker worker = sessionManager.getWorker();
        if (worker != null) {
            tvWorkerName.setText(worker.getUsername());
            // TODO: Handle permissions for menu button
        }
    }

    private void setupRecyclerViews() {
        // Categories
        categoryAdapter = new CategoryAdapter(this, categoryList, category -> {
            searchInput.setText("");
            if (category.getId() == null) {
                loadProducts(null);
            } else {
                loadProductsByCategory(category.getId());
            }
        });
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoryRecyclerView.setAdapter(categoryAdapter);

        // Products
        productAdapter = new ProductAdapter(this, productList, this::addToTicket);
        productRecyclerView.setLayoutManager(new GridLayoutManager(this, 3)); // 3 columns for TPV
        productRecyclerView.setAdapter(productAdapter);

        // Ticket
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
    }

    private void handleQuantityChange(SaleLine line, int delta) {
        int newQty = line.getQuantity() + delta;
        if (newQty <= 0) {
            ticketLines.remove(line);
        } else {
            line.setQuantity(newQty);
            line.setSubtotal(newQty * line.getUnitPrice());
        }
        ticketAdapter.notifyDataSetChanged();
        calculateTotal();
    }

    private void calculateTotal() {
        totalAmount = 0.0;
        for (SaleLine line : ticketLines) {
            totalAmount += line.getSubtotal();
        }
        tvTicketTotal.setText(String.format("%.2f €", totalAmount));
        btnPay.setEnabled(!ticketLines.isEmpty());
    }

    private void loadCategories() {
        RetrofitClient.getApiService().getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList.clear();
                    categoryList.add(new Category(null, "Todos"));
                    categoryList.addAll(response.body());
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

    private void loadProductsByCategory(Long categoryId) {
        loadProducts(categoryId);
    }

    private void setupSearch() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    loadProducts(null);
                } else {
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

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void processCheckout() {
        if (ticketLines.isEmpty())
            return;

        Sale sale = new Sale();
        sale.setLines(new ArrayList<>(ticketLines));
        sale.setTotalAmount(totalAmount);
        sale.setPaymentMethod("CASH"); // Default for now

        RetrofitClient.getApiService().createSale(sale).enqueue(new Callback<Sale>() {
            @Override
            public void onResponse(Call<Sale> call, Response<Sale> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProductsActivity.this, "Venta realizada", Toast.LENGTH_SHORT).show();
                    ticketLines.clear();
                    ticketAdapter.notifyDataSetChanged();
                    calculateTotal();
                    // TODO: Open Receipt/Ticket Activity
                } else {
                    Toast.makeText(ProductsActivity.this, "Error al vender: " + response.code(), Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<Sale> call, Throwable t) {
                Toast.makeText(ProductsActivity.this, "Error conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}