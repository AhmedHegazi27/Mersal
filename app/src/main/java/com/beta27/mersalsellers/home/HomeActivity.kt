package com.beta27.mersalsellers.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.beta27.mersalsellers.R
import com.beta27.mersalsellers.auth.LoginActivity
import com.beta27.mersalsellers.databinding.ActivityHomeBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        val navController = Navigation.findNavController(this, R.id.nav_host)
        NavigationUI.setupWithNavController(binding.homeNavigationView, navController)


    }
}