package com.latticeonfhir.android.repository.prescriptionflow.prescription

import com.latticeonfhir.android.base.BaseClass
import com.latticeonfhir.android.data.local.model.prescription.PrescriptionResponseLocal
import com.latticeonfhir.android.data.local.repository.prescription.PrescriptionRepositoryImpl
import com.latticeonfhir.android.data.local.roomdb.dao.PrescriptionDao
import com.latticeonfhir.android.data.local.roomdb.entities.medication.MedicationEntity
import com.latticeonfhir.android.data.local.roomdb.entities.prescription.PrescriptionAndMedicineRelation
import com.latticeonfhir.android.data.local.roomdb.entities.prescription.PrescriptionDirectionsEntity
import com.latticeonfhir.android.data.local.roomdb.entities.prescription.PrescriptionEntity
import com.latticeonfhir.android.data.local.roomdb.views.PrescriptionDirectionAndMedicineView
import com.latticeonfhir.android.data.server.model.prescription.prescriptionresponse.Medication
import com.latticeonfhir.android.utils.converters.responseconverter.toPrescriptionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.util.Date
import java.util.UUID

class PrescriptionRepositoryTest : BaseClass() {

    @Mock
    private lateinit var prescriptionDao: PrescriptionDao
    private lateinit var prescriptionRepositoryImpl: PrescriptionRepositoryImpl

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
        timing = "Before Lunch",
        duration = 3,
        qtyPrescribed = 12,
        note = "Dummy note",
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

    private val prescriptionAndMedicineRelation = PrescriptionAndMedicineRelation(
        prescriptionEntity = prescriptionEntity,
        prescriptionDirectionAndMedicineView = listOf(
            PrescriptionDirectionAndMedicineView(
                prescriptionDirectionsEntity = prescriptionDirectionsEntity,
                medicationEntity = medicationEntity
            )
        )
    )

    private val prescriptionResponseLocal = PrescriptionResponseLocal(
        patientId = patientResponse.id,
        patientFhirId = null,
        generatedOn = Date(),
        prescriptionId = UUID.randomUUID().toString(),
        prescription = listOf(
            Medication(
                doseForm = "DUMMY_DOSE_FORM",
                duration = 7,
                frequency = 2,
                medFhirId = "MED_FHIR_ID_01",
                note = "Sone ke baad",
                qtyPerDose = 1,
                qtyPrescribed = 5,
                timing = "After Lunch"
            )
        )
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    public override fun setUp() {

        MockitoAnnotations.openMocks(this)
        prescriptionRepositoryImpl = PrescriptionRepositoryImpl(prescriptionDao)

        runTest {
            `when`(prescriptionDao.insertPrescription(prescriptionResponseLocal.toPrescriptionEntity())).thenReturn(listOf(1L))
            `when`(prescriptionDao.insertPrescriptionMedicines(prescriptionDirectionsEntity)).thenReturn(
                listOf(2L)
            )
            `when`(prescriptionDao.getPastPrescriptions(patientResponse.id)).thenReturn(
                listOf(
                    prescriptionAndMedicineRelation
                )
            )
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    internal fun insertPrescription() = runTest {
        val prescriptionInserted = prescriptionRepositoryImpl.insertPrescription(prescriptionResponseLocal)
        assertEquals(1L, prescriptionInserted)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    internal fun getLastPrescription() = runTest {
        val lastPrescriptions = prescriptionRepositoryImpl.getLastPrescription(patientResponse.id)
        assertEquals(listOf(prescriptionAndMedicineRelation),lastPrescriptions)
    }
}