package com.latticeonfhir.android.di

import com.latticeonfhir.android.data.local.repository.person.PersonRepository
import com.latticeonfhir.android.data.local.repository.person.PersonRepositoryImpl
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
    abstract fun providePersonRepository(personRepositoryImpl: PersonRepositoryImpl): PersonRepository

    @Binds
    @ViewModelScoped
    abstract fun provideSyncRepository(syncRepositoryImpl: SyncRepositoryImpl): SyncRepository
}