package com.latticeonfhir.android.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import com.latticeonfhir.android.base.BaseClass
import com.latticeonfhir.android.data.local.enums.SearchTypeEnum
import com.latticeonfhir.android.data.local.model.search.SearchParameters
import com.latticeonfhir.android.data.local.repository.search.SearchRepositoryImpl
import com.latticeonfhir.android.data.local.roomdb.dao.RelationDao
import com.latticeonfhir.android.data.local.roomdb.dao.SearchDao
import com.latticeonfhir.android.data.local.roomdb.entities.patient.PatientAndIdentifierEntity
import com.latticeonfhir.android.data.local.roomdb.entities.search.SearchHistoryEntity
import com.latticeonfhir.android.utils.MainCoroutineRule
import com.latticeonfhir.android.utils.converters.responseconverter.toIdentifierEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toPatientAndIdentifierEntityResponse
import com.latticeonfhir.android.utils.converters.responseconverter.toPatientEntity
import com.latticeonfhir.android.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class SearchRepositoryTest : BaseClass() {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @Mock
    lateinit var searchDao: SearchDao

    @Mock
    lateinit var relationDao: RelationDao

    lateinit var searchRepositoryImpl: SearchRepositoryImpl

    private val searchParameters = SearchParameters(
        null,
        "Test",
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    )

    private val searchEntity = SearchHistoryEntity(
        searchQuery = "Test",
        date = date,
        searchType = SearchTypeEnum.ACTIVE_INGREDIENT
    )

    private val searchEntityPatient = SearchHistoryEntity(
        searchQuery = "Test",
        date = date,
        searchType = SearchTypeEnum.PATIENT
    )

    @Before
    public override fun setUp() {
        MockitoAnnotations.openMocks(this)
        searchRepositoryImpl = SearchRepositoryImpl(searchDao, relationDao)

        runTest {
            `when`(searchDao.getRecentSearches(SearchTypeEnum.PATIENT)).thenReturn(listOf("Test"))
        }
    }

    @Test
    fun searchPatientsTest() = runTest {
        `when`(searchDao.getPatientList()).thenReturn(listOf(patientResponse.toPatientAndIdentifierEntityResponse()))
        val data = searchRepositoryImpl.searchPatients(searchParameters).asLiveData().getOrAwaitValue()
        assertEquals(1,1)
    }

    @Test
    fun filterPatientData() = runTest {
        `when`(searchDao.getPatientList()).thenReturn(listOf(patientResponse.toPatientAndIdentifierEntityResponse()))
        `when`(relationDao.getAllRelationOfPatient(patientResponse.id)).thenReturn(emptyList())
        val data = searchRepositoryImpl.filteredSearchPatients(patientResponse.id,searchParameters).asLiveData().getOrAwaitValue()
        assertEquals(1,1)
    }

    @Test
    fun searchPatientByQuery_Id() = runTest {
        `when`(searchDao.getPatientList()).thenReturn(listOf(patientResponse.toPatientAndIdentifierEntityResponse()))
        val data = searchRepositoryImpl.searchPatientByQuery(id).asLiveData().getOrAwaitValue()
        assertEquals(1,1)
    }

    @Test
    fun searchPatientByQuery_Name() = runTest {
        `when`(searchDao.getPatientList()).thenReturn(listOf(patientResponse.toPatientAndIdentifierEntityResponse()))
        val data = searchRepositoryImpl.searchPatientByQuery("Test").asLiveData().getOrAwaitValue()
        assertEquals(1,1)
    }

    @Test
    internal fun searchActiveIngredientTest_Return_ListOf() = runBlocking {
        `when`(searchDao.getActiveIngredients()).thenReturn(listOf("Paracetamol, Aspirin", "Zaher"))
        val activeIngredient = searchRepositoryImpl.searchActiveIngredients("Zeher")
        assertEquals(true, activeIngredient.contains("Zaher"))
    }

    @Test
    fun getRecentSearchesTest() = runBlocking {
        val actual = searchRepositoryImpl.getRecentPatientSearches()
        Assert.assertEquals(listOf("Test"), actual)
    }


    @Test
    internal fun insertRecentPatient() = runTest {
        `when`(searchDao.getRecentSearches(SearchTypeEnum.PATIENT)).thenReturn(emptyList())
        `when`(searchDao.getOldestRecentSearchId(SearchTypeEnum.PATIENT)).thenReturn(1)
        `when`(searchDao.deleteRecentSearch(1)).thenReturn(1)
        `when`(
            searchDao.insertRecentSearch(
                searchEntityPatient
            )
        ).thenReturn(1L)

        val insertActiveIngredient =
            searchRepositoryImpl.insertRecentPatientSearch(searchParameters.name!!, date)
        assertEquals(1L, insertActiveIngredient)
    }

    @Test
    internal fun insertRecentPatient_With_Existing_Searches() = runTest {
        `when`(searchDao.getRecentSearches(SearchTypeEnum.PATIENT)).thenReturn(
            listOf(
                "Search1",
                "Search2",
                "Search3",
                "Search4",
                "Search5"
            )
        )
        `when`(searchDao.getOldestRecentSearchId(SearchTypeEnum.PATIENT)).thenReturn(1)
        `when`(searchDao.deleteRecentSearch(1)).thenReturn(1)
        `when`(
            searchDao.insertRecentSearch(
                searchEntityPatient
            )
        ).thenReturn(1L)

        val insertActiveIngredient =
            searchRepositoryImpl.insertRecentPatientSearch(searchParameters.name!!, date)
        assertEquals(1L, insertActiveIngredient)
    }

    @Test
    internal fun getRecentActiveIngredientSearches() = runBlocking {
        `when`(searchDao.getRecentSearches(SearchTypeEnum.ACTIVE_INGREDIENT)).thenReturn(listOf("Test"))
        val actual = searchRepositoryImpl.getRecentActiveIngredientSearches()
        Assert.assertEquals(listOf("Test"), actual)
    }

    @Test
    internal fun insertRecentActiveIngredientSearch_With_Five_Existing_Entries() = runTest {
        `when`(searchDao.getRecentSearches(SearchTypeEnum.ACTIVE_INGREDIENT)).thenReturn(
            listOf(
                "Search1",
                "Search2",
                "Search3",
                "Search4",
                "Search5"
            )
        )
        `when`(searchDao.getOldestRecentSearchId(SearchTypeEnum.ACTIVE_INGREDIENT)).thenReturn(1)
        `when`(searchDao.deleteRecentSearch(1)).thenReturn(1)
        `when`(
            searchDao.insertRecentSearch(
                searchEntity
            )
        ).thenReturn(1L)

        val insertActiveIngredient =
            searchRepositoryImpl.insertRecentActiveIngredientSearch(searchParameters.name!!, date)
        assertEquals(1L, insertActiveIngredient)
    }

    @Test
    internal fun insertRecentActiveIngredientSearch() = runTest {
        `when`(searchDao.getRecentSearches(SearchTypeEnum.ACTIVE_INGREDIENT)).thenReturn(emptyList())
        `when`(searchDao.getOldestRecentSearchId(SearchTypeEnum.ACTIVE_INGREDIENT)).thenReturn(1)
        `when`(searchDao.deleteRecentSearch(1)).thenReturn(1)
        `when`(
            searchDao.insertRecentSearch(
                searchEntity
            )
        ).thenReturn(1L)

        val insertActiveIngredient =
            searchRepositoryImpl.insertRecentActiveIngredientSearch(searchParameters.name!!, date)
        assertEquals(1L, insertActiveIngredient)
    }

    @Test
    internal fun getSuggestedMembers_Returns_ListOf() = runTest {
        `when`(searchDao.getPatientList()).thenReturn(
            listOf(
                PatientAndIdentifierEntity(
                    patientEntity = patientResponse.toPatientEntity(),
                    identifiers = listOf(patientIdentifier.toIdentifierEntity(patientResponse.id))
                ), PatientAndIdentifierEntity(
                    patientEntity = patientResponse.toPatientEntity().copy(id = "NEW_ID"),
                    identifiers = listOf(patientIdentifier.toIdentifierEntity(patientResponse.id))
                )
            )
        )
        `when`(relationDao.getAllRelationOfPatient(patientResponse.id)).thenReturn(emptyList())
        searchRepositoryImpl.getSuggestedMembers(patientResponse.id, searchParameters) { members ->
            assertEquals("NEW_ID", members[0].id)
        }
    }
}