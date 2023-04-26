package com.latticeonfhir.android.ui.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import androidx.work.WorkManager
import com.latticeonfhir.android.base.activity.BaseActivity
import com.latticeonfhir.android.navigation.NavigationAppHost
import com.latticeonfhir.android.service.workmanager.workers.upload.patient.post.PatientUploadSyncWorker.Companion.PatientUploadProgress
import com.latticeonfhir.android.service.workmanager.workers.upload.patient.post.PatientUploadSyncWorkerImpl
import com.latticeonfhir.android.ui.theme.FHIRAndroidTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FHIRAndroidTheme {
                val navController = rememberNavController()
                NavigationAppHost(navController = navController)
            }
        }
        viewModel.toString()

        WorkManager.getInstance(applicationContext)
            .getWorkInfosForUniqueWorkLiveData(PatientUploadSyncWorkerImpl::class.java.name)
            .observe(this) { work ->
                work.forEach { workInfo ->
                    if (workInfo != null) {
                        val progress = workInfo.progress
                        val value = progress.getInt(PatientUploadProgress, 0)
                        if (value == 100) {
                            /** Update Fhir Id in Generic Entity */
                            lifecycleScope.launch(Dispatchers.IO) {
                                viewModel.updateFhirIdInRelation()
                            }
                        }
                    }
                }
            }
    }

    override fun viewModel() = viewModel
}