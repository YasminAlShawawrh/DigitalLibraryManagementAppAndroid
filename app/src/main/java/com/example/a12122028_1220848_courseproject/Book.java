package com.example.a12122028_1220848_courseproject;

public class Book {
    private String id, title, author, category, status, isbn, year, coverUrl, dueDate;
    private String borrowDate, returnDate;
    private double fine;
    private boolean isAvailable;
    private String studentId;

    public Book(String id, String title, String author, String category, String status, String isbn, String year, String coverUrl) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.category = category;
        this.status = status;
        this.isbn = isbn;
        this.year = year;
        this.coverUrl = coverUrl;
    }

    public Book(String id, String title, String author, String dueDate) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.dueDate = dueDate;
    }
    public Book(String id, String title, String author, String borrowDate,
                String dueDate, String returnDate, String status, double fine) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = status;
        this.fine = fine;
    }


    // --- Add Getters ---
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getCategory() { return category; }
    public String getStatus() { return status; }
    public String getIsbn() { return isbn; }
    public String getYear() { return year; }
    public String getCoverUrl() { return coverUrl; }
    public String getDueDate() { return dueDate; }
    public String getBorrowDate() { return borrowDate; }
    public String getReturnDate() { return returnDate; }

    public double getFine() { return fine; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getStudentId() {
        return studentId;
    }
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
}
