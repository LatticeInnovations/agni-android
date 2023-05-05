package com.latticeonfhir.android.repository

import com.latticeonfhir.android.base.BaseClass
import com.latticeonfhir.android.data.local.model.SearchParameters
import com.latticeonfhir.android.data.local.repository.search.SearchRepositoryImpl
import com.latticeonfhir.android.data.local.roomdb.dao.RelationDao
import com.latticeonfhir.android.data.local.roomdb.dao.SearchDao
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class SearchRepositoryTest : BaseClass() {

    @Mock
    lateinit var searchDao: SearchDao
    @Mock
    lateinit var relationDao: RelationDao
    lateinit var searchRepositoryImpl: SearchRepositoryImpl

    val searchParameters = SearchParameters(
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
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        searchRepositoryImpl = SearchRepositoryImpl(searchDao, relationDao)
    }

//    @Test
//    fun searchPatientsTest() = runBlocking {
//        `when`(searchDao.getPatientList()).thenReturn(listOf(patientResponse.toPatientAndIdentifierEntityResponse()))
//        val actual = mutableListOf<PatientResponse>()
//        val searchParametersId = SearchParameters(
//            null,
//            patientResponse.firstName,
//            null,
//            null,
//            patientResponse.gender,
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
//        Assert.assertEquals(listOf(patientResponse.toPatientAndIdentifierEntityResponse()), actual)
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
        `when`(searchDao.getRecentSearches()).thenReturn(listOf("Test"))
        val actual = searchRepositoryImpl.getRecentSearches()
        Assert.assertEquals(listOf("Test"), actual)
    }

//    @Test
//    fun getSuggestedMembersTest() = runBlocking {
//
//    }
}