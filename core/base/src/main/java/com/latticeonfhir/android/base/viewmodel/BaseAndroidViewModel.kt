package com.latticeonfhir.android.base.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel

open class BaseAndroidViewModel(application: Application) : AndroidViewModel(application),
    ParentViewModel