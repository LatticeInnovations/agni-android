package com.latticeonfhir.android.data.server.api

import com.latticeonfhir.android.base.server.BaseResponse
import com.latticeonfhir.android.data.server.model.create.CreateResponse
import com.latticeonfhir.android.data.server.model.scheduleandappointment.appointment.AppointmentResponse
import com.latticeonfhir.android.data.server.model.scheduleandappointment.schedule.ScheduleResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.QueryMap

interface ScheduleAndAppointmentApiService {

    @GET("Schedule")
    suspend fun getScheduleList(@QueryMap(encoded = true) map: Map<String,String>?): Response<BaseResponse<List<ScheduleResponse>>>

    @POST("sync/Schedule")
    suspend fun postScheduleData(@Body scheduleResponses: List<Any>) : Response<BaseResponse<List<CreateResponse>>>

    @GET("Appointment")
    suspend fun getAppointmentList(@QueryMap(encoded = true) map: Map<String,String>?): Response<BaseResponse<List<AppointmentResponse>>>

    @POST("sync/Appointment")
    suspend fun createAppointment(@Body appointmentResponse: List<Any>) : Response<BaseResponse<List<CreateResponse>>>

    @PATCH("sync/Appointment")
    suspend fun patchListOfChanges(@Body patchLogs: List<Map<String, Any>>): Response<BaseResponse<List<CreateResponse>>>
}