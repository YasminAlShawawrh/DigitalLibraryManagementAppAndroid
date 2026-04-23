package com.example.a12122028_1220848_courseproject;

import android.annotation.SuppressLint;
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

import java.util.ArrayList;
import java.util.List;

public class ReadingListFragment extends Fragment {

    private RecyclerView recyclerView;
    private ReadingListAdapter adapter;
    private DatabaseHelper dbHelper;

    public ReadingListFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reading_list, container, false);

        recyclerView = view.findViewById(R.id.readingListRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dbHelper = new DatabaseHelper(getContext());

        List<Book> readingList = dbHelper.getReadingListBooks(); // You must have this method
        adapter = new ReadingListAdapter(readingList, book -> {
            dbHelper.removeFromReadingList(book.getId());
            Toast.makeText(getContext(), "Book removed", Toast.LENGTH_SHORT).show();
            reloadList();
        }, dbHelper, getContext());



        recyclerView.setAdapter(adapter);
        return view;
    }

    private void reloadList() {
        List<Book> updatedList = dbHelper.getReadingListBooks();
        adapter = new ReadingListAdapter(updatedList, book -> {
            dbHelper.removeFromReadingList(book.getId());
            Toast.makeText(getContext(), "Book removed", Toast.LENGTH_SHORT).show();
            reloadList();
        }, dbHelper, getContext());

        recyclerView.setAdapter(adapter);
    }
}
