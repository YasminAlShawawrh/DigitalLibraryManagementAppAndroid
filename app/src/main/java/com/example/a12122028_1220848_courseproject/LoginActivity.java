package com.example.a12122028_1220848_courseproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText editTextLoginID, editTextLoginPassword;
    Button buttonLogin;
    DatabaseHelper dbHelper;
    CheckBox checkBoxRememberMe;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);

        editTextLoginID = findViewById(R.id.editTextLoginID);
        editTextLoginPassword = findViewById(R.id.editTextLoginPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        checkBoxRememberMe = findViewById(R.id.checkBoxRememberMe);

        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);

        // Prefill if "Remember me" was used
        String savedId = sharedPreferences.getString("user_id_or_email", null);
        String savedPassword = sharedPreferences.getString("password", null);
        if (savedId != null && savedPassword != null) {
            editTextLoginID.setText(savedId);
            editTextLoginPassword.setText(savedPassword);
            checkBoxRememberMe.setChecked(true);
        }

        buttonLogin.setOnClickListener(v -> {
            String id = editTextLoginID.getText().toString().trim();
            String password = editTextLoginPassword.getText().toString();

            if (id.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Cursor cursor;
            if (id.contains("@")) {
                cursor = dbHelper.getUserByEmail(id);
            } else {
                cursor = dbHelper.getUserById(id);
            }

            if (cursor != null && cursor.moveToFirst()) {
                String storedEncrypted = cursor.getString(cursor.getColumnIndexOrThrow("password_hash"));
                String decrypted = caesarDecrypt(storedEncrypted);

                if (password.equals(decrypted)) {
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

                    String actualUserId = cursor.getString(cursor.getColumnIndexOrThrow("university_id"));
                    String role = cursor.getString(cursor.getColumnIndexOrThrow("role"));

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("user_id", actualUserId);
                    editor.putString("user_role", role);

                    if (checkBoxRememberMe.isChecked()) {
                        editor.putString("user_id_or_email", id);
                        editor.putString("password", password);
                    } else {
                        editor.remove("user_id_or_email");
                        editor.remove("password");
                    }
                    editor.apply();

                    // Redirect by role
                    if ("librarian".equalsIgnoreCase(role)) {
                        startActivity(new Intent(this, LibrarianDashboardActivity.class));
                    } else {
                        startActivity(new Intent(this, StudentDashboardActivity.class));
                    }
                    finish();
                } else {
                    Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show();
                }
                cursor.close();
            } else {
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            }
        });

        TextView textRegister = findViewById(R.id.textRegister);
        textRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegistrationActivity.class)));
    }

    private String caesarDecrypt(String input) {
        StringBuilder decrypted = new StringBuilder();
        for (char c : input.toCharArray()) {
            decrypted.append((char) (c - 3));
        }
        return decrypted.toString();
    }
}
