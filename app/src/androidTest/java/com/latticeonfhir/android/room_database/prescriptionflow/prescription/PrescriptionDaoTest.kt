package com.latticeonfhir.android.room_database.prescriptionflow.prescription

import com.latticeonfhir.android.data.local.roomdb.entities.medication.MedicationEntity
import com.latticeonfhir.android.data.local.roomdb.entities.prescription.PrescriptionDirectionsEntity
import com.latticeonfhir.android.data.local.roomdb.entities.prescription.PrescriptionEntity
import com.latticeonfhir.android.room_database.FhirAppDatabaseTest
import com.latticeonfhir.android.utils.converters.responseconverter.toPatientEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.util.Date

class PrescriptionDaoTest: FhirAppDatabaseTest() {

    private val prescriptionEntity = PrescriptionEntity(
        id = "DUMMY_PRESCRIPTION_ID",
        prescriptionDate = Date(),
        patientId = "DUMMY_ID_123",
        patientFhirId = "20401",
        prescriptionFhirId = null
    )

    private val prescriptionDirectionsEntity = PrescriptionDirectionsEntity(
        medFhirId = "MED_FHIR_ID_01",
        qtyPerDose = 1,
        frequency = 2,
        dosageInstruction = "Before Lunch",
        duration = 3,
        qtyPrescribed = 12,
        note = "Dummy note",
        prescriptionId = "DUMMY_PRESCRIPTION_ID"
    )

    private val prescriptionDirectionsEntityOther = PrescriptionDirectionsEntity(
        medFhirId = "MED_FHIR_ID_02",
        qtyPerDose = 1,
        frequency = 2,
        dosageInstruction = "Before Awakening",
        duration = 5,
        qtyPrescribed = 12,
        note = "Dummy note Other",
        prescriptionId = "DUMMY_PRESCRIPTION_ID"
    )

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

    override fun setUp() {
        super.setUp()

        runBlocking(Dispatchers.IO) {
            patientDao.insertPatientData(patientResponse.copy(id = "DUMMY_ID_123").toPatientEntity())
            medicationDao.insertMedication(medicationEntity)
            medicationDao.insertMedication(medicationEntityOther)
            medicationDao.insertMedication(medicationEntityInsert)
            prescriptionDao.insertPrescription(prescriptionEntity)
            prescriptionDao.insertPrescriptionMedicines(prescriptionDirectionsEntity)
            prescriptionDao.insertPrescriptionMedicines(prescriptionDirectionsEntityOther)
        }
    }

    @Test
    internal fun insertAndFetchPrescription() = runBlocking {
        val prescription = prescriptionDao.getPastPrescriptions("DUMMY_ID_123")
        assertEquals(prescription.size,1)
        assertEquals(prescription[0].prescriptionDirectionAndMedicineView.size,2)
        assertEquals(prescription[0].prescriptionEntity.patientId,prescriptionEntity.patientId)
        assertEquals(prescription[0].prescriptionDirectionAndMedicineView[0].prescriptionDirectionsEntity.prescriptionId,prescriptionEntity.id)
    }
}