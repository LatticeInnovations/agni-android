package com.latticeonfhir.android.ui.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.latticeonfhir.core.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    preferenceRepository: PreferenceRepository
) : BaseViewModel() {

    var isUserLoggedIn by mutableStateOf(false)
    var startDestination by mutableStateOf(Screen.PhoneEmailScreen.route)

    init {
        isUserLoggedIn = preferenceRepository.getAuthenticationToken().isNotEmpty()
        if (isUserLoggedIn) startDestination = Screen.LandingScreen.route
    }
}