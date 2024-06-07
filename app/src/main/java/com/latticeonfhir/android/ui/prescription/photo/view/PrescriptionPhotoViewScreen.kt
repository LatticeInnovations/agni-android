package com.latticeonfhir.android.ui.prescription.photo.view

import android.content.Context
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.navigation.Screen
import com.latticeonfhir.android.ui.common.appointmentsfab.AppointmentsFab
import com.latticeonfhir.android.ui.patientlandingscreen.AllSlotsBookedDialog
import com.latticeonfhir.android.utils.constants.NavControllerConstants
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.isSameDay
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.isToday
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.isYesterday
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toDayFullMonthYear
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toPrescriptionNavDate
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toPrescriptionTime
import com.latticeonfhir.android.utils.file.FileManager
import com.latticeonfhir.android.utils.file.FileManager.getUriFromFileName
import com.latticeonfhir.android.utils.file.FileManager.shareImageToOtherApps
import kotlinx.coroutines.launch
import java.io.File
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrescriptionPhotoViewScreen(
    navController: NavController,
    viewModel: PrescriptionPhotoViewViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            viewModel.patient =
                navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(
                    NavControllerConstants.PATIENT
                )
            viewModel.getPastPrescription()
            viewModel.isLaunched = true
        }
    }

    BackHandler(enabled = true) {
        if (viewModel.isFabSelected) viewModel.isFabSelected = false
        else if (viewModel.selectedFile != null) {
            viewModel.isTapped = false
            viewModel.isLongPressed = false
            viewModel.displayNote = false
            viewModel.selectedFile = null
        } else navController.popBackStack(Screen.PatientLandingScreen.route, inclusive = false)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                ),
                title = {
                    Text(
                        text = stringResource(id = R.string.prescriptions),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.testTag("HEADING_TAG")
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack(
                            Screen.PatientLandingScreen.route,
                            inclusive = false
                        )
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "BACK_ICON")
                    }
                },
                actions = {
                    AnimatedVisibility(viewModel.isLongPressed) {
                        NavBarActions(context, viewModel)
                    }
                }
            )
        },
        content = {
            Box(modifier = Modifier.padding(it)) {
                if (viewModel.prescriptionPhotos.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.no_prescription_added),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                key (viewModel.prescriptionPhotos){
                    PhotoView(viewModel)
                }
                if (viewModel.showAllSlotsBookedDialog) {
                    AllSlotsBookedDialog {
                        viewModel.showAllSlotsBookedDialog = false
                    }
                }
            }
        }
    )
    viewModel.patient?.let { patient ->
        AppointmentsFab(
            modifier = Modifier.padding(end = 16.dp),
            navController = navController,
            patient = patient,
            viewModel.isFabSelected
        ) { showDialog ->
            if (showDialog) {
                viewModel.showAllSlotsBookedDialog = true
            } else viewModel.isFabSelected = !viewModel.isFabSelected
        }
    }
    AnimatedVisibility(
        visible = viewModel.isTapped,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        DisplayImage(context, viewModel)
    }
    if (viewModel.showDeleteDialog) {
        DeletePhotoDialog(
            dismiss = {
                viewModel.showDeleteDialog = false
            },
            confirm = {
                // delete prescription
                viewModel.showDeleteDialog = false
            }
        )
    }
    if (viewModel.showNoteDialog) {
        AddNoteDialog(
            image = viewModel.selectedFile!!.filename.getUriFromFileName(context),
            note = viewModel.selectedFile!!.note ?: "",
            dismiss = {
                viewModel.showNoteDialog = false
            },
            confirm = { note ->
                // add note to prescription
                viewModel.addNoteToPrescription(note) {
                    if (viewModel.isLongPressed){
                        viewModel.isLongPressed = false
                        viewModel.selectedFile = null
                    }
                    if (viewModel.isTapped) {
                        viewModel.displayNote = true
                        viewModel.selectedFile = viewModel.selectedFile?.copy(
                            note = note
                        )
                    }
                    viewModel.showNoteDialog = false
                }
            }
        )
    }
}

