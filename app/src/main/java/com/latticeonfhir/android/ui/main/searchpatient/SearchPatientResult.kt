package com.latticeonfhir.android.ui.main.searchpatient

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import com.latticeonfhir.android.ui.main.common.PatientItemCard

@Composable
fun SearchPatientResult(searchPatientViewModel: SearchPatientViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .testTag("search result list")
    ) {
        PatientItemCard(
            name = "Chetan A S Ramanathan",
            patientId = "M/51 · PID 23456",
            metaData = "Referred: 12 Jan"
        )
        PatientItemCard(
            name = "Pilavullakandi Thekkeparambimnsahgfh",
            patientId = "F/58 · PID 11111",
            metaData = "Seen on 18 May 2022"
        )
        PatientItemCard(
            name = "Pilavullakandi Thekkeparambimnsahgfh",
            patientId = "F/58 · PID 11111",
            metaData = "Referred: 12 Jan"
        )
        PatientItemCard(
            name = "Pilavullakandi Thekkeparambimnsahgfh",
            patientId = "F/58 · PID 11111",
            metaData = "Last seen: 23 Jan"
        )
        PatientItemCard(
            name = "Gauri Sharma",
            patientId = "F/44 · PID 12345",
            metaData = "Last seen: 23 Jan"
        )
        PatientItemCard(
            name = "Ramesh Seksaria",
            patientId = "F/44 · PID 12345",
            metaData = "Last seen: 23 Jan"
        )
        PatientItemCard(
            name = "Ramesh Seksaria",
            patientId = "F/44 · PID 12345",
            metaData = "Last seen: 23 Jan"
        )
        PatientItemCard(
            name = "Ramesh Seksaria",
            patientId = "F/44 · PID 12345",
            metaData = "Last seen: 23 Jan"
        )
        PatientItemCard(
            name = "Ramesh Seksaria",
            patientId = "F/44 · PID 12345",
            metaData = "Last seen: 23 Jan"
        )
    }
}