package com.example.starbucks_mock_app

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

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

    fun checkInternetConnection(viewId: Int): Boolean {
        val cm = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        // documentation on the recommended getAllNetworks is not very well defined... so going to use this instead
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        val connected = activeNetwork?.isConnectedOrConnecting == true

        if (!connected) {
            Snackbar.make(
                findViewById(viewId),
                "No network connection",
                Snackbar.LENGTH_SHORT
            ).show()
        }

        return connected
    }
}