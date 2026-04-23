package com.example.a12122028_1220848_courseproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import com.example.a12122028_1220848_courseproject.R;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

public class LibrarianDashboardActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_librarian_dashboard);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        frameLayout = findViewById(R.id.fragment_container);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ManageStudentsFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_manage_students);
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_manage_students) {
                replaceFragmentWithAnimation(new ManageStudentsFragment());
            } else if (id == R.id.nav_book_management) {
                replaceFragmentWithAnimation(new BookManagementFragment());
            } else if (id == R.id.nav_add_librarian) {
                replaceFragmentWithAnimation(new AddLibrarianFragment());
            } else if (id == R.id.nav_reservations) {
                replaceFragmentWithAnimation(new ReservationManagementFragment());
            } else if (id == R.id.nav_reports) {
                replaceFragmentWithAnimation(new ReportsFragment());
            } else if (id == R.id.nav_settings) {
                replaceFragmentWithAnimation(new LibrarySettingsFragment());
            } else if (id == R.id.nav_logout) {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });


    }
    private void replaceFragmentWithAnimation(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                )
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

}
