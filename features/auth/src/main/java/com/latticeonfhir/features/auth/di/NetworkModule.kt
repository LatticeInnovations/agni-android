package com.latticeonfhir.core.auth.di

import com.latticeonfhir.features.auth.data.server.api.AuthenticationApiService
import com.latticeonfhir.features.auth.data.server.api.SignUpApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    internal fun provideSignUpApiService(retrofit: Retrofit): com.latticeonfhir.features.auth.data.server.api.SignUpApiService {
        return retrofit.create(com.latticeonfhir.features.auth.data.server.api.SignUpApiService::class.java)
    }


    @Provides
    @Singleton
    fun provideAuthenticationApiService(retrofit: Retrofit): com.latticeonfhir.features.auth.data.server.api.AuthenticationApiService {
        return retrofit.create(com.latticeonfhir.features.auth.data.server.api.AuthenticationApiService::class.java)
    }
}