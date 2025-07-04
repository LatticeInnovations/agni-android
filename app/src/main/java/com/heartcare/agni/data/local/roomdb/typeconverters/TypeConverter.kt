package com.heartcare.agni.data.local.roomdb.typeconverters

import androidx.room.TypeConverter
import com.heartcare.agni.data.local.enums.RelationEnum
import java.util.Date

class TypeConverter {

    @TypeConverter
    internal fun relationEnumToInt(relationValue: String): Int =
        RelationEnum.fromString(relationValue).number

    @TypeConverter
    internal fun intToRelationEnum(int: Int): String = RelationEnum.fromInt(int).value

    @TypeConverter
    internal fun dateToLong(date: Date?): Long? = date?.time

    @TypeConverter
    internal fun longToDate(long: Long?): Date? = long?.let { Date(it) }
}