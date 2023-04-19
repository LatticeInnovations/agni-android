package com.latticeonfhir.android.data.local.repository.preference

import java.util.Date

interface PreferenceRepository {

    fun setLastUpdatedDate(long: Long)
    fun getLastUpdatedDate(): Long
}