package com.latticeonfhir.android.utils.converters.responseconverter

import com.latticeonfhir.android.data.server.model.prescription.photo.File
import com.latticeonfhir.android.utils.constants.LabTestAndMedConstants
import com.latticeonfhir.android.utils.constants.LabTestAndMedConstants.DOC_ID
import java.util.Date

object LabAndMedConverter {

    internal fun createGenericMap(
        dynamicKey: String,
        dynamicKeyValue: String,
        appointmentId: String,
        patientId: String,
        createdOn: Date,
        docUuid: String,
        docIdKey: String,
        files: List<File>
    ): Map<String, Any> {

        val fileList = files.map { file ->
            mapOf(
                docIdKey to docUuid,
                LabTestAndMedConstants.FILENAME to file.filename,
                LabTestAndMedConstants.NOTE to file.note
            )
        }

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