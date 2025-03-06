package com.latticeonfhir.android.ui.vaccination.add

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.ui.theme.MissedContainer
import com.latticeonfhir.android.ui.theme.MissedContainerDark
import com.latticeonfhir.android.ui.theme.MissedLabel
import com.latticeonfhir.android.ui.theme.MissedLabelDark
import com.latticeonfhir.android.utils.constants.NavControllerConstants.PATIENT
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toddMMYYYYString
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVaccinationScreen(
    navController: NavController,
    viewModel: AddVaccinationViewModel = hiltViewModel()
) {
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            viewModel.patient =
                navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(
                    PATIENT
                )
        }
        viewModel.isLaunched = true
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(Icons.Default.Clear, contentDescription = "CLEAR_ICON")
                    }
                },
                title = {
                    Text(stringResource(R.string.add_vaccination))
                },
                actions = {
                    TextButton(
                        onClick = {
                            // save vaccination
                        }
                    ) {
                        Text(text = stringResource(R.string.save))
                    }
                }
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = if (isSystemInDarkTheme()) Color.Black else Color.White)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusCard()
                    DateAndDoseRow()
                    Column(
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(22.dp)
                    ) {
                        VaccineDropDown(viewModel)
                        AnimatedVisibility(
                            visible = viewModel.listOfVaccines.contains(viewModel.selectedVaccine),
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(22.dp)
                            ) {
                                OutlinedTextField(
                                    value = viewModel.lotNo,
                                    onValueChange = { value ->
                                        if (value.length <= 20) viewModel.lotNo = value
                                    },
                                    label = {
                                        Text(stringResource(R.string.lot_no_mandatory))
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                )
                                OutlinedTextField(
                                    value = viewModel.dateOfExpiry?.toddMMYYYYString() ?: "",
                                    onValueChange = { },
                                    label = {
                                        Text(stringResource(R.string.date_of_expiry_mandatory))
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    trailingIcon = {
                                        Icon(
                                            painter = painterResource(R.drawable.today_icon),
                                            "CALENDER_ICON",
                                            Modifier.size(18.dp)
                                        )
                                    },
                                    placeholder = {
                                        Text(stringResource(R.string.date_format))
                                    },
                                    readOnly = true,
                                    interactionSource = remember {
                                        MutableInteractionSource()
                                    }.also { interactionSource ->
                                        LaunchedEffect(interactionSource) {
                                            interactionSource.interactions.collect {
                                                if (it is PressInteraction.Release) {
                                                    viewModel.showDatePicker = true
                                                }
                                            }
                                        }
                                    }
                                )
                                ManufacturerDropDown(viewModel)
                                OutlinedTextField(
                                    value = viewModel.notes,
                                    onValueChange = { value ->
                                        if (value.length <= 100 && value.matches(Regex("^[a-zA-Z0-9 ]*$"))) viewModel.notes =
                                            value
                                    },
                                    label = {
                                        Text(stringResource(R.string.notes_heading))
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    supportingText = {
                                        Text(stringResource(R.string.notes_for_adverse_reaction))
                                    }
                                )
                                UploadCertificatesComposable(viewModel)
                            }
                        }
                    }
                }
            }

            if (viewModel.showDatePicker) {
                DatePickerComposable(viewModel)
            }
            if (viewModel.showUploadSheet) {
                UploadFileBottomSheet(viewModel)
            }
            if (viewModel.showFileDeleteDialog) {
                ShowFileDeleteDialog(viewModel)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UploadFileBottomSheet(viewModel: AddVaccinationViewModel) {
    ModalBottomSheet(
        onDismissRequest = { viewModel.showUploadSheet = false },
        sheetState = rememberModalBottomSheetState(),
        modifier = Modifier
            .navigationBarsPadding(),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp, top = 8.dp)
        ) {
            BottomSheetOptionRow(
                icon = painterResource(R.drawable.gallery_image),
                label = stringResource(R.string.upload_from_gallery),
                onClick = {

                }
            )
            BottomSheetOptionRow(
                icon = painterResource(R.drawable.camera),
                label = stringResource(R.string.take_a_picture),
                onClick = {

                }
            )
        }
    }
}

@Composable
private fun BottomSheetOptionRow(
    icon: Painter,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            painter = icon,
            null,
            modifier = Modifier.weight(1f),
            tint = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.weight(9f)
        )
    }
}

