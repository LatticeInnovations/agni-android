
# About Agni
Agni is a mobile-first platform that:
* is designed for [offline use](https://agni.thelattice.in/key-features/offline-first/),
* uses evidence-based guidelines for clinical workflows,
* is built on [HL7® FHIR®](https://agni.thelattice.in/key-features/fhir-data-model/), with out-of-the-box interoperability

The system utilizes the WHO’s PEN protocols for cardiovascular disease (CVD) risk assessment[^1], and the Indian Academy of Pediatrics' immunization recommendations[^2]. Along with its focus on cardiac health and immunization, it provides a complete primary care workflow.

Detailed design documentation is available on the [agni website](https://agni.thelattice.in/)

# Core Capabilities

| Module                | Functionality                                                                 |
|-----------------------|-------------------------------------------------------------------------------|
| Patient Management    | Registration, profile management, household relationships                     |
| Clinical Workflows    | Prescriptions, vaccinations, appointments, CVD risk assessment                |
| Medical Records       | Lab tests, symptoms, diagnosis, vitals monitoring, drug dispensing            |
| Data Synchronization  | Bidirectional FHIR-compliant sync with backend server                         |
| Authentication        | Phone/email + OTP verification system                                         |

# Technology Stack
## Development Environment
- Android Studio: Meerkat Feature Drop 2024.3.2 or later
- Gradle: 8.10.2 with Kotlin DSL
- Java/Kotlin: Java 17, Kotlin 1.9.24
- Min SDK: 26 (Android 8.0), Target SDK: 34 (Android 14)

## Core Technologies
| Category             | Technology                    | Version          | Purpose                         |
|----------------------|-------------------------------|------------------|---------------------------------|
| UI Framework         | Jetpack Compose               | 1.6.6            | Modern declarative UI           |
| Architecture         | MVVM + Repository             | -                | Clean architecture pattern      |
| Dependency Injection | Dagger Hilt                   | 2.50             | Service location and DI         |
| Database             | Room + SQLCipher              | 2.6.1 + 4.5.4    | Encrypted local storage         |
| Networking           | Retrofit + OkHttp             | 2.9.0 + 4.11.0   | HTTP client and REST API        |
| Background Work      | WorkManager                   | 2.9.0            | Background sync operations      |
| Testing              | JUnit + Mockito + Espresso    | -                | Unit and integration testing    |


# Build Configuration and Quality Assurance
The application implements comprehensive build automation and quality assurance:

## Build System
- Application ID: `com.latticeonfhir.android`
- Version: 4.0.1 (build 69)
- Build Types: Debug (with test coverage) and Release (with ProGuard obfuscation)
- Signing: Custom keystore for release builds
- Build Pre-requisites: Firebase credentals for crashlytics and analytics

# Disclaimer
* HL7® and FHIR® are [registered trademarks](https://confluence.hl7.org/display/FHIR/FHIR+Trademark+Policy) of Health Level Seven International. HAPI FHIR (https://hapifhir.io/) in an open-source implementation of FHIR in Java. This website is unaffiliated with HL7, FHIR, or HAPI FHIR.
* We utilize publications from the public domain, from entities such as the World Health Organization, and the Indian Association of Pediatrics. We are unaffiliated with such agencies.
