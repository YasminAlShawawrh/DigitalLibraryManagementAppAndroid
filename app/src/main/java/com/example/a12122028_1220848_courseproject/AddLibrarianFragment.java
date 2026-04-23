package com.example.a12122028_1220848_courseproject;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.regex.Pattern;

public class AddLibrarianFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup parent, @Nullable Bundle st) {
        View v = inf.inflate(R.layout.fragment_add_librarian, parent, false);

        EditText firstName = v.findViewById(R.id.etFirstName);
        EditText lastName = v.findViewById(R.id.etLastName);
        EditText email = v.findViewById(R.id.etEmail);
        EditText pw = v.findViewById(R.id.etPassword);
        EditText confirmPw = v.findViewById(R.id.etConfirmPassword); // make sure your layout has this
        EditText phone = v.findViewById(R.id.etPhone);

        Button save = v.findViewById(R.id.btnSave);
        DatabaseHelper db = new DatabaseHelper(requireContext());

        save.setOnClickListener(b -> {
            String fName = firstName.getText().toString().trim();
            String lName = lastName.getText().toString().trim();
            String em = email.getText().toString().trim();
            String password = pw.getText().toString();
            String confirmPassword = confirmPw.getText().toString();
            String ph = phone.getText().toString().trim();

            // --- VALIDATION ---
            if (TextUtils.isEmpty(fName)) {
                firstName.setError("First name is required");
                firstName.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(lName)) {
                lastName.setError("Last name is required");
                lastName.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(em)) {
                email.setError("Email is required");
                email.requestFocus();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(em).matches()) {
                email.setError("Invalid email format");
                email.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                pw.setError("Password is required");
                pw.requestFocus();
                return;
            }

            // Password strength check: ≥6 chars, 1 uppercase, 1 lowercase, 1 number, 1 special char
            Pattern passwordPattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{6,}$");
            if (!passwordPattern.matcher(password).matches()) {
                pw.setError("Password must be ≥6 chars with 1 uppercase, 1 lowercase, 1 number, 1 special char");
                pw.requestFocus();
                return;
            }

            // Confirm password
            if (!password.equals(confirmPassword)) {
                confirmPw.setError("Passwords do not match");
                confirmPw.requestFocus();
                return;
            }

            if (!TextUtils.isEmpty(ph) && !Patterns.PHONE.matcher(ph).matches()) {
                phone.setError("Invalid phone number");
                phone.requestFocus();
                return;
            }

            if (db.isEmailExists(em)) {
                Toast.makeText(getContext(), "Email already exists", Toast.LENGTH_SHORT).show();
                return;
            }

            // --- INSERT LIBRARIAN ---
            String id = "lib_" + System.currentTimeMillis();
            boolean success = db.insertStudent(id, fName, lName, em, password, "N/A", "Library", ph.isEmpty() ? "0000000000" : ph);

            if (success) {
                db.updateStudentProfileRole(id, "librarian"); // set role
                Toast.makeText(getContext(), "Librarian saved", Toast.LENGTH_SHORT).show();

                // Clear fields
                firstName.setText("");
                lastName.setText("");
                email.setText("");
                pw.setText("");
                confirmPw.setText("");
                phone.setText("");
            } else {
                Toast.makeText(getContext(), "Failed to save librarian", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }
}