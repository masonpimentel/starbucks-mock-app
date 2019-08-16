package com.example.starbucks_mock_app

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity;

import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        setSupportActionBar(toolbar)

    }

    fun goToLocator(view: View) {
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }
}
