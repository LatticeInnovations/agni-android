package com.latticeonfhir.android.di

import com.latticeonfhir.android.BuildConfig
import com.latticeonfhir.android.FhirApp.Companion.gson
import com.latticeonfhir.android.data.local.sharedpreferences.PreferenceStorage
import com.latticeonfhir.android.data.server.api.AuthenticationApiService
import com.latticeonfhir.android.data.server.api.PatientApiService
import com.latticeonfhir.android.data.server.api.PrescriptionApiService
import com.latticeonfhir.android.utils.constants.AuthenticationConstants.X_ACCESS_TOKEN
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(preferenceStorage: PreferenceStorage): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor { chain ->
            chain.proceed(chain.request().newBuilder().also { requestBuilder ->
                requestBuilder.addHeader("Content-Type", "application/json")
                if(preferenceStorage.token.isNotBlank()) requestBuilder.addHeader(X_ACCESS_TOKEN, preferenceStorage.token)
            }.build())
        }.also { client ->
            if (BuildConfig.DEBUG) {
                val interceptor = HttpLoggingInterceptor()
                interceptor.level = HttpLoggingInterceptor.Level.BODY
                client.addInterceptor(interceptor)
            }
        }.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun providePatientApiService(retrofit: Retrofit): PatientApiService {
        return retrofit.create(PatientApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthenticationApiService(retrofit: Retrofit): AuthenticationApiService {
        return retrofit.create(AuthenticationApiService::class.java)
    }

    @Provides
    @Singleton
    fun providePrescriptionApiService(retrofit: Retrofit): PrescriptionApiService {
        return retrofit.create(PrescriptionApiService::class.java)
    }
}