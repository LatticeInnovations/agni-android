package com.heartcare.agni.utils.converters.responseconverter

import com.heartcare.agni.data.local.model.vaccination.Immunization
import com.heartcare.agni.data.local.roomdb.entities.vaccination.ImmunizationEntity
import com.heartcare.agni.data.local.roomdb.entities.vaccination.ImmunizationFileEntity
import com.heartcare.agni.data.local.roomdb.entities.vaccination.ImmunizationRecommendationEntity
import com.heartcare.agni.data.local.roomdb.entities.vaccination.ManufacturerEntity
import com.heartcare.agni.data.server.model.vaccination.ImmunizationFile
import com.heartcare.agni.data.server.model.vaccination.ImmunizationRecommendationResponse
import com.heartcare.agni.data.server.model.vaccination.ImmunizationResponse
import com.heartcare.agni.data.server.model.vaccination.ManufacturerResponse

object Vaccination {

    internal fun Immunization.toImmunizationEntity(): ImmunizationEntity {
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

    internal fun ImmunizationRecommendationResponse.toImmunizationRecommendationEntity(patientId: String): ImmunizationRecommendationEntity {
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

    internal fun ImmunizationResponse.toImmunizationFileEntity(): List<ImmunizationFileEntity>? {
        return this.immunizationFiles?.map { immunizationFile ->
            ImmunizationFileEntity(
                filename = immunizationFile.filename,
                immunizationId = this.immunizationUuid
            )
        }
    }

    internal fun Immunization.toImmunizationResponse(): ImmunizationResponse {
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

    internal fun ManufacturerResponse.toManufacturerEntity(): ManufacturerEntity {
        return ManufacturerEntity(
            id = this.manufacturerId,
            name = this.manufacturerName,
            type = this.orgType,
            active = this.active
        )
    }
}