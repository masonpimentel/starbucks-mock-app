package com.example.starbucks_mock_app

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
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

    private var formIsValid = false
    private var emailIsValid = false
    private var passwordIsValid = false

    companion object {
        private val TAG: String = this::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_now)
        setSupportActionBar(toolbar)

        newEmail.setOnFocusChangeListener { _, b -> validateEmail(b) }
        newPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                //nothing to do
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //nothing to do
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, count: Int) {
                if (count > 0) {
                    passwordComplete()
                } else {
                    passwordIncomplete()
                }
            }
        })

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
        if (!formIsValid) return

        val email = newEmail.text.toString()
        val pass = newPassword.text.toString()

        val newUser = UsersDO()
        newUser.userId = email
        newUser.password = pass

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
                    hideEmailRegisteredText()
                }
                goToLocator()
            }
            //TODO: error handling (including network timeout)
        }
    }

    private fun validateEmail(hasFocus: Boolean) {
        if (hasFocus) return

        if (Patterns.EMAIL_ADDRESS.matcher(newEmail.text.toString()).matches()) {
            emailValid()
        } else {
            emailInvalid()
        }
    }

    private fun showSpinner() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideSpinner() {
        progressBar.visibility = View.INVISIBLE
    }

    private fun emailValid() {
        checkEmail.visibility = View.INVISIBLE
        emailIsValid = true
        updateFormStatus()
    }

    private fun emailInvalid() {
        checkEmail.visibility = View.VISIBLE
        emailIsValid = false
        updateFormStatus()
    }

    private fun passwordComplete() {
        delete.text = "OK"
        passwordIsValid = true
        updateFormStatus()
    }

    private fun passwordIncomplete() {
        delete.text = "X"
        passwordIsValid = false
        updateFormStatus()
    }

    private fun updateFormStatus() {
        formIsValid = emailIsValid && passwordIsValid
    }

    private fun showEmailRegisteredText() {
        emailRegisteredView.visibility = View.VISIBLE
    }

    private fun hideEmailRegisteredText() {
        emailRegisteredView.visibility = View.INVISIBLE
    }

    private fun goToLocator() {
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }
}
