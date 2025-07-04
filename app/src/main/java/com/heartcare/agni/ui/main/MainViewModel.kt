package com.heartcare.agni.ui.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.repository.preference.PreferenceRepository
import com.heartcare.agni.navigation.Screen
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