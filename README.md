#  Suara Rakyat

**Diploma In Information Technology - Mobile App Development - Final Project - Kolej Poly-Tech MARA (KPTM)**

**Suara Rakyat** is an Android application designed to empower citizens to easily report and track issues within their community. Whether it's a security concern, an environmental hazard, or a social issue like bullying, this app provides a transparent platform to submit evidence, pinpoint locations, and track the resolution status of reports.

---

##  Features

* **User Authentication**: Secure Login and Registration system with role-based access (Citizen / Non-Citizen). Also supports "Guest Mode" for anonymous browsing.
* **Issue Reporting**: Users can submit detailed reports including:
  * **Categories**: Bullying, Environmental Issues, Security Issues.
  * **Location Tagging**: Automatically fetches the user's current location via GPS (`FusedLocationProviderClient`) and Geocoding.
  * **Evidence Upload**: Attach image/file evidence to reports.
  * **Anonymous Submissions**: Option to hide identity when submitting sensitive reports.
* **Report Tracking**: View the real-time status of complaints (Pending, In Progress, Resolved) and read feedback left by administrators.
* **PDF Export & Sharing**: Automatically generate formatted PDF documents of report details and save them to the device's local Downloads folder.
* **Email Integration**: Seamlessly share the generated PDF reports via email directly from the app.
* **Modern UI**: Built with Material Design 3 guidelines for a clean, responsive, and accessible user experience.

---

##  Tech Stack & Architecture

* **Language**: Kotlin
* **Architecture**: Model-View-ViewModel (MVVM)
* **UI Design**: XML with ViewBinding and Material Components (M3)
* **Asynchronous Programming**: Kotlin Coroutines & `lifecycleScope`
* **Backend & Database (Firebase)**:
  * **Firebase Authentication**: For user sign-up and login.
  * **Cloud Firestore**: Real-time NoSQL database to store and retrieve report details.
  * **Firebase Storage**: For uploading and serving evidence media.
* **Location Services**: Google Play Services Location API
* **PDF Generation**: Android `PdfDocument` API

---

##  Getting Started

### Prerequisites

* **Android Studio**: Android Studio Giraffe | 2022.3.1 or newer.
* **JDK**: Minimum JDK 17.
* **Firebase Project**: You need to create a Firebase project and connect it to this app.

### Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/your-username/suararakyat.git
   cd suararakyat
   ```

2. **Open in Android Studio:**
   Open Android Studio and select **File > Open**, then choose the cloned directory.

3. **Add Firebase Credentials:**
   * Go to the Firebase Console.
   * Create a new project and add an Android app with the package name `com.example.suararakyatv2`.
   * Enable **Authentication** (Email/Password), **Firestore Database**, and **Firebase Storage**.
   * Download the `google-services.json` file.
   * Place the `google-services.json` file in the `app/` directory of the project.

4. **Build and Run:**
   * Sync the project with Gradle files.
   * Select an emulator or connect a physical device.
   * Click the **Run** button (▶) in Android Studio.

---

##  Project Structure

```text
SUARARAKYAT/app/src/main/java/com/example/suararakyatv2/
├── CreateActivity.kt          # User registration screen
├── LoginActivity.kt           # User authentication screen
├── HomeActivity.kt            # Main dashboard & navigation
├── CreateReportActivity.kt    # Screen to submit a new civic issue
├── ReportDetail.kt            # Screen displaying details, status, & PDF generation
└── viewmodels/
    ├── CreateUserViewModel.kt # Logic for Registration
    ├── LoginViewModel.kt      # Logic for Authentication
    ├── HomeViewModel.kt       # Logic for fetching user profile
    ├── CreateReportViewModel  # Logic for saving reports & uploading files
    └── ReportDetailViewModel  # Logic for retrieving report status
```

---

##  Future Enhancements

* **OAuth Integration**: Planned support for Google and Facebook Sign-In.
* **Push Notifications**: Notify users when the status of their report changes.
* **Community Forum**: A space for citizens to discuss ongoing local issues.
* **Advanced Analytics**: Allow admins to view issue hotspots on a map.

---

