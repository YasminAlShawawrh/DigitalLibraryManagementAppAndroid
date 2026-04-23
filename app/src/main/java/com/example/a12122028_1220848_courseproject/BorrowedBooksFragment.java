package com.example.a12122028_1220848_courseproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class BorrowedBooksFragment extends Fragment {

    private RecyclerView recyclerView;
    private BorrowedBooksAdapter adapter;
    private DatabaseHelper dbHelper;

    public BorrowedBooksFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_borrowed_books, container, false);

        recyclerView = view.findViewById(R.id.borrowedBooksRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dbHelper = new DatabaseHelper(getContext());
        SharedPreferences prefs = requireContext().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);

        if (userId == null) {
            Toast.makeText(getContext(), "You must be logged in to view borrowed books", Toast.LENGTH_SHORT).show();
            return view;
        }

        List<Book> borrowedBooks = dbHelper.getBorrowedBooksByUser(userId);
        adapter = new BorrowedBooksAdapter(borrowedBooks);
        recyclerView.setAdapter(adapter);
        return view;
    }
}
