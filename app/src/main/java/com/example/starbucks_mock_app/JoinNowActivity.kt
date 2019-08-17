package com.example.starbucks_mock_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.auth.AWSCredentialsProvider

import com.amazonaws.mobile.auth.core.IdentityHandler
import com.amazonaws.mobile.auth.core.IdentityManager

import kotlinx.android.synthetic.main.activity_join_now.*
import kotlin.concurrent.thread

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.config.AWSConfiguration
import kotlinx.android.synthetic.main.content_join_now.*

class JoinNowActivity : AppCompatActivity() {
    private var dynamoDBMapper: DynamoDBMapper? = null
    private var credentialsProvider: AWSCredentialsProvider? = null
    private var awsConfiguration: AWSConfiguration? = null

    companion object {
        private val TAG: String = this::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_now)
        setSupportActionBar(toolbar)

        AWSMobileClient.getInstance().initialize(this) {
            credentialsProvider = AWSMobileClient.getInstance().credentialsProvider
            awsConfiguration = AWSMobileClient.getInstance().configuration

            IdentityManager.getDefaultIdentityManager().getUserID(object : IdentityHandler {
                override fun handleError(exception: Exception?) {
                    Log.e(TAG, "Retrieving identity: ${exception?.message}")
                }

                override fun onIdentityId(identityId: String?) {
                    Log.d(TAG, "Identity = $identityId")
                    val cachedIdentityId = IdentityManager.getDefaultIdentityManager().cachedUserID
                    // Do something with the identity here

                    val client = AmazonDynamoDBClient(AWSMobileClient.getInstance().credentialsProvider)
                    dynamoDBMapper = DynamoDBMapper.builder()
                        .dynamoDBClient(client)
                        .awsConfiguration(AWSMobileClient.getInstance().configuration)
                        .build()
                }
            })
        }.execute()
    }

    fun registerUser(view: View) {
        val email = newEmail.text.toString()
        val pass = newPassword.text.toString()

        val newUser = UsersDO()
        newUser.userId = email
        newUser.email = email
        newUser.password = pass

        //TODO: form validation

        showSpinner()
        thread(start = true) {
            val res = dynamoDBMapper?.load(UsersDO::class.java, email)

            if (res != null) {
                runOnUiThread {
                    hideSpinner()
                    showEmailRegisteredText()
                }
            } else {
                dynamoDBMapper?.save(newUser)
                runOnUiThread {
                    hideSpinner()
                }
                goToLocator()
            }
        }
    }

    private fun goToLocator() {
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }

    private fun showEmailRegisteredText() {
        emailRegisteredView.visibility = View.VISIBLE
    }

    private fun showSpinner() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideSpinner() {
        progressBar.visibility = View.INVISIBLE
    }
}
