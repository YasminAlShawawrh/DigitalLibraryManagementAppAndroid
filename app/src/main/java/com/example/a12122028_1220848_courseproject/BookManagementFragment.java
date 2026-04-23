package com.example.a12122028_1220848_courseproject;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BookManagementFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private BookAdapter adapter;
    private List<Book> bookList = new ArrayList<>();
    private Button btnAddBook;

    public BookManagementFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_management, container, false);

        dbHelper = new DatabaseHelper(getContext());
        recyclerView = view.findViewById(R.id.recycler_books);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        btnAddBook = view.findViewById(R.id.button_add_book);

        loadBooks();

        btnAddBook.setOnClickListener(v -> showBookDialog(null));

        return view;
    }

    private void loadBooks() {
        bookList = dbHelper.getAllBooks();
        adapter = new BookAdapter(bookList, this::showBookDialog, this::confirmDeleteBook);
        recyclerView.setAdapter(adapter);
    }

    private void showBookDialog(@Nullable Book bookToEdit) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_book_form, null);

        EditText etId = dialogView.findViewById(R.id.edit_book_id);
        EditText etTitle = dialogView.findViewById(R.id.edit_book_title);
        EditText etAuthor = dialogView.findViewById(R.id.edit_book_author);
        Spinner spCategory = dialogView.findViewById(R.id.spinner_book_category);
        Spinner spStatus = dialogView.findViewById(R.id.spinner_book_status);
        EditText etIsbn = dialogView.findViewById(R.id.edit_book_isbn);
        EditText etYear = dialogView.findViewById(R.id.edit_book_year);

        // Spinners setup
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                getContext(), R.array.categories, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(categoryAdapter);

        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(
                getContext(), R.array.statuses, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStatus.setAdapter(statusAdapter);

        if (bookToEdit != null) {
            etId.setText(bookToEdit.getId());
            etId.setEnabled(false);
            etTitle.setText(bookToEdit.getTitle());
            etAuthor.setText(bookToEdit.getAuthor());

            // Preselect current values in spinners
            int catIndex = indexOf(categoryAdapter, bookToEdit.getCategory());
            if (catIndex >= 0) spCategory.setSelection(catIndex);

            int statusIndex = indexOf(statusAdapter, bookToEdit.getStatus());
            if (statusIndex >= 0) spStatus.setSelection(statusIndex);

            etIsbn.setText(bookToEdit.getIsbn());
            etYear.setText(String.valueOf(bookToEdit.getYear()));
        }

        new AlertDialog.Builder(getContext())
                .setTitle(bookToEdit == null ? "Add New Book" : "Edit Book")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String id = etId.getText().toString().trim();
                    String title = etTitle.getText().toString().trim();
                    String author = etAuthor.getText().toString().trim();
                    String category = spCategory.getSelectedItem() == null
                            ? "" : spCategory.getSelectedItem().toString();
                    String status = spStatus.getSelectedItem() == null
                            ? "" : spStatus.getSelectedItem().toString();
                    String isbn = etIsbn.getText().toString().trim();
                    String year = etYear.getText().toString().trim();

                    if (id.isEmpty() || title.isEmpty() || author.isEmpty()) {
                        Toast.makeText(getContext(), "ID, Title, and Author are required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Book book = new Book(id, title, author, category, status, isbn, year, "");
                    if (bookToEdit == null) {
                        dbHelper.insertBook(id, title, author, category, status, isbn, year, "");
                    } else {
                        dbHelper.updateBook(book);
                    }
                    loadBooks();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private int indexOf(ArrayAdapter<CharSequence> adapter, String value) {
        if (value == null) return -1;
        for (int i = 0; i < adapter.getCount(); i++) {
            if (value.equalsIgnoreCase(String.valueOf(adapter.getItem(i)))) return i;
        }
        return -1;
    }

    private void confirmDeleteBook(Book book) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Book")
                .setMessage("Delete \"" + book.getTitle() + "\"?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dbHelper.deleteBook(book.getId());
                    loadBooks();
                })
                .setNegativeButton("No", null)
                .show();
    }
}
