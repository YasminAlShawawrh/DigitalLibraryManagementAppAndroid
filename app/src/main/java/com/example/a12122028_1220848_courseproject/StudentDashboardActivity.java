package com.example.a12122028_1220848_courseproject;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

public class StudentDashboardActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        drawerLayout = findViewById(R.id.drawerLayout);
        navView = findViewById(R.id.navView);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        replaceFragment(new BookCatalogFragment());

        navView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_dashboard) {
                replaceFragment(new DashboardFragment());
            }else if (itemId == R.id.nav_book_catalog) {
                replaceFragment(new BookCatalogFragment());
            } else if (itemId == R.id.nav_reading_list) {
                replaceFragment(new ReadingListFragment());
            } else if (itemId == R.id.nav_borrowed) {
                replaceFragment(new BorrowedBooksFragment());
            }else if (itemId == R.id.nav_new_arrivals) {
                replaceFragment(new NewArrivalsFragment());
            } else if (itemId == R.id.nav_profile) {
                replaceFragment(new ProfileFragment());
            } else if (itemId == R.id.nav_info) {
                replaceFragment(new LibraryInfoFragment());
            } else if (itemId == R.id.nav_logout) {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }

            drawerLayout.closeDrawers();
            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                )
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }

}
