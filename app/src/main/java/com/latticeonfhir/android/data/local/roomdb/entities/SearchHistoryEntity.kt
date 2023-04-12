package com.latticeonfhir.android.data.local.roomdb.entities

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
@Keep
data class SearchHistoryEntity(
    val searchQuery: String,
    val date: Date
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
