package com.example.starbucks_mock_app

import android.os.Bundle
import android.util.Log
import android.view.View

import kotlinx.android.synthetic.main.content_sign_in.*
import kotlin.concurrent.thread

class SignInActivity : FormActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        signInEmail.setOnFocusChangeListener { _, b -> validateEmail(b) }
        signInPassword.setOnFocusChangeListener { _, b -> validatePassword(b) }
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
                    signInPasswordInfo.text = ""
                    clearPasswordFieldError()
                }
                goToLocator()
            } else {
                runOnUiThread {
                    signInPasswordInfo.text = getString(R.string.sign_in_incorrect_credentials)
                    passwordFieldError()
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
            signInCheckEmail.visibility = View.INVISIBLE
            signInEmailError.visibility = View.INVISIBLE
            emailIsValid = true
            updateFormStatus()
        } else {
            signInCheckEmail.visibility = View.VISIBLE
            signInEmailError.visibility = View.VISIBLE
            emailIsValid = false
            updateFormStatus()
        }
    }

    private fun validatePassword(hasFocus: Boolean, submissionCheck: Boolean = false) {
        if (!submissionCheck && hasFocus) return

        if (passwordIsValid(getPassword())) {
            signInPasswordInfo.text = ""
            clearPasswordFieldError()
            passwordIsValid = true
            updateFormStatus()
        } else {
            signInPasswordInfo.text = getString(R.string.sign_in_password_requirements)
            passwordFieldError()
            passwordIsValid = false
            updateFormStatus()
        }
    }

    private fun clearPasswordFieldError() {
        signInPasswordError.visibility = View.INVISIBLE
    }

    private fun passwordFieldError() {
        signInPasswordError.visibility = View.VISIBLE
    }

    private fun getEmail(): String {
        return signInEmail.text.toString()
    }

    private fun getPassword(): String {
        return signInPassword.text.toString()
    }
}
