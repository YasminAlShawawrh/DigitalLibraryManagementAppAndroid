package com.example.a12122028_1220848_courseproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class ReadingListAdapter extends RecyclerView.Adapter<ReadingListAdapter.BookViewHolder> {

    private List<Book> bookList;
    private OnRemoveClickListener listener;
    private DatabaseHelper dbHelper;
    private Context context;

    public interface OnRemoveClickListener {
        void onRemoveClick(Book book);
    }

    public ReadingListAdapter(List<Book> bookList, OnRemoveClickListener listener, DatabaseHelper dbHelper, Context context) {
        this.bookList = bookList;
        this.listener = listener;
        this.dbHelper = dbHelper;
        this.context = context;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book_reading_list, parent, false);
        return new BookViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);
        holder.title.setText(book.getTitle());
        holder.author.setText(book.getAuthor());

        holder.availability.setText(book.isAvailable() ? "Available" : "Reserved");

        holder.btnRemove.setOnClickListener(v -> listener.onRemoveClick(book));

        holder.btnReserve.setOnClickListener(v -> {
            Animation bounce = AnimationUtils.loadAnimation(context, R.anim.bounce);
            v.startAnimation(bounce);

            showReserveDialog(book, holder.getAdapterPosition());
        });

        holder.btnReserve.setOnClickListener(v -> {
            showReserveDialog(book, holder.getAdapterPosition());
        });
    }

    private void showReserveDialog(Book book, int position) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_borrow_form, null);
        Spinner spinnerDuration = dialogView.findViewById(R.id.spinnerDuration);
        Spinner spinnerCollection = dialogView.findViewById(R.id.spinnerCollectionMethod);
        EditText editNotes = dialogView.findViewById(R.id.editNotes);

        String[] durations = {"1 week", "2 weeks", "3 weeks", "4 weeks"};
        spinnerDuration.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, durations));

        String[] methods = {"Pickup from library", "Digital access (e-book)"};
        spinnerCollection.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, methods));

        new AlertDialog.Builder(context)
                .setTitle("Reserve Book")
                .setView(dialogView)
                .setPositiveButton("Reserve", (dialogInterface, which) -> {
                    int weeks = spinnerDuration.getSelectedItemPosition() + 1;
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.WEEK_OF_YEAR, weeks);
                    String dueDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.getTime());

                    String collection = spinnerCollection.getSelectedItem().toString();
                    String notes = editNotes.getText().toString();

                    SharedPreferences prefs = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
                    String studentId = prefs.getString("user_id", null);

                    if (studentId == null) {
                        Toast.makeText(context, "You must be logged in", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    boolean success = dbHelper.requestBookReservation(studentId, book.getId(), dueDate, collection, notes);


                    if (success) {
                        Toast.makeText(context, "Reservation requested! Waiting for approval.", Toast.LENGTH_LONG).show();
                        book.setAvailable(false);
                        notifyItemChanged(position);
                    } else {
                        Toast.makeText(context, "Failed to request reservation!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView title, author, availability;
        Button btnRemove, btnReserve;

        public BookViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textTitle);
            author = itemView.findViewById(R.id.textAuthor);
            availability = itemView.findViewById(R.id.textAvailability);
            btnRemove = itemView.findViewById(R.id.btnRemove);
            btnReserve = itemView.findViewById(R.id.btnReserve);
        }
    }
}
