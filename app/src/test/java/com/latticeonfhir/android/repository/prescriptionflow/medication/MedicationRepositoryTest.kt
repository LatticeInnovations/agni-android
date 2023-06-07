package com.latticeonfhir.android.repository.prescriptionflow.medication

import com.latticeonfhir.android.base.BaseClass
import com.latticeonfhir.android.data.local.repository.medication.MedicationRepositoryImpl
import com.latticeonfhir.android.data.local.roomdb.dao.MedicationDao
import com.latticeonfhir.android.data.local.roomdb.entities.medication.MedicationEntity
import com.latticeonfhir.android.data.local.roomdb.entities.medication.MedicineDosageInstructionsEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toMedicationResponse
import junit.framework.TestCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class MedicationRepositoryTest: BaseClass() {

    @Mock
    lateinit var medicationDao: MedicationDao
    private lateinit var medicationRepositoryImpl: MedicationRepositoryImpl


    private val medicationEntity = MedicationEntity(
        medFhirId = "MED_FHIR_ID_01",
        medCodeName = "MED_CODE_01",
        medName = "MED_NAME",
        doseForm = "DOSE_FORM",
        doseFormCode = "DOSE_FORM_CODE",
        activeIngredient = "Paracetamol",
        activeIngredientCode = "ACTIVE_INGREDIENT_CODE",
        medUnit = "mL",
        medNumeratorVal = 1.00
    )

    private val medicationEntityOther = MedicationEntity(
        medFhirId = "MED_FHIR_ID_02",
        medCodeName = "MED_CODE_02",
        medName = "MED_NAME_OTHER",
        doseForm = "DOSE_FORM_OTHER",
        doseFormCode = "DOSE_FORM_CODE_OTHER",
        activeIngredient = "Analgesic",
        activeIngredientCode = "ACTIVE_INGREDIENT_CODE_OTHER",
        medUnit = "g",
        medNumeratorVal = 2.00
    )


    private val medicineDosageInstructionsEntity = MedicineDosageInstructionsEntity(
        medicalDosage = "Before Lunch",
        medicalDosageId = "BEF_LUN_01"
    )

    @Before
    public override fun setUp() {
        MockitoAnnotations.openMocks(this)
        medicationRepositoryImpl = MedicationRepositoryImpl(medicationDao)

        runBlocking(Dispatchers.IO) {
            `when`(medicationDao.getActiveIngredients()).thenReturn(listOf("activeIngredients"))
            `when`(medicationDao.getMedicationByActiveIngredient(medicationEntityOther.activeIngredient)).thenReturn(listOf(medicationEntity))
            `when`(medicationDao.getAllMedicineDosageInstructions()).thenReturn(listOf(medicineDosageInstructionsEntity))
        }
    }

    @Test
    internal fun getActiveIngredient() = runBlocking {
        val activeIngredient = medicationRepositoryImpl.getActiveIngredients()
        assertEquals(listOf("activeIngredients"),activeIngredient)
    }

    @Test
    internal fun getMedicineByActiveIngredient() = runBlocking {
        val medicine = medicationRepositoryImpl.getMedicationByActiveIngredient(medicationEntityOther.activeIngredient)
        assertEquals(listOf(medicationEntity.toMedicationResponse()),medicine)
    }

    @Test
    internal fun getAllMedicineDosageDirections() = runBlocking {
        val medicineDosage = medicationRepositoryImpl.getAllMedicationDirections()
        assertEquals(listOf(medicineDosageInstructionsEntity),medicineDosage)
    }

}