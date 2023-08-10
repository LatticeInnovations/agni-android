package com.latticeonfhir.android.ui.landingscreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
                Detail(detail = viewModel.userRole, tag = "ROLE")
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