package com.latticeonfhir.android.room_database.prescriptionflow.medication

import com.latticeonfhir.android.data.local.roomdb.entities.medication.MedicationEntity
import com.latticeonfhir.android.data.local.roomdb.entities.medication.MedicineTimingEntity
import com.latticeonfhir.android.room_database.FhirAppDatabaseTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Test

class MedicationDaoTest : FhirAppDatabaseTest() {

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

    private val medicationEntityInsert = MedicationEntity(
        medFhirId = "MED_FHIR_ID_03",
        medCodeName = "MED_CODE_03",
        medName = "MED_NAME_OTHER",
        doseForm = "DOSE_FORM_OTHER",
        doseFormCode = "DOSE_FORM_CODE_OTHER",
        activeIngredient = "Zeher",
        activeIngredientCode = "ACTIVE_INGREDIENT_CODE_OTHER",
        medUnit = "g",
        medNumeratorVal = 2.00
    )

    private val medicineTimingEntity = MedicineTimingEntity(
        medicalDosage = "Before Lunch",
        medicalDosageId = "BEF_LUN_01"
    )


    override fun setUp() {
        super.setUp()

        runBlocking(Dispatchers.IO) {
            medicationDao.insertMedication(medicationEntity)
            medicationDao.insertMedication(medicationEntityOther)
        }
    }


    @Test
    internal fun getActiveIngredient() = runBlocking {
        val activeIngredient = medicationDao.getActiveIngredients()
        assertEquals(activeIngredient.size,2)
        assertEquals(activeIngredient[0],medicationEntity.activeIngredient)
    }

    @Test
    internal fun getMedicationByActiveIngredient() = runBlocking {
        val listOfMedication = medicationDao.getMedicationByActiveIngredient(medicationEntity.activeIngredient)
        assertEquals(listOfMedication.size,1)
        assertEquals(listOfMedication[0].medFhirId,medicationEntity.medFhirId)
    }

    @Test
    internal fun insertMedication() {
        runBlocking(Dispatchers.IO) {
            val inserted = medicationDao.insertMedication(medicationEntityInsert)
            assertEquals(inserted.size,1)
            assertEquals(inserted[0],3L)
        }
    }

    @Test
    internal fun insertAndFetchMedicineDosage() = runBlocking {
        val medicineDosageInserted = medicationDao.insertMedicineDosageInstructions(medicineTimingEntity)
        assertEquals(medicineDosageInserted.size,1)
        assertEquals(medicineDosageInserted[0],1L)
        val medicineDosageInstructionsEntity = medicationDao.getAllMedicineDosageInstructions()
        assertEquals(medicineDosageInstructionsEntity.size,1)
        assertEquals(medicineDosageInstructionsEntity[0].medicalDosageId,this@MedicationDaoTest.medicineTimingEntity.medicalDosageId)
    }
}