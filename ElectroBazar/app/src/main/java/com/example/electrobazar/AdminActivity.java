package com.example.electrobazar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.electrobazar.adapter.AdminProductAdapter;
import com.example.electrobazar.models.DashboardData;
import com.example.electrobazar.models.Product;
import com.example.electrobazar.models.Sale;
import com.example.electrobazar.network.RetrofitClient;
import com.example.electrobazar.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminActivity extends AppCompatActivity {

    private View viewDashboard;
    private LinearLayout viewProducts, viewWorkers, viewCrm, viewInvoices;
    private TextView tvDashSales, tvDashTotal;
    private SessionManager sessionManager;

    private RecyclerView rvRecentSales, rvAdminProducts, rvAdminWorkers, rvAdminCustomers, rvAdminInvoices;
    private EditText etSearchCustomer;
    private AdminProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        sessionManager = new SessionManager(this);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        viewDashboard = findViewById(R.id.viewDashboard);
        viewProducts = findViewById(R.id.viewProducts);
        viewWorkers = findViewById(R.id.viewWorkers);
        viewCrm = findViewById(R.id.viewCrm);
        viewInvoices = findViewById(R.id.viewInvoices);

        tvDashSales = findViewById(R.id.tvDashSales);
        tvDashTotal = findViewById(R.id.tvDashTotal);

        rvAdminProducts = findViewById(R.id.rvAdminProducts);
        rvAdminWorkers = findViewById(R.id.rvAdminWorkers);
        rvAdminCustomers = findViewById(R.id.rvAdminCustomers);
        rvAdminInvoices = findViewById(R.id.rvAdminInvoices);

        // Setup AdminProducts list
        rvAdminProducts.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new AdminProductAdapter(new ArrayList<>());
        rvAdminProducts.setAdapter(productAdapter);

        BottomNavigationView bottomNav = findViewById(R.id.adminBottomNav);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_dashboard)
                showView(R.id.viewDashboard);
            else if (id == R.id.nav_products)
                showView(R.id.viewProducts);
            else if (id == R.id.nav_invoices)
                showView(R.id.viewInvoices);
            else if (id == R.id.nav_workers)
                showView(R.id.viewWorkers);
            else if (id == R.id.nav_crm)
                showView(R.id.viewCrm);
            return true;
        });

        // Load dashboard data
        loadDashboard();
        loadAdminProducts();
    }

    private void loadAdminProducts() {
        RetrofitClient.getApiService().getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productAdapter.setProducts(response.body());
                } else {
                    Toast.makeText(AdminActivity.this, "Error cargando productos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(AdminActivity.this, "Error de red al cargar productos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showView(int viewId) {
        int[] allViews = { R.id.viewDashboard, R.id.viewProducts, R.id.viewWorkers, R.id.viewCrm, R.id.viewInvoices };
        for (int id : allViews) {
            View v = findViewById(id);
            if (v != null)
                v.setVisibility(id == viewId ? View.VISIBLE : View.GONE);
        }
    }

    private void loadDashboard() {
        RetrofitClient.getApiService().getDashboardSummary().enqueue(new Callback<DashboardData>() {
            @Override
            public void onResponse(Call<DashboardData> call, Response<DashboardData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DashboardData data = response.body();
                    tvDashSales.setText(String.valueOf(data.getTodaySalesCount()));
                    tvDashTotal.setText(String.format("%.2f€", data.getTodayTotalRevenue()));
                }
            }

            @Override
            public void onFailure(Call<DashboardData> call, Throwable t) {
                Toast.makeText(AdminActivity.this, "Error al cargar datos del dashboard", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
