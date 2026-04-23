package com.example.a12122028_1220848_courseproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;
import android.icu.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "LibraryDB";
    private static final int DB_VERSION = 1;

    public static final String TABLE_STUDENTS = "students";
    public static final String COL_ID = "university_id";
    public static final String COL_FIRST = "first_name";
    public static final String COL_LAST = "last_name";
    public static final String COL_EMAIL = "email";
    public static final String COL_PASSWORD = "password_hash";
    public static final String COL_LEVEL = "level";
    public static final String COL_DEPT = "department";
    public static final String COL_PHONE = "phone";
    public static final String COL_IMAGE = "profile_image_path";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createStudentsTable = "CREATE TABLE " + TABLE_STUDENTS + " (" +
                COL_ID + " TEXT PRIMARY KEY, " +
                COL_FIRST + " TEXT, " +
                COL_LAST + " TEXT, " +
                COL_EMAIL + " TEXT, " +
                COL_PASSWORD + " TEXT, " +
                COL_LEVEL + " TEXT, " +
                COL_DEPT + " TEXT, " +
                COL_PHONE + " TEXT, " +
                COL_IMAGE + " TEXT, " +
                "role TEXT DEFAULT 'student'" +
                ")";

        db.execSQL(createStudentsTable);

        // Books table
        String createBooksTable = "CREATE TABLE books (" +
                "id TEXT PRIMARY KEY, " +
                "title TEXT, " +
                "author TEXT, " +
                "category TEXT, " +
                "status TEXT, " +
                "isbn TEXT, " +
                "year TEXT, " +
                "cover_url TEXT)";
        db.execSQL(createBooksTable);

        db.execSQL("CREATE TABLE borrowed_books (" +
                "book_id TEXT PRIMARY KEY, " +
                "student_id TEXT, " +  // new column
                "borrow_date TEXT, " +
                "due_date TEXT, " +
                "return_date TEXT, " +
                "status TEXT, " +
                "fine REAL, " +
                "collection_method TEXT, " +
                "notes TEXT)");
        ContentValues librarian = new ContentValues();
        librarian.put(COL_ID, "admin001");
        librarian.put(COL_FIRST, "Admin");
        librarian.put(COL_LAST, "User");
        librarian.put(COL_EMAIL, "librarian@library.edu");
        librarian.put(COL_PASSWORD, caesarEncrypt("Library123!"));
        librarian.put(COL_LEVEL, "N/A");
        librarian.put(COL_DEPT, "Library");
        librarian.put(COL_PHONE, "0000000000");
        librarian.put("role", "librarian");
        db.insert(TABLE_STUDENTS, null, librarian);

        db.execSQL("CREATE TABLE reading_list (book_id TEXT PRIMARY KEY)");
        db.execSQL("CREATE TABLE IF NOT EXISTS settings (" +
                "setting_key TEXT PRIMARY KEY, " +
                "setting_value TEXT)");
        ContentValues policy = new ContentValues();
        policy.put("setting_key", "borrowing_policy");
        policy.put("setting_value", "Max 5 books, 2 weeks");
        db.insert("settings", null, policy);

        seedBooks(db);
    }


    public void seedBooks(SQLiteDatabase db) {


        Cursor c = db.rawQuery("SELECT COUNT(*) FROM books", null);
        if (c.moveToFirst() && c.getInt(0) > 0) {
            c.close();
            return; // Already seeded
        }
        c.close();


    }
    public boolean insertBook(String id, String title, String author,
                              String category, String status, String isbn, String year, String coverUrl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("title", title);
        values.put("author", author);
        values.put("category", category);
        values.put("status", status);
        values.put("isbn", isbn);
        values.put("year", year);
        values.put("cover_url", coverUrl);
        long result = db.insert("books", null, values);
        return result != -1;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS books");
        db.execSQL("DROP TABLE IF EXISTS reading_list");
        db.execSQL("DROP TABLE IF EXISTS borrowed_books");
        onCreate(db);
    }



    public boolean insertStudent(String id, String first, String last, String email,
                                 String password, String level, String dept, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ID, id);
        values.put(COL_FIRST, first);
        values.put(COL_LAST, last);
        values.put(COL_EMAIL, email);
        values.put(COL_PASSWORD, caesarEncrypt(password)); // <- inside DB
        values.put(COL_LEVEL, level);
        values.put(COL_DEPT, dept);
        values.put(COL_PHONE, phone);

        long result = db.insert(TABLE_STUDENTS, null, values);
        return result != -1;
    }


    public boolean isUserExists(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_STUDENTS, new String[]{COL_ID},
                COL_ID + "=?", new String[]{id}, null, null, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_STUDENTS, new String[]{COL_EMAIL},
                COL_EMAIL + "=?", new String[]{email}, null, null, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }
    public Cursor getUserById(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_STUDENTS, null, COL_ID + "=?", new String[]{id},
                null, null, null);
    }
    public boolean addToReadingList(String bookId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("book_id", bookId);

        long result = db.insert("reading_list", null, values);
        return result != -1; // true if inserted
    }
    public boolean removeFromReadingList(String bookId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete("reading_list", "book_id = ?", new String[]{bookId});
        return rows > 0;
    }
    public List<Book> getReadingListBooks() {
        List<Book> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT b.id, b.title, b.author, b.category, b.status, b.isbn, b.year, b.cover_url " +
                "FROM books b INNER JOIN reading_list r ON b.id = r.book_id";

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(0);
                String title = cursor.getString(1);
                String author = cursor.getString(2);
                String category = cursor.getString(3);
                String status = cursor.getString(4);
                String isbn = cursor.getString(5);
                String year = cursor.getString(6);
                String coverUrl = cursor.getString(7);

                Book book = new Book(id, title, author, category, status, isbn, year, coverUrl);
                book.setAvailable("Available".equalsIgnoreCase(status));

                list.add(book);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }


    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM books", null);

        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String author = cursor.getString(cursor.getColumnIndexOrThrow("author"));
                String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
                String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                String isbn = cursor.getString(cursor.getColumnIndexOrThrow("isbn"));
                String year = cursor.getString(cursor.getColumnIndexOrThrow("year"));
                String coverUrl = cursor.getString(cursor.getColumnIndexOrThrow("cover_url"));

                books.add(new Book(id, title, author, category, status, isbn, year, coverUrl));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return books;
    }

    public boolean borrowBook(String studentId, String bookId, String dueDate, String collectionMethod, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("student_id", studentId);
        values.put("book_id", bookId);
        values.put("borrow_date", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
        values.put("due_date", dueDate);
        values.put("return_date", "");
        values.put("status", "Reserved");
        values.put("fine", 0.0);
        values.put("collection_method", collectionMethod);
        values.put("notes", notes);
        long result = db.insert("borrowed_books", null, values);
        return result != -1;
    }

    public List<Book> getBorrowedBooks() {
        List<Book> out = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT b.id,b.title,b.author,bb.borrow_date,bb.due_date,bb.return_date,bb.status,bb.fine " +
                        "FROM books b INNER JOIN borrowed_books bb ON b.id=bb.book_id " +
                        "ORDER BY bb.borrow_date DESC", null);

        if (c.moveToFirst()) {
            do {
                String id   = c.getString(0);
                String t    = c.getString(1);
                String a    = c.getString(2);
                String bor  = c.getString(3);
                String due  = c.getString(4);
                String ret  = c.getString(5);
                String st   = c.getString(6);
                double fine = c.isNull(7) ? 0.0 : c.getDouble(7);

                out.add(new Book(id, t, a, bor, due, ret, st, fine));
            } while (c.moveToNext());
        }
        c.close();
        return out;
    }

    public Cursor getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM students WHERE email = ?", new String[]{email});
    }
    public boolean borrowBook(String bookId, String dueDate, String collectionMethod, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("book_id", bookId);
        values.put("borrow_date", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
        values.put("due_date", dueDate);
        values.put("return_date", "");
        values.put("status", "Active");
        values.put("fine", 0.0);
        values.put("collection_method", collectionMethod);
        values.put("notes", notes);

        long result = db.insert("borrowed_books", null, values);
        return result != -1;
    }
    public void reserveBook(String bookId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", "Reserved");
        db.update("books", values, "id = ?", new String[]{bookId});
    }
    public List<Book> getNewArrivalBooks() {
        List<Book> newBooks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM Books ORDER BY year DESC LIMIT 10";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndex("id"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String author = cursor.getString(cursor.getColumnIndex("author"));
                String category = cursor.getString(cursor.getColumnIndex("category"));
                String status = cursor.getString(cursor.getColumnIndex("status"));
                String isbn = cursor.getString(cursor.getColumnIndex("isbn"));
                String year = cursor.getString(cursor.getColumnIndex("year"));
                String coverUrl = cursor.getString(cursor.getColumnIndex("cover_url")); // If you're using this

                Book book = new Book(id, title, author, category, status, isbn, year, coverUrl);
                newBooks.add(book);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return newBooks;
    }
    public boolean updateStudentProfile(String id, String firstName, String lastName, String phone, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_FIRST, firstName);
        values.put(COL_LAST, lastName);
        values.put(COL_PHONE, phone);

        if (newPassword != null && !newPassword.isEmpty()) {
            values.put(COL_PASSWORD, caesarEncrypt(newPassword));
        }

        int rows = db.update(TABLE_STUDENTS, values, COL_ID + "=?", new String[]{id});
        return rows > 0;
    }
    public Cursor getStudentById(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_STUDENTS, null, COL_ID + "=?", new String[]{id}, null, null, null);
    }
    public boolean updateProfileImagePath(String studentId, String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_IMAGE, imagePath);
        int rows = db.update(TABLE_STUDENTS, values, COL_ID + "=?", new String[]{studentId});
        return rows > 0;
    }

    public String getProfileImagePath(String studentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_STUDENTS,
                new String[]{COL_IMAGE},
                COL_ID + "=?",
                new String[]{studentId},
                null, null, null);
        String path = null;
        if (cursor != null && cursor.moveToFirst()) {
            path = cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE));
            cursor.close();
        }
        return path;
    }
    public class BorrowStats {
        public int total;
        public int overdue;
        public int returned;
        // maybe more
    }

    public BorrowStats getBorrowingStats(String studentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        BorrowStats stats = new BorrowStats();

        // total
        Cursor cTotal = db.rawQuery("SELECT COUNT(*) FROM borrowed_books WHERE student_id = ?", new String[]{studentId});
        if (cTotal.moveToFirst()) {
            stats.total = cTotal.getInt(0);
        }
        cTotal.close();

        // returned
        Cursor cReturned = db.rawQuery("SELECT COUNT(*) FROM borrowed_books WHERE student_id = ? AND status = ?", new String[]{studentId, "Returned"});
        if (cReturned.moveToFirst()) {
            stats.returned = cReturned.getInt(0);
        }
        cReturned.close();

        // overdue
        Cursor cOverdue = db.rawQuery("SELECT COUNT(*) FROM borrowed_books WHERE student_id = ? AND status = ?", new String[]{studentId, "Overdue"});
        if (cOverdue.moveToFirst()) {
            stats.overdue = cOverdue.getInt(0);
        }
        cOverdue.close();

        return stats;
    }
    public List<Book> getBorrowedBooksByUser(String studentId) {
        List<Book> out = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT b.id,b.title,b.author,bb.borrow_date,bb.due_date,bb.return_date,bb.status,bb.fine " +
                        "FROM books b INNER JOIN borrowed_books bb ON b.id=bb.book_id " +
                        "WHERE bb.student_id=? ORDER BY bb.borrow_date DESC", new String[]{studentId});

        if (c.moveToFirst()) {
            do {
                String id = c.getString(0);
                String t = c.getString(1);
                String a = c.getString(2);
                String bor = c.getString(3);
                String due = c.getString(4);
                String ret = c.getString(5);
                String st = c.getString(6);
                double fine = c.isNull(7) ? 0.0 : c.getDouble(7);
                out.add(new Book(id, t, a, bor, due, ret, st, fine));
            } while (c.moveToNext());
        }
        c.close();
        return out;
    }
    public String getUserRole(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT role FROM students WHERE email = ?", new String[]{email});
        String role = "student";
        if (cursor.moveToFirst()) {
            role = cursor.getString(0);
        }
        cursor.close();
        return role;
    }
    public Cursor getAllStudents() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM students", null);
    }

    public boolean deleteStudent(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_STUDENTS, COL_ID + "=?", new String[]{id}) > 0;
    }
    public boolean updateBook(Book book) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", book.getTitle());
        values.put("author", book.getAuthor());
        values.put("category", book.getCategory());
        values.put("status", book.getStatus());
        values.put("isbn", book.getIsbn());
        values.put("year", book.getYear());
        return db.update("books", values, "id = ?", new String[]{book.getId()}) > 0;
    }

    public boolean deleteBook(String bookId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("books", "id = ?", new String[]{bookId}) > 0;
    }
    public boolean updateBorrowStatus(String bookId, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", newStatus);

        // Update borrowed_books
        int rows = db.update("borrowed_books", values, "book_id = ?", new String[]{bookId});

        // Also update the books table
        if ("Approved".equalsIgnoreCase(newStatus)) {
            ContentValues bookValues = new ContentValues();
            bookValues.put("status", "Reserved");
            db.update("books", bookValues, "id = ?", new String[]{bookId});
        } else if ("Rejected".equalsIgnoreCase(newStatus)) {
            ContentValues bookValues = new ContentValues();
            bookValues.put("status", "Available");
            db.update("books", bookValues, "id = ?", new String[]{bookId});
        }

        return rows > 0;
    }


    public boolean markBookReturned(String bookId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", "Returned");
        values.put("return_date", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));

        int rows = db.update("borrowed_books", values, "book_id = ?", new String[]{bookId});
        return rows > 0;
    }
    public List<String> getTopActiveStudents(int limit) {
        List<String> out = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT s.first_name || ' ' || s.last_name AS name, COUNT(*) AS borrow_count " +
                        "FROM students s JOIN borrowed_books b ON s.university_id = b.student_id " +
                        "GROUP BY b.student_id ORDER BY borrow_count DESC LIMIT ?", new String[]{String.valueOf(limit)}
        );

        while (c.moveToNext()) {
            String name = c.getString(0);
            int count = c.getInt(1);
            out.add(name + " - " + count + " books");
        }

        c.close();
        return out;
    }
    public List<String> getMostBorrowedBooks(int limit) {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT b.title, COUNT(*) AS times " +
                        "FROM books b JOIN borrowed_books bb ON b.id = bb.book_id " +
                        "GROUP BY b.id ORDER BY times DESC LIMIT ?", new String[]{String.valueOf(limit)}
        );

        while (c.moveToNext()) {
            list.add(c.getString(0) + " - borrowed " + c.getInt(1) + " times");
        }

        c.close();
        return list;
    }
    public List<String> getOverdueBooks() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT b.title, s.first_name || ' ' || s.last_name AS student, bb.due_date " +
                        "FROM books b JOIN borrowed_books bb ON b.id = bb.book_id " +
                        "JOIN students s ON s.university_id = bb.student_id " +
                        "WHERE bb.status = 'Overdue'", null
        );

        while (c.moveToNext()) {
            String title = c.getString(0);
            String student = c.getString(1);
            String due = c.getString(2);

            list.add(title + " (due: " + due + ") - " + student);
        }

        c.close();
        return list;
    }
    public String getSetting(String key) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT setting_value FROM settings WHERE setting_key = ?", new String[]{key});
        String val = null;
        if (c.moveToFirst()) val = c.getString(0);
        c.close();
        return val;
    }

    public void setSetting(String key, String value) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("setting_key", key);
        cv.put("setting_value", value);

        db.insertWithOnConflict("settings", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
    }
    public String caesarEncrypt(String input) {
        StringBuilder encrypted = new StringBuilder();
        for (char c : input.toCharArray()) {
            encrypted.append((char) (c + 3));
        }
        return encrypted.toString();
    }
    public boolean requestBookReservation(String studentId, String bookId, String dueDate, String collectionMethod, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("student_id", studentId);
        values.put("book_id", bookId);
        values.put("borrow_date", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
        values.put("due_date", dueDate);
        values.put("return_date", "");
        values.put("status", "Pending");
        values.put("fine", 0.0);
        values.put("collection_method", collectionMethod);
        values.put("notes", notes);
        long result = db.insert("borrowed_books", null, values);
        return result != -1;
    }
    public List<Book> getPendingReservations() {
        List<Book> out = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT b.id,b.title,b.author,bb.borrow_date,bb.due_date,bb.return_date,bb.status,bb.fine " +
                        "FROM books b INNER JOIN borrowed_books bb ON b.id=bb.book_id " +
                        "WHERE bb.status = 'Pending' ORDER BY bb.borrow_date DESC", null);


        if (c.moveToFirst()) {
            do {
                String id = c.getString(0);
                String t = c.getString(1);
                String a = c.getString(2);
                String bor = c.getString(3);
                String due = c.getString(4);
                String ret = c.getString(5);
                String st = c.getString(6);
                double fine = c.isNull(7) ? 0.0 : c.getDouble(7);


                out.add(new Book(id, t, a, bor, due, ret, st, fine));
            } while (c.moveToNext());
        }
        c.close();
        return out;
    }
    public void approveReservation(String bookId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues borrowStatus = new ContentValues();
        borrowStatus.put("status", "Approved");
        db.update("borrowed_books", borrowStatus, "book_id = ? AND status = 'Pending'", new String[]{bookId});


        ContentValues bookStatus = new ContentValues();
        bookStatus.put("status", "Reserved");
        db.update("books", bookStatus, "id = ?", new String[]{bookId});
    }
    public void rejectReservation(String bookId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", "Rejected");
        db.update("borrowed_books", values, "book_id = ? AND status = 'Pending'", new String[]{bookId});
    }
    public boolean updateStudentProfileRole(String id, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("role", role);
        return db.update(TABLE_STUDENTS, values, COL_ID + "=?", new String[]{id}) > 0;
    }


}
