package com.latticeonfhir.core.utils.converters.responseconverter

import com.latticeonfhir.android.data.local.model.vaccination.Immunization
import com.latticeonfhir.core.database.entities.vaccination.ImmunizationEntity
import com.latticeonfhir.core.database.entities.vaccination.ImmunizationFileEntity
import com.latticeonfhir.core.database.entities.vaccination.ImmunizationRecommendationEntity
import com.latticeonfhir.core.database.entities.vaccination.ManufacturerEntity
import com.latticeonfhir.core.model.server.vaccination.ImmunizationFile
import com.latticeonfhir.core.model.server.vaccination.ImmunizationRecommendationResponse
import com.latticeonfhir.core.model.server.vaccination.ImmunizationResponse
import com.latticeonfhir.core.model.server.vaccination.ManufacturerResponse

object Vaccination {

    fun Immunization.toImmunizationEntity(): ImmunizationEntity {
        return ImmunizationEntity(
            id = this.id,
            appointmentId = this.appointmentId,
            patientId = this.patientId,
            createdOn = this.takenOn,
            lotNumber = this.lotNumber,
            expiryDate = this.expiryDate,
            manufacturerId = this.manufacturer?.id,
            notes = this.notes,
            vaccineCode = this.vaccineCode,
            immunizationFhirId = null
        )
    }

    fun ImmunizationRecommendationResponse.toImmunizationRecommendationEntity(patientId: String): ImmunizationRecommendationEntity {
        return ImmunizationRecommendationEntity(
            id = "${patientId}-${vaccineCode}-${doseNumber}",
            patientId = patientId,
            vaccine = vaccine,
            vaccineShortName = vaccineText,
            vaccineCode = vaccineCode,
            seriesDoses = seriesDoses,
            doseNumber = doseNumber,
            vaccineStartDate = vaccineStartDate,
            vaccineEndDate = vaccineEndDate,
            vaccineBufferDate = vaccineBufferDate,
            vaccineDueDate = vaccineDueDate
        )
    }

    internal fun ImmunizationResponse.toImmunizationEntity(patientId: String, appointmentId: String): ImmunizationEntity {
        return ImmunizationEntity(
            id = this.immunizationUuid,
            appointmentId = appointmentId,
            patientId = patientId,
            createdOn = this.createdOn,
            lotNumber = this.lotNumber,
            expiryDate = this.expiryDate,
            manufacturerId = this.manufacturerId,
            notes = this.notes,
            vaccineCode = this.vaccineCode,
            immunizationFhirId = this.immunizationId
        )
    }

    fun ImmunizationResponse.toImmunizationFileEntity(): List<ImmunizationFileEntity>? {
        return this.immunizationFiles?.map { immunizationFile ->
            ImmunizationFileEntity(
                filename = immunizationFile.filename,
                immunizationId = this.immunizationUuid
            )
        }
    }

    fun Immunization.toImmunizationResponse(): ImmunizationResponse {
        return ImmunizationResponse(
            appointmentId = appointmentId,
            createdOn = takenOn,
            expiryDate = expiryDate,
            immunizationFiles = filename?.map { ImmunizationFile(it) },
            immunizationId = null,
            immunizationUuid = id,
            lotNumber = lotNumber,
            manufacturerId = manufacturer?.id,
            notes = notes,
            patientId = patientId,
            vaccineCode = vaccineCode
        )
    }

    fun ManufacturerResponse.toManufacturerEntity(): ManufacturerEntity {
        return ManufacturerEntity(
            id = this.manufacturerId,
            name = this.manufacturerName,
            type = this.orgType,
            active = this.active
        )
    }
}