package com.latticeonfhir.core.auth.di

import com.latticeonfhir.core.data.repository.server.authentication.AuthenticationRepository
import com.latticeonfhir.core.auth.data.server.repository.authentication.AuthenticationRepositoryImpl
import com.latticeonfhir.core.auth.data.server.repository.signup.SignUpRepository
import com.latticeonfhir.android.auth.data.server.repository.signup.SignUpRepositoryImpl
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
    abstract fun provideAuthenticationRepository(authenticationRepositoryImpl: AuthenticationRepositoryImpl): AuthenticationRepository

    @Binds
    @ViewModelScoped
    abstract fun provideSignUpRepository(signUpRepositoryImpl: SignUpRepositoryImpl): SignUpRepository
}