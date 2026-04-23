package com.example.a12122028_1220848_courseproject;

import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {

    public interface OnReservationActionListener {
        void onApprove(Book book);
        void onReject(Book book);
        void onMarkReturned(Book book);
    }

    private List<Book> reservations;
    private OnReservationActionListener listener;

    public ReservationAdapter(List<Book> reservations, OnReservationActionListener listener) {
        this.reservations = reservations;
        this.listener = listener;
    }

    public static class ReservationViewHolder extends RecyclerView.ViewHolder {
        TextView textInfo;
        Button btnApprove, btnReject, btnReturned;

        public ReservationViewHolder(View itemView) {
            super(itemView);
            textInfo = itemView.findViewById(R.id.text_reservation_info);
            btnApprove = itemView.findViewById(R.id.button_approve);
            btnReject = itemView.findViewById(R.id.button_reject);
            btnReturned = itemView.findViewById(R.id.button_returned);
        }
    }

    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reservation, parent, false);
        return new ReservationViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
        Book book = reservations.get(position);

        String display = book.getTitle() + "\nBorrowed by: " + book.getStudentId() +
                "\nBorrow: " + book.getBorrowDate() +
                " | Due: " + book.getDueDate() +
                "\nStatus: " + book.getStatus();

        holder.textInfo.setText(display);

        holder.btnApprove.setOnClickListener(v -> listener.onApprove(book));
        holder.btnReject.setOnClickListener(v -> listener.onReject(book));
        holder.btnReturned.setOnClickListener(v -> listener.onMarkReturned(book));
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }
}
