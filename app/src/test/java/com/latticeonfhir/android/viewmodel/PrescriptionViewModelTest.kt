package com.latticeonfhir.android.viewmodel

import com.latticeonfhir.android.base.BaseClass
import com.latticeonfhir.android.data.local.model.prescription.PrescriptionResponseLocal
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.medication.MedicationRepository
import com.latticeonfhir.android.data.local.repository.prescription.PrescriptionRepository
import com.latticeonfhir.android.data.local.repository.search.SearchRepository
import com.latticeonfhir.android.data.local.roomdb.entities.medication.MedicationEntity
import com.latticeonfhir.android.data.local.roomdb.entities.medication.MedicineTimingEntity
import com.latticeonfhir.android.data.local.roomdb.entities.prescription.PrescriptionAndMedicineRelation
import com.latticeonfhir.android.data.local.roomdb.entities.prescription.PrescriptionDirectionsEntity
import com.latticeonfhir.android.data.local.roomdb.entities.prescription.PrescriptionEntity
import com.latticeonfhir.android.data.local.roomdb.views.PrescriptionDirectionAndMedicineView
import com.latticeonfhir.android.data.server.model.prescription.prescriptionresponse.Medication
import com.latticeonfhir.android.ui.prescription.PrescriptionViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class PrescriptionViewModelTest : BaseClass() {
    @Mock
    lateinit var prescriptionRepository: PrescriptionRepository

    @Mock
    lateinit var medicationRepository: MedicationRepository

    @Mock
    lateinit var searchRepository: SearchRepository

    @Mock
    lateinit var genericRepository: GenericRepository
    lateinit var prescriptionViewModel: PrescriptionViewModel

    private val medication = Medication(
        doseForm = medicationResponse.doseForm,
        duration = 2,
        frequency = 1,
        qtyPerDose = 3,
        qtyPrescribed = 6,
        note = "note",
        medFhirId = medicationResponse.medFhirId,
        timing = "Before food"
    )
    private val prescriptionResponseLocal = PrescriptionResponseLocal(
        patientId = id,
        patientFhirId = patientResponse.fhirId,
        generatedOn = Date(),
        prescriptionId = prescribedResponse.prescriptionId,
        prescription = listOf(medication)
    )

    private val date = Date()

    private val prescriptionDirectionsEntity = PrescriptionDirectionsEntity(
        id = medication.medFhirId + prescribedResponse.prescriptionId,
        medFhirId = medicationResponse.medFhirId,
        qtyPerDose = medication.qtyPerDose,
        frequency = medication.frequency,
        timing = medication.timing,
        duration = medication.duration,
        qtyPrescribed = medication.qtyPrescribed,
        note = medication.note,
        prescriptionId = prescribedResponse.prescriptionId
    )

    private val medicationEntity = MedicationEntity(
        medFhirId = medicationResponse.medFhirId,
        activeIngredient = medicationResponse.activeIngredient,
        activeIngredientCode = medicationResponse.activeIngredientCode,
        medNumeratorVal = medicationResponse.medNumeratorVal,
        medName = medicationResponse.medName,
        medUnit = medicationResponse.medUnit,
        doseForm = medicationResponse.doseForm,
        doseFormCode = medicationResponse.doseFormCode,
        medCodeName = medicationResponse.medCode
    )

    private val prescriptionEntity = PrescriptionEntity(
        id = prescribedResponse.prescriptionId,
        patientId = patientResponse.id,
        prescriptionFhirId = prescribedResponse.prescriptionFhirId,
        patientFhirId = patientResponse.fhirId,
        prescriptionDate = date
    )

    @Before
    public override fun setUp() {
        MockitoAnnotations.openMocks(this)
        prescriptionViewModel = PrescriptionViewModel(
            prescriptionRepository,
            medicationRepository,
            searchRepository,
            genericRepository
        )

        runBlocking {
            `when`(prescriptionRepository.insertPrescription(prescriptionResponseLocal)).thenReturn(
                -1
            )
            `when`(
                genericRepository.insertPrescription(
                    prescribedResponse
                )
            ).thenReturn(-1)
            `when`(
                searchRepository.insertRecentActiveIngredientSearch(
                    "search",
                    date
                )
            ).thenReturn(-1)
            `when`(searchRepository.getRecentActiveIngredientSearches()).thenReturn(listOf("search"))
        }
    }

    @Test
    fun insertPrescriptionTest() = runTest {
        prescriptionViewModel.insertPrescription {
            assertEquals(-1, it)
        }
    }

    @Test
    fun insertRecentSearchTest() = runTest {
        prescriptionViewModel.insertRecentSearch("search") {
            assertEquals(-1, it)
        }
    }

    @Test
    fun getPreviousSearchTest() = runTest {
        prescriptionViewModel.getPreviousSearch {
            assertEquals(listOf("search"), it)
        }
    }

    @Test
    fun getAllMedicationDirectionsTest() = runTest {
        `when`(medicationRepository.getAllMedicationDirections()).thenReturn(
            listOf(
                MedicineTimingEntity(
                    medicalDosage = "Before meal",
                    medicalDosageId = "307165006"
                )
            )
        )
        prescriptionViewModel.getAllMedicationDirections {
            assertEquals(
                listOf(
                    MedicineTimingEntity(
                        medicalDosage = "Before meal",
                        medicalDosageId = "307165006"
                    )
                ), it
            )
        }
    }

    @Test
    fun getActiveIngredientsTest() = runTest {
        `when`(medicationRepository.getActiveIngredients()).thenReturn(listOf("ampicillin"))
        prescriptionViewModel.getActiveIngredients {
            assertEquals(listOf("ampicillin"), it)
        }
    }

    @Test
    fun getActiveIngredientSearchListTest() = runTest {
        `when`(searchRepository.searchActiveIngredients(medicationResponse.activeIngredient)).thenReturn(
            listOf("ampicillin")
        )
        prescriptionViewModel.getActiveIngredientSearchList(medicationResponse.activeIngredient) {
            assertEquals(listOf("ampicillin"), it)
        }
    }

    @Test
    fun getPreviousPrescriptionTest() = runTest {
        `when`(prescriptionRepository.getLastPrescription(patientResponse.id)).thenReturn(
            listOf(
                PrescriptionAndMedicineRelation(
                    prescriptionDirectionAndMedicineView = listOf(
                        PrescriptionDirectionAndMedicineView(
                            prescriptionDirectionsEntity = prescriptionDirectionsEntity ,
                            medicationEntity = medicationEntity
                        )
                    ),
                    prescriptionEntity = prescriptionEntity
                )
            )
        )
        prescriptionViewModel.getPreviousPrescription(patientResponse.id) {
            assertEquals(
                listOf(
                    PrescriptionAndMedicineRelation(
                        prescriptionDirectionAndMedicineView = listOf(
                            PrescriptionDirectionAndMedicineView(
                                prescriptionDirectionsEntity = prescriptionDirectionsEntity ,
                                medicationEntity = medicationEntity
                            )
                        ),
                        prescriptionEntity = prescriptionEntity
                    )
                ), it
            )
        }
    }
}