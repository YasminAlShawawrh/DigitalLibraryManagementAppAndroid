# Digital Library Management App — Android

A full-featured Android application for university library management. Students can browse books, reserve items, manage reading lists, and view borrowing history. Librarians get a separate dashboard to manage books, students, and reservations. The app integrates a REST API for live data and SQLite for full offline support.

---

## Table of contents

- [App overview](#app-overview)
- [Features](#features)
- [Architecture & tech stack](#architecture--tech-stack)
- [Database schema](#database-schema)
- [Screens & navigation](#screens--navigation)
- [User roles](#user-roles)
- [File structure](#file-structure)
- [How to build & run](#how-to-build--run)

---

## App overview

A role-based Android library system with two user types — **Student** and **Librarian** — each with their own navigation drawer and feature set. The app fetches book data and categories from a REST API using Retrofit, caches everything locally in SQLite for offline use, and loads book cover images with Glide.

**Minimum SDK:** API 26 (Android 8.0)  
**Target SDK:** API 36  
**Tested on:** Pixel 3a XL, API 26, Software Graphics

---

## Features

### Student features
- Animated splash screen with fade-in logo
- REST API connectivity check on launch
- Login with "Remember Me" via SharedPreferences
- Full registration form with field validation (university ID format, password strength, phone auto-format)
- Book catalog with search (title / author / ISBN) and filters (category, availability, publication year)
- Reserve / borrow books with duration and collection method selection
- Add books to reading list (with book cover flip animation)
- View borrowed books history with status, due date, and fine tracking
- New arrivals section with direct reservation
- Profile management with photo, borrowing statistics, and password update
- Contact Us — tap-to-call, Google Maps, and pre-filled email intent
- Offline mode — all reservations saved to SQLite and synced when connection resumes

### Librarian features
- Separate `LibrarianDashboardActivity` with its own navigation drawer
- Manage students — view and delete accounts
- Add new librarian — same validation rules as student registration
- Book management — add, edit, delete books from catalog
- Reservation management — approve/reject reservations, mark as returned
- Generate reports — student activity, popular books, overdue items
- Library settings — update library info, policies, and hours

---

## Architecture & tech stack

| Layer | Technology |
|---|---|
| Language | Java |
| UI | XML layouts · RecyclerView · Navigation Drawer · Fragments |
| Networking | Retrofit 2.11 · OkHttp 4.12 · Gson converter |
| Image loading | Glide 4.16 |
| Local storage | SQLite via `SQLiteOpenHelper` |
| Preferences | SharedPreferences (login persistence, user settings) |
| Animations | Fade-in splash · Book cover flip · Slide fragment transitions · Bounce reservation button · Loading spinner |
| Build system | Gradle (Kotlin DSL) |

### REST API
- Built with Retrofit + `GsonConverterFactory`
- `ApiService` defines endpoints (e.g. `@GET` for book categories)
- `ApiClient` maintains a singleton `Retrofit` instance, rebuilds if base URL changes
- Network failures → Toast error, stay on current screen

---

## Database schema

Implemented in `DatabaseHelper.java` extending `SQLiteOpenHelper`:

```
Books
  id, title, author, isbn, category, availability, cover_url, publication_year

Reservations
  id, student_id, book_id, reservation_date, due_date, status

Students
  id, university_id, first_name, last_name, email, password_hash, department, level

Reading_List
  id, student_id, book_id, added_date

Settings
  key, value  (library info, policies — uses CONFLICT_REPLACE on insert)
```

Passwords are stored using a simple cipher before being saved to the Students table.

---

## Screens & navigation

```
SplashActivity         → fade-in logo (2–3s)
    ↓
IntroActivity          → app description + "Connect to Library" button
    ↓ (API success)
LoginActivity          → university ID + password + Remember Me
    ├── RegistrationActivity   → 9-field form with full validation
    ↓ (student login)
StudentDashboardActivity  (Navigation Drawer)
    ├── DashboardFragment         → library announcements
    ├── BookCatalogFragment       → search, filter, reserve, add to list
    ├── BorrowedBooksFragment     → borrowing history + fines
    ├── ReadingListFragment       → saved books
    ├── NewArrivalsFragment       → recently added books
    ├── ProfileFragment           → profile info + photo + stats
    └── LibraryInfoFragment       → call / map / email + hours

LoginActivity  (librarian@library.edu / Library123!)
    ↓
LibrarianDashboardActivity  (Navigation Drawer)
    ├── ManageStudentsFragment
    ├── AddLibrarianFragment
    ├── BookManagementFragment
    ├── ReservationManagementFragment
    ├── ReportsFragment
    └── LibrarySettingsFragment
```

---

## User roles

| Role | Login | Access |
|---|---|---|
| Student | University ID + password | Student dashboard, book catalog, reservations, reading list |
| Librarian | `librarian@library.edu` / `Library123!` | Librarian dashboard, full management panel |

The app routes to the correct dashboard based on the role stored with the user account.

---

## File structure

```
app/src/main/java/.../
├── SplashActivity.java              # Animated logo screen
├── IntroActivity.java               # Welcome + Connect button + API check
├── LoginActivity.java               # Login + Remember Me
├── RegistrationActivity.java        # Student registration + validation
├── MainActivity.java                # Student nav drawer host
├── StudentDashboardActivity.java    # Student home
├── LibrarianDashboardActivity.java  # Librarian home
├── ApiClient.java                   # Retrofit singleton
├── ApiService.java                  # Retrofit endpoint definitions
├── DatabaseHelper.java              # SQLiteOpenHelper — all DB operations
├── Book.java / Student.java / Category.java   # Data models
├── BookAdapter.java / BookCatalogAdapter.java  # RecyclerView adapters
├── BorrowedBooksAdapter.java / ReadingListAdapter.java
├── ReservationAdapter.java / StudentAdapter.java
├── BookCatalogFragment.java         # Search, filter, reserve
├── BorrowedBooksFragment.java       # Borrowing history
├── ReadingListFragment.java         # Saved books
├── NewArrivalsFragment.java         # Recent books
├── DashboardFragment.java           # Announcements
├── ProfileFragment.java             # Profile + stats
├── LibraryInfoFragment.java         # Contact + location
├── BookManagementFragment.java      # Librarian: add/edit/delete books
├── ManageStudentsFragment.java      # Librarian: view/delete students
├── AddLibrarianFragment.java        # Librarian: register new librarian
├── ReservationManagementFragment.java
├── ReportsFragment.java
└── LibrarySettingsFragment.java
```

---

## How to build & run

### From Android Studio

1. Clone or extract the project folder
2. Open Android Studio → File → Open → select the `project/` folder
3. Let Gradle sync complete
4. Connect a device or start an emulator (API 26+, recommended: Pixel 3a XL)
5. Run → Run 'app'

### Install the APK directly

```bash
adb install app-debug.apk
```

### Librarian login credentials

```
Email:    librarian@library.edu
Password: Library123!
```

### Dependencies (key libraries)

```groovy
implementation("com.squareup.retrofit2:retrofit:2.11.0")
implementation("com.squareup.retrofit2:converter-gson:2.11.0")
implementation("com.squareup.okhttp3:okhttp:4.12.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
implementation("com.github.bumptech.glide:glide:4.16.0")
implementation("com.android.volley:volley:1.2.1")
implementation("com.google.android.material:material:1.12.0")
```
