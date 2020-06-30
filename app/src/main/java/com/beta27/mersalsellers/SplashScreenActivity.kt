package com.beta27.mersalsellers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.beta27.mersalsellers.auth.LoginActivity
import com.beta27.mersalsellers.info.InfoActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        mAuth = FirebaseAuth.getInstance()

        CoroutineScope(Dispatchers.IO).launch {
            delay(4000)
            if (mAuth.currentUser != null) {
                startActivity(Intent(baseContext, InfoActivity::class.java))
                finish()
            } else {
                startActivity(Intent(baseContext, LoginActivity::class.java))
                finish()
            }
        }
    }
}