package com.example.starbucks_mock_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

//        toolbar_layout.apply {
//            setCollapsedTitleTextColor(500)
//            setExpandedTitleColor(500)
//        }


        //        Log.i("logger info", "go to login")
    }

    fun goToLogin(view: View) {
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
    }

    fun goToJoinNow(view: View) {
        val intent = Intent(this, JoinNowActivity::class.java)
        startActivity(intent)
    }
}
