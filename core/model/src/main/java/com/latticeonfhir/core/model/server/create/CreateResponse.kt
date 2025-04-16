package com.latticeonfhir.core.model.server.create

import androidx.annotation.Keep
import com.latticeonfhir.core.model.server.create.MedDispenseIdResponse
import com.latticeonfhir.core.model.server.create.MedReqIdResponse

@Keep
data class CreateResponse(
    val status: String,
    val fhirId: String?,
    val id: String?,
    val error: String?,
    val prescription: List<MedReqIdResponse>?,
    val prescriptionFiles: List<DocumentIdResponse>?,
    val medicineDispensedList: List<MedDispenseIdResponse>?,
    val files: List<*>?,
)
