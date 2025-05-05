package com.latticeonfhir.features.vaccination.ui.view

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.net.toFile
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.latticeonfhir.core.data.local.model.vaccination.ImmunizationRecommendation
import com.latticeonfhir.core.model.server.patient.PatientResponse
import com.latticeonfhir.core.theme.TakenContainer
import com.latticeonfhir.core.theme.TakenContainerDark
import com.latticeonfhir.core.theme.TakenLabel
import com.latticeonfhir.core.theme.TakenLabelDark
import com.latticeonfhir.core.utils.constants.NavControllerConstants.PATIENT
import com.latticeonfhir.core.utils.constants.NavControllerConstants.VACCINE
import com.latticeonfhir.core.utils.converters.TimeConverter.convertStringToDate
import com.latticeonfhir.core.utils.converters.TimeConverter.daysBetween
import com.latticeonfhir.core.utils.converters.TimeConverter.toFileDateAndTimeName
import com.latticeonfhir.core.utils.converters.TimeConverter.toPrescriptionDate
import com.latticeonfhir.core.utils.converters.TimeConverter.toPrescriptionNavDate
import com.latticeonfhir.core.utils.converters.TimeConverter.toddMMYYYYString
import com.latticeonfhir.core.utils.file.FileManager.getUriFromFileName
import com.latticeonfhir.features.vaccination.R
import com.latticeonfhir.features.vaccination.utils.VaccinesUtils.formatBytes
import com.latticeonfhir.features.vaccination.utils.VaccinesUtils.getNumberWithOrdinalIndicator
import com.latticeonfhir.features.vaccination.utils.VaccinesUtils.numberOfWeeksToLabel
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewVaccinationScreen(
    navController: NavController,
    viewModel: ViewVaccinationViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            viewModel.patient =
                navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(PATIENT)!!
            viewModel.immunizationRecommendation =
                navController.previousBackStackEntry?.savedStateHandle?.get<ImmunizationRecommendation>(
                    VACCINE
                )!!
            viewModel.getImmunizationByTime(createdOn = viewModel.immunizationRecommendation!!.takenOn!!)
            viewModel.isLaunched = true
        }
    }
    BackHandler {
        if (viewModel.selectedUri != null) viewModel.selectedUri = null
        else navController.navigateUp()
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
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
                    Text(stringResource(R.string.view_vaccination))
                }
            )
        },
        content = { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                viewModel.immunization?.let { immunization ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = if (isSystemInDarkTheme()) Color.Black else Color.White)
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(topEnd = 12.dp, topStart = 12.dp),
                                    color = if (isSystemInDarkTheme()) TakenContainerDark else TakenContainer
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
                                            text = stringResource(
                                                R.string.taken_on_date,
                                                viewModel.immunizationRecommendation!!.takenOn!!.toPrescriptionDate()
                                            ),
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = if (isSystemInDarkTheme()) TakenLabelDark else TakenLabel
                                        )
                                    }
                                }
                                Row(
                                    modifier = Modifier.padding(
                                        horizontal = 16.dp,
                                        vertical = 24.dp
                                    ),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier.background(
                                            color = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                                5.dp
                                            ),
                                            shape = CircleShape
                                        ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.syringe),
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier
                                                .padding(8.dp)
                                                .size(18.dp)
                                        )
                                    }
                                    Text(
                                        text = stringResource(
                                            R.string.vaccine_view_name_info,
                                            viewModel.immunizationRecommendation!!.name.replaceFirstChar { it.titlecase() },
                                            viewModel.immunizationRecommendation!!.shortName.replaceFirstChar { it.titlecase() },
                                            viewModel.immunizationRecommendation!!.doseNumber.getNumberWithOrdinalIndicator(),
                                            numberOfWeeksToLabel(
                                                daysBetween(
                                                    viewModel.patient!!.birthDate.convertStringToDate(),
                                                    viewModel.immunizationRecommendation!!.vaccineStartDate
                                                ) / 7
                                            )
                                        ),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Column(
                                    modifier = Modifier.padding(
                                        start = 16.dp,
                                        end = 16.dp,
                                        bottom = 16.dp
                                    ),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    LabelAndDetail(
                                        stringResource(R.string.lot_no_label),
                                        immunization.lotNumber
                                    )
                                    LabelAndDetail(
                                        stringResource(R.string.date_of_expiry_label),
                                        immunization.expiryDate.toddMMYYYYString()
                                    )
                                    immunization.manufacturer?.let {
                                        LabelAndDetail(
                                            stringResource(R.string.vaccine_manufacturer_label),
                                            it.name
                                        )
                                    }
                                    immunization.notes?.let {
                                        LabelAndDetail(
                                            stringResource(
                                                R.string.notes_colon
                                            ), it
                                        )
                                    }
                                }
                            }
                        }
                        if (!immunization.filename.isNullOrEmpty()) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.uploaded_certificates),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                immunization.filename!!.forEach { file ->
                                    val uri = file.getUriFromFileName(context)
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                viewModel.selectedUri = uri
                                            },
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier.background(
                                                color = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                                    11.dp
                                                ),
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
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = Date(
                                                    uri.toFile().name.substringBefore(".").toLong()
                                                ).toFileDateAndTimeName(),
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = uri.toFile().readBytes().size.formatBytes(),
                                                style = MaterialTheme.typography.bodyMedium,
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
        }
    )
    AnimatedVisibility(
        visible = viewModel.selectedUri != null,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        DisplayImage(viewModel)
    }
}

@Composable
private fun LabelAndDetail(label: String, detail: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = detail,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DisplayImage(
    viewModel: ViewVaccinationViewModel
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    val imageSize = remember { mutableStateOf(IntSize.Zero) }
    if (viewModel.selectedUri != null) {
        Scaffold(
            topBar = {
                MediumTopAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                    ),
                    title = {
                        Text(
                            text = Date(viewModel.selectedUri!!.toFile().name.substringBefore(".").toLong()).toPrescriptionNavDate(),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.testTag("HEADING_TAG")
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            viewModel.selectedUri = null
                        }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "BACK_ICON"
                            )
                        }
                    }
                )
            },
            content = { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(2f)
                        .padding(paddingValues),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Image(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = MaterialTheme.colorScheme.surface)
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                translationX = offsetX,
                                translationY = offsetY
                            )
                            .pointerInput(Unit) {
                                detectTransformGestures { _, pan, zoom, _ ->
                                    val newScale = scale * zoom
                                    if (newScale in 1f..8f) {
                                        scale = newScale

                                        // calculate maximum allowable translations
                                        val maxOffsetX = (imageSize.value.width * (scale - 1)) / 2
                                        val maxOffsetY = (imageSize.value.height * (scale - 1)) / 2

                                        // update offsets within bounds
                                        offsetX =
                                            (offsetX + pan.x).coerceIn(-maxOffsetX, maxOffsetX)
                                        offsetY =
                                            (offsetY + pan.y).coerceIn(-maxOffsetY, maxOffsetY)
                                    }
                                }
                            }
                            .onSizeChanged {
                                imageSize.value = it
                            },
                        painter = rememberAsyncImagePainter(
                            viewModel.selectedUri
                        ),
                        contentDescription = null,
                        contentScale = ContentScale.Fit
                    )
                }
            }
        )
    }
}
