package com.latticeonfhir.android.ui.main

import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.repository.person.PersonRepository
import com.latticeonfhir.android.data.server.repository.sync.SyncRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class MainViewModel @Inject constructor(private val syncRepository: SyncRepository, private val personRepository: PersonRepository) : BaseViewModel() {

    init {
        viewModelScope.launch(Dispatchers.IO) {
            syncRepository.getListPersonData()
        }
    }
}