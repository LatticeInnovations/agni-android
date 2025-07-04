package com.heartcare.agni.data.local.model.vaccination

import androidx.annotation.Keep
import com.heartcare.agni.data.local.roomdb.entities.vaccination.ManufacturerEntity
import java.util.Date

@Keep
data class Immunization(
    val id: String,
    val vaccineName: String,
    val vaccineSortName: String,
    val vaccineCode: String,
    val lotNumber: String,
    val takenOn: Date,
    val expiryDate: Date,
    val manufacturer: ManufacturerEntity?,
    val notes: String?,
    val filename: List<String>?,
    val patientId: String,
    val appointmentId: String
)
