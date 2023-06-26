package com.latticeonfhir.android.room_database

import com.latticeonfhir.android.data.local.enums.SearchTypeEnum
import com.latticeonfhir.android.data.local.roomdb.entities.medication.MedicationEntity
import com.latticeonfhir.android.data.local.roomdb.entities.search.SearchHistoryEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toPatientEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toPatientResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class SearchDaoTest : FhirAppDatabaseTest() {

    @Test
    fun insertRecentSearchTest() = runTest {
        val result = searchDao.insertRecentSearch(
            SearchHistoryEntity(
                "mansi",
                date = Date(),
                SearchTypeEnum.PATIENT
            )
        )
        Assert.assertEquals("recent search not inserted", 1, result)
    }

    @Test
    fun getPatientListTest() = runTest {
        patientDao.insertPatientData(patientResponse.toPatientEntity())
        val result = searchDao.getPatientList()
        Assert.assertEquals(
            "patient list not returned correctly",
            patientResponse.toPatientEntity(),
            result[0].toPatientResponse().toPatientEntity()
        )
    }

    @Test
    fun getRecentSearchesTest() = runTest {
        insertRecentSearchTest()
        val result = searchDao.getRecentSearches(SearchTypeEnum.PATIENT)
        Assert.assertEquals("recent search not returned correctly", listOf("mansi"), result)
    }

    @Test
    fun getOldestRecentSearchIdTest() = runTest {
        insertRecentSearchTest()
        searchDao.insertRecentSearch(
            SearchHistoryEntity(
                "blah",
                Date(),
                SearchTypeEnum.PATIENT
            )
        )
        val result = searchDao.getOldestRecentSearchId(SearchTypeEnum.PATIENT)
        Assert.assertEquals("oldest id returned is not correct", 1, result)
    }

    @Test
    fun deleteRecentSearchTest() = runTest {
        insertRecentSearchTest()
        val result = searchDao.deleteRecentSearch(1)
        Assert.assertEquals("recent search not deleted", 1, result)
    }

    @Test
    fun getActiveIngredientsTest() = runTest {
        val medicationEntity = MedicationEntity(
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
        medicationDao.insertMedication(
            medicationEntity
        )
        val result = searchDao.getActiveIngredients()
        assertEquals(medicationEntity.activeIngredient,result[0])
    }
}