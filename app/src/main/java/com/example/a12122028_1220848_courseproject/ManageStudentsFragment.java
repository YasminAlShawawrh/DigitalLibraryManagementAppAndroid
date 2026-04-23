package com.example.a12122028_1220848_courseproject;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class ManageStudentsFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private StudentAdapter adapter;
    private List<Student> studentList = new ArrayList<>();

    public ManageStudentsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_students, container, false);

        dbHelper = new DatabaseHelper(getContext());
        recyclerView = view.findViewById(R.id.recycler_students);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadStudents();

        return view;
    }

    private void loadStudents() {
        studentList.clear();
        Cursor cursor = dbHelper.getAllStudents();

        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndexOrThrow("university_id"));
                String first = cursor.getString(cursor.getColumnIndexOrThrow("first_name"));
                String last = cursor.getString(cursor.getColumnIndexOrThrow("last_name"));
                String role = cursor.getString(cursor.getColumnIndexOrThrow("role"));

                if (!"librarian".equalsIgnoreCase(role)) {
                    studentList.add(new Student(id, first, last));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        adapter = new StudentAdapter(studentList,
                student -> confirmDelete(student),
                student -> viewStudentDetails(student));

        recyclerView.setAdapter(adapter);
    }

    private void confirmDelete(Student student) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Student")
                .setMessage("Are you sure you want to delete " + student.getFullName() + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dbHelper.deleteStudent(student.getId());
                    loadStudents(); // refresh list
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void viewStudentDetails(Student student) {
        Cursor cursor = dbHelper.getStudentById(student.getId());
        if (cursor.moveToFirst()) {
            String id = cursor.getString(cursor.getColumnIndexOrThrow("university_id"));
            String first = cursor.getString(cursor.getColumnIndexOrThrow("first_name"));
            String last = cursor.getString(cursor.getColumnIndexOrThrow("last_name"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            String level = cursor.getString(cursor.getColumnIndexOrThrow("level"));
            String dept = cursor.getString(cursor.getColumnIndexOrThrow("department"));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));

            String info = "ID: " + id +
                    "\nName: " + first + " " + last +
                    "\nEmail: " + email +
                    "\nLevel: " + level +
                    "\nDepartment: " + dept +
                    "\nPhone: " + phone;

            new AlertDialog.Builder(getContext())
                    .setTitle("Student Details")
                    .setMessage(info)
                    .setPositiveButton("OK", null)
                    .show();
        }
        cursor.close();
    }
}
