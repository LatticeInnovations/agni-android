package com.heartcare.agni.base.activity

import androidx.appcompat.app.AppCompatActivity
import com.heartcare.agni.base.viewmodel.ParentViewModel

abstract class BaseActivity : AppCompatActivity() {

    abstract fun viewModel(): ParentViewModel
}