package com.latticeonfhir.android.ui.patientregistration.step2

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.latticeonfhir.android.R
import com.latticeonfhir.android.ui.common.CustomTextField
import com.latticeonfhir.android.ui.common.IdLength
import com.latticeonfhir.android.ui.common.IdSelectionChip
import com.latticeonfhir.android.ui.patientregistration.PatientRegistrationViewModel
import com.latticeonfhir.android.ui.theme.Neutral40
import com.latticeonfhir.android.utils.constants.patient.IdentificationConstants.LATTICE
import com.latticeonfhir.android.utils.constants.patient.IdentificationConstants.LATTICE_CODE
import com.latticeonfhir.android.utils.constants.patient.IdentificationConstants.LATTICE_SYSTEM
import com.latticeonfhir.android.utils.constants.patient.IdentificationConstants.PASSPORT_TYPE
import com.latticeonfhir.android.utils.constants.patient.IdentificationConstants.PATIENT_ID_TYPE
import com.latticeonfhir.android.utils.constants.patient.IdentificationConstants.VOTER_ID_TYPE
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Identifier

@Composable
fun PatientRegistrationStepTwo(
    patientRegistrationViewModel: PatientRegistrationViewModel,
    viewModel: PatientRegistrationStepTwoViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            if (patientRegistrationViewModel.isEditing) viewModel.setData(patientRegistrationViewModel.patient)
            viewModel.isLaunched = true
        }
    }
    Column(
        modifier = Modifier.padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.identification),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Page 2/${patientRegistrationViewModel.totalSteps}",
                style = MaterialTheme.typography.bodySmall,
                color = Neutral40
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                IdSelectionChip(
                    idSelected = viewModel.isPassportSelected,
                    label = stringResource(id = R.string.passport_id)
                ) {
                    viewModel.isPassportSelected = !it
                }
                Spacer(modifier = Modifier.width(5.dp))
                IdSelectionChip(
                    idSelected = viewModel.isVoterSelected,
                    label = stringResource(id = R.string.voter_id)
                ) {
                    viewModel.isVoterSelected = !it
                }
                Spacer(modifier = Modifier.width(5.dp))
                IdSelectionChip(
                    idSelected = viewModel.isPatientSelected,
                    label = stringResource(id = R.string.patient_id)
                ) {
                    viewModel.isPatientSelected = !it
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            if (viewModel.isPassportSelected) {
                Spacer(modifier = Modifier.height(5.dp))
                CustomTextField(
                    value = viewModel.passportId,
                    label = stringResource(id = R.string.passport_id),
                    weight = 1f,
                    viewModel.maxPassportIdLength,
                    viewModel.isPassportValid,
                    stringResource(id = R.string.passport_id_error_msg),
                    KeyboardType.Text
                ) {
                    viewModel.passportId = it
                    viewModel.isPassportValid =
                        !viewModel.passportPattern.matches(viewModel.passportId)
                }
                IdLength(viewModel.passportId, viewModel.maxPassportIdLength, "PASSPORT_ID_LENGTH")
            } else {
                viewModel.passportId = ""
            }
            if (viewModel.isVoterSelected) {
                Spacer(modifier = Modifier.height(5.dp))
                CustomTextField(
                    value = viewModel.voterId,
                    label = stringResource(id = R.string.voter_id),
                    weight = 1f,
                    viewModel.maxVoterIdLength,
                    viewModel.isVoterValid,
                    stringResource(id = R.string.voter_id_error_msg),
                    KeyboardType.Text
                ) {
                    viewModel.voterId = it
                    viewModel.isVoterValid = !viewModel.voterPattern.matches(viewModel.voterId)
                }
                IdLength(viewModel.voterId, viewModel.maxVoterIdLength, "VOTER_ID_LENGTH")
            } else {
                viewModel.voterId = ""
            }
            if (viewModel.isPatientSelected) {
                Spacer(modifier = Modifier.height(5.dp))
                CustomTextField(
                    value = viewModel.patientId,
                    label = stringResource(id = R.string.patient_id),
                    weight = 1f,
                    viewModel.maxPatientIdLength,
                    viewModel.isPatientValid,
                    stringResource(id = R.string.patient_id_error_msg),
                    KeyboardType.Text
                ) {
                    viewModel.patientId = it
                    viewModel.isPatientValid = viewModel.patientId.length < 10
                }
                IdLength(viewModel.patientId, viewModel.maxPatientIdLength, "PATIENT_ID_LENGTH")
            } else {
                viewModel.patientId = ""
            }
        }
        Button(
            onClick = {
                patientRegistrationViewModel.showLoader = true
                coroutineScope.launch {
                    patientRegistrationViewModel.patient.apply {
                        identifier.clear()
                        identifier.add(
                            Identifier().apply {
                                system = LATTICE
                                value = patientRegistrationViewModel.patient.id
                                type = CodeableConcept(
                                    Coding(
                                        LATTICE_SYSTEM,
                                        LATTICE_CODE,
                                        ""
                                    )
                                )
                            }
                        )
                        if (viewModel.isPassportSelected) {
                            identifier.add(
                                Identifier().apply {
                                    system = PASSPORT_TYPE
                                    value = viewModel.passportId
                                    use = if (viewModel.isIdDuplicate(PASSPORT_TYPE, viewModel.passportId)) Identifier.IdentifierUse.TEMP else Identifier.IdentifierUse.OFFICIAL
                                }
                            )
                        }
                        if (viewModel.isVoterSelected) {
                            identifier.add(
                                Identifier().apply {
                                    system = VOTER_ID_TYPE
                                    value = viewModel.voterId
                                    use = if (viewModel.isIdDuplicate(VOTER_ID_TYPE, viewModel.voterId)) Identifier.IdentifierUse.TEMP else Identifier.IdentifierUse.OFFICIAL
                                }
                            )
                        }
                        if (viewModel.isPatientSelected) {
                            identifier.add(
                                Identifier().apply {
                                    system = PATIENT_ID_TYPE
                                    value = viewModel.patientId
                                    use = if (viewModel.isIdDuplicate(PATIENT_ID_TYPE, viewModel.patientId)) Identifier.IdentifierUse.TEMP else Identifier.IdentifierUse.OFFICIAL
                                }
                            )
                        }
                    }
                    patientRegistrationViewModel.showLoader = false
                    patientRegistrationViewModel.currentStep = 3
                }
            },
            modifier = Modifier
                .fillMaxWidth(),
            enabled = viewModel.identityInfoValidation()
        ) {
            Text(text = "Next")
        }
    }
}