package com.example.starbucks_mock_app

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View

import kotlinx.android.synthetic.main.activity_join_now.*
import kotlin.concurrent.thread

import kotlinx.android.synthetic.main.content_join_now.*

class JoinNowActivity : FormActivity() {
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
                if (passwordIsValid(getPassword())) {
                    delete.text = "OK"
                    passwordIsValid = true
                    updateFormStatus()
                } else {
                    delete.text = "X"
                    passwordIsValid = false
                    updateFormStatus()
                }
            }
        })
    }

    fun registerUser(view: View) {
        if (!formIsValid) return

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
                    hideSpinner()
                    emailRegisteredView.visibility = View.VISIBLE
                }
            } else {
                MainActivity.dynamoDBMapper?.save(newUser)
                runOnUiThread {
                    hideSpinner()
                    emailRegisteredView.visibility = View.INVISIBLE
                }
                goToLocator()
            }
            //TODO: error handling (including network timeout)
        }
    }

    private fun showSpinner() {
        joinNowProgressBar.visibility = View.VISIBLE
    }

    private fun hideSpinner() {
        joinNowProgressBar.visibility = View.INVISIBLE
    }

    private fun validateEmail(hasFocus: Boolean) {
        if (hasFocus) return

        if (emailIsValid(getEmail())) {
            joinNowCheckEmail.visibility = View.INVISIBLE
            emailIsValid = true
            updateFormStatus()
        } else {
            joinNowCheckEmail.visibility = View.VISIBLE
            emailIsValid = false
            updateFormStatus()
        }
    }

    private fun getEmail(): String {
        return newEmail.text.toString()
    }

    private fun getPassword(): String {
        return newPassword.text.toString()
    }
}