@Composable
private fun UploadCertificatesComposable(
    viewModel: AddVaccinationViewModel
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.upload_certifications),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = stringResource(R.string.upload_certifications_info),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
        FilledTonalButton(
            onClick = {
                viewModel.showUploadSheet = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Add, Icons.Default.Add.name, Modifier.size(18.dp))
                Text(stringResource(R.string.upload))
            }
        }
        viewModel.uploadedFile.forEach { file ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier.background(
                        color = MaterialTheme.colorScheme.surfaceColorAtElevation(11.dp),
                        shape = RoundedCornerShape(8.dp)
                    ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.file),
                        null,
                        modifier = Modifier.padding(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Column(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = file,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "123 kb",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(
                    onClick = {
                        // delete dialog
                        viewModel.showFileDeleteDialog = true
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.delete_icon),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ManufacturerDropDown(viewModel: AddVaccinationViewModel) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        var showDropDown by remember {
            mutableStateOf(false)
        }
        OutlinedTextField(
            value = viewModel.selectedManufacturer,
            onValueChange = { },
            label = {
                Text(stringResource(R.string.vaccine_manufacturer))
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDropDown = true },
            singleLine = true,
            readOnly = true,
            trailingIcon = {
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    Icons.Default.KeyboardArrowDown.name
                )
            },
            interactionSource = remember {
                MutableInteractionSource()
            }.also { interactionSource ->
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect {
                        if (it is PressInteraction.Release) {
                            showDropDown = true
                        }
                    }
                }
            }
        )
        DropdownMenu(
            modifier = Modifier
                .fillMaxWidth(0.91f)
                .heightIn(0.dp, 300.dp),
            expanded = showDropDown,
            onDismissRequest = { showDropDown = false },
        ) {
            viewModel.listOfManufacturer.forEach { label ->
                DropdownMenuItem(
                    onClick = {
                        showDropDown = false
                        viewModel.selectedManufacturer = label
                    },
                    text = {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                )
            }
        }
    }
}


@Composable
private fun VaccineDropDown(viewModel: AddVaccinationViewModel) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        var showDropDown by remember {
            mutableStateOf(false)
        }
        OutlinedTextField(
            value = viewModel.selectedVaccine,
            onValueChange = { value ->
                if (value.length <= 100) viewModel.selectedVaccine = value
            },
            placeholder = {
                Text(stringResource(R.string.search_vaccination))
            },
            modifier = Modifier
                .fillMaxWidth(),
            singleLine = true,
            interactionSource = remember {
                MutableInteractionSource()
            }.also { interactionSource ->
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect {
                        if (it is PressInteraction.Release) {
                            showDropDown = true
                        }
                    }
                }
            }
        )
        DropdownMenu(
            modifier = Modifier
                .fillMaxWidth(0.91f)
                .heightIn(0.dp, 300.dp),
            expanded = showDropDown,
            onDismissRequest = { showDropDown = false },
        ) {
            viewModel.listOfVaccines
                .filter { it.contains(viewModel.selectedVaccine) }
                .forEach { label ->
                    DropdownMenuItem(
                        onClick = {
                            showDropDown = false
                            viewModel.selectedVaccine = label
                        },
                        text = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    )
                }
        }
    }
}

@Composable
private fun DateAndDoseRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = stringResource(R.string.date),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = Date().toddMMYYYYString(),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = stringResource(R.string.number_dose, "2nd"),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun StatusCard() {
    val isDelayed = false
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = getColorOfContainer(isDelayed = isDelayed)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = stringResource(R.string.status_info),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = if (isDelayed) stringResource(R.string.due_on_date, "24 Jan 2024")
                else stringResource(R.string.upcoming_on_info, "24 Jan 2024"),
                style = MaterialTheme.typography.bodyLarge,
                color = getColorOfLabel(isDelayed = isDelayed)
            )
        }
    }
}

@Composable
private fun getColorOfContainer(isDelayed: Boolean): Color {
    return when (isSystemInDarkTheme()) {
        true -> {
            if (isDelayed) MissedContainerDark
            else MaterialTheme.colorScheme.surface
        }

        false -> {
            if (isDelayed) MissedContainer
            else MaterialTheme.colorScheme.surface
        }
    }
}

@Composable
fun getColorOfLabel(isDelayed: Boolean): Color {
    return when (isSystemInDarkTheme()) {
        true -> {
            if (isDelayed) MissedLabelDark
            else MaterialTheme.colorScheme.onSurfaceVariant
        }

        false -> {
            if (isDelayed) MissedLabel
            else MaterialTheme.colorScheme.onSurfaceVariant
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerComposable(viewModel: AddVaccinationViewModel) {
    val selectableDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            return utcTimeMillis >= Date().toTodayStartDate()
        }
    }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = viewModel.dateOfExpiry?.time,
        selectableDates = selectableDates
    )
    val confirmEnabled = remember {
        derivedStateOf { datePickerState.selectedDateMillis != null }
    }
    DatePickerDialog(
        onDismissRequest = {
            viewModel.showDatePicker = false
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.showDatePicker = false
                    viewModel.dateOfExpiry =
                        datePickerState.selectedDateMillis?.let { dateInLong ->
                            Date(
                                dateInLong
                            )
                        } ?: Date()
                },
                enabled = confirmEnabled.value
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    viewModel.showDatePicker = false
                }
            ) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(
            state = datePickerState
        )
    }
}

@Composable
private fun ShowFileDeleteDialog(viewModel: AddVaccinationViewModel) {
    AlertDialog(
        onDismissRequest = {
            viewModel.showFileDeleteDialog = false
        },
        title = {
            Text(stringResource(R.string.confirm_deletion))
        },
        text = {
            Text(stringResource(R.string.confirm_deletion_info))
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // delete file
                }
            ) {
                Text(stringResource(R.string.yes_delete))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    viewModel.showFileDeleteDialog = false
                }
            ) {
                Text(stringResource(R.string.no_go_back))
            }
        }
    )
}