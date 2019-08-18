package com.example.starbucks_mock_app

import android.content.Intent
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity

open class FormActivity : AppCompatActivity() {
    var formIsValid = false
    var emailIsValid = false
    var passwordIsValid = false

    fun updateFormStatus() {
        formIsValid = emailIsValid && passwordIsValid
    }

    open fun emailIsValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun passwordIsValid(password: String): Boolean {
        return password.isNotBlank()
    }

    fun goToLocator() {
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }
}