package com.latticeonfhir.android.sharedpreferences

import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.latticeonfhir.android.data.local.sharedpreferences.PreferenceStorageImpl
import com.latticeonfhir.android.data.server.model.user.UserResponse
import com.latticeonfhir.android.utils.builders.UUIDBuilder
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class SharedPreferencesTest: TestCase() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var preferenceStorageImpl: PreferenceStorageImpl

    private val userResponse = UserResponse(
        userName = "User Name",
        userEmail = "User Email",
        mobileNumber = 9876543210,
        role = "Doctor"
    )

    private val lastSyncTime = 500L

    @Before
    public override fun setUp() {
        sharedPreferences = EncryptedSharedPreferences.create(
            PreferenceStorageImpl.PREFS_NAME,
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            ApplicationProvider.getApplicationContext(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        preferenceStorageImpl = PreferenceStorageImpl(sharedPreferences)
    }

    @Test
    internal fun setAndGetToken() = runTest {
        val token = "TOKEN123"

        preferenceStorageImpl.token = token

        assertEquals(preferenceStorageImpl.token,token)
    }

    @Test
    internal fun setAndGetUSerName() = runTest {
        preferenceStorageImpl.userName = userResponse.userName
        assertEquals(preferenceStorageImpl.userName,userResponse.userName)
    }

    @Test
    internal fun setAndGetUserRole() = runTest {
        preferenceStorageImpl.userRole = userResponse.role
        assertEquals(preferenceStorageImpl.userRole,userResponse.role)
    }

    @Test
    internal fun setAndGetUserMobile() = runTest {
        preferenceStorageImpl.userMobile = userResponse.mobileNumber!!
        assertEquals(preferenceStorageImpl.userMobile,userResponse.mobileNumber)
    }

    @Test
    internal fun setAndGetUserEmail() = runTest {
        preferenceStorageImpl.userEmail = userResponse.userEmail!!
        assertEquals(preferenceStorageImpl.userEmail,userResponse.userEmail)
    }


    @Test
    internal fun setAndGetRoomDBEncryptionKey() = runTest {
        val roomDbEncryptionKey = UUIDBuilder.generateUUID()
        preferenceStorageImpl.roomDBEncryptionKey = roomDbEncryptionKey
        assertEquals(roomDbEncryptionKey,preferenceStorageImpl.roomDBEncryptionKey)
    }

    @Test
    internal fun setAndGetLastPatientSyncTime() = runTest {
        preferenceStorageImpl.lastPatientSyncTime = lastSyncTime
        assertEquals(preferenceStorageImpl.lastPatientSyncTime,lastSyncTime)
    }


    @Test
    internal fun setAndGetLastRelationSyncTime() = runTest {
        preferenceStorageImpl.lastRelationSyncTime = lastSyncTime
        assertEquals(preferenceStorageImpl.lastRelationSyncTime,lastSyncTime)
    }

    @Test
    internal fun setAndGetLastPrescriptionSyncTime() = runTest {
        preferenceStorageImpl.lastPrescriptionSyncTime = lastSyncTime
        assertEquals(preferenceStorageImpl.lastPrescriptionSyncTime,lastSyncTime)
    }

    @Test
    internal fun setAndGetLastMedicationSyncTime() = runTest {
        preferenceStorageImpl.lastMedicationSyncTime = lastSyncTime
        assertEquals(preferenceStorageImpl.lastMedicationSyncTime,lastSyncTime)
    }

    @Test
    internal fun setAndGetLastMedicineDosageInstructionSyncTime() = runTest {
        preferenceStorageImpl.lastMedicineDosageInstructionSyncTime = lastSyncTime
        assertEquals(preferenceStorageImpl.lastMedicineDosageInstructionSyncTime,lastSyncTime)
    }

    @Test
    internal fun resetAuthToken() = runTest {
        preferenceStorageImpl.token = ""
        assertEquals(true,preferenceStorageImpl.token.isBlank())
    }

    @Test
    internal fun clearSharedPreferences() = runTest {
        preferenceStorageImpl.userName = userResponse.userName
        assertEquals(userResponse.userName,preferenceStorageImpl.userName)
        preferenceStorageImpl.clear()
        assertEquals(true,preferenceStorageImpl.userName.isBlank())
    }
}