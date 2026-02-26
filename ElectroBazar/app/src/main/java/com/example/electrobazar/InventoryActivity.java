package com.example.electrobazar;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.electrobazar.utils.SessionManager;
import com.example.electrobazar.adapter.AdminProductAdapter;
import com.example.electrobazar.adapter.AdminCategoryAdapter;
import com.example.electrobazar.models.Product;
import com.example.electrobazar.models.Category;
import com.example.electrobazar.network.RetrofitClient;
import com.google.android.material.tabs.TabLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventoryActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private LinearLayout viewProducts, viewCategories;
    private RecyclerView rvProducts, rvCategories;
    private AdminProductAdapter productAdapter;
    private AdminCategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);
        // Verify permission: needs MANAGE_PRODUCTS_TPV or ADMIN_ACCESS
        if (!sessionManager.hasPermission("MANAGE_PRODUCTS_TPV") && !sessionManager.hasPermission("ADMIN_ACCESS")) {
            Toast.makeText(this, "Acceso denegado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setContentView(R.layout.activity_inventory);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        viewProducts = findViewById(R.id.viewProducts);
        viewCategories = findViewById(R.id.viewCategories);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    viewProducts.setVisibility(View.VISIBLE);
                    viewCategories.setVisibility(View.GONE);
                } else {
                    viewProducts.setVisibility(View.GONE);
                    viewCategories.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        // Initialize RecyclerViews
        rvProducts = findViewById(R.id.rvProducts);
        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new AdminProductAdapter(new ArrayList<>());
        rvProducts.setAdapter(productAdapter);

        rvCategories = findViewById(R.id.rvCategories);
        rvCategories.setLayoutManager(new LinearLayoutManager(this));
        categoryAdapter = new AdminCategoryAdapter(new ArrayList<>());
        rvCategories.setAdapter(categoryAdapter);

        loadProducts();
        loadCategories();
    }

    private void loadProducts() {
        RetrofitClient.getApiService().getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productAdapter.setProducts(response.body());
                } else {
                    Toast.makeText(InventoryActivity.this, "Error al cargar productos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(InventoryActivity.this, "Fallo de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCategories() {
        RetrofitClient.getApiService().getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryAdapter.setCategories(response.body());
                } else {
                    Toast.makeText(InventoryActivity.this, "Error al cargar categorías", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(InventoryActivity.this, "Fallo de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
