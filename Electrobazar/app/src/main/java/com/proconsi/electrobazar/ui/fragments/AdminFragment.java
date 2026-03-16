package com.proconsi.electrobazar.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.proconsi.electrobazar.R;
import com.proconsi.electrobazar.models.AdminSection;
import com.proconsi.electrobazar.ui.adapters.AdminAdapter;

import java.util.ArrayList;
import java.util.List;

public class AdminFragment extends Fragment {

    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private View fragmentContainer;
    private View gridContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin, container, false);

        drawerLayout = view.findViewById(R.id.adminDrawerLayout);
        recyclerView = view.findViewById(R.id.adminRecyclerView);
        fragmentContainer = view.findViewById(R.id.admin_fragment_container);
        gridContainer = recyclerView; // In this layout they are siblings or nested?
        
        ImageButton hamburgerBtn = view.findViewById(R.id.adminHamburgerBtn);
        hamburgerBtn.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        NavigationView navigationView = view.findViewById(R.id.adminNavigationView);
        navigationView.setNavigationItemSelectedListener(item -> {
            handleNavigation(item.getItemId(), item.getTitle().toString());
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        setupGrid();

        return view;
    }

    private void setupGrid() {
        List<AdminSection> sections = new ArrayList<>();
        sections.add(new AdminSection(R.id.admin_nav_dashboard, "Dashboard", R.drawable.ic_admin));
        sections.add(new AdminSection(R.id.admin_nav_inventory, "Inventario", R.drawable.ic_inventory));
        sections.add(new AdminSection(R.id.admin_nav_invoices, "Facturas", R.drawable.ic_invoice));
        sections.add(new AdminSection(R.id.admin_nav_cash_closures, "Cierres de Caja", R.drawable.ic_cash_close));
        sections.add(new AdminSection(R.id.admin_nav_returns, "Devoluciones", R.drawable.ic_return));
        sections.add(new AdminSection(R.id.admin_nav_workers, "Trabajadores", R.drawable.ic_workers));
        sections.add(new AdminSection(R.id.admin_nav_roles, "Roles", R.drawable.ic_role));
        sections.add(new AdminSection(R.id.admin_nav_analytics, "Analytics", R.drawable.ic_analytics));
        sections.add(new AdminSection(R.id.admin_nav_crm, "CRM", R.drawable.ic_crm));
        sections.add(new AdminSection(R.id.admin_nav_price_temp, "Precios Temporales", R.drawable.ic_price_temp));
        sections.add(new AdminSection(R.id.admin_nav_price_bulk, "Precios Masivos", R.drawable.ic_price_bulk));
        sections.add(new AdminSection(R.id.admin_nav_activity, "Log de Actividad", R.drawable.ic_activity_log));
        sections.add(new AdminSection(R.id.admin_nav_tariffs, "Tarifas", R.drawable.ic_tariff));
        sections.add(new AdminSection(R.id.admin_nav_vat_types, "Tipos de IVA", R.drawable.ic_vat));
        sections.add(new AdminSection(R.id.admin_nav_vat_selective, "IVA Selectivo", R.drawable.ic_vat_selective));
        sections.add(new AdminSection(R.id.admin_nav_settings, "Ajustes", R.drawable.ic_settings));
        sections.add(new AdminSection(R.id.admin_nav_security, "Seguridad", R.drawable.ic_security));

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(new AdminAdapter(sections, section -> {
            handleNavigation(section.getId(), section.getTitle());
        }));
    }

    private void handleNavigation(int id, String title) {
        if (id == R.id.admin_nav_dashboard) {
            showSubFragment(new DashboardFragment());
        } else if (id == R.id.admin_nav_inventory) {
            showSubFragment(new InventoryTabsFragment());
        } else if (id == R.id.admin_nav_invoices) {
            showSubFragment(new InvoicesAdminFragment());
        } else if (id == R.id.admin_nav_workers) {
            showSubFragment(new WorkersAdminFragment());
        } else if (id == R.id.admin_nav_crm) {
            showSubFragment(new CrmAdminFragment());
        } else if (id == R.id.admin_nav_roles) {
            showSubFragment(new RolesAdminFragment());
        } else if (id == R.id.admin_nav_cash_closures) {
            showSubFragment(new CashClosuresAdminFragment());
        } else if (id == R.id.admin_nav_returns) {
            showSubFragment(new ReturnsAdminFragment());
        } else if (id == R.id.admin_nav_tariffs) {
            showSubFragment(new TariffsAdminFragment());
        } else if (id == R.id.admin_nav_vat_types) {
            showSubFragment(new TaxRatesAdminFragment());
        } else if (id == R.id.admin_nav_vat_selective) {
            showSubFragment(new SelectiveTaxFragment());
        } else if (id == R.id.admin_nav_price_bulk) {
            showSubFragment(new BulkPricesFragment());
        } else if (id == R.id.admin_nav_analytics) {
            showSubFragment(new AnalyticsFragment());
        } else if (id == R.id.admin_nav_activity) {
            showSubFragment(new ActivityLogFragment());
        } else if (id == R.id.admin_nav_settings) {
            showSubFragment(new SettingsAdminFragment());
        } else if (id == R.id.admin_nav_security) {
            showSubFragment(new SecurityAdminFragment());
        } else {

            // Placeholder for other sections
            Toast.makeText(getContext(), "Sección: " + title, Toast.LENGTH_SHORT).show();
        }
    }

    private void showSubFragment(Fragment fragment) {
        gridContainer.setVisibility(View.GONE);
        fragmentContainer.setVisibility(View.VISIBLE);
        
        getChildFragmentManager().beginTransaction()
                .replace(R.id.admin_fragment_container, fragment)
                .commit();
    }
    
    // Logic to go back to grid
    public boolean onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
        if (fragmentContainer.getVisibility() == View.VISIBLE) {
            fragmentContainer.setVisibility(View.GONE);
            gridContainer.setVisibility(View.VISIBLE);
            return true;
        }
        return false;
    }
}
