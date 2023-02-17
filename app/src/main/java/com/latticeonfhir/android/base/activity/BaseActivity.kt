package com.latticeonfhir.android.base.activity

import androidx.appcompat.app.AppCompatActivity
import com.latticeonfhir.android.base.viewmodel.ParentViewModel

abstract class BaseActivity: AppCompatActivity() {

    abstract fun viewModel(): ParentViewModel
}