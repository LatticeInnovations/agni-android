# FHIR-Android
Android Application integrated with Hapi fhir server to demonstrate FHIR compliant system

# JKS Password
latticeagni

<br/>

**NOTE- After cloning the project for first time, run following command in terminal to decrypt `google-services.json` file.**

```
GOOGLE_SERVICES_PASSPHRASE="$PASSPHRASE" ./decrypt_secret.sh
```


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
