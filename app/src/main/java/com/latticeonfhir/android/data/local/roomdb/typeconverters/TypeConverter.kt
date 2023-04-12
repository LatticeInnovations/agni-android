package com.latticeonfhir.android.data.local.roomdb.typeconverters

import androidx.room.TypeConverter
import com.latticeonfhir.android.data.local.enums.GenderEnum
import com.latticeonfhir.android.data.local.enums.RelationEnum
import java.util.Date

class TypeConverter {

    @TypeConverter
    internal fun genderEnumToInt(genderEnum: GenderEnum): Int = genderEnum.number
    @TypeConverter
    internal fun intToGenderEnum(int: Int): GenderEnum = GenderEnum.fromInt(int)

    @TypeConverter
    internal fun relationEnumToInt(relationEnum: RelationEnum): Int = relationEnum.number
    @TypeConverter
    internal fun intToRelationEnum(int: Int): RelationEnum = RelationEnum.fromInt(int)

    @TypeConverter
    internal fun dateToLong(date: Date): Long = date.time
    @TypeConverter
    internal fun longToDate(long: Long): Date = Date(long)
}