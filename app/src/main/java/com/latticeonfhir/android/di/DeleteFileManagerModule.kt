package com.latticeonfhir.core.di

import android.content.Context
import com.latticeonfhir.core.utils.file.DeleteFileManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DeleteFileManagerModule {
    @Singleton
    @Provides
    fun provideDeleteFileManage(
        @ApplicationContext context: Context,
    ): DeleteFileManager {
        return DeleteFileManager(context)
    }
}