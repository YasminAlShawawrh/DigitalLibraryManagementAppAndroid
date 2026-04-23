package com.example.a12122028_1220848_courseproject;

import android.os.Bundle;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NewArrivalsFragment extends Fragment {
    private RecyclerView recyclerView;
    private BookCatalogAdapter adapter;
    private DatabaseHelper dbHelper;

    public NewArrivalsFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_arrivals, container, false);

        recyclerView = view.findViewById(R.id.recyclerNewArrivals);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dbHelper = new DatabaseHelper(getContext());

        List<Book> newBooks = dbHelper.getNewArrivalBooks(); // You'll write this method

        adapter = new BookCatalogAdapter(
                newBooks,
                new BookCatalogAdapter.OnBookActionListener() {
                    @Override
                    public void onAddToReadingList(Book book, int position, View itemView) {
                        boolean success = dbHelper.addToReadingList(book.getId());
                        if (success) {
                            Toast.makeText(getContext(), "Added to reading list", Toast.LENGTH_SHORT).show();

                            // Flip the cover image
                            ImageView cover = itemView.findViewById(R.id.imageCover);
                            if (cover != null) {
                                cover.animate()
                                        .rotationYBy(180f)
                                        .setDuration(2000)
                                        .start();
                            }
                        } else {
                            Toast.makeText(getContext(), "Already added", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onBorrow(Book book) {
                        adapter.showReservationDialog(book);
                    }
                },
                getContext(),
                dbHelper
        );

        recyclerView.setAdapter(adapter);

        return view;
    }
}

