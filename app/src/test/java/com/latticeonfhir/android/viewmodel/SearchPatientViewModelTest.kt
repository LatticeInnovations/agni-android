package com.latticeonfhir.android.viewmodel

import com.latticeonfhir.android.ui.searchpatient.SearchPatientViewModel
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class SearchPatientViewModelTest {

    lateinit var viewModel: SearchPatientViewModel

    @Before
    fun setUp(){
        viewModel = SearchPatientViewModel()
    }

    @Test
    fun updateRangeTest() {
        viewModel.minAge = "23"
        viewModel.maxAge = "80"
        viewModel.updateRange(viewModel.minAge, viewModel.maxAge)
        Assert.assertEquals(23F..80F, viewModel.range)
    }
}