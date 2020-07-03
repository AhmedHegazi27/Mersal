package com.beta27.mersalsellers

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.beta27.mersalsellers.auth.LoginActivity
import com.beta27.mersalsellers.home.HomeActivity
import com.beta27.mersalsellers.info.InfoActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        val infoSharedPref = getSharedPreferences("infoShared", Context.MODE_PRIVATE)
        val pass = infoSharedPref.getBoolean("pass", false)
        CoroutineScope(Dispatchers.IO).launch {
            delay(4000)
            val user = Firebase.auth.currentUser
            if (user != null) {
                // User is signed in
                if (pass == true) {
                    startActivity(Intent(this@SplashScreenActivity, HomeActivity::class.java))
                    finish()
                } else {
                    startActivity(Intent(this@SplashScreenActivity, InfoActivity::class.java))
                    finish()
                }
            } else {
                // No user is signed in
                startActivity(Intent(this@SplashScreenActivity, LoginActivity::class.java))
                finish()
            }
        }
    }
}