@Composable
fun NavBarActions(
    context: Context,
    viewModel: PrescriptionPhotoViewViewModel
) {
    Row {
        IconButton(onClick = {
            viewModel.showDeleteDialog = true
        }) {
            Icon(
                painter = painterResource(id = R.drawable.delete_icon),
                contentDescription = "DELETE_ICON",
                modifier = Modifier.size(24.dp)
            )
        }
        IconButton(onClick = {
            shareImageToOtherApps(
                context,
                viewModel.selectedFile!!.filename.getUriFromFileName(context)
            )
        }) {
            Icon(
                painter = painterResource(id = R.drawable.share),
                contentDescription = null
            )
        }
        IconButton(onClick = {
            viewModel.showNoteDialog = true
        }) {
            Icon(
                painter = if (viewModel.selectedFile?.note.isNullOrEmpty())
                    painterResource(id = R.drawable.note_add)
                else painterResource(id = R.drawable.edit_icon),
                contentDescription = "NOTE_ICON",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalCoilApi::class)
@Composable
private fun DisplayImage(
    context: Context,
    viewModel: PrescriptionPhotoViewViewModel
) {
    Scaffold(
        topBar = {
            MediumTopAppBar(
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                ),
                title = {
                    Text(
                        text = viewModel.selectedFile?.filename?.substringBefore(".")
                            ?.toLong()
                            ?.let {
                                Date(
                                    it
                                ).toPrescriptionNavDate()
                            } ?: "",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.testTag("HEADING_TAG")
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.isTapped = false
                        viewModel.selectedFile = null
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "BACK_ICON")
                    }
                },
                actions = {
                    NavBarActions(context, viewModel)
                }
            )
        },
        content = {
            Box(
                modifier = Modifier.padding(it),
                contentAlignment = Alignment.BottomCenter
            ) {
                Image(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = MaterialTheme.colorScheme.surface)
                        .clickable(
                            enabled = !viewModel.selectedFile?.note.isNullOrEmpty()
                        ) {
                            viewModel.displayNote = !viewModel.displayNote
                        },
                    painter = rememberImagePainter(
                        viewModel.selectedFile?.filename?.getUriFromFileName(
                            context
                        )
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.Fit
                )
                if (!viewModel.selectedFile?.note.isNullOrBlank()) {
                    AnimatedVisibility(
                        visible = viewModel.displayNote,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Text(
                            text = viewModel.selectedFile!!.note!!,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .alpha(0.5f)
                                .background(color = Color.Black)
                                .padding(16.dp)
                        )
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun PhotoView(viewModel: PrescriptionPhotoViewViewModel) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(viewModel.prescriptionPhotos.size) {
        coroutineScope.launch {
            if (viewModel.prescriptionPhotos.isNotEmpty()) listState.scrollToItem(viewModel.prescriptionPhotos.size - 1)
        }
    }

    LazyColumn(
        state = listState
    ) {
        itemsIndexed(viewModel.prescriptionPhotos) { index, file ->
            val currentDate = Date(file.filename.substringBefore(".").toLong())
            val previousDate =
                viewModel.prescriptionPhotos.getOrNull(index - 1)?.filename?.substringBefore(".")
                    ?.toLong()
                    ?.let { Date(it) }

            val showHeader = when {
                previousDate == null -> true
                !isSameDay(currentDate, previousDate) -> true
                else -> false
            }

            if (showHeader) {
                val headerText = when {
                    isToday(currentDate) -> "Today"
                    isYesterday(currentDate) -> "Yesterday"
                    else -> currentDate.toDayFullMonthYear()
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = headerText,
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier
                                .padding(8.dp)
                        )
                    }
                }
            }

            val uploadFolder = FileManager.createFolder(context)
            val photoFile = File(
                uploadFolder,
                file.filename
            )
            val uri = Uri.fromFile(photoFile)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Box(
                        modifier = Modifier
                            .shadow(
                                elevation = if (viewModel.isLongPressed && viewModel.selectedFile == file)
                                    11.dp else 0.dp,
                                shape = RoundedCornerShape(
                                    topStart = 48f,
                                    topEnd = 48f,
                                    bottomStart = 48f,
                                    bottomEnd = 0f
                                )
                            )
                            .background(
                                shape = RoundedCornerShape(
                                    topStart = 48f,
                                    topEnd = 48f,
                                    bottomStart = 48f,
                                    bottomEnd = 0f
                                ),
                                color = if (viewModel.isLongPressed && viewModel.selectedFile == file)
                                    MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surface
                            )
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onLongPress = {
                                        viewModel.isLongPressed = true
                                        viewModel.selectedFile = file
                                    },
                                    onTap = {
                                        if (viewModel.isLongPressed) {
                                            viewModel.isLongPressed = false
                                            viewModel.selectedFile = null
                                        } else {
                                            viewModel.isTapped = true
                                            viewModel.selectedFile = file
                                        }
                                    }
                                )
                            }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.End,
                            modifier = Modifier.width(250.dp)
                        ) {
                            Image(
                                painter = rememberImagePainter(uri),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(250.dp, 330.dp)
                                    .padding(4.dp)
                                    .clip(RoundedCornerShape(16.dp)),
                                contentScale = ContentScale.Crop
                            )
                            if (!file.note.isNullOrEmpty()) {
                                Text(
                                    text = file.note,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 6.dp, start = 6.dp, end = 6.dp)
                                )
                            }
                            Text(
                                text = Date(
                                    file.filename.substringBefore(".").toLong()
                                ).toPrescriptionTime(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(end = 10.dp, bottom = 6.dp)
                            )
                        }
                    }

                    AnimatedVisibility(viewModel.isLongPressed && viewModel.selectedFile == file) {
                        Icon(
                            painter = painterResource(id = R.drawable.check_circle),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun DeletePhotoDialog(
    dismiss: () -> Unit,
    confirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { dismiss() },
        confirmButton = {
            TextButton(onClick = { confirm() }) {
                Text(text = stringResource(id = R.string.yes_discard))
            }
        },
        dismissButton = {
            TextButton(onClick = { dismiss() }) {
                Text(text = stringResource(id = R.string.no_go_back))
            }
        },
        title = {
            Text(text = stringResource(id = R.string.discard_prescription))
        },
        text = {
            Text(text = stringResource(id = R.string.discard_prescription_description))
        }
    )
}

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun AddNoteDialog(
    image: Uri,
    note: String,
    dismiss: () -> Unit,
    confirm: (String) -> Unit
) {
    var noteValue by remember {
        mutableStateOf(note)
    }
    Dialog(
        onDismissRequest = { dismiss() },
        content = {
            Column(
                modifier = Modifier.background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(8.dp)
                )
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Image(
                            painter = rememberImagePainter(image),
                            contentDescription = null,
                            modifier = Modifier.size(60.dp),
                            contentScale = ContentScale.Crop
                        )
                        OutlinedTextField(
                            value = noteValue,
                            onValueChange = {
                                noteValue = it
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.15f),
                            placeholder = {
                                Text(
                                    text = stringResource(id = R.string.add_notes_here),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                focusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(8.dp),
                            textStyle = MaterialTheme.typography.bodyMedium,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences
                            )
                        )
                    }
                    Button(
                        onClick = { confirm(noteValue.trim()) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = noteValue.trim() != note.trim()
                    ) {
                        Text(text = stringResource(id = R.string.save))
                    }
                }
            }
        }
    )
}
