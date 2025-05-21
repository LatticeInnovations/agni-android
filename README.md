# Main Branch
Production Ready Code

# FHIR-Android
Android Application integrated with Hapi fhir server to demonstrate FHIR compliant system

# Minimum Requirements
Android Studio Meerkat Feature Drop | 2024.3.2
Build #AI-243.25659.59.2432.13423653, built on April 29, 2025
Runtime version: 21.0.6+-13368085-b895.109 amd64
VM: OpenJDK 64-Bit Server VM by JetBrains s.r.o.
Toolkit: sun.awt.windows.WToolkit
Kotlin plugin: K2 mode
GC: G1 Young Generation, G1 Concurrent GC, G1 Old Generation

# Build Pre-requisite
This app requires Firebase credentials for crashlytics and analytics purpose. Please generate your own credentials and put it into **app** folder for a successful build.

# Build Instructions
If you met minimum requirements, clone the repository and open in Android Studio. It will automatically build project.

<br/>

## `development-nonfhirsdk` branch
#### Modules included
* Patient
* Household member
* Appointment
* Prescription
* CVD
* Drug dispense
* Lab test
* Medical record
* Symptoms and diagnosis
* Vaccinations
* Vitals
* Sign-up

#### Architecture
Monolithic

#### Backend used
Facade server
- Update your own ```BASE_URL``` into Gradle File.
