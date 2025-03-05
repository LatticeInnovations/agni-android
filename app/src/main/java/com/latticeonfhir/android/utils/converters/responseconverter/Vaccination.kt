package com.latticeonfhir.android.utils.converters.responseconverter

import com.latticeonfhir.android.data.local.model.vaccination.Immunization
import com.latticeonfhir.android.data.local.roomdb.entities.vaccination.ImmunizationEntity

object Vaccination {

    internal fun Immunization.toImmunizationEntity(): ImmunizationEntity {
        return ImmunizationEntity(
            id = this.id,
            appointmentId = this.appointmentId,
            patientId = this.patientId,
            createdOn = this.takenOn,
            lotNumber = this.lotNumber,
            expiryDate = this.expiryDate,
            manufacturerId = this.manufacturer.id,
            notes = this.notes,
            vaccineCode = this.vaccineCode,
            immunizationFhirId = null
        )
    }
}