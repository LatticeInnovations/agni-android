package com.latticeonfhir.android.ui.common

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
import com.latticeonfhir.android.R
import com.latticeonfhir.android.utils.constants.patient.IdentificationConstants.PASSPORT_TYPE
import com.latticeonfhir.android.utils.constants.patient.IdentificationConstants.PATIENT_ID_TYPE
import com.latticeonfhir.android.utils.constants.patient.IdentificationConstants.VOTER_ID_TYPE
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toPatientPreviewDate
import org.hl7.fhir.r4.model.Patient

@Composable
fun PreviewScreen(
    patient: Patient,
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
                    text = "${patient.nameFirstRep.nameAsSingleString}, ${patient.gender.display}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.testTag("NAME_TAG")
                )
                Spacer(modifier = Modifier.height(10.dp))
                Label("Date of birth")
                Detail(patient.birthDate.toPatientPreviewDate(), "DOB_TAG")
                Spacer(modifier = Modifier.height(10.dp))
                Label("Phone No.")
                Detail("+91 ${patient.telecom[0].value}", "PHONE_NO_TAG")
                if (patient.telecom.size > 1) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Label("Email")
                    Detail(patient.telecom[1].value, "EMAIL_TAG")
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
                patient.identifier.forEach { identifier ->
                    when(identifier.system) {
                        PASSPORT_TYPE -> {
                            Spacer(modifier = Modifier.height(10.dp))
                            Label("Passport ID")
                            Detail(identifier.value, "PASSPORT_ID_TAG")
                        }
                        VOTER_ID_TYPE -> {
                            Spacer(modifier = Modifier.height(10.dp))
                            Label("Voter ID")
                            Detail(identifier.value, "VOTER_ID_TAG")
                        }
                        PATIENT_ID_TYPE -> {
                            Spacer(modifier = Modifier.height(10.dp))
                            Label("Patient ID")
                            Detail(identifier.value, "PATIENT_ID_TAG")
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            val homeAddressLine1 = patient.addressFirstRep.line[0].value +
                    if (patient.addressFirstRep.line.size < 1) "" else {
                        ", " + patient.addressFirstRep.line[1].value
                    }
            val homeAddressLine2 = patient.addressFirstRep.city +
                    if (patient.addressFirstRep.district.isNullOrBlank()) "" else {
                        ", " + patient.addressFirstRep.district
                    }
            val homeAddressLine3 =
                "${patient.addressFirstRep.state}, ${patient.addressFirstRep.postalCode}"
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