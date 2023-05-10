package com.latticeonfhir.android.service.authentication

import com.latticeonfhir.android.data.local.sharedpreferences.PreferenceStorage
import com.latticeonfhir.android.utils.constants.AuthenticationConstants.BEARER_TOKEN
import com.latticeonfhir.android.utils.constants.AuthenticationConstants.X_ACCESS_TOKEN
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(private val preferenceStorage: PreferenceStorage): Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        return runBlocking {
            if(preferenceStorage.userName.isNotBlank()) {
                response.request.newBuilder()
                    .header(X_ACCESS_TOKEN, String.format(BEARER_TOKEN,getAuthToken()))
                    .build()
            } else {
                response.request.newBuilder().build()
            }
        }
    }

    private fun getAuthToken(): String {
        return preferenceStorage.token
    }
}