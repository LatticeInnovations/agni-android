package com.latticeonfhir.core.data.local.roomdb.entities.search

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.latticeonfhir.core.data.local.enums.SearchTypeEnum
import java.util.Date

@Entity
@Keep
data class SearchHistoryEntity(
    val searchQuery: String,
    val date: Date,
    val searchType: SearchTypeEnum
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
