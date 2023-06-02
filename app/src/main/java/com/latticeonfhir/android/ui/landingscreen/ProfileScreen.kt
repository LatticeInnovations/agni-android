package com.latticeonfhir.android.ui.landingscreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.latticeonfhir.android.R
import com.latticeonfhir.android.ui.main.patientregistration.Detail
import com.latticeonfhir.android.ui.main.patientregistration.Label

@Composable
fun ProfileScreen(viewModel: LandingScreenViewModel = hiltViewModel()) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
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
                Label(stringResource(id = R.string.name_label))
                Detail(detail = viewModel.userName, tag = "NAME")
                Spacer(modifier = Modifier.height(20.dp))
                Label(stringResource(id = R.string.role_label))
                Detail(detail = viewModel.userRole, tag = "DOCTOR")
                Spacer(modifier = Modifier.height(20.dp))
                Label(stringResource(id = R.string.phone_number_label))
                Detail(detail = "+91 ${viewModel.userPhoneNo}", tag = "PHONE_NO")
                Spacer(modifier = Modifier.height(20.dp))
                Label(stringResource(id = R.string.email))
                Detail(detail = viewModel.userEmail, tag = "EMAIL")
            }
        }
    }
}