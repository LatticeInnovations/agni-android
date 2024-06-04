package com.latticeonfhir.android.ui.prescription.photo.view

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toFile
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.ui.common.appointmentsfab.AppointmentsFab
import com.latticeonfhir.android.ui.patientlandingscreen.AllSlotsBookedDialog
import com.latticeonfhir.android.utils.constants.NavControllerConstants
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.isSameDay
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.isToday
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.isYesterday
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toPrescriptionDate
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toPrescriptionNavDate
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toPrescriptionTime
import com.latticeonfhir.android.utils.file.FileManager
import kotlinx.coroutines.launch
import java.io.File
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrescriptionPhotoViewScreen(
    navController: NavController,
    viewModel: PrescriptionPhotoViewViewModel = hiltViewModel()
) {
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
        if (viewModel.selectedImageUri != null) viewModel.selectedImageUri = null
        else if (viewModel.isFabSelected) viewModel.isFabSelected = false
        else navController.popBackStack()
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
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "BACK_ICON")
                    }
                }
            )
        },
        content = {
            Box(modifier = Modifier.padding(it)) {
                PhotoView(viewModel)
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
            modifier = Modifier.padding(16.dp),
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
        visible = viewModel.selectedImageUri != null,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        DisplayImage(viewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalCoilApi::class)
@Composable
private fun DisplayImage(viewModel: PrescriptionPhotoViewViewModel) {
    val context = LocalContext.current
    val shareLauncher = rememberLauncherForActivityResult(CreateDocument("image/*")) { uri ->
        uri?.let {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, viewModel.selectedImageUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Share Image"))
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                ),
                title = {
                    Text(
                        text = viewModel.selectedImageUri?.toFile()?.name?.substringBefore(".")?.toLong()
                            ?.let {
                                Date(
                                    it
                                ).toPrescriptionNavDate()
                            }?:"",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.testTag("HEADING_TAG")
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.selectedImageUri = null }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "BACK_ICON")
                    }
                },
                actions = {
                    IconButton(onClick = { shareLauncher.launch(viewModel.selectedImageUri!!.toFile().name) }) {
                        Icon(painter = painterResource(id = R.drawable.share), contentDescription = null)
                    }
                }
            )
        },
        content = {
            Box(modifier = Modifier.padding(it)) {
                Image(
                    modifier = Modifier.fillMaxSize()
                        .background(color = Color.Black),
                    painter = rememberImagePainter(viewModel.selectedImageUri),
                    contentDescription = null,
                    contentScale = ContentScale.Fit
                )
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
            listState.scrollToItem(0)
        }
    }

    LazyColumn(
        state = listState,
        reverseLayout = true
    ) {
        itemsIndexed(viewModel.prescriptionPhotos) { index, photo ->
            val uploadFolder = FileManager.createFolder(context)
            val photoFile = File(
                uploadFolder,
                photo
            )
            val uri = Uri.fromFile(photoFile)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable {
                        viewModel.selectedImageUri = uri
                    },
                contentAlignment = Alignment.BottomEnd
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(
                                topStart = 48f,
                                topEnd = 48f,
                                bottomStart = 48f,
                                bottomEnd = 0f
                            )
                        )
                ) {
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Image(
                            painter = rememberImagePainter(uri),
                            contentDescription = null,
                            modifier = Modifier
                                .size(250.dp, 330.dp)
                                .padding(4.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.FillBounds
                        )
                        Text(
                            text = Date(
                                photo.substringBefore(".").toLong()
                            ).toPrescriptionTime(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(end = 10.dp, bottom = 6.dp)
                        )
                    }
                }
            }
            val currentDate = Date(photo.substringBefore(".").toLong())
            val previousDate =
                viewModel.prescriptionPhotos.getOrNull(index - 1)?.substringBefore(".")?.toLong()
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
                    else -> currentDate.toPrescriptionDate()
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
        }
    }
}
