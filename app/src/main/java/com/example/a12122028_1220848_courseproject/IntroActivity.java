package com.example.a12122028_1220848_courseproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IntroActivity extends AppCompatActivity {

    private ProgressBar progress;
    private static final String BASE_URL = "https://mocki.io/v1/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        progress = findViewById(R.id.progress);
        Button btnConnect = findViewById(R.id.btnConnect);

        btnConnect.setOnClickListener(v -> {
            progress.setVisibility(View.VISIBLE);
            btnConnect.setEnabled(false);

            // Retrofit API call
            ApiService api = ApiClient.get(BASE_URL).create(ApiService.class);
            api.getCategories().enqueue(new Callback<List<Category>>() {
                @Override
                public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                    progress.setVisibility(View.GONE);
                    btnConnect.setEnabled(true);

                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        // Success → navigate to Login
                        Toast.makeText(IntroActivity.this,
                                "Connected! First category: " + response.body().get(0).getName(),
                                Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(IntroActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(IntroActivity.this,
                                "Could not connect to library. Please try again.",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Category>> call, Throwable t) {
                    progress.setVisibility(View.GONE);
                    btnConnect.setEnabled(true);
                    Toast.makeText(IntroActivity.this,
                            "Connection failed: " + t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
