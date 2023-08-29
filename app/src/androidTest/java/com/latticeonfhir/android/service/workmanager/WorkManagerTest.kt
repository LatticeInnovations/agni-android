package com.latticeonfhir.android.service.workmanager

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.latticeonfhir.android.FhirApp
import com.latticeonfhir.android.data.server.repository.sync.SyncRepository
import junit.framework.TestCase
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.util.concurrent.CountDownLatch

// TODO: update tests according to one time sync
@RunWith(AndroidJUnit4::class)
class WorkManagerTest : TestCase() {

    @Mock
    private lateinit var syncRepository: SyncRepository

    private val countDownLatch = CountDownLatch(1)

    private val applicationContext = ApplicationProvider.getApplicationContext<FhirApp>()

    @Before
    public override fun setUp() {
        MockitoAnnotations.openMocks(this)
    }
}