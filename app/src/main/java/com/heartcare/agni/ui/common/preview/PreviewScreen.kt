package com.heartcare.agni.ui.common.preview

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.heartcare.agni.R
import com.heartcare.agni.data.local.enums.NationalIdUse
import com.heartcare.agni.data.local.enums.YesNoEnum
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.utils.constants.IdentificationConstants
import com.heartcare.agni.utils.converters.responseconverter.NameConverter
import com.heartcare.agni.utils.converters.responseconverter.StringUtils.capitalizeFirst
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toPatientPreviewDate

@Composable
fun PreviewScreen(
    patientResponse: PatientResponse,
    viewModel: PreviewScreenViewModel = hiltViewModel(),
    navigate: (Int) -> Unit
) {
    LaunchedEffect(patientResponse) {
        viewModel.provinceName = viewModel.getLevelNames(patientResponse.permanentAddress.province)
        viewModel.areaCouncilName =
            viewModel.getLevelNames(patientResponse.permanentAddress.areaCouncil)
        viewModel.islandName = viewModel.getLevelNames(patientResponse.permanentAddress.island)
        viewModel.villageName = if (patientResponse.permanentAddress.village.isNullOrBlank()) ""
            else {
                if (patientResponse.permanentAddress.addressLine2.isNullOrBlank()) viewModel.getLevelNames(
                patientResponse.permanentAddress.village
            )
            else patientResponse.permanentAddress.addressLine2
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BasicInformationCard(patientResponse, navigate)
        IdentificationCard(patientResponse, navigate)
        AddressCard(viewModel, patientResponse, navigate)
        Spacer(modifier = Modifier.height(60.dp))
    }
}

@Composable
private fun BasicInformationCard(
    patientResponse: PatientResponse,
    navigate: (Int) -> Unit
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
            Heading(stringResource(R.string.basic_information), 1) { step ->
                navigate(step)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "${
                    NameConverter.getFullName(
                        patientResponse.firstName,
                        patientResponse.lastName
                    )
                }, ${patientResponse.gender.capitalizeFirst()}",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(10.dp))
            Label(stringResource(R.string.date_of_birth))
            Detail(patientResponse.birthDate.toPatientPreviewDate())
            if (patientResponse.mobileNumber != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Label(stringResource(R.string.phone_number_label))
                Detail("${patientResponse.mobileNumber}")
            }
            Spacer(modifier = Modifier.height(10.dp))
            Label(stringResource(R.string.patient_deceased_label))
            Detail(
                if (patientResponse.patientDeceasedReason.isNullOrBlank()) YesNoEnum.NO.display
                else "${YesNoEnum.YES.display} | ${patientResponse.patientDeceasedReason}"
            )
            Spacer(modifier = Modifier.height(10.dp))
            Label(stringResource(R.string.mother_name))
            Detail(patientResponse.mothersName)
            if (!patientResponse.fathersName.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Label(stringResource(R.string.father_name))
                Detail(patientResponse.fathersName)
            }
            if (!patientResponse.spouseName.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Label(stringResource(R.string.spouse_name))
                Detail(patientResponse.spouseName)
            }
        }
    }
}

@Composable
private fun IdentificationCard(
    patientResponse: PatientResponse,
    navigate: (Int) -> Unit
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

            Heading(stringResource(R.string.identification), 2) { step ->
                navigate(step)
            }
            patientResponse.identifier.forEach { identifier ->
                if (identifier.identifierType == IdentificationConstants.HOSPITAL_ID) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Label(stringResource(R.string.hospital_id))
                    Detail(identifier.identifierNumber)
                }
            }
            patientResponse.identifier.forEach { identifier ->
                if (identifier.identifierType == IdentificationConstants.NATIONAL_ID) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Column {
                            Label(stringResource(R.string.national_id))
                            Detail(identifier.identifierNumber.map { "X" }.joinToString(""))
                        }
                        val text: String
                        val icon: Painter
                        if (identifier.use == NationalIdUse.OFFICIAL.use) {
                            text = stringResource(R.string.verified)
                            icon = painterResource(R.drawable.sync_completed_icon)
                        } else {
                            text = stringResource(R.string.unverified)
                            icon = painterResource(R.drawable.info)
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant
                            ),
                            color = Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    painter = icon,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = text,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddressCard(
    viewModel: PreviewScreenViewModel,
    patientResponse: PatientResponse,
    navigate: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        val homeAddressLine1 = viewModel.villageName.ifBlank { null }
        val homeAddressLine2 =
            "${viewModel.islandName}, ${viewModel.areaCouncilName}"
        val homeAddressLine3 =
            "${viewModel.provinceName} ${patientResponse.permanentAddress.postalCode ?: ""}"
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            Heading(stringResource(R.string.addresses), 3) { step ->
                navigate(step)
            }
            Spacer(modifier = Modifier.height(10.dp))
            if (!homeAddressLine1.isNullOrBlank()) Detail(homeAddressLine1)
            Detail(homeAddressLine2)
            Detail(homeAddressLine3)
        }
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
private fun Label(label: String) {
    Text(text = label, style = MaterialTheme.typography.bodySmall)
}

@Composable
private fun Detail(detail: String) {
    Text(
        text = detail,
        style = MaterialTheme.typography.bodyLarge
    )
}