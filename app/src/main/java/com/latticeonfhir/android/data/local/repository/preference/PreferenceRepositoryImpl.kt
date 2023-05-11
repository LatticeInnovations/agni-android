package com.latticeonfhir.android.data.local.repository.preference

import com.latticeonfhir.android.data.local.sharedpreferences.PreferenceStorage
import javax.inject.Inject

class PreferenceRepositoryImpl @Inject constructor(private val preferenceStorage: PreferenceStorage): PreferenceRepository {

    override fun setLastUpdatedDate(long: Long) {
        preferenceStorage.lastUpdatedTime = long
    }

    override fun getLastUpdatedDate(): Long = preferenceStorage.lastUpdatedTime

    override fun setUserName(userName: String) {
        preferenceStorage.userName = userName
    }

    override fun getUserName() = preferenceStorage.userName

    override fun setUserRole(userRole: String) {
        preferenceStorage.userRole = userRole
    }

    override fun getUserRole() = preferenceStorage.userRole

    override fun setUserMobile(userMobile: Long) {
        preferenceStorage.userMobile = userMobile
    }

    override fun getUserMobile() = preferenceStorage.userMobile

    override fun setUserEmail(userEmail: String) {
        preferenceStorage.userEmail = userEmail
    }

    override fun getUserEmail() = preferenceStorage.userEmail

    override fun setAuthenticationToken(authToken: String) {
        preferenceStorage.token = authToken
    }

    override fun getAuthenticationToken() = preferenceStorage.token

    override fun clearPreferences() {
        preferenceStorage.clear()
    }
}