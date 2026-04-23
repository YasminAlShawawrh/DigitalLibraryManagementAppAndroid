package com.example.a12122028_1220848_courseproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {

    private List<Student> students;
    private OnStudentDeleteListener listener;
    private OnStudentViewListener viewListener;

    public interface OnStudentDeleteListener {
        void onDelete(Student student);
    }

    public interface OnStudentViewListener {
        void onView(Student student);
    }

    public StudentAdapter(List<Student> students,
                          OnStudentDeleteListener deleteListener,
                          OnStudentViewListener viewListener) {
        this.students = students;
        this.listener = deleteListener;
        this.viewListener = viewListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        Button deleteButton;
        Button viewButton;

        public ViewHolder(View view) {
            super(view);
            nameText = view.findViewById(R.id.text_student_name);
            deleteButton = view.findViewById(R.id.button_delete_student);
            viewButton = view.findViewById(R.id.button_view_student);
        }
    }

    @Override
    public StudentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(StudentAdapter.ViewHolder holder, int position) {
        Student student = students.get(position);
        holder.nameText.setText(student.getFullName());

        holder.deleteButton.setOnClickListener(v -> listener.onDelete(student));
        holder.viewButton.setOnClickListener(v -> viewListener.onView(student));
    }

    @Override
    public int getItemCount() {
        return students.size();
    }
}
