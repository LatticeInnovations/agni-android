# Android Project Setup Guide
This guide explains how to configure the project after cloning the repository and how to generate a release APK.

The project requires the following configuration before it can be built:
- Backend Base URL
- Package name (optional)
- Firebase configuration (Crashlytics)
- Release keystore for signing

## 1. Clone the Repository

```
git clone <repo-url>
cd <project-folder>
```

Open the project in Android Studio.
Allow Gradle to sync and download dependencies.

## 2. Configure Backend Base URL
The project expects a backend URL to be configured in Gradle.

Open:
```
app/build.gradle
```

Locate the placeholder:

```
buildConfigField "String", "BASE_URL", "\"YOUR_BASE_URL\""
```

Replace "YOUR_BASE_URL" with your backend API base URL.

Example:
```
buildConfigField "String", "BASE_URL", "\"https://api.example.com/\""
```

Important notes:
- The URL must end with /
- After editing, sync Gradle again

## 3. Change the Application Package (Optional)
If you want to publish the app under your own package name, you must change it.

Example change:
```
com.latticeonfhir.android
```

to
```
com.company.newapp
```

### Step 1: Update Package in Android Studio
In Android Studio:

```
Go to Project → Android View
```

Navigate to:
```
app/src/main/java/com/latticeonfhir/android
```

Right click on each folder level and select:
```
Refactor → Rename
```

Rename sequentially:
```
com → (same usually)
latticeonfhir → your company
android → your app name
```

Android Studio will update references automatically.

### Step 2: Update applicationId
Open:
```
app/build.gradle
```

Update:
```
defaultConfig {
    applicationId "com.company.newapp"
}
```

## 4. Configure Firebase (Crashlytics)
The project uses Firebase Crashlytics for crash reporting.

### Step 1: Create Firebase Project

Go to:
[https://console.firebase.google.com/](https://console.firebase.google.com/)

1. Click Create Project
2. Add an Android app
3. Use the same package name as the app

Example:
```
com.example.app
```

### Step 2: Download google-services.json
Firebase will provide a file:
```
google-services.json
```

Place this file inside:
```
app/google-services.json
```

Folder structure should look like:
```
project-root
 └── app
      ├── google-services.json
      └── build.gradle
```

### Step 3: Enable Crashlytics
In Firebase console:

```
Build → Crashlytics → Enable
```

Run the app once to verify Crashlytics integration.

## 5. Generate a Release Keystore
To build a release APK you must generate a signing keystore.

### Step 1: Generate Keystore
In Android Studio:
```
Build → Generate Signed Bundle / APK
```

Select:
```
APK
```

Then click:
```
Create new...
```

Fill:
```
Keystore path: /your/path/release.keystore
Password: ********
Alias: release
Key password: ********
Validity: 25+ years
```

Save the keystore securely.

## 6. Configure Signing in Gradle
Open:
```
app/build.gradle
```

Add signing configuration:
```
android {
    signingConfigs {
        release {
            storeFile file("keystore/release.keystore")
            storePassword "your_store_password"
            keyAlias "release"
            keyPassword "your_key_password"
        }
    }

    buildTypes {
        release {
            signingConfig signingConfigs.release
            minifyEnabled true
            shrinkResources true
        }
    }
}
```

### Recommended structure:

```
project-root
 ├── keystore
 │    └── release.keystore
 └── app
```