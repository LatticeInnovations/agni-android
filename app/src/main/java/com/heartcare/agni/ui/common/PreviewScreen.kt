package com.heartcare.agni.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.heartcare.agni.R
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.utils.constants.IdentificationConstants
import com.heartcare.agni.utils.converters.responseconverter.NameConverter
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toPatientPreviewDate
import java.util.Locale

@Composable
fun PreviewScreen(
    patientResponse: PatientResponse,
    navigate: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)
            .verticalScroll(rememberScrollState())
            .testTag("columnLayout")
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Heading("Basic Information", 1) { step ->
                    navigate(step)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "${
                        NameConverter.getFullName(
                            patientResponse.firstName,
                            patientResponse.middleName,
                            patientResponse.lastName
                        )
                    }, ${
                        patientResponse.gender.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(
                                Locale.getDefault()
                            ) else it.toString()
                        }
                    }",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.testTag("NAME_TAG")
                )
                Spacer(modifier = Modifier.height(10.dp))
                Label("Date of birth")
                Detail(patientResponse.birthDate.toPatientPreviewDate(), "DOB_TAG")
                Spacer(modifier = Modifier.height(10.dp))
                Label("Phone No.")
                Detail("+91 ${patientResponse.mobileNumber}", "PHONE_NO_TAG")
                if (!patientResponse.email.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Label("Email")
                    Detail(patientResponse.email, "EMAIL_TAG")
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {

                Heading("Identification", 2) { step ->
                    navigate(step)
                }
                patientResponse.identifier.forEach { identifier ->
                    if (identifier.identifierType == IdentificationConstants.PASSPORT_TYPE) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Label("Passport ID")
                        Detail(identifier.identifierNumber, "PASSPORT_ID_TAG")
                    }
                }
                patientResponse.identifier.forEach { identifier ->
                    if (identifier.identifierType == IdentificationConstants.VOTER_ID_TYPE) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Label("Voter ID")
                        Detail(identifier.identifierNumber, "VOTER_ID_TAG")
                    }
                }
                patientResponse.identifier.forEach { identifier ->
                    if (identifier.identifierType == IdentificationConstants.PATIENT_ID_TYPE) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Label("Patient ID")
                        Detail(identifier.identifierNumber, "PATIENT_ID_TAG")
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            val homeAddressLine1 = patientResponse.permanentAddress.addressLine1 +
                    if (patientResponse.permanentAddress.addressLine2.isNullOrBlank()) "" else {
                        ", " + patientResponse.permanentAddress.addressLine2
                    }
            val homeAddressLine2 = patientResponse.permanentAddress.city +
                    if (patientResponse.permanentAddress.district.isNullOrBlank()) "" else {
                        ", " + patientResponse.permanentAddress.district
                    }
            val homeAddressLine3 =
                "${patientResponse.permanentAddress.state}, ${patientResponse.permanentAddress.postalCode}"
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Heading("Addresses", 3) { step ->
                    navigate(step)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Label("Home Address")
                Detail(homeAddressLine1, "ADDRESS_LINE1_TAG")
                Detail(homeAddressLine2, "ADDRESS_LINE2_TAG")
                Detail(homeAddressLine3, "ADDRESS_LINE3_TAG")
            }
        }
        Spacer(
            modifier = Modifier
                .padding(bottom = 60.dp)
                .testTag("end of page")
        )
    }

}

@Composable
fun Heading(
    heading: String,
    step: Int,
    navigate: (Int) -> Unit
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = heading,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.secondary
        )
        TextButton(
            onClick = {
                navigate(step)
            },
            modifier = Modifier
                .testTag("edit btn $step")
        ) {
            Icon(
                Icons.Outlined.Edit,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = context.getString(R.string.edit),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun Label(label: String) {
    Text(text = label, style = MaterialTheme.typography.bodySmall)
}

@Composable
fun Detail(detail: String, tag: String) {
    Text(
        text = detail,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.testTag(tag)
    )
}