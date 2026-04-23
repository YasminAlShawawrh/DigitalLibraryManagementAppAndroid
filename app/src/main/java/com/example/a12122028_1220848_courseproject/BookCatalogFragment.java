package com.example.a12122028_1220848_courseproject;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BookCatalogFragment extends Fragment {

    private RecyclerView recyclerView;
    private BookCatalogAdapter adapter;
    private Spinner spinnerCategory, spinnerAvailability;
    private EditText editMinYear, editMaxYear;
    private Button btnApplyFilters;
    private SearchView searchView;
    private List<Book> booksList = new ArrayList<>();
    private DatabaseHelper dbHelper;

    public BookCatalogFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_book_catalog, container, false);

        recyclerView = view.findViewById(R.id.recyclerBookCatalog);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        searchView = view.findViewById(R.id.searchView);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        spinnerAvailability = view.findViewById(R.id.spinnerAvailability);
        editMinYear = view.findViewById(R.id.editMinYear);
        editMaxYear = view.findViewById(R.id.editMaxYear);
        btnApplyFilters = view.findViewById(R.id.btnApplyFilters);
        dbHelper = new DatabaseHelper(getContext());

        adapter = new BookCatalogAdapter(new ArrayList<>(), new BookCatalogAdapter.OnBookActionListener() {

            @Override
            public void onAddToReadingList(Book book, int position, View itemView) {
                boolean success = dbHelper.addToReadingList(book.getId());

                if (success) {
                    flipImage(itemView); //  flipping
                    Toast.makeText(getContext(), "Added to reading list", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Book is already in reading list", Toast.LENGTH_SHORT).show();
                }
            }



            @Override
            public void onBorrow(Book book) {
                showBorrowDialog(book);
            }
        }, getContext(), null);

        recyclerView.setAdapter(adapter);

        setupFilters();
        setupSearchView();

        fetchBooksFromAPI();

        return view;
    }

    private void setupFilters() {
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                getContext(), R.array.categories, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(
                getContext(), R.array.statuses, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAvailability.setAdapter(statusAdapter);

        btnApplyFilters.setOnClickListener(v -> {
            String category = spinnerCategory.getSelectedItem().toString();
            String status = spinnerAvailability.getSelectedItem().toString();

            int minYear = 0;
            int maxYear = Integer.MAX_VALUE;

            try {
                if (!editMinYear.getText().toString().isEmpty()) {
                    minYear = Integer.parseInt(editMinYear.getText().toString());
                }
                if (!editMaxYear.getText().toString().isEmpty()) {
                    maxYear = Integer.parseInt(editMaxYear.getText().toString());
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Invalid year input", Toast.LENGTH_SHORT).show();
            }

            adapter.applyFilters(category, status, minYear, maxYear);
        });
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });
    }

    private void fetchBooksFromAPI() {
        String url = "https://mocki.io/v1/bd4dd29d-ff43-4f05-b4c1-60a271e952d1";
        RequestQueue queue = Volley.newRequestQueue(getContext());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray array = response.getJSONArray("books"); // was "categories"
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            Book book = new Book(
                                    obj.getString("id"),
                                    obj.getString("title"),
                                    obj.getString("author"),
                                    obj.getString("category"),
                                    obj.getString("status"),
                                    obj.getString("isbn"),
                                    obj.getString("year"),
                                    obj.getString("cover_url")
                            );
                            booksList.add(book);
                            dbHelper.insertBook(
                                    book.getId(),
                                    book.getTitle(),
                                    book.getAuthor(),
                                    book.getCategory(),
                                    book.getStatus(),
                                    book.getIsbn(),
                                    book.getYear(),
                                    book.getCoverUrl()
                            );
                        }
                        adapter.updateBooks(booksList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "JSON Parsing Error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getContext(), "API Fetch Error", Toast.LENGTH_SHORT).show()
        );

        queue.add(request);

    }

    private void showBorrowDialog(Book book) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_borrow_form, null);

        Spinner spinnerDuration = dialogView.findViewById(R.id.spinnerDuration);
        Spinner spinnerCollection = dialogView.findViewById(R.id.spinnerCollectionMethod);
        EditText editNotes = dialogView.findViewById(R.id.editNotes);

        String[] durations = {"1 week", "2 weeks", "3 weeks", "4 weeks"};
        spinnerDuration.setAdapter(new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, durations));

        String[] methods = {"Pickup from library", "Digital access (e-book)"};
        spinnerCollection.setAdapter(new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, methods));

        new AlertDialog.Builder(getContext())
                .setTitle("Reserve / Borrow Book")
                .setView(dialogView)
                .setPositiveButton("Reserve", (dialogInterface, which) -> {
                    Toast.makeText(getContext(), "Book reserved successfully!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    private void flipImage(View itemView) {
        ImageView cover = itemView.findViewById(R.id.imageCover);
        if (cover == null) return;

        cover.animate()
                .rotationY(90f)
                .setDuration(200)
                .withEndAction(() -> {
                    cover.setRotationY(-90f);
                    cover.animate().rotationY(0f).setDuration(200).start();
                })
                .start();
    }

}
