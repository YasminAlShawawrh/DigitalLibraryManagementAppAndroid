package com.example.a12122028_1220848_courseproject;

import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    public interface OnEditListener {
        void onEdit(Book book);
    }

    public interface OnDeleteListener {
        void onDelete(Book book);
    }

    private List<Book> books;
    private OnEditListener editListener;
    private OnDeleteListener deleteListener;

    public BookAdapter(List<Book> books, OnEditListener editListener, OnDeleteListener deleteListener) {
        this.books = books;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView textInfo;
        Button btnEdit, btnDelete;

        public BookViewHolder(View view) {
            super(view);
            textInfo = view.findViewById(R.id.text_book_info);
            btnEdit = view.findViewById(R.id.button_edit_book);
            btnDelete = view.findViewById(R.id.button_delete_book);
        }
    }

    @NonNull
    @Override
    public BookAdapter.BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_admin, parent, false);
        return new BookViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BookAdapter.BookViewHolder holder, int position) {
        Book book = books.get(position);
        holder.textInfo.setText(book.getTitle() + " by " + book.getAuthor() + " (" + book.getYear() + ")");

        holder.btnEdit.setOnClickListener(v -> editListener.onEdit(book));
        holder.btnDelete.setOnClickListener(v -> deleteListener.onDelete(book));
    }

    @Override
    public int getItemCount() {
        return books.size();
    }
}
