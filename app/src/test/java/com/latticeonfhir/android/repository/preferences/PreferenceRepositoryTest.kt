package com.latticeonfhir.android.repository.preferences

import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepositoryImpl
import com.latticeonfhir.android.data.local.sharedpreferences.PreferenceStorage
import com.latticeonfhir.android.utils.builders.UUIDBuilder
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class PreferenceRepositoryTest: TestCase() {

    @Mock
    private lateinit var preferenceStorage: PreferenceStorage
    private lateinit var preferenceRepositoryImpl: PreferenceRepositoryImpl

    private val token = "TOKEN"
    private val userName = "USERNAME"
    private val userRole = "ROLE"
    private val userMobile = 987L
    private val userEmail = "EMAIL"
    private val lastSyncTime = 0L
    private val roomEncryptionKey = UUIDBuilder.generateUUID()


    @Before
    public override fun setUp() {
        MockitoAnnotations.openMocks(this)
        preferenceRepositoryImpl = PreferenceRepositoryImpl(preferenceStorage)
    }

    @Test
    internal fun setAndGetLastSyncPatient() = runTest {
        `when`(preferenceStorage.lastPatientSyncTime).thenReturn(lastSyncTime)
        preferenceRepositoryImpl.setLastSyncPatient(lastSyncTime)
        assertEquals(lastSyncTime,preferenceRepositoryImpl.getLastSyncPatient())
    }

    @Test
    internal fun setAndGetLastSyncRelation() = runTest {
        `when`(preferenceStorage.lastRelationSyncTime).thenReturn(lastSyncTime)
        preferenceRepositoryImpl.setLastSyncRelation(lastSyncTime)
        assertEquals(lastSyncTime,preferenceRepositoryImpl.getLastSyncRelation())
    }

    @Test
    internal fun setAndGetLastSyncPrescription() = runTest {
        `when`(preferenceStorage.lastPrescriptionSyncTime).thenReturn(lastSyncTime)
        preferenceRepositoryImpl.setLastSyncPrescription(lastSyncTime)
        assertEquals(lastSyncTime,preferenceRepositoryImpl.getLastSyncPrescription())
    }

    @Test
    internal fun setAndGetLastSyncMedication() = runTest {
        `when`(preferenceStorage.lastMedicationSyncTime).thenReturn(lastSyncTime)
        preferenceRepositoryImpl.setLastMedicationSyncDate(lastSyncTime)
        assertEquals(lastSyncTime,preferenceRepositoryImpl.getLastMedicationSyncDate())
    }

    @Test
    internal fun setAndGetLastSyncMedicationTiming() = runTest {
        `when`(preferenceStorage.lastMedicineDosageInstructionSyncTime).thenReturn(lastSyncTime)
        preferenceRepositoryImpl.setLastMedicineDosageInstructionSyncDate(lastSyncTime)
        assertEquals(lastSyncTime,preferenceRepositoryImpl.getLastMedicineDosageInstructionSyncDate())
    }

    @Test
    internal fun setAndGetUserName() = runTest {
        `when`(preferenceStorage.userName).thenReturn(userName)
        preferenceRepositoryImpl.setUserName(userName)
        assertEquals(userName,preferenceRepositoryImpl.getUserName())
    }

    @Test
    internal fun setAndGetUserRole() = runTest {
        `when`(preferenceStorage.userRole).thenReturn(userRole)
        preferenceRepositoryImpl.setUserRole(userRole)
        assertEquals(userRole,preferenceRepositoryImpl.getUserRole())
    }

    @Test
    internal fun setAndGetUserMobile() = runTest {
        `when`(preferenceStorage.userMobile).thenReturn(userMobile)
        preferenceRepositoryImpl.setUserMobile(userMobile)
        assertEquals(userMobile,preferenceRepositoryImpl.getUserMobile())
    }

    @Test
    internal fun setAndGetUserEmail() = runTest {
        `when`(preferenceStorage.userEmail).thenReturn(userEmail)
        preferenceRepositoryImpl.setUserEmail(userEmail)
        assertEquals(userEmail,preferenceRepositoryImpl.getUserEmail())
    }

    @Test
    internal fun setAndGetToken() = runTest {
        `when`(preferenceStorage.token).thenReturn(token)
        preferenceRepositoryImpl.setAuthenticationToken(token)
        assertEquals(token,preferenceRepositoryImpl.getAuthenticationToken())
    }

    @Test
    internal fun setAndGetRoomDBEncryptionKey() = runTest {
        `when`(preferenceStorage.roomDBEncryptionKey).thenReturn(roomEncryptionKey)
        preferenceRepositoryImpl.setRoomDBEncryptionKey(roomEncryptionKey)
        assertEquals(roomEncryptionKey,preferenceRepositoryImpl.getRoomDBEncryptionKey())
    }

    @Test
    internal fun resetAuthToken() = runTest {
        `when`(preferenceStorage.token).thenReturn("")
        preferenceRepositoryImpl.resetAuthenticationToken()
        assertEquals(true,preferenceRepositoryImpl.getAuthenticationToken().isBlank())
    }

    @Test
    internal fun clearPreferences() = runTest {
        `when`(preferenceStorage.userRole).thenReturn("")
        `when`(preferenceStorage.lastMedicationSyncTime).thenReturn(0L)

        preferenceRepositoryImpl.clearPreferences()

        assertEquals(true,preferenceRepositoryImpl.getUserRole().isBlank())
        assertEquals(0L,preferenceRepositoryImpl.getLastMedicationSyncDate())
    }
}