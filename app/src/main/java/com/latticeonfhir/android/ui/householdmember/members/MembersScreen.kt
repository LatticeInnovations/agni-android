package com.latticeonfhir.android.ui.householdmember.members

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
import com.google.android.fhir.logicalId
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.local.enums.RelationEnum
import com.latticeonfhir.android.ui.common.Loader
import com.latticeonfhir.android.utils.converters.responseconverter.RelationConverter.getRelationFromRelationEnum
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toAge
import com.latticeonfhir.android.utils.fhirengine.FhirQueries.getLatticeId
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.RelatedPerson
import java.util.Locale

@Composable
fun MembersScreen(
    patient: Patient,
    viewModel: MembersScreenViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(true) {
        viewModel.getAllRelations(patientId = patient.logicalId)
    }
    if (viewModel.loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Loader()
        }
    } else {
        if (viewModel.relationsListWithRelation.isEmpty()) {
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
                        getRelationFromRelationEnum(
                            context,
                            RelationEnum.fromString(relation.resource.relationship[0].coding[0].code)
                        )
                            .replaceFirstChar { it.titlecase(Locale.getDefault()) },
                        relation.included?.get(RelatedPerson.PATIENT.paramName)?.get(0) as Patient
                    )
                }
            }
        }
    }
}

@Composable
fun MembersCard(relation: String, relative: Patient) {
    val name = relative.nameFirstRep.nameAsSingleString
    val age = relative.birthDate.time.toAge()
    val subtitle = "${relative.gender.display[0]}/$age · PID ${getLatticeId(relative)}"
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