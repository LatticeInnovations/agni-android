package com.latticeonfhir.android.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import com.latticeonfhir.android.base.activity.BaseActivity
import com.latticeonfhir.android.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.runMethod()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getPersonList()
    }

    override fun viewModel() = viewModel
}