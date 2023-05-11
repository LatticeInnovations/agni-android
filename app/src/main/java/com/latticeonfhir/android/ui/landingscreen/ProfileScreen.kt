package com.latticeonfhir.android.ui.landingscreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.latticeonfhir.android.ui.main.patientregistration.Detail
import com.latticeonfhir.android.ui.main.patientregistration.Label

@Preview(showBackground = true)
@Composable
fun ProfileScreen() {
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
                Label("Name")
                Detail(detail = "Vikram Kumar Pandey", tag = "NAME")
                Spacer(modifier = Modifier.height(20.dp))
                Label("Role")
                Detail(detail = "Doctor", tag = "DOCTOR")
                Spacer(modifier = Modifier.height(20.dp))
                Label("Phone No.")
                Detail(detail = "+91 8734080500", tag = "PHONE_NO")
                Spacer(modifier = Modifier.height(20.dp))
                Label("Email")
                Detail(detail = "Abc@gmail.com", tag = "EMAIL")
            }

        }
    }
}