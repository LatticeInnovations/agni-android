package com.latticeonfhir.android.utils.converters.responseconverter

object NameConverter {

    internal fun getFullName(firstName: String?, middleName: String?, lastName: String?): String{
        return firstName +
                if (middleName.isNullOrEmpty()) "" else {
                    " $middleName"
                } +
                if (lastName.isNullOrEmpty()) "" else {
                    " $lastName"
                }
    }

}