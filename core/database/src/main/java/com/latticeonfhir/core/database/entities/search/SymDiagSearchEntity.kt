package com.latticeonfhir.core.database.entities.search

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.latticeonfhir.android.data.local.enums.SearchTypeEnum
import java.util.Date


@Entity
@Keep
data class SymDiagSearchEntity(
    val searchQuery: String,
    val date: Date,
    val searchCount: Long,
    val searchType: SearchTypeEnum
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}