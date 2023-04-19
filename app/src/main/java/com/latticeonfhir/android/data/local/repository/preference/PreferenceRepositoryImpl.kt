package com.latticeonfhir.android.data.local.repository.preference

import com.latticeonfhir.android.data.local.sharedpreferences.PreferenceStorage
import java.util.Date
import javax.inject.Inject

class PreferenceRepositoryImpl @Inject constructor(private val preferenceStorage: PreferenceStorage): PreferenceRepository {

    override fun setLastUpdatedDate(long: Long) {
        preferenceStorage.lastUpdatedTime = long
    }

    override fun getLastUpdatedDate(): Long = preferenceStorage.lastUpdatedTime
}