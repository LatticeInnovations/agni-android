package com.latticeonfhir.android.ui.patientregistration.step4

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.latticeonfhir.android.base.viewmodel.BaseViewModel

class ConfirmRelationshipViewModel : BaseViewModel() {
    var openEditDialog by mutableStateOf(false)
    var openDeleteDialog by mutableStateOf(false)

    var editRelation by mutableStateOf("Son")

    var relationsList = listOf<MemberRelation>(
        MemberRelation(
            "Vikran Pandey",
            "father",
            "Alok Pandey"
        ),
        MemberRelation(
            "Vikran Pandey",
            "father",
            "Alok Pandey"
        )
    )
}

data class MemberRelation(
    var patientName: String,
    var relationship: String,
    var relation: String
)