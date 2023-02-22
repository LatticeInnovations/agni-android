package com.latticeonfhir.android.base.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.latticeonfhir.android.base.viewmodel.ParentViewModel

open class BaseAndroidViewModel(application: Application): AndroidViewModel(application),
    ParentViewModel {
}