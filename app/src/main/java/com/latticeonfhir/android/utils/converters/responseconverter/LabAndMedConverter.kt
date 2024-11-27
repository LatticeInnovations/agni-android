package com.latticeonfhir.android.utils.converters.responseconverter

import com.latticeonfhir.android.data.server.model.prescription.photo.File
import com.latticeonfhir.android.utils.constants.LabTestAndMedConstants

object LabAndMedConverter {

    internal fun createGenericMap(
        dynamicKey: String,
        dynamicKeyValue: String,
        appointmentId: String,
        patientId: String,
        createdOn: String,
        files: List<File>
    ): Map<String, Any> {

        val fileList = files.map { file ->
            mapOf(
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
        dynamicKey: String,
        dynamicKeyValue: String,
        files: List<File>
    ): Map<String, Any> {

        val fileList = files.map { file ->
            mapOf(
                LabTestAndMedConstants.FILENAME to file.filename,
                LabTestAndMedConstants.NOTE to file.note
            )
        }
        return mapOf(
            dynamicKey to dynamicKeyValue,
            LabTestAndMedConstants.FILES to fileList
        )
    }

}