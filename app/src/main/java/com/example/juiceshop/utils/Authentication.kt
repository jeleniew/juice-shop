package com.example.juiceshop.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.management.ManagementException
import com.auth0.android.management.UsersAPIClient
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.auth0.android.result.UserProfile
import com.example.juiceshop.R
import com.auth0.android.callback.Callback

object Authentication {

    private var cachedCredentials: Credentials? = null
    private var cachedUserProfile: UserProfile? = null

    fun loginWithBrowser(context: Context, callback: (String?) -> Unit) {
        var account = Auth0(
            context.getString(R.string.com_auth0_client_id),
            context.getString(R.string.com_auth0_domain)
        )
        WebAuthProvider.login(account)
            .withScheme(context.getString(R.string.com_auth0_scheme))
            .withScope("openid profile email read:current_user update:current_user_metadata")
            .withAudience("https://${context.getString(R.string.com_auth0_domain)}/api/v2/")

            .start(context, object: Callback<Credentials, AuthenticationException> {
                override fun onFailure(exception: AuthenticationException) {
//                    showSnackBar("Failure: ${exception.getCode()}")
                }

                override fun onSuccess(credentials: Credentials) {
                    cachedCredentials = credentials
                    cachedUserProfile = cachedCredentials?.user
                    Log.d("debug", "cachedCredentials: ${cachedUserProfile?.email}")
                    Log.d("debug", "cachedCredentials: ${cachedCredentials?.accessToken}")
                    callback(cachedUserProfile?.email)
//                    showSnackBar("Success: ${credentials.accessToken}")
//                    showUserProfile()
                }
            })
    }

    fun logout(context: Context) {
        var account = Auth0(
            context.getString(R.string.com_auth0_client_id),
            context.getString(R.string.com_auth0_domain)
        )
        WebAuthProvider.logout(account)
            .withScheme(context.getString(R.string.com_auth0_scheme))
            .start(context, object : Callback<Void?, AuthenticationException> {
                override fun onSuccess(payload: Void?) {
                    cachedCredentials = null
                    cachedUserProfile = null
                }

                override fun onFailure(exception: AuthenticationException) {
//                    showSnackBar("Failure: ${exception.getCode()}")
                }
            })
    }
//
//    private fun showUserProfile() {
//        val client = AuthenticationAPIClient(account)
//        client.userInfo(cachedCredentials!!.accessToken!!)
//            .start(object : Callback<UserProfile, AuthenticationException> {
//                override fun onFailure(exception: AuthenticationException) {
////                    showSnackBar("Failure: ${exception.getCode()}")
//                }
//
//                override fun onSuccess(profile: UserProfile) {
//                    cachedUserProfile = profile;
//                }
//            })
//    }
//
//    private fun getUserMetadata() {
//        val usersClient = UsersAPIClient(account, cachedCredentials!!.accessToken!!)
//
//        // Get the full user profile
//        usersClient.getProfile(cachedUserProfile!!.getId()!!)
//            .start(object : Callback<UserProfile, ManagementException> {
//                override fun onFailure(exception: ManagementException) {
////                    showSnackBar("Failure: ${exception.getCode()}")
//                }
//
//                override fun onSuccess(userProfile: UserProfile) {
//                    cachedUserProfile = userProfile;
//
//                    val country = userProfile.getUserMetadata()["country"] as String?
//                }
//            })
//    }

}