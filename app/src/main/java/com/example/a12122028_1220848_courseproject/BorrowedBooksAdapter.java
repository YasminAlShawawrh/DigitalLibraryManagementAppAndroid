package com.example.a12122028_1220848_courseproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Locale;

public class BorrowedBooksAdapter extends RecyclerView.Adapter<BorrowedBooksAdapter.VH> {
    private final List<Book> borrowedBooks;
    public BorrowedBooksAdapter(List<Book> borrowedBooks) { this.borrowedBooks = borrowedBooks; }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int v) {
        View view = LayoutInflater.from(p.getContext()).inflate(R.layout.item_borrowed_book, p, false);
        return new VH(view);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        Book b = borrowedBooks.get(pos);
        h.title.setText(b.getTitle());
        h.author.setText("by " + b.getAuthor());
        h.borrow.setText("Borrowed: " + (b.getBorrowDate() == null ? "-" : b.getBorrowDate()));
        h.due.setText("Due: " + (b.getDueDate() == null ? "-" : b.getDueDate()));
        h.ret.setText("Returned: " + (b.getReturnDate() == null || b.getReturnDate().isEmpty() ? "Not returned" : b.getReturnDate()));
        h.status.setText("Status: " + (b.getStatus() == null ? "Active" : b.getStatus()));
        h.fine.setText("Fine: $" + String.format(Locale.getDefault(), "%.2f", b.getFine()));
    }

    @Override public int getItemCount() { return borrowedBooks.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView title, author, borrow, due, ret, status, fine;
        VH(@NonNull View v) {
            super(v);
            title  = v.findViewById(R.id.bookTitle);
            author = v.findViewById(R.id.bookAuthor);
            borrow = v.findViewById(R.id.bookBorrowDate);
            due    = v.findViewById(R.id.bookDueDate);
            ret    = v.findViewById(R.id.bookReturnDate);
            status = v.findViewById(R.id.bookStatus);
            fine   = v.findViewById(R.id.bookFine);
        }
    }
}
