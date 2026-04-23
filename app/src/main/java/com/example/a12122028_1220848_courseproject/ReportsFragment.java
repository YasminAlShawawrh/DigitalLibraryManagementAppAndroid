package com.example.a12122028_1220848_courseproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

public class ReportsFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private LinearLayout layoutContainer;

    public ReportsFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_reports, container, false);

        dbHelper = new DatabaseHelper(getContext());
        layoutContainer = view.findViewById(R.id.layout_reports_container);

        showReports();
        return view;
    }

    private void showReports() {
        layoutContainer.removeAllViews();

        addReportSection("Top Active Students", dbHelper.getTopActiveStudents(5));
        addReportSection("Most Borrowed Books", dbHelper.getMostBorrowedBooks(5));
        addReportSection("Overdue Books", dbHelper.getOverdueBooks());
    }

    private void addReportSection(String title, List<String> items) {
        TextView titleView = new TextView(getContext());
        titleView.setText(title);
        titleView.setTextSize(18f);
        titleView.setPadding(0, 24, 0, 12);
        layoutContainer.addView(titleView);

        if (items == null || items.isEmpty()) {
            TextView emptyView = new TextView(getContext());
            emptyView.setText("No data available.");
            layoutContainer.addView(emptyView);
            return;
        }

        for (String item : items) {
            TextView text = new TextView(getContext());
            text.setText("• " + item);
            text.setPadding(0, 4, 0, 4);
            layoutContainer.addView(text);
        }
    }
}
