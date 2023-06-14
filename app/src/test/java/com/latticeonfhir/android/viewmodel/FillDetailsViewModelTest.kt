package com.latticeonfhir.android.viewmodel

import com.latticeonfhir.android.base.BaseClass
import com.latticeonfhir.android.data.local.repository.medication.MedicationRepository
import com.latticeonfhir.android.ui.prescription.filldetails.FillDetailsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class FillDetailsViewModelTest: BaseClass() {
    @Mock
    lateinit var medicationRepository: MedicationRepository
    lateinit var fillDetailsViewModel: FillDetailsViewModel

    @Before
    public override fun setUp(){
        MockitoAnnotations.openMocks(this)
        fillDetailsViewModel = FillDetailsViewModel(medicationRepository)
    }

    @Test
    fun getMedicationByActiveIngredientTest() = runTest {
        `when`(medicationRepository.getMedicationByActiveIngredient(medicationResponse.activeIngredient)).thenReturn(listOf(medicationResponse))
        fillDetailsViewModel.getMedicationByActiveIngredient(medicationResponse.activeIngredient) {
            assertEquals(listOf(medicationResponse), it)
        }
    }

    @Test
    fun quantityPrescribedTest() {
        fillDetailsViewModel.quantityPerDose = "1"
        fillDetailsViewModel.frequency = "2"
        fillDetailsViewModel.duration = "3"
        val actual = fillDetailsViewModel.quantityPrescribed()
        assertEquals("6", actual)
    }

    @Test
    fun resetTest() {
        fillDetailsViewModel.reset()
        assertEquals("", fillDetailsViewModel.medSelected)
        assertEquals("1", fillDetailsViewModel.quantityPerDose)
        assertEquals("1", fillDetailsViewModel.frequency)
        assertEquals("", fillDetailsViewModel.duration)
        assertEquals("", fillDetailsViewModel.timing)
        assertEquals("", fillDetailsViewModel.notes)
    }
}