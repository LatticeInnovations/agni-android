package com.latticeonfhir.android.di

import android.content.Context
import com.google.android.fhir.FhirEngine
import com.latticeonfhir.android.FhirApp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FhirModule {

    @Provides
    @Singleton
    internal fun provideFhirEngine(@ApplicationContext context: Context): FhirEngine {
        return FhirApp.fhirEngine(context)
    }
}