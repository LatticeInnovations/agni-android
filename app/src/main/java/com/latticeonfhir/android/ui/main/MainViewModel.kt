package com.latticeonfhir.android.ui.main

import com.latticeonfhir.android.FhirApp
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.server.repository.sync.SyncRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(syncRepository: SyncRepository) : BaseViewModel() {

    init {
        FhirApp.syncRepository = syncRepository
    }
}