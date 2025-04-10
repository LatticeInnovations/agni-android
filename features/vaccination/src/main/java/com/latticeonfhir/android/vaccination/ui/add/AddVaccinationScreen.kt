package com.latticeonfhir.core.vaccination.ui.add

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.latticeonfhir.core.data.local.model.vaccination.ImmunizationRecommendation
import com.latticeonfhir.core.data.local.roomdb.entities.vaccination.ManufacturerEntity
import com.latticeonfhir.core.data.server.model.patient.PatientResponse
import com.latticeonfhir.core.theme.MissedContainer
import com.latticeonfhir.core.theme.MissedContainerDark
import com.latticeonfhir.android.theme.MissedLabel
import com.latticeonfhir.android.theme.MissedLabelDark
import com.latticeonfhir.android.ui.CustomDialog
import com.latticeonfhir.core.utils.constants.NavControllerConstants.PATIENT
import com.latticeonfhir.core.utils.constants.NavControllerConstants.VACCINE
import com.latticeonfhir.core.utils.constants.NavControllerConstants.VACCINE_ADDED
import com.latticeonfhir.android.utils.constants.NavControllerConstants.VACCINE_ERROR_TYPE
import com.latticeonfhir.core.utils.converters.TimeConverter.daysBetween
import com.latticeonfhir.core.utils.converters.TimeConverter.toFileDateAndTimeName
import com.latticeonfhir.android.utils.converters.TimeConverter.toTodayStartDate
import com.latticeonfhir.android.utils.converters.TimeConverter.toddMMYYYYString
import com.latticeonfhir.core.utils.file.FileManager
import com.latticeonfhir.core.vaccination.R
import com.latticeonfhir.core.vaccination.data.enums.VaccineErrorTypeEnum
import com.latticeonfhir.core.vaccination.navigation.Screen
import com.latticeonfhir.core.vaccination.ui.add.AddVaccinationViewModel.Companion.MAX_FILE_SIZE_IN_KB
import com.latticeonfhir.android.vaccination.utils.VaccinesUtils.formatBytes
import com.latticeonfhir.core.vaccination.utils.VaccinesUtils.getNumberWithOrdinalIndicator
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVaccinationScreen(
    navController: NavController,
    viewModel: AddVaccinationViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val pickImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                viewModel.showUploadSheet = false
                viewModel.isImageCaptured = true
                viewModel.selectedImageUri = it
                viewModel.isSelectedFromGallery = true
            }
        }
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                viewModel.showUploadSheet = false
                viewModel.displayCamera = true
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        context as Activity,
                        Manifest.permission.CAMERA
                    )
                ) {
                    viewModel.showOpenSettingsDialog = true
                } else {
                    viewModel.showUploadSheet = false
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(context.getString(R.string.please_grant_permission))
                    }
                }
            }
        })
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            viewModel.patient =
                navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(
                    PATIENT
                )
            viewModel.getImmunizationRecommendationAndManufacturerList(viewModel.patient!!.id)
            viewModel.selectedVaccine =
                navController.previousBackStackEntry?.savedStateHandle?.get<ImmunizationRecommendation>(
                    VACCINE
                )
            viewModel.selectedVaccineName =
                viewModel.selectedVaccine?.let { vaccine ->
                    vaccine.name.replaceFirstChar { it.titlecase(Locale.getDefault()) } + " (" +
                            vaccine.shortName.replaceFirstChar {
                                it.titlecase(
                                    Locale.getDefault()
                                )
                            } + ")"
                } ?: ""
        }
        viewModel.isLaunched = true
    }
    BackHandler {
        when (true) {
            viewModel.isImagePreview -> {
                viewModel.isImagePreview = false
                viewModel.selectedImageUri = null
            }

            viewModel.isImageCaptured -> {
                if (!viewModel.isSelectedFromGallery) {
                    FileManager.removeFromInternalStorage(context, viewModel.tempFileName)
                    viewModel.tempFileName = ""
                }
                viewModel.isImageCaptured = false
                viewModel.selectedImageUri = null
                viewModel.isSelectedFromGallery = false
            }

            viewModel.displayCamera -> viewModel.displayCamera = false
            else -> {
                val files = viewModel.uploadedFileUri.toList()
                files.forEach {
                    viewModel.uploadedFileUri.remove(it)
                    FileManager.removeFromInternalStorage(context, it.toFile().name)
                }
                navController.navigateUp()
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .imePadding(),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    navigationIcon = {
                        IconButton(onClick = {
                            val files = viewModel.uploadedFileUri.toList()
                            files.forEach {
                                viewModel.uploadedFileUri.remove(it)
                                FileManager.removeFromInternalStorage(context, it.toFile().name)
                            }
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
                                viewModel.addVaccination {
                                    coroutineScope.launch {
                                        navController.previousBackStackEntry?.savedStateHandle?.set(
                                            VACCINE_ADDED,
                                            true
                                        )
                                        navController.navigateUp()
                                    }
                                }
                            },
                            enabled = viewModel.lotNo.isNotBlank() && viewModel.dateOfExpiry != null && viewModel.selectedVaccine != null
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
                        AnimatedVisibility(
                            viewModel.selectedVaccine != null
                        ) {
                            viewModel.selectedVaccine?.let { vaccine ->
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (vaccine.vaccineStartDate != Date(Date().toTodayStartDate()))
                                        StatusCard(vaccine)
                                    DateAndDoseRow(vaccine)
                                }
                            }
                        }
                        if (viewModel.selectedVaccine == null) Spacer(Modifier.height(16.dp))
                        Column(
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(22.dp)
                        ) {
                            VaccineDropDown(viewModel, navController)
                            AnimatedVisibility(
                                visible = viewModel.immunizationRecommendationList.contains(
                                    viewModel.selectedVaccine
                                ),
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
                                            .fillMaxWidth(),
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(
                                            capitalization = KeyboardCapitalization.Characters
                                        )
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
                                                painter = painterResource(R.drawable.today_calendar),
                                                "CALENDER_ICON",
                                                Modifier.size(24.dp)
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
                                        },
                                        keyboardOptions = KeyboardOptions(
                                            capitalization = KeyboardCapitalization.Sentences
                                        )
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
                    UploadFileBottomSheet(
                        viewModel = viewModel,
                        pickFromGalleryClick = {
                            pickImageLauncher.launch("image/*")
                        },
                        openCamera = {
                            if (ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.CAMERA
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                // request permission
                                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                            } else {
                                viewModel.showUploadSheet = false
                                viewModel.displayCamera = true
                            }
                        }
                    )
                }
                if (viewModel.showFileDeleteDialog) {
                    ShowFileDeleteDialog(viewModel)
                }

                if (viewModel.showOpenSettingsDialog) {
                    CustomDialog(
                        canBeDismissed = false,
                        title = stringResource(id = R.string.permissions_required),
                        text = stringResource(id = R.string.permissions_required_description),
                        dismissBtnText = stringResource(id = R.string.cancel),
                        confirmBtnText = stringResource(id = R.string.go_to_settings),
                        dismiss = {
                            viewModel.showOpenSettingsDialog = false
                            viewModel.showUploadSheet = false
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(context.getString(R.string.please_grant_permission))
                            }
                        },
                        confirm = {
                            val intent =
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    addCategory(Intent.CATEGORY_DEFAULT)
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    data = Uri.fromParts("package", context.packageName, null)
                                }
                            context.startActivity(intent)
                            viewModel.showOpenSettingsDialog = false
                        }
                    )
                }
            }
        )
    }

    AnimatedVisibility(
        visible = viewModel.displayCamera,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        CameraComposable(
            viewModel
        )
    }

    AnimatedVisibility(
        visible = viewModel.isImageCaptured || viewModel.isImagePreview,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        DisplayImage(
            viewModel
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UploadFileBottomSheet(
    viewModel: AddVaccinationViewModel,
    pickFromGalleryClick: () -> Unit,
    openCamera: () -> Unit
) {
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
                    pickFromGalleryClick()
                }
            )
            BottomSheetOptionRow(
                icon = painterResource(R.drawable.camera),
                label = stringResource(R.string.take_a_picture),
                onClick = {
                    openCamera()
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
            color = if (viewModel.isFileError) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.outline
        )
        FilledTonalButton(
            onClick = {
                viewModel.isFileError = false
                viewModel.showUploadSheet = true
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = viewModel.uploadedFileUri.size < 10
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Add, Icons.Default.Add.name, Modifier.size(18.dp))
                Text(stringResource(R.string.upload))
            }
        }
        viewModel.uploadedFileUri.forEach { file ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.isImagePreview = true
                        viewModel.selectedImageUri = file
                    },
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
                        text = Date(
                            file.toFile().name.substringBefore(".").toLong()
                        ).toFileDateAndTimeName(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = file.toFile().readBytes().size.formatBytes(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(
                    onClick = {
                        // delete dialog
                        viewModel.selectedUriToDelete = file
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
            value = viewModel.selectedManufacturer.name,
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
            viewModel.manufacturerList.forEach { manufacturer ->
                DropdownMenuItem(
                    onClick = {
                        showDropDown = false
                        viewModel.selectedManufacturer = manufacturer
                    },
                    text = {
                        Text(
                            text = manufacturer.name,
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
private fun VaccineDropDown(
    viewModel: AddVaccinationViewModel,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        var showDropDown by remember {
            mutableStateOf(false)
        }
        OutlinedTextField(
            value = viewModel.selectedVaccineName,
            onValueChange = { value ->
                if (value.length <= 100) viewModel.selectedVaccineName = value
                if (!viewModel.immunizationRecommendationList.map { vaccine ->
                        vaccine.name.lowercase() + " (" +
                                vaccine.shortName.lowercase() + ")"
                    }.contains(viewModel.selectedVaccineName.lowercase())) {
                    viewModel.selectedVaccine = null
                    viewModel.lotNo = ""
                    viewModel.dateOfExpiry = null
                    viewModel.selectedManufacturer = ManufacturerEntity(
                        id = "0",
                        name = "Select",
                        type = "empty",
                        active = false
                    )
                    viewModel.notes = ""
                    viewModel.uploadedFileUri = mutableStateListOf()
                }
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
        if (showDropDown) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(0.dp, 300.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceBright
                    )
                    .verticalScroll(rememberScrollState())
            ) {
                if (viewModel.immunizationRecommendationList.groupBy { it.name }
                        .filter { vaccine ->
                            stringResource(
                                R.string.vaccine_name_with_code,
                                vaccine.key.replaceFirstChar { it.titlecase(Locale.getDefault()) },
                                vaccine.value[0].shortName.replaceFirstChar { it.titlecase(Locale.getDefault()) }
                            ).contains(viewModel.selectedVaccineName, ignoreCase = true)
                        }.isEmpty()) {
                    Text(
                        text = stringResource(R.string.no_results_found),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
                viewModel.immunizationRecommendationList
                    .groupBy { it.name }
                    .filter { vaccine ->
                        stringResource(
                            R.string.vaccine_name_with_code,
                            vaccine.key.replaceFirstChar { it.titlecase(Locale.getDefault()) },
                            vaccine.value[0].shortName.replaceFirstChar { it.titlecase(Locale.getDefault()) }
                        ).contains(viewModel.selectedVaccineName, ignoreCase = true)
                    }
                    .forEach { vaccine ->
                        val isFullyVaccinated = vaccine.value.none { it.takenOn == null }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    enabled = !isFullyVaccinated,
                                    onClick = {
                                        showDropDown = false
                                        val vaccineToBeGiven =
                                            vaccine.value.sortedBy { it.vaccineStartDate }
                                                .first { it.takenOn == null }
                                        val timeRange = if (daysBetween(
                                                viewModel.patient!!.birthDate.convertStringToDate(),
                                                vaccineToBeGiven.vaccineStartDate
                                            ) <= 90
                                        ) {
                                            vaccineToBeGiven.vaccineStartDate.plusMinusDays(-3)..vaccineToBeGiven.vaccineEndDate.plusMinusDays(
                                                3
                                            )
                                        } else {
                                            vaccineToBeGiven.vaccineStartDate.plusMinusDays(-15)..vaccineToBeGiven.vaccineEndDate.plusMinusDays(
                                                15
                                            )
                                        }
                                        if (Date() in timeRange) {
                                            viewModel.selectedVaccineName =
                                                vaccine.key.replaceFirstChar { it.titlecase(Locale.getDefault()) } + " (" +
                                                        vaccine.value[0].shortName.replaceFirstChar {
                                                            it.titlecase(
                                                                Locale.getDefault()
                                                            )
                                                        } + ")"
                                            viewModel.selectedVaccine = vaccineToBeGiven
                                        } else {
                                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                                VACCINE_ERROR_TYPE,
                                                VaccineErrorTypeEnum.TIME.errorType
                                            )
                                            navController.navigate(Screen.VaccinationErrorScreen.route)
                                        }
                                    }
                                )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                if (isFullyVaccinated) {
                                    Text(
                                        text = stringResource(R.string.fully_vaccinated),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = stringResource(
                                        R.string.vaccine_name_with_code,
                                        vaccine.key.replaceFirstChar { it.titlecase(Locale.getDefault()) },
                                        vaccine.value[0].shortName.replaceFirstChar {
                                            it.titlecase(
                                                Locale.getDefault()
                                            )
                                        }
                                    ),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (isFullyVaccinated) MaterialTheme.colorScheme.outline
                                    else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

            }
        }
    }
}

@Composable
private fun DateAndDoseRow(
    vaccine: ImmunizationRecommendation
) {
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
            text = stringResource(
                R.string.number_dose,
                vaccine.doseNumber.getNumberWithOrdinalIndicator()
            ),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun StatusCard(
    vaccine: ImmunizationRecommendation
) {
    val isDelayed = vaccine.vaccineStartDate.toEndOfDay() < Date().time
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = getColorOfContainer(isDelayed = isDelayed)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(R.string.status_info),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = (if (isDelayed) stringResource(
                    R.string.due_on_date,
                    vaccine.vaccineDueDate.toPrescriptionDate()
                )
                else stringResource(
                    R.string.upcoming_on_info,
                    vaccine.vaccineDueDate.toPrescriptionDate()
                )) + " " + stringResource(
                    R.string.vaccine_date_range,
                    vaccine.vaccineStartDate.toSlotDate(),
                    vaccine.vaccineEndDate.toSlotDate()
                ),
                style = MaterialTheme.typography.labelLarge,
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
    val context = LocalContext.current
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
                    FileManager.removeFromInternalStorage(
                        context,
                        viewModel.selectedUriToDelete!!.toFile().name
                    )
                    viewModel.uploadedFileUri.remove(viewModel.selectedUriToDelete)
                    viewModel.selectedUriToDelete = null
                    viewModel.showFileDeleteDialog = false
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

@Composable
private fun DisplayImage(
    viewModel: AddVaccinationViewModel
) {
    val context = LocalContext.current
    if (viewModel.selectedImageUri != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            IconButton(
                onClick = {
                    if (viewModel.isImagePreview) {
                        viewModel.isImagePreview = false
                        viewModel.selectedImageUri = null
                    } else {
                        if (!viewModel.isSelectedFromGallery) {
                            FileManager.removeFromInternalStorage(context, viewModel.tempFileName)
                            viewModel.tempFileName = ""
                        }
                        viewModel.isImageCaptured = false
                        viewModel.selectedImageUri = null
                        viewModel.isSelectedFromGallery = false
                    }
                },
                modifier = Modifier
                    .zIndex(2f)
                    .padding(8.dp)
                    .statusBarsPadding()
            ) {
                Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
            }
            Image(
                painter = rememberImagePainter(viewModel.selectedImageUri),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Black),
                contentScale = ContentScale.Fit
            )
            if (!viewModel.isImagePreview) {
                Button(
                    onClick = {
                        var uri = viewModel.selectedImageUri
                        if (viewModel.isSelectedFromGallery) {
                            val fileName = "${Date().time}.jpeg"
                            val uploadFolder = FileManager.createFolder(context)
                            FileManager.insertFileToInternalStorage(
                                uploadFolder,
                                fileName,
                                viewModel.selectedImageUri!!.toString(),
                                context
                            )
                            val photoFile = File(
                                uploadFolder,
                                fileName
                            )
                            uri = Uri.fromFile(photoFile)
                        }
                        if (uri!!.toFile().readBytes().size / 1024 > MAX_FILE_SIZE_IN_KB) {
                            viewModel.isFileError = true
                        } else {
                            viewModel.uploadedFileUri.add(uri)
                        }
                        viewModel.displayCamera = false
                        viewModel.isImageCaptured = false
                        viewModel.selectedImageUri = null
                        viewModel.isSelectedFromGallery = false
                    },
                    modifier = Modifier
                        .zIndex(2f)
                        .fillMaxWidth()
                        .padding(16.dp)
                        .navigationBarsPadding()
                        .align(Alignment.BottomCenter)
                ) {
                    Text(text = stringResource(id = R.string.save))
                }
            }
        }
    }
}

@Composable
private fun CameraComposable(
    viewModel: AddVaccinationViewModel
) {
    val context = LocalContext.current
    var hasFlashUnit by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current
    var camera: Camera? by remember { mutableStateOf(null) }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var preview by remember { mutableStateOf<Preview?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    )
    {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .zIndex(2f),
            contentAlignment = Alignment.TopCenter
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                IconButton(
                    onClick = {
                        viewModel.displayCamera = false
                    }
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = {
                        viewModel.flashOn = !viewModel.flashOn
                        camera?.cameraControl?.enableTorch(!viewModel.flashOn)
                    },
                    enabled = hasFlashUnit
                ) {
                    Icon(
                        if (viewModel.flashOn) painterResource(id = R.drawable.flash_on)
                        else painterResource(id = R.drawable.flash_off),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }
        key(viewModel.cameraSelector) {
            AndroidView(
                factory = { context ->
                    val previewView = PreviewView(context)
                    val cameraProvider = cameraProviderFuture.get()
                    val previewUseCase = Preview.Builder()
                        .build()
                        .also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }
                    cameraProviderFuture.addListener({
                        try {
                            cameraProvider.unbindAll()

                            imageCapture = ImageCapture.Builder().build()
                            camera = cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                viewModel.cameraSelector,
                                previewUseCase,
                                imageCapture
                            )
                            hasFlashUnit = camera?.cameraInfo?.hasFlashUnit() == true
                        } catch (ex: Exception) {
                            Timber.e("manseeyy Use case binding failed", ex)
                        }
                        preview = previewUseCase
                    }, ContextCompat.getMainExecutor(context))
                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(2f),
            contentAlignment = Alignment.BottomCenter
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable {
                            val uploadFolder = FileManager.createFolder(context)
                            viewModel.tempFileName = "${Date().time}.jpeg"
                            val photoFile = File(
                                uploadFolder,
                                viewModel.tempFileName
                            )

                            val outputOptions =
                                ImageCapture.OutputFileOptions.Builder(photoFile).build()
                            imageCapture?.takePicture(
                                outputOptions,
                                ContextCompat.getMainExecutor(context),
                                object : ImageCapture.OnImageSavedCallback {
                                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                        viewModel.flashOn = false
                                        camera?.cameraControl?.enableTorch(false)
                                        viewModel.isImageCaptured = true
                                        viewModel.selectedImageUri =
                                            output.savedUri ?: Uri.fromFile(photoFile)
                                    }

                                    override fun onError(exc: ImageCaptureException) {
                                        onError(exc)
                                    }
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(89.dp)
                            .background(color = Color.Transparent, shape = CircleShape)
                            .border(width = 4.dp, color = Color.White, shape = CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .padding(20.dp)
                            .size(60.dp)
                            .background(color = Color.White, shape = CircleShape)
                    )
                }
            }
        }
    }
}