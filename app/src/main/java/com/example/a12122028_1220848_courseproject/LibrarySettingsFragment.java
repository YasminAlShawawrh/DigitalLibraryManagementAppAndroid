package com.example.a12122028_1220848_courseproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LibrarySettingsFragment extends Fragment {

    private EditText etName, etEmail, etPhone, etBorrowPolicy, etFinePolicy;
    private DatabaseHelper db;

    public LibrarySettingsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library_settings, container, false);
        db = new DatabaseHelper(getContext());

        etName = view.findViewById(R.id.edit_library_name);
        etEmail = view.findViewById(R.id.edit_contact_email);
        etPhone = view.findViewById(R.id.edit_contact_phone);
        etBorrowPolicy = view.findViewById(R.id.edit_borrow_policy);
        etFinePolicy = view.findViewById(R.id.edit_fine_policy);
        Button btnSave = view.findViewById(R.id.button_save_settings);

        loadSettings();

        btnSave.setOnClickListener(v -> {
            db.setSetting("library_name", etName.getText().toString());
            db.setSetting("contact_email", etEmail.getText().toString());
            db.setSetting("contact_phone", etPhone.getText().toString());
            db.setSetting("borrow_policy", etBorrowPolicy.getText().toString());
            db.setSetting("fine_policy", etFinePolicy.getText().toString());

            Toast.makeText(getContext(), "Settings saved", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void loadSettings() {
        etName.setText(db.getSetting("library_name"));
        etEmail.setText(db.getSetting("contact_email"));
        etPhone.setText(db.getSetting("contact_phone"));
        etBorrowPolicy.setText(db.getSetting("borrow_policy"));
        etFinePolicy.setText(db.getSetting("fine_policy"));
    }
}
