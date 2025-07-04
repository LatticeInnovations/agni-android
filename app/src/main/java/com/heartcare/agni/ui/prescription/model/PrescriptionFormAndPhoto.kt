package com.heartcare.agni.ui.prescription.model

import java.util.Date

data class PrescriptionFormAndPhoto(
    val date: Date,
    val type: String,
    val prescription: Any
)
