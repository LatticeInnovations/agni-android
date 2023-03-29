package com.latticeonfhir.android.di

import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.generic.GenericRepositoryImpl
import com.latticeonfhir.android.data.local.repository.patient.PatientRepository
import com.latticeonfhir.android.data.local.repository.patient.PatientRepositoryImpl
import com.latticeonfhir.android.data.local.repository.relation.RelationRepository
import com.latticeonfhir.android.data.local.repository.relation.RelationRepositoryImpl
import com.latticeonfhir.android.data.local.repository.search.SearchRepository
import com.latticeonfhir.android.data.local.repository.search.SearchRepositoryImpl
import com.latticeonfhir.android.data.server.repository.sync.SyncRepository
import com.latticeonfhir.android.data.server.repository.sync.SyncRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @Binds
    @ViewModelScoped
    abstract fun provideSyncRepository(syncRepositoryImpl: SyncRepositoryImpl): SyncRepository

    @Binds
    @ViewModelScoped
    abstract fun providePatientRepository(patientRepositoryImpl: PatientRepositoryImpl): PatientRepository

    @Binds
    @ViewModelScoped
    abstract fun provideGenericRepository(genericRepositoryImpl: GenericRepositoryImpl): GenericRepository

    @Binds
    @ViewModelScoped
    abstract fun provideRelationRepository(relationRepositoryImpl: RelationRepositoryImpl): RelationRepository

    @Binds
    @ViewModelScoped
    abstract fun provideSearchRepository(searchRepositoryImpl: SearchRepositoryImpl): SearchRepository
}