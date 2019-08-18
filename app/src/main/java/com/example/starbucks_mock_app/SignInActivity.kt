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
        setSupportActionBar(toolbar)

        signInEmail.setOnFocusChangeListener { _, b -> validateEmail(b) }
        signInPassword.setOnFocusChangeListener { _, b -> validatePassword(b) }
    }

    override fun emailIsValid(email: String): Boolean {
        return email.isNotBlank()
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
