package com.example.starbucks_mock_app

import android.os.Bundle
import android.view.View

import kotlinx.android.synthetic.main.content_sign_in.*
import kotlin.concurrent.thread

class SignInActivity : FormActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        signInEmail.setOnFocusChangeListener { _, b -> validateEmail(b) }
        signInPassword.setOnFocusChangeListener { _, b -> validatePassword(b) }

        signInPasswordTE.isPasswordVisibilityToggleEnabled = true
    }

    override fun emailIsValid(email: String): Boolean {
        return email.isNotBlank()
    }

    fun signInUser(view: View) {
        validateEmail(hasFocus = false, submissionCheck = true)
        validatePassword(hasFocus = false, submissionCheck = true)

        //future work - handle db connection not ready
        if (!formIsValid || !checkInternetConnection(R.id.signInLayout) || MainActivity.dynamoDBMapper == null) return

        val email = getEmail()
        val pass = getPassword()

        val newUser = UsersDO()
        newUser.userId = email
        newUser.password = pass

        showSpinner()
        thread(start = true) {
            val entry = MainActivity.dynamoDBMapper?.load(UsersDO::class.java, email)

            if (entry != null && pass == entry.password) {
                runOnUiThread {
                    signInPasswordTE.error = null
                }
                goToLocator()
            } else {
                runOnUiThread {
                    signInPasswordTE.error = getString(R.string.sign_in_incorrect_credentials)
                }
            }
            runOnUiThread {
                hideSpinner()
            }
            //TODO: error handling (including network timeout)
        }
    }

    fun popActivity(view: View) {
        finish()
    }

    private fun showSpinner() {
        signInProgressBar.visibility = View.VISIBLE
    }

    private fun hideSpinner() {
        signInProgressBar.visibility = View.INVISIBLE
    }

    private fun validateEmail(hasFocus: Boolean, submissionCheck: Boolean = false) {
        if (!submissionCheck && hasFocus) return

        if (emailIsValid(getEmail())) {
            signInEmailTE.error = null
            emailIsValid = true
            updateFormStatus()
        } else {
            signInEmailTE.error = getString(R.string.sign_in_check_email)
            emailIsValid = false
            updateFormStatus()
        }
    }

    private fun validatePassword(hasFocus: Boolean, submissionCheck: Boolean = false) {
        if (!submissionCheck && hasFocus) return

        if (passwordIsValid(getPassword())) {
            signInPasswordTE.error = null
            passwordIsValid = true
            updateFormStatus()
        } else {
            signInPasswordTE.error = getString(R.string.sign_in_password_requirements)
            passwordIsValid = false
            updateFormStatus()
        }
    }

    private fun getEmail(): String {
        return signInEmail.text.toString()
    }

    private fun getPassword(): String {
        return signInPassword.text.toString()
    }
}
