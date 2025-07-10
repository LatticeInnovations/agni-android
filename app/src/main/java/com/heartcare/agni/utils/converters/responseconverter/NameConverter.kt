package com.heartcare.agni.utils.converters.responseconverter

object NameConverter {

    internal fun getFullName(firstName: String?, lastName: String?): String {
        return firstName +
                if (lastName.isNullOrEmpty()) "" else {
                    " $lastName"
                }
    }

}