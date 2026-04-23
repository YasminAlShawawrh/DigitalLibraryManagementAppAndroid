package com.example.a12122028_1220848_courseproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imageProfile;
    private EditText editFirstName, editLastName, editPhone, editPassword, editConfirmPassword;
    private TextView tvBorrowingStats;
    private Uri imageUri;

    private DatabaseHelper dbHelper;
    private String currentUserId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        SharedPreferences prefs = requireContext().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        currentUserId = prefs.getString("user_id", null);
        dbHelper = new DatabaseHelper(requireContext());

        imageProfile = view.findViewById(R.id.imageProfile);
        editFirstName = view.findViewById(R.id.editFirstName);
        editLastName = view.findViewById(R.id.editLastName);
        editPhone = view.findViewById(R.id.editPhone);
        editPassword = view.findViewById(R.id.editPassword);
        editConfirmPassword = view.findViewById(R.id.editConfirmPassword);
        Button btnChangeImage = view.findViewById(R.id.btnChangeImage);
        Button btnUpdateProfile = view.findViewById(R.id.btnUpdateProfile);
        tvBorrowingStats = view.findViewById(R.id.tvBorrowingStats);

        loadUserData();

        btnChangeImage.setOnClickListener(v -> chooseImage());
        btnUpdateProfile.setOnClickListener(v -> {
            if (validateInputs()) {
                saveChanges();
            }
        });

        return view;
    }

    private void loadUserData() {
        Cursor cursor = dbHelper.getUserById(currentUserId);
        if (cursor != null && cursor.moveToFirst()) {
            editFirstName.setText(cursor.getString(cursor.getColumnIndexOrThrow("first_name")));
            editLastName.setText(cursor.getString(cursor.getColumnIndexOrThrow("last_name")));
            editPhone.setText(cursor.getString(cursor.getColumnIndexOrThrow("phone")));

            String imagePath = cursor.getString(cursor.getColumnIndexOrThrow("profile_image_path"));
            if (imagePath != null) {
                File imgFile = new File(imagePath);
                if (imgFile.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    imageProfile.setImageBitmap(bitmap);
                }
            }
            DatabaseHelper.BorrowStats stats = dbHelper.getBorrowingStats(currentUserId);
            tvBorrowingStats.setText(
                    "Borrowed: " + stats.total +
                            " | Returned: " + stats.returned +
                            " | Overdue: " + stats.overdue
            );

            cursor.close();
        }

        DatabaseHelper.BorrowStats stats = dbHelper.getBorrowingStats(currentUserId);
        tvBorrowingStats.setText(
                "Borrowed: " + stats.total +
                        " | Returned: " + stats.returned +
                        " | Overdue: " + stats.overdue
        );

    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);
                imageProfile.setImageBitmap(bitmap);
                saveProfileImageLocally(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveProfileImageLocally(Bitmap bitmap) {
        Context ctx = requireContext();
        String filename = "profile_" + currentUserId + ".png";
        try {
            FileOutputStream fos = ctx.openFileOutput(filename, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            String filePath = ctx.getFilesDir().getAbsolutePath() + "/" + filename;
            dbHelper.updateProfileImagePath(currentUserId, filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean validateInputs() {
        String first = editFirstName.getText().toString().trim();
        String last = editLastName.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String pass = editPassword.getText().toString();
        String confirm = editConfirmPassword.getText().toString();

        if (first.length() < 3 || last.length() < 3) {
            showToast("Name must be at least 3 characters");
            return false;
        }

        if (!Patterns.PHONE.matcher(phone).matches()) {
            showToast("Invalid phone number");
            return false;
        }

        if (!TextUtils.isEmpty(pass)) {
            if (!isValidPassword(pass)) {
                showToast("Password must be ≥6 chars with uppercase, lowercase, number, and special character");
                return false;
            }

            if (!pass.equals(confirm)) {
                showToast("Passwords do not match");
                return false;
            }
        }

        return true;
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 6 &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[a-z].*") &&
                password.matches(".*[0-9].*") &&
                password.matches(".*[!@#$%^&*+=?-].*");
    }

    private void saveChanges() {
        String first = editFirstName.getText().toString().trim();
        String last = editLastName.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String rawPass = editPassword.getText().toString().trim();

        String encrypted = null;
        if (!TextUtils.isEmpty(rawPass)) {
            encrypted = caesarEncrypt(rawPass);
        }

        boolean updated = dbHelper.updateStudentProfile(currentUserId, first, last, phone, encrypted);
        if (updated) {
            showToast("Profile updated successfully");
        } else {
            showToast("Update failed");
        }
    }

    private void showToast(String msg) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private String caesarEncrypt(String input) {
        StringBuilder encrypted = new StringBuilder();
        for (char c : input.toCharArray()) {
            encrypted.append((char)(c + 3));
        }
        return encrypted.toString();
    }
}