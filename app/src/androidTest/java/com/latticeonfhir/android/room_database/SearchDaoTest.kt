package com.latticeonfhir.android.room_database

import com.latticeonfhir.android.data.local.roomdb.entities.search.SearchHistoryEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toPatientEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toPatientResponse
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import java.util.Date

class SearchDaoTest: BaseClass() {

    @Test
    fun insertRecentSearchTest() = runBlocking {
        val result = searchDao.insertRecentSearch(
            SearchHistoryEntity(
                "mansi",
                date = Date()
            )
        )
        Assert.assertEquals("recent search not inserted", 1, result)
    }

    @Test
    fun getPatientListTest() = runBlocking {
        patientDao.insertPatientData(patientResponse.toPatientEntity())
        val result = searchDao.getPatientList()
        Assert.assertEquals("patient list not returned correctly", patientResponse.toPatientEntity(), result[0].toPatientResponse().toPatientEntity())
    }

    @Test
    fun getRecentSearchesTest() = runBlocking {
        insertRecentSearchTest()
        val result = searchDao.getRecentSearches()
        Assert.assertEquals("recent search not returned correctly", listOf("mansi"), result)
    }

    @Test
    fun getOldestRecentSearchIdTest() = runBlocking {
        insertRecentSearchTest()
        searchDao.insertRecentSearch(
            SearchHistoryEntity(
                "blah",
                Date()
            )
        )
        val result = searchDao.getOldestRecentSearchId()
        Assert.assertEquals("oldest id returned is not correct", 1, result)
    }

    @Test
    fun deleteRecentSearchTest() = runBlocking {
        insertRecentSearchTest()
        val result = searchDao.deleteRecentSearch(1)
        Assert.assertEquals("recent search not deleted", 1, result)
    }
}