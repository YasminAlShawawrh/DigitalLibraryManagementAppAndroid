package com.example.a12122028_1220848_courseproject;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.*;

public class BookCatalogAdapter extends RecyclerView.Adapter<BookCatalogAdapter.BookViewHolder> {

    private List<Book> books;
    private OnBookActionListener listener;
    private List<Book> allBooks;
    private DatabaseHelper dbHelper;
    private Context context;

    private List<Book> originalBooks = new ArrayList<>();

    public interface OnBookActionListener {
        void onAddToReadingList(Book book, int position, View itemView);
        void onBorrow(Book book);
    }



    public BookCatalogAdapter(List<Book> books, OnBookActionListener listener, Context context, DatabaseHelper dbHelper) {
        this.books = books;
        this.listener = listener;
        this.context = context;
        this.dbHelper = dbHelper;
        this.allBooks = new ArrayList<>(books);
        this.originalBooks = new ArrayList<>(books);
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_catalog, parent, false);
        return new BookViewHolder(view);
    }

    public void filter(String query) {
        books.clear();
        if (query == null || query.trim().isEmpty()) {
            books.addAll(originalBooks);
        } else {
            query = query.toLowerCase().trim();
            for (Book book : originalBooks) {
                String title = book.getTitle() != null ? book.getTitle().toLowerCase() : "";
                String author = book.getAuthor() != null ? book.getAuthor().toLowerCase() : "";
                String category = book.getCategory() != null ? book.getCategory().toLowerCase() : "";
                String isbn = book.getIsbn() != null ? book.getIsbn().toLowerCase() : "";

                if (title.contains(query) || author.contains(query) || category.contains(query) || isbn.contains(query)) {
                    books.add(book);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void applyFilters(String category, String status, int minYear, int maxYear) {
        books.clear();
        for (Book book : originalBooks) {
            boolean matchesCategory = category.equals("All") || book.getCategory().equalsIgnoreCase(category);
            boolean matchesStatus = status.equals("All") || book.getStatus().equalsIgnoreCase(status);
            int bookYear = Integer.parseInt(book.getYear());

            if (matchesCategory && matchesStatus && bookYear >= minYear && bookYear <= maxYear) {
                books.add(book);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = books.get(position);
        holder.textTitle.setText(book.getTitle());
        holder.textAuthor.setText("by " + book.getAuthor());
        holder.textCategory.setText("Category: " + book.getCategory());
        holder.textStatus.setText("Status: " + book.getStatus());
        holder.textIsbn.setText("ISBN: " + book.getIsbn());
        holder.textYear.setText("Year: " + book.getYear());

        Glide.with(holder.itemView.getContext())
                .load(book.getCoverUrl())
                .placeholder(R.drawable.bookcover)
                .into(holder.imageCover);
        holder.btnAddToReadingList.setOnClickListener(v -> {
            listener.onAddToReadingList(book, holder.getAdapterPosition(), holder.itemView);
        });


        holder.btnReserve.setOnClickListener(v -> {

            Animation bounce = AnimationUtils.loadAnimation(context, R.anim.bounce);
            v.startAnimation(bounce);

            listener.onBorrow(book);
        });


        holder.btnShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Book Recommendation");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this book: " + book.getTitle());
            context.startActivity(Intent.createChooser(shareIntent, "Share via"));
        });
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textAuthor, textCategory, textStatus, textIsbn, textYear;
        ImageView imageCover;
        Button btnAddToReadingList, btnReserve, btnShare;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textAuthor = itemView.findViewById(R.id.textAuthor);
            textCategory = itemView.findViewById(R.id.textCategory);
            textStatus = itemView.findViewById(R.id.textStatus);
            textIsbn = itemView.findViewById(R.id.textIsbn);
            textYear = itemView.findViewById(R.id.textYear);
            imageCover = itemView.findViewById(R.id.imageCover);
            btnAddToReadingList = itemView.findViewById(R.id.btnAddToReadingList);
            btnReserve = itemView.findViewById(R.id.btnReserve);
            btnShare = itemView.findViewById(R.id.btnShare);
        }
    }

    public void showReservationDialog(Book book) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_borrow_form, null);
        Spinner spinnerDuration = dialogView.findViewById(R.id.spinnerDuration);
        Spinner spinnerMethod = dialogView.findViewById(R.id.spinnerCollectionMethod);
        EditText etNotes = dialogView.findViewById(R.id.editNotes);

        ArrayAdapter<CharSequence> durationAdapter = ArrayAdapter.createFromResource(context,
                R.array.borrowing_durations, android.R.layout.simple_spinner_item);
        durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDuration.setAdapter(durationAdapter);

        ArrayAdapter<CharSequence> methodAdapter = ArrayAdapter.createFromResource(context,
                R.array.collection_methods, android.R.layout.simple_spinner_item);
        methodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMethod.setAdapter(methodAdapter);

        new AlertDialog.Builder(context)
                .setTitle("Reserve Book")
                .setView(dialogView)
                .setPositiveButton("Reserve", (dialog, which) -> {
                    String duration = spinnerDuration.getSelectedItem().toString();
                    String method = spinnerMethod.getSelectedItem().toString();
                    String notes = etNotes.getText().toString();

                    SharedPreferences prefs = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
                    String studentId = prefs.getString("user_id", null);
                    if (studentId != null && dbHelper.requestBookReservation(studentId, book.getId(), duration, method, notes)) {
                        dbHelper.reserveBook(book.getId()); // Optional if you already update status in DB
                        book.setStatus("Pending");
                        Toast.makeText(context, "Reservation requested. Awaiting approval.", Toast.LENGTH_SHORT).show();
                        notifyDataSetChanged();
                    } else {
                        Toast.makeText(context, "Reservation failed", Toast.LENGTH_SHORT).show();
                    }

                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    public void updateBooks(List<Book> newBooks) {
        this.books.clear();
        this.books.addAll(newBooks);

        this.allBooks.clear();
        this.allBooks.addAll(newBooks);

        this.originalBooks.clear();
        this.originalBooks.addAll(newBooks);

        notifyDataSetChanged();
    }
    private void flipImage(final ImageView imageView) {
        imageView.animate()
                .rotationYBy(180f)
                .setDuration(600)
                .start();
    }


}
