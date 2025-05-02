package com.latticeonfhir.core.base.activity

import androidx.appcompat.app.AppCompatActivity
import com.latticeonfhir.core.base.viewmodel.ParentViewModel

abstract class BaseActivity : AppCompatActivity() {

    abstract fun viewModel(): ParentViewModel
}