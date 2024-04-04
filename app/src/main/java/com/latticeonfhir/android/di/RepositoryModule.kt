package com.latticeonfhir.android.di

import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepositoryImpl
import com.latticeonfhir.android.data.local.repository.search.SearchRepository
import com.latticeonfhir.android.data.local.repository.search.SearchRepositoryImpl
import com.latticeonfhir.android.data.server.repository.authentication.AuthenticationRepository
import com.latticeonfhir.android.data.server.repository.authentication.AuthenticationRepositoryImpl
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
    abstract fun provideSearchRepository(searchRepositoryImpl: SearchRepositoryImpl): SearchRepository

    @Binds
    @ViewModelScoped
    abstract fun providePreferenceRepository(preferenceRepositoryImpl: PreferenceRepositoryImpl): PreferenceRepository

    @Binds
    @ViewModelScoped
    abstract fun provideAuthenticationRepository(authenticationRepositoryImpl: AuthenticationRepositoryImpl): AuthenticationRepository
}