package com.proconsi.electrobazar.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.proconsi.electrobazar.R;
import androidx.lifecycle.ViewModelProvider;

import com.proconsi.electrobazar.ui.fragments.SaleFragment;
import com.proconsi.electrobazar.ui.fragments.DashboardFragment;
import com.proconsi.electrobazar.ui.fragments.AdminFragment;
import com.proconsi.electrobazar.ui.fragments.InventoryFragment;
import com.proconsi.electrobazar.utils.SessionManager;
import com.proconsi.electrobazar.viewmodels.SaleViewModel;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private SessionManager sessionManager;
    private SaleViewModel saleViewModel;
    private TextView navWorkerName, navWorkerRole;
    private TextView cartBadge, cartBadgeLand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);
        saleViewModel = new ViewModelProvider(this).get(SaleViewModel.class);
        
        // Portrait elements
        drawerLayout = findViewById(R.id.drawerLayout);
        cartBadge = findViewById(R.id.cartBadge);
        NavigationView navigationView = findViewById(R.id.navigationView);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
            View headerView = navigationView.getHeaderView(0);
            navWorkerName = headerView.findViewById(R.id.navWorkerName);
            navWorkerRole = headerView.findViewById(R.id.navWorkerRole);
            updateNavHeader();
        }

        ImageButton hamburgerBtn = findViewById(R.id.hamburgerBtn);
        if (hamburgerBtn != null) {
            hamburgerBtn.setOnClickListener(v -> {
                if (drawerLayout != null) {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }

        // Landscape elements
        cartBadgeLand = findViewById(R.id.cartBadgeLand);
        View cashCloseBtn = findViewById(R.id.cashCloseBtn);
        if (cashCloseBtn != null) cashCloseBtn.setOnClickListener(v -> Toast.makeText(this, "Cierre de caja", Toast.LENGTH_SHORT).show());

        View cartButton = findViewById(R.id.cartButton);
        if (cartButton != null) cartButton.setOnClickListener(v -> saleViewModel.toggleTicket());

        View cartButtonLand = findViewById(R.id.cartButtonLand);
        if (cartButtonLand != null) cartButtonLand.setOnClickListener(v -> saleViewModel.toggleTicket());

        ImageButton moreOptionsBtnLand = findViewById(R.id.moreOptionsBtnLand);
        if (moreOptionsBtnLand != null) {
            moreOptionsBtnLand.setOnClickListener(this::showMoreOptionsLand);
        }

        // Observe cart items
        saleViewModel.getTotalItems().observe(this, count -> {
            String countStr = String.valueOf(count);
            int visibility = count > 0 ? View.VISIBLE : View.GONE;
            
            if (cartBadge != null) {
                cartBadge.setText(countStr);
                cartBadge.setVisibility(visibility);
            }
            if (cartBadgeLand != null) {
                cartBadgeLand.setText(countStr);
                cartBadgeLand.setVisibility(visibility);
            }
        });

        // Set App Name from Session (if available, else default)
        // Note: App name usually comes from CompanySettings, for now we use default
        
        if (savedInstanceState == null) {
            loadFragment(new SaleFragment(), "TPV_FRAGMENT");
        }
    }

    private void updateNavHeader() {
        if (navWorkerName != null) {
            navWorkerName.setText(sessionManager.getUsername());
        }
        if (navWorkerRole != null) {
            navWorkerRole.setText(sessionManager.getRole());
        }
    }

    private void showMoreOptionsLand(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.getMenuInflater().inflate(R.menu.landscape_more_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            return handleNavigation(item.getItemId(), item.getTitle().toString());
        });
        popup.show();
    }

    private void showUserMenu(View v) {
        // Redundant now that it's moved to moreOptionsBtnLand, but kept if needed or just removed
        showMoreOptionsLand(v);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        boolean handled = handleNavigation(item.getItemId(), item.getTitle().toString());
        if (handled && drawerLayout != null) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        return handled;
    }

    /**
     * Unified navigation handler for Drawer, Landscape Header, and Popup Menus
     */
    private boolean handleNavigation(int id, String title) {
        if (id == R.id.nav_tpv || id == R.id.menu_suspended) { // Mapping suspended to TPV for now or adding specific logic
            loadFragment(new SaleFragment(), "TPV_FRAGMENT");
            return true;
        } else if (id == R.id.nav_admin) {
            loadFragment(new AdminFragment(), "ADMIN_FRAGMENT");
            return true;
        } else if (id == R.id.nav_inventory) {
            loadFragment(new InventoryFragment(), "INVENTORY_FRAGMENT");
            return true;
        } else if (id == R.id.nav_logout || id == R.id.menu_logout) {
            logout();
            return true;
        } else if (id == R.id.nav_preferences || id == R.id.menu_preferences) {
            Toast.makeText(this, "Preferencias", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            // General handler for items not yet having a fragment
            Toast.makeText(this, "Opción: " + title, Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    private void loadFragment(Fragment fragment, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment, tag);
        transaction.commit();
    }

    private void logout() {
        sessionManager.clearSession();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            Fragment currentFragment = getSupportFragmentManager().findFragmentByTag("ADMIN_FRAGMENT");
            if (currentFragment instanceof AdminFragment && ((AdminFragment) currentFragment).onBackPressed()) {
                return;
            }
            super.onBackPressed();
        }
    }
}
