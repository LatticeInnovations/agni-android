package com.latticeonfhir.android.ui.prescription.previousprescription

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp

@Composable
fun PreviousPrescriptionsScreen() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        PrescriptionCard(date = "25 Mar 2023")
        PrescriptionCard(date = "23 Mar 2023")
        PrescriptionCard(date = "10 Mar 2023")
        PrescriptionCard(date = "9 Mar 2023")
        PrescriptionCard(date = "5 Mar 2023")
    }
}

@Composable
fun PrescriptionCard(date: String) {
    var expanded by remember {
        mutableStateOf(false)
    }
    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "Rotation state of expand icon button",
    )
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp),
        shadowElevation = 5.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        expanded = !expanded
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = date, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = "DOWN_ARROW",
                    modifier = Modifier.rotate(rotationState)
                )
            }
            AnimatedVisibility(
                visible = expanded
            ) {
                Column {
                    Divider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier.padding(vertical = 20.dp)
                    )
                    MedicineDetails(
                        "Amlodipine 5 mg tablet",
                        "1 Tablet OD, After breakfast\n" +
                                "Duration : 7 days , Qty : 7 \n" +
                                "Notes : Take rest \n"
                    )
                    MedicineDetails(
                        "Epinephrine hydrochloride 1 mg/mL injection",
                        "1 ml OD, Before food\n" +
                                "Duration : 7 days , Qty : 7 \n" +
                                "Notes : Take rest \n"
                    )
                    TextButton(
                        onClick = { /*TODO*/ },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(text = "Re-Prescribe")
                    }
                }
            }
        }
    }
}

@Composable
fun MedicineDetails(drug: String, details: String) {
    Column {
        Text(
            text = drug,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = details,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}