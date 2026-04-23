package com.example.a12122028_1220848_courseproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReservationManagementFragment extends Fragment {

    private RecyclerView recyclerView;
    private ReservationAdapter adapter;
    private DatabaseHelper dbHelper;
    private List<Book> reservations;

    public ReservationManagementFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_reservation_management, container, false);

        dbHelper = new DatabaseHelper(getContext());
        recyclerView = view.findViewById(R.id.recycler_reservations);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadReservations();
        return view;
    }

    private void loadReservations() {
        reservations = dbHelper.getPendingReservations();
        adapter = new ReservationAdapter(reservations, new ReservationAdapter.OnReservationActionListener() {
            @Override
            public void onApprove(Book book) {
                if ("Pending".equalsIgnoreCase(book.getStatus())) {
                    dbHelper.approveReservation(book.getId());
                    Toast.makeText(getContext(), "Approved", Toast.LENGTH_SHORT).show();
                    loadReservations();
                }
            }


            @Override
            public void onReject(Book book) {
                dbHelper.rejectReservation(book.getId());
                Toast.makeText(getContext(), "Rejected", Toast.LENGTH_SHORT).show();
                loadReservations();
            }

            @Override
            public void onMarkReturned(Book book) {
                dbHelper.markBookReturned(book.getId());
                Toast.makeText(getContext(), "Marked as Returned", Toast.LENGTH_SHORT).show();
                loadReservations();
            }
        });
        recyclerView.setAdapter(adapter);
    }
}
