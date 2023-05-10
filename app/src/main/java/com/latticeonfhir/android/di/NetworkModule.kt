package com.latticeonfhir.android.di

import com.latticeonfhir.android.BuildConfig
import com.latticeonfhir.android.FhirApp.Companion.gson
import com.latticeonfhir.android.data.local.sharedpreferences.PreferenceStorage
import com.latticeonfhir.android.data.server.api.ApiService
import com.latticeonfhir.android.service.authentication.TokenAuthenticator
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
    fun provideOkHttpClient(tokenAuthenticator: TokenAuthenticator): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor { chain ->
            chain.proceed(chain.request().newBuilder().also { requestBuilder ->
                requestBuilder.addHeader("Accept", "application/json")
            }.build())
        }.also { client ->
            client.authenticator(tokenAuthenticator)
            if (BuildConfig.DEBUG) {
                val interceptor = HttpLoggingInterceptor()
                interceptor.level = HttpLoggingInterceptor.Level.BODY
                client.addInterceptor(interceptor)
            } else {
                val interceptor = HttpLoggingInterceptor()
                interceptor.level = HttpLoggingInterceptor.Level.NONE
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
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideTokenAuthenticator(preferenceStorage: PreferenceStorage) =
        TokenAuthenticator(preferenceStorage)
}