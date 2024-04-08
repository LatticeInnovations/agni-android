package com.latticeonfhir.android.data.local.enums

enum class UserRoleEnum(val code: String, val display: String) {
    HEALTHCARE_PROVIDER("doctor", "Healthcare provider"),
    FRONT_OFFICE_STAFF("224608005", "Front office staff"),
    DEVICE_ISSUER("224529009", "Device Issuer");

    companion object {
        fun fromCode(code:String) = entries.first { it.code == code }
    }
}