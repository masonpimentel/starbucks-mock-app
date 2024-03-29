package com.example.starbucks_mock_app

import android.content.Intent
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

        joinNowEmail.setOnFocusChangeListener { _, hasFocus -> validateEmail(hasFocus) }
        joinNowPassword.setOnFocusChangeListener { _, hasFocus -> validatePassword(hasFocus) }
        joinNowPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                //nothing to do
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //nothing to do
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, count: Int) {
                validatePassword(false)
            }
        })
    }

    fun registerUser(view: View) {
        validateEmail(hasFocus = false, submissionCheck = true)
        validatePassword(hasFocus = false, submissionCheck = false)

        //future work - handle db connection not ready
        if (!formIsValid || !checkInternetConnection(R.id.joinNowLayout) || MainActivity.dynamoDBMapper == null) return

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
                    emailRegisteredView.visibility = View.VISIBLE
                }
            } else {
                MainActivity.dynamoDBMapper?.save(newUser)
                runOnUiThread {
                    emailRegisteredView.visibility = View.INVISIBLE
                }
                goToLocator()
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
        joinNowProgressBar.visibility = View.VISIBLE
    }

    private fun hideSpinner() {
        joinNowProgressBar.visibility = View.INVISIBLE
    }

    private fun validateEmail(hasFocus: Boolean, submissionCheck: Boolean = false) {
        if (!submissionCheck && hasFocus) return

        if (emailIsValid(getEmail())) {
            joinNowEmailTE.error = null
            emailIsValid = true
            updateFormStatus()
        } else {
            joinNowEmailTE.error = getString(R.string.join_now_check_email)
            emailIsValid = false
            updateFormStatus()
        }
    }

    private fun validatePassword(hasFocus: Boolean, submissionCheck: Boolean = false) {
        if (!submissionCheck && hasFocus) {
            passwordCompletion.visibility = View.VISIBLE
            joinNowPasswordRequirement.visibility = View.VISIBLE
            return
        }

        if (passwordIsValid(getPassword())) {
            passwordCompletion.setImageResource(R.drawable.ic_baseline_done_24px)
            passwordIsValid = true
            updateFormStatus()
        } else {
            passwordCompletion.setImageResource(R.drawable.ic_baseline_clear_24px)
            passwordIsValid = false
            updateFormStatus()
        }
    }

    private fun getEmail(): String {
        return joinNowEmail.text.toString()
    }

    private fun getPassword(): String {
        return joinNowPassword.text.toString()
    }

    fun goToLoginFromJoinNow(view: View) {
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }
}
