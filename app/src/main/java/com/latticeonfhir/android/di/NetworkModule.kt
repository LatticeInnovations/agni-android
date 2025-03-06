package com.latticeonfhir.android.di

import com.latticeonfhir.android.BuildConfig
import com.latticeonfhir.android.FhirApp.Companion.gson
import com.latticeonfhir.android.data.local.sharedpreferences.PreferenceStorage
import com.latticeonfhir.android.data.server.api.AuthenticationApiService
import com.latticeonfhir.android.data.server.api.CVDApiService
import com.latticeonfhir.android.data.server.api.DispenseApiService
import com.latticeonfhir.android.data.server.api.FileUploadApiService
import com.latticeonfhir.android.data.server.api.LabTestAndMedRecordService
import com.latticeonfhir.android.data.server.api.PatientApiService
import com.latticeonfhir.android.data.server.api.PrescriptionApiService
import com.latticeonfhir.android.data.server.api.ScheduleAndAppointmentApiService
import com.latticeonfhir.android.data.server.api.SignUpApiService
import com.latticeonfhir.android.data.server.api.VitalApiService
import com.latticeonfhir.android.data.server.api.SymptomsAndDiagnosisService
import com.latticeonfhir.android.data.server.api.VaccinationApiService
import com.latticeonfhir.android.utils.constants.AuthenticationConstants.X_ACCESS_TOKEN
import com.latticeonfhir.android.utils.constants.ErrorConstants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import okio.IOException
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(preferenceStorage: PreferenceStorage): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                try {
                    chain.proceed(chain.request().newBuilder().also { requestBuilder ->
                        requestBuilder.addHeader("Content-Type", "application/json")
                        if (preferenceStorage.token.isNotBlank()) requestBuilder.addHeader(
                            X_ACCESS_TOKEN,
                            preferenceStorage.token
                        )
                    }.build())
                } catch (e: IOException) {
                    val errorMsg: String = when (e) {
                        is SocketTimeoutException -> ErrorConstants.SOCKET_TIMEOUT_EXCEPTION
                        else -> ErrorConstants.IO_EXCEPTION
                    }
                    Response.Builder()
                        .request(chain.request())
                        .protocol(Protocol.HTTP_1_1)
                        .code(200)
                        .message("OK")
                        .body(
                            "${
                                JSONObject().run {
                                    put("status", 0)
                                    put("message", errorMsg)
                                }
                            }".toByteArray().toResponseBody("application/json".toMediaType())
                        )
                        .build()
                }
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

    @Provides
    @Singleton
    fun provideScheduleApiService(retrofit: Retrofit): ScheduleAndAppointmentApiService {
        return retrofit.create(ScheduleAndAppointmentApiService::class.java)
    }

    @Provides
    @Singleton
    internal fun provideFileUploadApiService(retrofit: Retrofit): FileUploadApiService {
        return retrofit.create(FileUploadApiService::class.java)
    }

    @Provides
    @Singleton
    internal fun provideSignUpApiService(retrofit: Retrofit): SignUpApiService {
        return retrofit.create(SignUpApiService::class.java)
    }

    @Provides
    @Singleton
    internal fun provideCVDApiService(retrofit: Retrofit): CVDApiService {
        return retrofit.create(CVDApiService::class.java)
    }

    @Provides
    @Singleton
    internal fun provideVitalAPiService(retrofit: Retrofit): VitalApiService {
        return retrofit.create(VitalApiService::class.java)
    }
    @Provides
    @Singleton
    internal fun provideSymptomsAndDiagnosisAPiService(retrofit: Retrofit): SymptomsAndDiagnosisService {
        return retrofit.create(SymptomsAndDiagnosisService::class.java)
    }

    @Provides
    @Singleton
    internal fun provideLabAndMedAPiService(retrofit: Retrofit): LabTestAndMedRecordService {
        return retrofit.create(LabTestAndMedRecordService::class.java)
    }

    @Provides
    @Singleton
    internal fun provideDispenseApiService(retrofit: Retrofit): DispenseApiService {
        return retrofit.create(DispenseApiService::class.java)
    }

    @Provides
    @Singleton
    internal fun provideVaccinationApiService(retrofit: Retrofit): VaccinationApiService {
        return retrofit.create(VaccinationApiService::class.java)
    }
}