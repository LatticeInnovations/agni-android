package com.latticeonfhir.android.data.local.roomdb.typeconverters

import androidx.room.TypeConverter
import com.latticeonfhir.android.data.local.enums.GenderEnum
import com.latticeonfhir.android.data.local.enums.RelationEnum

class TypeConverter {

    @TypeConverter
    fun genderEnumToInt(genderEnum: GenderEnum): Int = genderEnum.number
    @TypeConverter
    fun intToGenderEnum(int: Int): GenderEnum = GenderEnum.fromInt(int)

    @TypeConverter
    fun relationEnumToInt(relationEnum: RelationEnum): Int = relationEnum.number
    @TypeConverter
    fun intToRelationEnum(int: Int): RelationEnum = RelationEnum.fromInt(int)
}