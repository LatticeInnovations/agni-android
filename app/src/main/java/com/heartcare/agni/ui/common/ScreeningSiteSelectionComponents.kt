package com.heartcare.agni.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.heartcare.agni.R
import com.heartcare.agni.data.local.enums.RecordType
import com.heartcare.agni.ui.theme.Black
import com.heartcare.agni.ui.theme.White
import com.heartcare.agni.utils.constants.ScreenSiteConstants.SITE_LIST

@Composable
fun SelectableRadioCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String? = null,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor =  MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selected,
                onClick = onClick
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun RecordTypeSelectionContent(
    modifier: Modifier = Modifier,
    selectedType: RecordType?,
    onTypeSelected: (RecordType) -> Unit,
    onContinueClick: () -> Unit
) {
    Scaffold(modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface,
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .background(
                        color = if (isSystemInDarkTheme()) Black else White
                    )
            ) {
                Button(
                    onClick = onContinueClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    enabled = selectedType != null
                ) {
                    Text(stringResource(R.string.continue_text))
                }
            }
        }) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(it)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.record_type_title),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            SelectableRadioCard(
                title = stringResource(R.string.facility),
                subtitle = stringResource(R.string.standard_clinic_record_not_linked_to_any_campaign),
                selected = selectedType == RecordType.FACILITY,
                onClick = { onTypeSelected(RecordType.FACILITY) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            SelectableRadioCard(
                title = stringResource(R.string.screening_site),
                subtitle = stringResource(R.string.tag_this_record_to_an_active_screening_campaign),
                selected = selectedType == RecordType.SCREENING_SITE,
                onClick = { onTypeSelected(RecordType.SCREENING_SITE) }
            )

            Spacer(modifier = Modifier.weight(1f))

        }
    }

}


@Composable
fun ScreeningSiteListContent(
    modifier: Modifier = Modifier,
    sites: List<String>,
    selectedSite: String?,
    onSiteSelected: (String) -> Unit,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    Scaffold(modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface,
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .background(
                        color = if (isSystemInDarkTheme()) Black else White
                    )
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)

                ) {
                    OutlinedButton(
                        onClick = onBackClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.back))
                    }
                    Button(
                        onClick = onContinueClick,
                        modifier = Modifier.weight(1f),
                        enabled = selectedSite != null
                    ) {
                        Text(stringResource(R.string.continue_text))
                    }
                }
            }
        }) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(it)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.screening_site_title),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sites) { site ->
                    SelectableRadioCard(
                        title = site,
                        selected = selectedSite == site,
                        onClick = { onSiteSelected(site) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RecordTypeSelectionPreview() {
    MaterialTheme {
        Surface {
            var selectedType by remember { mutableStateOf<RecordType?>(RecordType.FACILITY) }
            RecordTypeSelectionContent(
                modifier = Modifier.height(400.dp),
                selectedType = selectedType,
                onTypeSelected = { selectedType = it },
                onContinueClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ScreeningSiteListPreview() {
    MaterialTheme {
        Surface {
            Column(modifier = Modifier.fillMaxSize()) {
                var selectedSite by remember { mutableStateOf<String?>("Shefa Vaccine Drive") }
                ScreeningSiteListContent(
                    modifier = Modifier.height(500.dp),
                    sites = SITE_LIST,
                    selectedSite = selectedSite,
                    onSiteSelected = { selectedSite = it },
                    onBackClick = {},
                    onContinueClick = {}
                )
            }
        }
    }
}
