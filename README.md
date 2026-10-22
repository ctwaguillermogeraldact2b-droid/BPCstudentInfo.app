[README.md](https://github.com/user-attachments/files/33834431/README.md)
# BPC Student Information System (BPC-SIS)

A comprehensive student information and academic management Android application developed for **Bulacan Polytechnic College (BPC)**. Built using **Kotlin**, **XML Layouts**, and **Material 3**, the system supports three distinct user roles (**Student**, **Teacher**, and **Administrator**) with centralized data persistence and real-time synchronization.

---

## 👥 Developers & Team Members
* **Lead Developer / System Architect:** Gerald Guillermo
* **Groupmates** 
Lazaro. Justine
Dela Cruz Reinna
Negrido Nichul Ann A.
* **Institution:** Bulacan Polytechnic College (BPC)
* **Program:** Associate In Computer Technology / (ACT)
* **Section** ACT 2B

## 📌 Version Information
* **Version:** 1.0.0 (Stable Release)
* **Target SDK:** Android 34 (API Level 34)
* **Minimum SDK:** Android 7.0 (API Level 24)
* **Build System:** Gradle 8.9 / AGP 8.7.2

---

## 💻 Technologies Used
* **Language:** Kotlin 2.0+
* **UI Toolkit:** Android XML Layouts, Material Components (Material 3), RecyclerView, ViewPager2
* **Architecture:** Repository Pattern with Central In-Memory Persistence (`StudentRepository`)
* **Development Environment:** Android Studio (2026.1.3+)

---

## ⚙️ Installation & Setup Instructions

1. **Clone or Download Repository:**
   ```bash
   git clone https://github.com/geraldguillermo/BPCStudentInfo.git
   ```
2. **Open in Android Studio:**
   * Launch Android Studio, select **File ▸ Open**, and choose the cloned `BPCStudentInfo` folder.
3. **Gradle Synchronization:**
   * Allow Gradle to sync dependencies automatically (accept recommended AGP and Gradle versions if prompted).
4. **Deploy & Run:**
   * Connect an Android device with USB Debugging enabled or start an Android Emulator (API 24+).
   * Click the **Run** (`▶`) button in Android Studio to build and deploy the app.

---

## 🔑 Demo Login Accounts

| Role | Username / ID | Password / Code | Notes |
|---|---|---|---|
| **Administrator** | `admin` | `admin123` | Full administrative control, section management & teacher authorization |
| **Teacher (ICT Cluster Head)** | `ICT-8492` | `teacher123` | Faculty portal for Paulo A. Victoria, MIT (ICT Cluster Head) |
| **Student (Default)** | `MA-25011126` | `bpc2026` | Gerald Guillermo (BSIS 2-A) |

> 💡 **All 95 Teacher Accounts Pre-Signed Up:**
> Every faculty member across all 5 Department Clusters (`ICT`, `LANG`, `HB`, `ASC`, `SKP`) is pre-authorized and pre-signed up (`isSignedUp = true`). Teachers can sign in instantly using their assigned **Shuffled Non-Year Teacher Code** (e.g., `ICT-8492`, `LANG-5921`) with default password `teacher123`.
> 
> 💡 **Pre-Registered Student Roster:**
> Features 39 pre-authorized students under **ACT 2nd Year Section B** (Associate in Computer Technology) pre-populated in the database ready for testing and grading.

---

## 🎓 Official BPC Academic Programs & Departments

### 4-Year Bachelor's Degree Programs
* **BSIS** — Bachelor of Science in Information Systems
* **BSOM** — Bachelor of Science in Office Management
* **BSAIS** — Bachelor of Science in Accounting Information System
* **BTVTED** — Bachelor of Technical Vocational Teacher Education
* **BSCA** — Bachelor of Science in Customs Administration
* **ACT** — Associate in Computer Technology (2-Year)

### 2-Year Technical-Vocational & Secretarial Courses
* **CS** — 2-Year Computer Secretarial with competency in Bookkeeping NC III
* **HRS** — 2-Year Hotel and Restaurant Services
* **CCM** — Call Center Management
* **CIT** — Certificate in Industrial Technology major in Electrical

---

## 📋 Complete System Features & Role Permissions

### 🛠 Administrator Role
* **Department Cluster & Teacher Management:** Group, filter, and manage faculty across 5 department clusters (`ICT`, `LANG`, `HB`, `ASC`, `SKP`).
* **Section Creation & Department Mapping:** Create sections per department / program using all BPC courses in the database.
* **Master Schedule Management & Section Filtering:** Manage course schedules, meeting times, rooms, and teacher assignments with **Filter by Section** capabilities (`2-A`, `2-B`, etc.) and cluster filtering.
* **Official Report of Grades (Bulk & Individual):** Generate official grade sheet report templates matching institutional templates (bulk class reports per section or individual student report cards) with grading system legends and registrar/dean approval signature lines.
* **Student Enrollment Status & Records:** Track students as **Attending** or **Dropped**, manage academic standing, and update student profiles.

### 👩‍🏫 Teacher Role
* **Per-Section Student Directory:** View and filter assigned students by specific sections (`All Sections`, `2-B`, etc.) in real time.
* **Profile Picture Support:** Teachers can upload, change, or remove a profile picture that persists across all portals.
* **Assigned Class Schedules & Missed Activities:** Inspect subject loads, teaching schedules, and students' missed coursework.

### 👨‍🎓 Student Role
* **Editable Personal Details & Emergency Contacts:** Students can securely edit their personal details (address, mobile, civil status, birth date, sex) and emergency contact / guardian details.
* **Section Option in Enrollment:** Select program, year level, and database-driven section when filing enrollment.
* **Unit Tracking & Academic Dashboard:** Automatically track enrolled units against the maximum load (24 units) and monitor remaining curriculum units required to graduate.
* **Universal Password Recovery:** Smooth password renewal support for both students and teachers.

---

## 🏗 Project Architecture & Layout

```
BPCStudentInfo/
├── README.md
├── source-code/ (app/src/main/java/ph/edu/bpc/sis/)
├── assets/ (app/src/main/res/drawable & mipmap)
├── documentation/ (PROJECT_DOCUMENTATION.md)
└── screenshots/
```

---

## ⚙️ Data Architecture

* **In-Memory Central Repository (`StudentRepository.kt`):** Single source of truth managing persistence for 95 pre-signed up teachers across 5 clusters, 39 ACT Section B students, curriculum subjects, sections, schedules, grade records, and teacher assignments with instant real-time reflection across all views.
