package com.latticeonfhir.core.auth.di

import com.latticeonfhir.core.auth.data.server.api.AuthenticationApiService
import com.latticeonfhir.android.auth.data.server.api.SignUpApiService
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
    internal fun provideSignUpApiService(retrofit: Retrofit): SignUpApiService {
        return retrofit.create(SignUpApiService::class.java)
    }


    @Provides
    @Singleton
    fun provideAuthenticationApiService(retrofit: Retrofit): AuthenticationApiService {
        return retrofit.create(AuthenticationApiService::class.java)
    }
}