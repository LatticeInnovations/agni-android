package com.latticeonfhir.android.ui.main.patientlandingscreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.latticeonfhir.android.ui.householdmember.members.MembersScreenViewModel
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.ui.common.Loader
import com.latticeonfhir.android.utils.converters.responseconverter.NameConverter
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toAge
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTimeInMilli
import com.latticeonfhir.android.utils.relation.RelationConverter

@Composable
fun MembersScreen(patient: PatientResponse, viewModel: MembersScreenViewModel = hiltViewModel()) {
    val context = LocalContext.current
    viewModel.getAllRelations(patientId = patient.id)
    if (viewModel.loading){
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            Loader()
        }
    }
    else{
        if (viewModel.relationsList.isEmpty()){
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                Text(text = "No household members.")
            }
        }
        else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                items(viewModel.relationsList) { relation ->
                    var relative by remember {
                        mutableStateOf<PatientResponse?>(null)
                    }
                    viewModel.getPatientData(relation.toId) {
                        relative = it
                    }
                    relative?.let {
                        MembersCard(
                            RelationConverter.getRelationFromRelationEnum(
                                context,
                                relation.relation
                            ).capitalize(),
                            it
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MembersCard(relation: String, relative: PatientResponse) {
    val name = NameConverter.getFullName(relative.firstName, relative.middleName, relative.lastName)
    val age = relative.birthDate.toTimeInMilli().toAge()
    val subtitle = "${relative.gender[0].uppercase()}/$age · PID ${relative.fhirId}"
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        shape = RoundedCornerShape(1.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            Text(
                text = "$relation of",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}