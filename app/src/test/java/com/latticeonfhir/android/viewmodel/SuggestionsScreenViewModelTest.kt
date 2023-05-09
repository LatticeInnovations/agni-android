package com.latticeonfhir.android.viewmodel

import com.latticeonfhir.android.data.local.model.Relation
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.relation.RelationRepository
import com.latticeonfhir.android.data.local.repository.search.SearchRepository
import com.latticeonfhir.android.data.local.roomdb.dao.PatientDao
import com.latticeonfhir.android.base.BaseClass
import com.latticeonfhir.android.ui.householdmember.suggestions.SuggestionsScreenViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class SuggestionsScreenViewModelTest: BaseClass() {
    @Mock
    lateinit var patientDao: PatientDao
    @Mock
    lateinit var relationRepository: RelationRepository
    @Mock
    lateinit var genericRepository: GenericRepository
    @Mock
    lateinit var searchRepository: SearchRepository
    lateinit var viewModel: SuggestionsScreenViewModel

    @Before
    fun setUp(){
        MockitoAnnotations.initMocks(this)
        viewModel = SuggestionsScreenViewModel(searchRepository, genericRepository, relationRepository, patientDao)
    }

    @Test
    fun addRelationTest() = runBlocking {
        var actual = listOf<Long>()
        `when`(relationRepository.addRelation(Relation(id, relativeId, relationSpouse.value)){
            actual = listOf(-1)
        }).thenReturn(Unit)
        viewModel.addRelation(Relation(id, relativeId, relationSpouse.value), relativeId){
            Assert.assertEquals(listOf<Long>(-1), actual)
        }
    }
}