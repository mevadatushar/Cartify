package com.example.cartify.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import com.example.cartify.databinding.ActivityIntroBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class IntroActivity : BaseActivity() {

    lateinit var binding: ActivityIntroBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

                    initViews()
                     initView()
    }

    private fun initViews() {
        with(binding){
            btnStart.setOnClickListener {
                startActivity(Intent(this@IntroActivity, SignUpActivity::class.java))
            }
        }
    }


    private fun initView() {
        auth = Firebase.auth
        sharedPreferences = getPreferences(Context.MODE_PRIVATE)

        // Check if the user is already logged in using shared preferences
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        // Check if the user is already logged in
        val currentUser = auth.currentUser


            if (isLoggedIn || currentUser != null) {
                // User is already logged in, navigate to com.example.mytravelers.Activity.DashboardActivity
                startActivity(Intent(this@IntroActivity, MainActivity::class.java))
                finish()    // Finish IntroActivity so it's removed from the back stack

            } else {
                // User is not logged in, navigate to com.example.cartify.activity.LoginActivity
                binding.btnStart.setOnClickListener {
                    startActivity(Intent(this@IntroActivity, LoginActivity::class.java))
                    finish()    // Finish IntroActivity so it's removed from the back stack
                }
            }

    }
}