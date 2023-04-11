package com.latticeonfhir.android.ui.main.patientlandingscreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.latticeonfhir.android.base.viewmodel.BaseViewModel

class SuggestionsScreenViewModel: BaseViewModel() {
    var showConnectDialog by mutableStateOf(false)

    val list = listOf(
        DummyData(
            "Prashant Pandey",
            "M/32",
            "A-45, Rajendra Nagar, Delhi\n" +
                    "+91-99000-88222 · PID 12345"
        ),
        DummyData(
            "Shashank Pandey",
            "M/24",
            "A-45, Rajendar Nagar, Delhi\n" +
                    "+91-99000-88222 · PID 12345"
        ),
        DummyData(
            "Deepak Pandey",
            "M/31",
            "A45, RajendarNagar, Delhi\n" +
                    "+91-99000-88222 · PID 12345"
        ),
        DummyData(
            "Prashant Pandey",
            "M/28",
            "A-45, Rajendr Nagar, Delhi\n" +
                    "+91-99000-88222 · PID 12345"
        )
    )
}

data class DummyData(
    val name: String,
    val age: String,
    val address: String
)