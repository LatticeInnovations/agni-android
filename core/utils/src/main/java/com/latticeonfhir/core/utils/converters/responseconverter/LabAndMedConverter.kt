package com.latticeonfhir.core.utils.converters.responseconverter

import com.latticeonfhir.core.model.server.prescription.photo.File
import com.latticeonfhir.core.utils.constants.LabTestAndMedConstants
import com.latticeonfhir.core.utils.constants.LabTestAndMedConstants.DOC_ID
import java.util.Date

object LabAndMedConverter {

    internal fun createGenericMap(
        dynamicKey: String,
        dynamicKeyValue: String,
        appointmentId: String,
        patientId: String,
        createdOn: Date,
        fileList: List<Map<String, String>>
    ): Map<String, Any> {

        return mapOf(
            dynamicKey to dynamicKeyValue,
            LabTestAndMedConstants.APPOINTMENT_ID to appointmentId,
            LabTestAndMedConstants.PATIENT_ID to patientId,
            LabTestAndMedConstants.CREATED_ON to createdOn,
            LabTestAndMedConstants.FILES to fileList
        )
    }

    internal fun patchGenericMap(
        dynamicKeyValue: String,
        files: List<File>
    ): Map<String, Any> {


        return mapOf(
            DOC_ID to dynamicKeyValue,
            LabTestAndMedConstants.FILENAME to files[0].filename,
            LabTestAndMedConstants.NOTE to files[0].note
        )
    }

}