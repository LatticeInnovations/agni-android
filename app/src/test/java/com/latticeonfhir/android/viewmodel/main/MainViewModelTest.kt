package com.latticeonfhir.android.viewmodel.main

import com.latticeonfhir.android.FhirApp
import com.latticeonfhir.android.base.BaseClass
import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.data.server.repository.sync.SyncRepository
import com.latticeonfhir.android.navigation.Screen
import com.latticeonfhir.android.ui.main.MainViewModel
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@RunWith(JUnit4::class)
class MainViewModelTest: BaseClass() {

    @Mock
    private lateinit var preferenceRepository: PreferenceRepository
    @Mock
    private lateinit var syncRepository: SyncRepository

    private lateinit var mainViewModel: MainViewModel

    @Before
    public override fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `user already logged in`() {
        `when`(preferenceRepository.getAuthenticationToken()).thenReturn("AUTH_TOKEN")
        mainViewModel = MainViewModel(syncRepository, preferenceRepository)
        assertEquals(true,mainViewModel.isUserLoggedIn)
    }

    @Test
    fun `user not logged in`() {
        `when`(preferenceRepository.getAuthenticationToken()).thenReturn("")
        mainViewModel = MainViewModel(syncRepository, preferenceRepository)
        assertEquals(false,mainViewModel.isUserLoggedIn)
    }

    @Test
    fun `sync repo initialized`() {
        `when`(preferenceRepository.getAuthenticationToken()).thenReturn("")
        mainViewModel = MainViewModel(syncRepository, preferenceRepository)
        assertEquals(FhirApp.syncRepository,syncRepository)
    }

    @Test
    fun `start destination assigned when logged in`() {
        `when`(preferenceRepository.getAuthenticationToken()).thenReturn("AUTH_TOKEN")
        mainViewModel = MainViewModel(syncRepository, preferenceRepository)
        assertEquals(Screen.LandingScreen.route,mainViewModel.startDestination)
    }

    @Test
    fun `start destination assigned when not logged in`() {
        `when`(preferenceRepository.getAuthenticationToken()).thenReturn("")
        mainViewModel = MainViewModel(syncRepository, preferenceRepository)
        assertEquals(Screen.PhoneEmailScreen.route,mainViewModel.startDestination)
    }
}