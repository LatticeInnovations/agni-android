package com.latticeonfhir.android.ui.main.patientregistration

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.latticeonfhir.android.ui.main.ui.theme.Primary40
import com.latticeonfhir.android.ui.main.ui.theme.Primary70
import com.latticeonfhir.android.ui.main.ui.theme.Primary95

@Composable
fun PatientRegistrationPreview (viewModel: PatientRegistrationViewModel){
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(15.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .weight(1f)
        ) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp)
                ) {
                    Heading("Page 1/3")
                    DetailsText("First Name", viewModel.firstName)
                    if (viewModel.middleName != "") DetailsText("Middle Name", viewModel.middleName)
                    if (viewModel.lastName != "") DetailsText("Last Name", viewModel.lastName)
                    if (viewModel.dobAgeSelector == "dob") DetailsText(
                        "Date of birth",
                        viewModel.dob
                    )
                    if (viewModel.dobAgeSelector == "age") AgeText(
                        "Age",
                        viewModel.years,
                        viewModel.months,
                        viewModel.days
                    )
                    DetailsText("Phone Number", "+91 ${viewModel.phoneNumber}")
                    DetailsText("Email Address", viewModel.email)
                    DetailsText("Gender", viewModel.gender)
                    EditButton(viewModel, 1)
                }
                Divider(
                    modifier = Modifier.fillMaxWidth(),
                    color = Primary70
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp)
                ) {
                    Heading("Page 2/3")
                    if (viewModel.isPassportSelected) DetailsText(
                        "Passport Id",
                        viewModel.passportId
                    )
                    if (viewModel.isVoterSelected) DetailsText("Voter Id", viewModel.voterId)
                    if (viewModel.isPatientSelected) DetailsText("Patient Id", viewModel.patientId)
                    EditButton(viewModel, 2)
                }
                Divider(
                    modifier = Modifier.fillMaxWidth(),
                    color = Primary70
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp)
                ) {
                    Heading("Page 3/3")
                    SubHeading("Home Address")
                    AddressCard(viewModel.homeAddress)
                    if (viewModel.addWorkAddress) {
                        Spacer(modifier = Modifier.height(20.dp))
                        SubHeading("Work Address")
                        AddressCard(viewModel.workAddress)
                    }
                    EditButton(viewModel, 3)
                }
                Divider(
                    modifier = Modifier.fillMaxWidth(),
                    color = Primary70
                )
            }
        }
        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        ) {
            Text(text = "Save")
        }
    }
}

@Composable
fun Heading(title: String){
    Text(
        text = title,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
fun SubHeading(title: String){
    Text(
        text = title,
        modifier = Modifier.fillMaxWidth(),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
fun DetailsText(label:String, text: String){
    Text(
        text = "$label: ${text.capitalize()}",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(top = 15.dp)
    )
}

@Composable
fun AgeText(label:String, years: String, months: String, days: String){
    Text(
        text = "$label: $years years $months months $days days",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(top = 15.dp)
    )
}

@Composable
fun EditButton(viewModel: PatientRegistrationViewModel, step: Int){
    Column (modifier = Modifier.fillMaxWidth()){
        TextButton(
            onClick = { viewModel.step = step },
            modifier = Modifier.align(Alignment.End)
        ) {
            Icon(
                Icons.Outlined.Edit,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = "Edit",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun AddressCard(address: Address){
    Column (
        modifier = Modifier.padding(5.dp)
            ){
        DetailsText("Postal Code", address.pincode)
        DetailsText("State", address.state)
        DetailsText("House No., Building, Street, Area", address.area)
        DetailsText("Town/ Locality", address.town)
        DetailsText("City/ District", address.city)
    }
}