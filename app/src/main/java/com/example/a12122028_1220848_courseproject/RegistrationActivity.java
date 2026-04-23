package com.example.a12122028_1220848_courseproject;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class RegistrationActivity extends AppCompatActivity {
    DatabaseHelper dbHelper;
    EditText editTextID, editTextFirstName, editTextLastName, editTextEmail,
            editTextPassword, editTextConfirmPassword, editTextPhone;
    Spinner spinnerLevel, spinnerDepartment;
    Button buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        dbHelper = new DatabaseHelper(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);



        editTextID = findViewById(R.id.editTextID);
        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editTextPhone = findViewById(R.id.editTextPhone);

        spinnerLevel = findViewById(R.id.spinnerLevel);
        spinnerDepartment = findViewById(R.id.spinnerDepartment);
        buttonRegister = findViewById(R.id.buttonRegister);

        String[] levels = {"Freshman", "Sophomore", "Junior", "Senior", "Graduate"};
        String[] departments = {"Computer Science", "Engineering", "Business", "Literature", "Medicine"};

        spinnerLevel.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, levels));
        spinnerDepartment.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, departments));

        buttonRegister.setOnClickListener(v -> {
            if (validateInputs()) {
                String id = editTextID.getText().toString().trim();
                String first = editTextFirstName.getText().toString().trim();
                String last = editTextLastName.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString();
                String level = spinnerLevel.getSelectedItem().toString();
                String dept = spinnerDepartment.getSelectedItem().toString();
                String phone = editTextPhone.getText().toString().trim();

                if (dbHelper.isUserExists(id)) {
                    Toast.makeText(this, "User already registered with this ID.", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean inserted = dbHelper.insertStudent(id, first, last, email, password, level, dept, phone);
                if (inserted) {
                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Error saving user. Try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean validateInputs() {
        String id = editTextID.getText().toString().trim();
        String fName = editTextFirstName.getText().toString().trim();
        String lName = editTextLastName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString();
        String confirmPassword = editTextConfirmPassword.getText().toString();
        String phone = editTextPhone.getText().toString().trim();

        // ID must start with a valid year (YYYY format)
        if (id.length() < 8 || !id.matches("^\\d{4}\\d{4}$")) {
            showError("University ID must be in format YYYY####");
            return false;
        }

        int year = Integer.parseInt(id.substring(0, 4));
        if (year < 1995 || year > 2025) {
            showError("Invalid enrollment year in ID.");
            return false;
        }

        if (fName.length() < 3 || lName.length() < 3) {
            showError("First and last name must be ≥ 3 characters.");
            return false;
        }

        if (!email.endsWith("@university.edu") || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError("Email must be a valid university address.");
            return false;
        }


        if (dbHelper.isEmailExists(email)) {
            Toast.makeText(this, "Email already in use.", Toast.LENGTH_SHORT).show();
            return false;
        }


        if (!isValidPassword(password)) {
            showError("Password must be ≥6 characters and include uppercase, lowercase, number, special char.");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match.");
            return false;
        }

        if (!PhoneNumberUtils.isGlobalPhoneNumber(phone)) {
            showError("Invalid phone number.");
            return false;
        }

        return true;
    }

    private void showError(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    // Simple Caesar Cipher (shift by 3)
    private String caesarEncrypt(String input) {
        StringBuilder encrypted = new StringBuilder();
        for (char c : input.toCharArray()) {
            encrypted.append((char)(c + 3));
        }
        return encrypted.toString();
    }

    // Password must include: uppercase, lowercase, digit, special char
    private boolean isValidPassword(String pwd) {
        return pwd.length() >= 6 &&
                pwd.matches(".*[A-Z].*") &&
                pwd.matches(".*[a-z].*") &&
                pwd.matches(".*\\d.*") &&
                pwd.matches(".*[!@#$%^&*(),.?\":{}|<>].*");
    }
}
