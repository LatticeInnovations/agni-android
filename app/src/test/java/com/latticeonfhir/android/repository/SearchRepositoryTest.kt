package com.latticeonfhir.android.repository

import com.latticeonfhir.android.base.BaseClass
import com.latticeonfhir.android.data.local.enums.SearchTypeEnum
import com.latticeonfhir.android.data.local.model.search.SearchParameters
import com.latticeonfhir.android.data.local.repository.search.SearchRepositoryImpl
import com.latticeonfhir.android.data.local.roomdb.dao.RelationDao
import com.latticeonfhir.android.data.local.roomdb.dao.SearchDao
import com.latticeonfhir.android.data.local.roomdb.entities.patient.PatientAndIdentifierEntity
import com.latticeonfhir.android.data.local.roomdb.entities.search.SearchHistoryEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toIdentifierEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toPatientEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class SearchRepositoryTest : BaseClass() {

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

    @Before
    public override fun setUp() {
        MockitoAnnotations.openMocks(this)
        searchRepositoryImpl = SearchRepositoryImpl(searchDao, relationDao)

        runTest {
            `when`(searchDao.getRecentSearches(SearchTypeEnum.PATIENT)).thenReturn(listOf("Test"))
        }
    }

//    @Test
//    fun searchPatientsTest() = runBlocking {
//        `when`(searchDao.getPatientList()).thenReturn(listOf(patientResponse.json.toPatientAndIdentifierEntityResponse()))
//        val actual = mutableListOf<PatientResponse>()
//        val searchParametersId = SearchParameters(
//            null,
//            patientResponse.json.firstName,
//            null,
//            null,
//            patientResponse.json.gender,
//            null,
//            null,
//            null,
//            null,
//            null,
//            null,
//            null
//        )
//        searchRepositoryImpl.searchPatients(searchParametersId).map {
//            it.map {
//                actual.add(it.data)
//            }
//        }
//
//        Assert.assertEquals(listOf(patientResponse.json.toPatientAndIdentifierEntityResponse()), actual)
//    }

//    @Test
//    fun insertRecentSearchTest() = runBlocking {
//        `when`(searchDao.getRecentSearches()).thenReturn(listOf("Test"))
//        `when`(searchDao.getOldestRecentSearchId()).thenReturn(1)
//        `when`(searchDao.deleteRecentSearch(1)).thenReturn(1)
//        `when`(searchDao.insertRecentSearch(SearchHistoryEntity("blah", Date()))).thenReturn(1)
//
//        val actual = searchRepositoryImpl.insertRecentSearch("blah")
//        Assert.assertEquals(1, actual)
//    }


    @Test
    fun getRecentSearchesTest() = runBlocking {
        val actual = searchRepositoryImpl.getRecentPatientSearches()
        Assert.assertEquals(listOf("Test"), actual)
    }


    @Test
    internal fun insertRecentPatient() = runTest {
        val searchQuery = "Test"
        val date = Date()
        `when`(searchDao.getRecentSearches(SearchTypeEnum.PATIENT)).thenReturn(emptyList())
        `when`(searchDao.getOldestRecentSearchId(SearchTypeEnum.PATIENT)).thenReturn(1)
        `when`(searchDao.deleteRecentSearch(1)).thenReturn(1)
        `when`(
            searchDao.insertRecentSearch(
                SearchHistoryEntity(
                    searchQuery = searchQuery,
                    date = date,
                    searchType = SearchTypeEnum.PATIENT
                )
            )
        ).thenReturn(1L)

        val insertActiveIngredient =
            searchRepositoryImpl.insertRecentPatientSearch(searchQuery, date)
        assertEquals(1L, insertActiveIngredient)
    }

    @Test
    internal fun getRecentActiveIngredientSearches() = runBlocking {
        val actual = searchRepositoryImpl.getRecentActiveIngredientSearches()
        Assert.assertEquals(listOf("Test"), actual)
    }

    @Test
    internal fun insertRecentActiveIngredientSearch() = runTest {
        val searchQuery = "Test"
        val date = Date()
        `when`(searchDao.getRecentSearches(SearchTypeEnum.ACTIVE_INGREDIENT)).thenReturn(emptyList())
        `when`(searchDao.getOldestRecentSearchId(SearchTypeEnum.ACTIVE_INGREDIENT)).thenReturn(1)
        `when`(searchDao.deleteRecentSearch(1)).thenReturn(1)
        `when`(
            searchDao.insertRecentSearch(
                SearchHistoryEntity(
                    searchQuery = searchQuery,
                    date = date,
                    searchType = SearchTypeEnum.ACTIVE_INGREDIENT
                )
            )
        ).thenReturn(1L)

        val insertActiveIngredient =
            searchRepositoryImpl.insertRecentActiveIngredientSearch(searchQuery, date)
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
            assertEquals(true, members.isNotEmpty())
        }
    }

//    @Test
//    fun getSuggestedMembersTest() = runBlocking {
//
//    }
}