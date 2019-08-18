package com.example.starbucks_mock_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.mobile.auth.core.IdentityHandler
import com.amazonaws.mobile.auth.core.IdentityManager
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var credentialsProvider: AWSCredentialsProvider? = null
    private var awsConfiguration: AWSConfiguration? = null

    companion object {
        var dynamoDBMapper: DynamoDBMapper? = null
    }


    //TODO: fix landscape
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //TODO: add some kind of initialization dialog or splash screen

        AWSMobileClient.getInstance().initialize(this) {
            credentialsProvider = AWSMobileClient.getInstance().credentialsProvider
            awsConfiguration = AWSMobileClient.getInstance().configuration

            IdentityManager.getDefaultIdentityManager().getUserID(object : IdentityHandler {
                override fun handleError(exception: Exception?) {
                    Log.e("AWSMobileClientLog", "Retrieving identity: ${exception?.message}")
                }

                override fun onIdentityId(identityId: String?) {
                    Log.i("AWSMobileClientLog", "Successfully established identity")
                    //future work would be storing this locally
                    //val cachedIdentityId = IdentityManager.getDefaultIdentityManager().cachedUserID

                    val client = AmazonDynamoDBClient(AWSMobileClient.getInstance().credentialsProvider)
                    dynamoDBMapper = DynamoDBMapper.builder()
                        .dynamoDBClient(client)
                        .awsConfiguration(AWSMobileClient.getInstance().configuration)
                        .build()
                }
            })
        }.execute()
    }

    fun goToLogin(view: View) {
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
    }

    fun goToJoinNow(view: View) {
        val intent = Intent(this, JoinNowActivity::class.java)
        startActivity(intent)
    }
}
