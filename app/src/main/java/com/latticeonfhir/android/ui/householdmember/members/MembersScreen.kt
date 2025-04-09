package com.latticeonfhir.core.ui.householdmember.members

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.latticeonfhir.core.R
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.core.navigation.Screen
import com.latticeonfhir.android.ui.common.Loader
import com.latticeonfhir.core.utils.constants.NavControllerConstants.PATIENT
import com.latticeonfhir.core.utils.constants.NavControllerConstants.SELECTED_INDEX
import com.latticeonfhir.android.utils.converters.responseconverter.NameConverter
import com.latticeonfhir.core.utils.converters.responseconverter.RelationConverter
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toAge
import com.latticeonfhir.core.utils.converters.responseconverter.TimeConverter.toTimeInMilli
import java.util.Locale

@Composable
fun MembersScreen(
    patient: PatientResponse,
    navController: NavController,
    selectedIndex: Int,
    viewModel: MembersScreenViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(true) {
        viewModel.getAllRelations(patientId = patient.id)
    }
    if (viewModel.loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Loader()
        }
    } else {
        if (viewModel.relationsList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(id = R.string.no_household_members))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                items(viewModel.relationsListWithRelation) { relation ->
                    MembersCard(
                        RelationConverter.getRelationFromRelationEnum(
                            context,
                            relation.relation
                        )
                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                        relation.patientResponse,
                        navController,
                        selectedIndex
                    )
                }
            }
        }
    }
}

@Composable
fun MembersCard(
    relation: String,
    relative: PatientResponse,
    navController: NavController,
    selectedIndex: Int
) {
    val name = NameConverter.getFullName(relative.firstName, relative.middleName, relative.lastName)
    val age = relative.birthDate.toTimeInMilli().toAge()
    val subtitle = "${relative.gender[0].uppercase()}/$age · PID ${relative.fhirId}"
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clickable {
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    PATIENT,
                    relative
                )
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    SELECTED_INDEX,
                    selectedIndex
                )
                navController.navigate(Screen.PatientLandingScreen.route)
            },
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