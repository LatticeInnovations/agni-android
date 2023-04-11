package com.latticeonfhir.android.ui.patientregistration.step4

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.enums.RelationEnum

class ConfirmRelationshipViewModel : BaseViewModel() {
    var openEditDialog by mutableStateOf(false)
    var openDeleteDialog by mutableStateOf(false)

    var editRelation by mutableStateOf("Son")

    var relationsList = listOf<MemberRelation>(
        MemberRelation(
            "Vikran Pandey",
            RelationEnum.FATHER.value,
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