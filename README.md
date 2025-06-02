
# About Agni
Agni is a mobile-first platform that:
* is designed for [offline use](https://agni.thelattice.in/key-features/offline-first/),
* uses evidence-based guidelines for clinical workflows,
* is built on [HL7® FHIR®](https://agni.thelattice.in/key-features/fhir-data-model/), with out-of-the-box interoperability

The system utilizes the WHO’s PEN protocols for cardiovascular disease (CVD) risk assessment[^1], and the Indian Academy of Pediatrics' immunization recommendations[^2]. Along with its focus on cardiac health and immunization, it provides a complete primary care workflow.

Detailed design documentation is available on the [agni website](https://agni.thelattice.in/)
# Minimum Requirements
Android Studio Meerkat Feature Drop | 2024.3.2
Build #AI-243.25659.59.2432.13423653, built on April 29, 2025
Runtime version: 21.0.6+-13368085-b895.109 amd64
VM: OpenJDK 64-Bit Server VM by JetBrains s.r.o.
Toolkit: sun.awt.windows.WToolkit
Kotlin plugin: K2 mode
GC: G1 Young Generation, G1 Concurrent GC, G1 Old Generation

# Build Pre-requisite
This app requires Firebase credentials for crashlytics and analytics purpose. Please generate your own credentials and put it into [app](/app) folder for a successful build.

# Build Instructions
If you met minimum requirements, clone the repository and open in Android Studio. It will automatically build project.

# Signing your App
https://developer.android.com/studio/publish/app-signing

<br/>

## `master` branch
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


# Disclaimer
* HL7® and FHIR® are [registered trademarks](https://confluence.hl7.org/display/FHIR/FHIR+Trademark+Policy) of Health Level Seven International. HAPI FHIR (https://hapifhir.io/) in an open-source implementation of FHIR in Java. This website is unaffiliated with HL7, FHIR, or HAPI FHIR.
* We utilize publications from the public domain, from entities such as the World Health Organization, and the Indian Association of Pediatrics. We are unaffiliated with such agencies.
