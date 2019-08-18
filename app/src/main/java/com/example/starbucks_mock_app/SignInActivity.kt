package com.example.starbucks_mock_app

import android.os.Bundle
import android.util.Log
import android.view.View

import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.content_join_now.*
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
        if (!formIsValid || MainActivity.dynamoDBMapper == null) return

        val email = getEmail()
        val pass = getPassword()

        val newUser = UsersDO()
        newUser.userId = email
        newUser.password = pass

        showSpinner()
        thread(start = true) {
            val res = MainActivity.dynamoDBMapper?.load(UsersDO::class.java, email)

            if (res != null) {
                runOnUiThread {
                    signInPasswordInfo.text = ""
                }
                goToLocator()
            } else {
                runOnUiThread {
                    signInPasswordInfo.text = getString(R.string.sign_in_incorrect_credentials)
                }
            }
            runOnUiThread {
                hideSpinner()
            }
            //TODO: error handling (including network timeout)
        }
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
            emailIsValid = true
            updateFormStatus()
        } else {
            signInCheckEmail.visibility = View.VISIBLE
            emailIsValid = false
            updateFormStatus()
        }
    }

    private fun validatePassword(hasFocus: Boolean, submissionCheck: Boolean = false) {
        if (!submissionCheck && hasFocus) return

        if (passwordIsValid(getPassword())) {
            Log.i("sign in activity", "pwd valid")
            signInPasswordInfo.text = ""
            passwordIsValid = true
            updateFormStatus()
        } else {
            Log.i("sign in activity", "pwd invalid")
            signInPasswordInfo.text = getString(R.string.sign_in_password_requirements)
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
